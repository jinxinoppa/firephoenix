package com.mzm.firephoenix.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.QueryMeta;
import com.mzm.firephoenix.dao.entity.FivepkAccount;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.dao.entity.FivepkSeoIdList;
import com.mzm.firephoenix.protobuf.CoreProtocol.ErrorCode;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCGuestLogin;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCLogin;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCRegister;

/**
 * 
 * @author oppa
 *
 */
@Component
public class AccountLogic {
	public final static Log logger = LogFactory.getLog(AccountLogic.class);
	@Autowired
	JdbcDaoSupport jdbcDaoSupport;

	public Builder csRegister(IoSession session, MessageContent content) {
		logger.debug("csRegister : " + content.toString());
		String account = content.getCsRegister().getAccount();
		String password = content.getCsRegister().getPassword();
		String seoid = content.getCsRegister().getSeoid();
		if (account.isEmpty() || password.isEmpty() || seoid.isEmpty()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		/*
		 * if(!account.matches("^[a-zA-Z]\\w{5,17}$")){ return
		 * MessageContent.newBuilder
		 * ().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE); }
		 */
		List<String> seoIdList = jdbcDaoSupport.queryGroupBy(FivepkSeoIdList.class, new QueryMeta("seoid"));
		if (!seoIdList.contains(seoid)) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NO_LOGIN_SEOID_VALUE);
		}
		FivepkAccount fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{account}, new String[]{"name"});
		if (fivepkAccount == null) {
			fivepkAccount = new FivepkAccount();
			fivepkAccount.setName(account);
			fivepkAccount.setPassword(password);
			fivepkAccount.setSeoid(seoid);
			fivepkAccount.setAccountType(GameConstant.ACCOUNT_TYPE_PLAYER);
			try {
				int generatedKey = jdbcDaoSupport.getJdbcTemplate().execute(new ConnectionCallback<Integer>() {

					@Override
					public Integer doInConnection(Connection arg0) throws SQLException, DataAccessException {
						String sql = "insert into `fivepk_account` (name, password, seoid, account_type) values (\"" + account + "\", \"" + password + "\", \"" + seoid + "\", \"" + GameConstant.ACCOUNT_TYPE_PLAYER + "\")";
						PreparedStatement pstmt = (PreparedStatement) arg0.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						pstmt.executeUpdate();
						ResultSet rs = pstmt.getGeneratedKeys();
						int generatedKey = 0;
						while (rs.next()) {
							generatedKey = rs.getInt(1);
						}
						return generatedKey;
					}
				});
				FivepkPlayerInfo fivepkPlayerInfo = new FivepkPlayerInfo(generatedKey, "玩家@" + generatedKey, GameConstant.ACCOUNT_DEFAULT_PIC);

				jdbcDaoSupport.save(fivepkPlayerInfo);
				session.setAttributeIfAbsent(GameConstant.SESSION_IS_REGISTERED, seoid);
				session.setAttributeIfAbsent("accountId", fivepkPlayerInfo.getAccountId());
			} catch (Exception e) {
				throw e;
			}
		} else {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_EXIT_VALUE);
		}
		return MessageContent.newBuilder().setResult(0).setScRegister(SCRegister.newBuilder().setAccount(account));
	}

	public Builder csLogin(IoSession session, MessageContent content) {
		String name = content.getCsLogin().getAccount();
		String password = content.getCsLogin().getPassword();
		String seoid = (String) session.getAttribute(GameConstant.SESSION_IS_REGISTERED);
		long accountId = 0;
		if (seoid == null) {
			FivepkAccount fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{name}, new String[]{"name"});
			if (fivepkAccount == null) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
			} else if (!fivepkAccount.getPassword().equals(password)) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PWD_WRONG_VALUE);
			}
			accountId = fivepkAccount.getAccountId();
		} else {
			accountId = (Long) session.getAttribute("accountId");
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		session.setAttributeIfAbsent("accountId", fivepkPlayerInfo.getAccountId());
		session.removeAttribute(GameConstant.SESSION_IS_REGISTERED);
		session.setAttributeIfAbsent(GameConstant.SESSION_ACCOUNT_TYPE, GameConstant.ACCOUNT_TYPE_PLAYER);

		GameCache.putPlayerInfo(accountId, fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName(), seoid);
		GameCache.putIoSessionIfAbsent(seoid, session);
		return MessageContent.newBuilder().setResult(0).setScLogin(SCLogin.newBuilder().setPic(fivepkPlayerInfo.getPic()).setNickname(fivepkPlayerInfo.getNickName()).setScore(fivepkPlayerInfo.getScore()).setCoin(fivepkPlayerInfo.getCoin()).setNickNameCount(fivepkPlayerInfo.getNickNameCount()));
	}

	public Builder csGuestLogin(IoSession session, MessageContent content) {
		int generatedKey = 0;
		byte pic = GameConstant.ACCOUNT_DEFAULT_PIC;
		FivepkPlayerInfo fivepkPlayerInfo = null;
		String accountId = content.getCsGuestLogin().getAccount();
		if (accountId == null || accountId != null && accountId.isEmpty()) {
			try {
				generatedKey = jdbcDaoSupport.getJdbcTemplate().execute(new ConnectionCallback<Integer>() {

					@Override
					public Integer doInConnection(Connection arg0) throws SQLException, DataAccessException {
						String sql = "insert into `fivepk_account` values ()";
						PreparedStatement pstmt = (PreparedStatement) arg0.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						pstmt.executeUpdate();
						ResultSet rs = pstmt.getGeneratedKeys();
						int generatedKey = 0;
						while (rs.next()) {
							generatedKey = rs.getInt(1);
						}
						return generatedKey;
					}
				});
				fivepkPlayerInfo = new FivepkPlayerInfo(generatedKey, "游客@" + generatedKey, pic);
				jdbcDaoSupport.save(fivepkPlayerInfo);
			} catch (Exception e) {
				throw e;
			}
		} else {
			fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
			if (fivepkPlayerInfo == null) {
				logger.debug("guest login wrong account : [" + accountId + "]");
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
			}
			pic = fivepkPlayerInfo.getPic();
		}
		session.setAttribute("accountId", fivepkPlayerInfo.getAccountId());
		session.setAttribute(GameConstant.SESSION_ACCOUNT_TYPE, GameConstant.ACCOUNT_TYPE_GUEST);
		GameCache.putPlayerInfo(fivepkPlayerInfo.getAccountId(), fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName());
		return MessageContent.newBuilder().setResult(0).setScGuestLogin(SCGuestLogin.newBuilder().setAccount(String.valueOf(fivepkPlayerInfo.getAccountId())).setPic(pic).setNickname(fivepkPlayerInfo.getNickName()).setScore(fivepkPlayerInfo.getScore()).setNickNameCount(fivepkPlayerInfo.getNickNameCount()));
	}

	public Builder ccBinding(IoSession session, MessageContent content) {
		logger.debug("csRegister : " + content.toString());
		String account = content.getCcBinding().getAccount();
		String password = content.getCcBinding().getPassword();
		String seoid = content.getCcBinding().getSeoid();
		if (account.isEmpty() || password.isEmpty() || seoid.isEmpty()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		List<String> seoIdList = jdbcDaoSupport.queryGroupBy(FivepkSeoIdList.class, new QueryMeta("seoid"));
		if (!seoIdList.contains(seoid)) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NO_LOGIN_SEOID_VALUE);
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		FivepkAccount fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{accountId});
		if (fivepkAccount == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		} else if (fivepkAccount.getAccountType() == 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NO_BUILDING_VALUE);
		}
		fivepkAccount.setName(account);
		fivepkAccount.setPassword(password);
		fivepkAccount.setSeoid(seoid);
		fivepkAccount.setAccountType(GameConstant.ACCOUNT_TYPE_PLAYER);
		jdbcDaoSupport.update(fivepkAccount);
		GameCache.putPlayerInfo(accountId, seoid);
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccHeadPic(IoSession session, MessageContent content) {
		logger.debug("csRegister : " + content.toString());
		byte headpic = (byte) content.getCcHeadPic().getHeadPic();
		if (headpic < 0 && headpic > 6) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		fivepkPlayerInfo.setPic(headpic);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		GameCache.putPlayerInfo(fivepkPlayerInfo.getAccountId(), fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName());
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccNickName(IoSession session, MessageContent content) {
		logger.debug("csRegister : " + content.toString());
		String nickName = content.getCcNickName().getNickName();
		if (nickName == null || nickName == "") {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NO_NULL_NICK_NAME_VALUE);
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		} else if (fivepkPlayerInfo.getNickNameCount() >= 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NICK_NAME_COUNT_VALUE);
		}
		fivepkPlayerInfo.setNickName(nickName);
		fivepkPlayerInfo.setNickNameCount(fivepkPlayerInfo.getNickNameCount() + 1);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		GameCache.putPlayerInfo(fivepkPlayerInfo.getAccountId(), fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName());
		return MessageContent.newBuilder().setResult(0);
	}
}
