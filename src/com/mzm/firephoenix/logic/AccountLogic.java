package com.mzm.firephoenix.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkAccount;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.protobuf.CoreProtocol.ErrorCode;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCGuestLogin;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCLogin;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCRegister;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;

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
						String sql = "insert into `fivepk_account` (name, password, seoid) values (\"" + account + "\", \"" + password + "\", \"" + seoid + "\")";
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
				session.setAttributeIfAbsent(GameConstant.SESSION_IS_REGISTERED, GameConstant.SESSION_IS_REGISTERED_VALUE);
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
		Integer isRegistered = (Integer) session.getAttribute(GameConstant.SESSION_IS_REGISTERED);
		long accountId = 0;
		if (isRegistered == null || isRegistered != null && isRegistered != GameConstant.SESSION_IS_REGISTERED_VALUE) {
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
		return MessageContent.newBuilder().setResult(0).setScLogin(SCLogin.newBuilder().setPic(fivepkPlayerInfo.getPic()).setNickname(fivepkPlayerInfo.getNickName()).setScore(fivepkPlayerInfo.getScore()).setCoin(fivepkPlayerInfo.getCoin()));
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
		return MessageContent.newBuilder().setResult(0).setScGuestLogin(SCGuestLogin.newBuilder().setAccount(String.valueOf(fivepkPlayerInfo.getAccountId())).setPic(pic).setNickname(fivepkPlayerInfo.getNickName()).setScore(fivepkPlayerInfo.getScore()));
	}
}
