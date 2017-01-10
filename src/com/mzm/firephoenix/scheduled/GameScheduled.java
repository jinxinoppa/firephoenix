package com.mzm.firephoenix.scheduled;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.MachineInfo;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;

@Component
public class GameScheduled {

	public final static Log logger = LogFactory.getLog(GameScheduled.class);

	@Autowired
	JdbcDaoSupport jdbcDaoSupport;

//	@Scheduled(cron = "0 0/5 * * * ?")
	public void perFiveMinite() {
//		long startTime = System.currentTimeMillis();
//		logger.info("five minute schedule started");
//		List<FivepkSeoId> fivepkSeoIdList = jdbcDaoSupport.query(FivepkSeoId.class);
//		for (FivepkSeoId fivepkSeoId : fivepkSeoIdList) {
//			MachineInfo machineInfo = GameCache.getMachineInfo(fivepkSeoId.getSeoId(), fivepkSeoId.getSeoMachineId());
//			logger.info("update machine info [ seoid : " + fivepkSeoId.getSeoId() + " machineId : " + fivepkSeoId.getSeoMachineId() + " accountId : " + machineInfo.getAccountId() + " machineType : " + machineInfo.getMachineType() + " ]");
//			if (machineInfo.getAccountId() != fivepkSeoId.getAccountId() || machineInfo.getMachineType() != fivepkSeoId.getSeoMachineType()) {
//				fivepkSeoId.setAccountId(machineInfo.getAccountId());
//				fivepkSeoId.setSeoMachineType(machineInfo.getMachineType());
//				jdbcDaoSupport.update(fivepkSeoId);
//			}
//		}
//		long endTime = System.currentTimeMillis() - startTime;
//		logger.info("five minute schedule end lasting :　[ " + endTime + " ]");
	}
}
