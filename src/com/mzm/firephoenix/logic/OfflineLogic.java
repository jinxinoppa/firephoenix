package com.mzm.firephoenix.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cardutils.CardResult;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;

@Component
public class OfflineLogic {
	public final static Log logger = LogFactory.getLog(OfflineLogic.class);
	@Autowired
	JdbcDaoSupport jdbcDaoSupport;

	public void sessionClosed(IoSession session) {
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return;
		}
		if (cr.getWin() > 0){
			long accountId = (long) session.getAttribute("accountId");
			FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
			if (fivepkPlayerInfo == null) {
				return;
			}
			fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet());
			jdbcDaoSupport.update(fivepkPlayerInfo);
		}
	}
}
