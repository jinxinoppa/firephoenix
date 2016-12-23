package com.mzm.firephoenix.logic;

import java.security.MessageDigest;
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
import com.mzm.firephoenix.constant.ErrorCode;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.QueryMeta;
import com.mzm.firephoenix.dao.entity.FivepkAccount;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.dao.entity.FivepkSeoIdList;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCBinding;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCNickName;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCGuestLogin;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCLogin;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCMachineInfo;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCRegister;
import com.mzm.firephoenix.utils.CardResult;

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
	@Autowired
	OfflineLogic offlineLogic;

	public Builder csRegister(IoSession session, MessageContent content) {
		logger.debug("csRegister : " + content.toString());
		String account = content.getCsRegister().getAccount();
		String password = content.getCsRegister().getPassword();
		String seoid = content.getCsRegister().getSeoid();
		if (account.isEmpty() || password.isEmpty() || seoid.isEmpty()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_UNAME_UPASS_USEOID.getErrorCode());
		}
		if (!account.matches("^[A-Za-z0-9]{5,17}$")) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_USER_NAME.getErrorCode());
		}
		List<String> seoIdList = jdbcDaoSupport.queryGroupBy(FivepkSeoIdList.class, new QueryMeta("seoid"));
		if (!seoIdList.contains(seoid)) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NO_LOGIN_SEOID.getErrorCode());
		}
		FivepkAccount fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{account}, new String[]{"name"});
		if (fivepkAccount == null) {
			fivepkAccount = new FivepkAccount(account, password, seoid, GameConstant.ACCOUNT_TYPE_PLAYER, (String)session.getAttribute("ipAddress"));
			jdbcDaoSupport.save(fivepkAccount);
			fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{account}, new String[]{"name"});
//			fivepkAccount.setName(account);
//			fivepkAccount.setPassword(password);
//			fivepkAccount.setSeoid(seoid);
//			fivepkAccount.setAccountType(GameConstant.ACCOUNT_TYPE_PLAYER);
			try {
//				int generatedKey = jdbcDaoSupport.getJdbcTemplate().execute(new ConnectionCallback<Integer>() {
//
//					@Override
//					public Integer doInConnection(Connection arg0) throws SQLException, DataAccessException {
//						String sql = "insert into `fivepk_account` (name, password, seoid, account_type) values (\"" + account + "\", \"" + password + "\", \"" + seoid + "\", \"" + GameConstant.ACCOUNT_TYPE_PLAYER + "\")";
//						PreparedStatement pstmt = (PreparedStatement) arg0.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//						pstmt.executeUpdate();
//						ResultSet rs = pstmt.getGeneratedKeys();
//						int generatedKey = 0;
//						while (rs.next()) {
//							generatedKey = rs.getInt(1);
//						}
//						return generatedKey;
//					}
//				});
				FivepkPlayerInfo fivepkPlayerInfo = new FivepkPlayerInfo(fivepkAccount.getAccountId(), "玩家@" + fivepkAccount.getAccountId(), GameConstant.ACCOUNT_DEFAULT_PIC, 20000);

				jdbcDaoSupport.save(fivepkPlayerInfo);
				session.setAttributeIfAbsent(GameConstant.SESSION_IS_REGISTERED, seoid);
				session.setAttributeIfAbsent("accountId", fivepkPlayerInfo.getAccountId());
			} catch (Exception e) {
				throw e;
			}
		} else {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_EXIT.getErrorCode());
		}
		return MessageContent.newBuilder().setResult(0).setScRegister(SCRegister.newBuilder().setAccount(account));
	}

	public Builder csLogin(IoSession session, MessageContent content) {
		String name = content.getCsLogin().getAccount();
		String password = content.getCsLogin().getPassword();
		String seoid = (String) session.getAttribute(GameConstant.SESSION_IS_REGISTERED);
		Long accountId = (long) 0;
		if (seoid == null) {
			FivepkAccount fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{name}, new String[]{"name"});
			if (fivepkAccount == null) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
			} else if (!fivepkAccount.getPassword().equals(password)) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PWD_WRONG.getErrorCode());
			}
			accountId = fivepkAccount.getAccountId();
			PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
			if (playerInfo != null) {
				IoSession ioSession = playerInfo.getPlayerInfoSession();
				accountId = (long) ioSession.removeAttribute("accountId");
				if (accountId != null){
					CardResult cr = (CardResult) ioSession.removeAttribute("cardResult");
					String machineId = (String) ioSession.removeAttribute("machineId");
					GameCache.removeIoSession(playerInfo.getSeoId(), ioSession);
					GameCache.removePlayerInfo(accountId);
					if (cr != null && cr.getWin() > 0) {
						FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
						fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet() + cr.getGiftWin());
						jdbcDaoSupport.update(fivepkPlayerInfo);
					}
					if (machineId != null) {
						GameCache.updateMachineInfo(playerInfo.getSeoId(),machineId, GameConstant.MACHINE_TYPE_FREE, 0, null);
						List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(playerInfo.getSeoId());
						for (IoSession ioSession2 : sessionList) {
							if (!ioSession2.isClosing() && ioSession2.isConnected() && (Long) ioSession2.getAttribute("accountId") != accountId) {
								MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
								returnMessagePack.setCmd(Cmd.CMD_MACHINE_INFO);
								returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScMachineInfo(SCMachineInfo.newBuilder().setMachineId(machineId).setMachineType(GameConstant.MACHINE_TYPE_FREE)));
								logger.info("sent message pack : " + returnMessagePack.toString());
								ioSession2.write(returnMessagePack);
							}
						}
					}
					
					MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
					returnMessagePack.setCmd(Cmd.CMD_LOGIN);
					returnMessagePack.setContent(MessageContent.newBuilder().setResult(ErrorCode.ERROR_LOGIN_OTHER_DEVICES.getErrorCode()));
					logger.info("sent message pack : " + returnMessagePack.toString());
					ioSession.write(returnMessagePack);
					ioSession.closeOnFlush();
					return MessageContent.newBuilder().setResult(ErrorCode.ERROR_YOUR_ACCOUNT_HAS_BEEN_LANDED.getErrorCode());
				}
			}
			seoid = fivepkAccount.getSeoid();
			fivepkAccount.setAccountIp((String)session.getAttribute("ipAddress"));
			jdbcDaoSupport.update(fivepkAccount);
		} else {
			accountId = (Long) session.getAttribute("accountId");
		}

		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		session.setAttributeIfAbsent("accountId", fivepkPlayerInfo.getAccountId());
		session.removeAttribute(GameConstant.SESSION_IS_REGISTERED);
		GameCache.putPlayerInfo(accountId, fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName(), seoid, session, GameConstant.ACCOUNT_TYPE_PLAYER);
		GameCache.putIoSessionIfAbsent(seoid, session);
		return MessageContent.newBuilder().setResult(0)
				.setScLogin(SCLogin.newBuilder().setPic(fivepkPlayerInfo.getPic()).setNickname(fivepkPlayerInfo.getNickName()).setScore(fivepkPlayerInfo.getScore()).setCoin(fivepkPlayerInfo.getCoin()).setNickNameCount(fivepkPlayerInfo.getNickNameCount()).setSeoid(seoid));
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
						String sql = "insert into `fivepk_account` (seoid) values (\"CE\")";
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
				fivepkPlayerInfo = new FivepkPlayerInfo(generatedKey, "游客@" + generatedKey, pic, 20000);
				jdbcDaoSupport.save(fivepkPlayerInfo);
			} catch (Exception e) {
				throw e;
			}
		} else {
			fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
			if (fivepkPlayerInfo == null) {
				logger.debug("guest login wrong account : [" + accountId + "]");
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
			}
			pic = fivepkPlayerInfo.getPic();
		}
		session.setAttribute("accountId", fivepkPlayerInfo.getAccountId());
		GameCache.putPlayerInfo(fivepkPlayerInfo.getAccountId(), fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName(), GameConstant.MACHINE_SEOID_GUEST, session, GameConstant.ACCOUNT_TYPE_GUEST);
		GameCache.putIoSessionIfAbsent(GameConstant.MACHINE_SEOID_GUEST, session);
		return MessageContent.newBuilder().setResult(0)
				.setScGuestLogin(SCGuestLogin.newBuilder().setAccount(String.valueOf(fivepkPlayerInfo.getAccountId())).setPic(pic).setNickname(fivepkPlayerInfo.getNickName()).setScore(fivepkPlayerInfo.getScore()).setNickNameCount(fivepkPlayerInfo.getNickNameCount()));
	}

	public Builder ccBinding(IoSession session, MessageContent content) {
		logger.debug("csRegister : " + content.toString());
		String account = content.getCcBinding().getAccount();
		String password = content.getCcBinding().getPassword();
		String seoid = content.getCcBinding().getSeoid();
		if (account.isEmpty() || password.isEmpty() || seoid.isEmpty()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_UNAME_UPASS_USEOID.getErrorCode());
		}
		if (!account.matches("^[A-Za-z0-9]{5,17}$")) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_USER_NAME.getErrorCode());
		}
		FivepkAccount fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{account}, new String[]{"name"});
		if (fivepkAccount != null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_USER_EXIST.getErrorCode());
		}
		List<String> seoIdList = jdbcDaoSupport.queryGroupBy(FivepkSeoIdList.class, new QueryMeta("seoid"));
		if (!seoIdList.contains(seoid)) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NO_LOGIN_SEOID.getErrorCode());
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{accountId});
		if (fivepkAccount == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		} else if (fivepkAccount.getAccountType() == 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NO_BUILDING.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		if (fivepkPlayerInfo.getNickName().startsWith("游客")) {
			fivepkPlayerInfo.setNickName(fivepkPlayerInfo.getNickName().replace("游客", "玩家"));
			jdbcDaoSupport.update(fivepkPlayerInfo);
		}
		fivepkAccount.setName(account);
		fivepkAccount.setPassword(password);
		fivepkAccount.setSeoid(seoid);
		fivepkAccount.setAccountType(GameConstant.ACCOUNT_TYPE_PLAYER);
		jdbcDaoSupport.update(fivepkAccount);
		GameCache.putPlayerInfo(accountId, seoid, GameConstant.ACCOUNT_TYPE_PLAYER);
		GameCache.removeIoSession(GameConstant.MACHINE_SEOID_GUEST, session);
		GameCache.putIoSessionIfAbsent(seoid, session);
		return MessageContent.newBuilder().setResult(0).setCcBinding(CCBinding.newBuilder().setAccount(account).setPassword(password).setSeoid(seoid));
	}

	public Builder ccHeadPic(IoSession session, MessageContent content) {
		byte headpic = (byte) content.getCcHeadPic().getHeadPic();
		if (headpic < 0 && headpic > 6) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_HEAD_PIC_NOT_EXIST.getErrorCode());
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
		fivepkPlayerInfo.setPic(headpic);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		GameCache.putPlayerInfo(fivepkPlayerInfo.getAccountId(), fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName());
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccNickName(IoSession session, MessageContent content) {
		String nickName = content.getCcNickName().getNickName();
		if (nickName == null || nickName.isEmpty()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NICK_NAME_ILLEGAL.getErrorCode());
		}
		for (int i = 0; i < GameConstant.NICKNAMECHECK.length; i++) {
			if (nickName.equals(GameConstant.NICKNAMECHECK[i])){
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NICK_NAME_LAW.getErrorCode());
			}
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{nickName}, new String[]{"nickName"});
		if (fivepkPlayerInfo != null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NICK_NAME_REPEATED.getErrorCode());
		}
		fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		} else if (fivepkPlayerInfo.getNickNameCount() >= 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NICK_NAME_COUNT.getErrorCode());
		}
		fivepkPlayerInfo.setNickName(nickName);
		fivepkPlayerInfo.setNickNameCount(fivepkPlayerInfo.getNickNameCount() + 1);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		GameCache.putPlayerInfo(fivepkPlayerInfo.getAccountId(), fivepkPlayerInfo.getPic(), fivepkPlayerInfo.getNickName());
		return MessageContent.newBuilder().setResult(0).setCcNickName(CCNickName.newBuilder().setNickName(nickName).setNickNameCount(fivepkPlayerInfo.getNickNameCount()));
	}

	public Builder csLoginOut(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		offlineLogic.sessionClosed(session);
		return MessageContent.newBuilder().setResult(0);
	}

	public static String shaEncode(String inStr) throws Exception {
		MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance("SHA");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}

		byte[] byteArray = inStr.getBytes("UTF-8");
		byte[] md5Bytes = sha.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

}
