package com.mzm.firephoenix.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.oppa.utils.cardutils.CardResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;

@Component
public class OfflineLogic {
	public final static Log logger = LogFactory.getLog(OfflineLogic.class);
	@Autowired
	JdbcDaoSupport jdbcDaoSupport;

	public void sessionClosed(IoSession session) {
		long accountId = (long) session.getAttribute("accountId");
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		GameCache.removeIoSession(playerInfo.getSeoId(), session);
		GameCache.removePlayerInfo(accountId);
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return;
		}
		if (cr.getWin() > 0) {
			FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
			if (fivepkPlayerInfo == null) {
				return;
			}
			fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet());
			jdbcDaoSupport.update(fivepkPlayerInfo);
		}

	}
}
