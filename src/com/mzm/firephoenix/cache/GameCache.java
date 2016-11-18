package com.mzm.firephoenix.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

public class GameCache {
	private final static Map<String, List<IoSession>> seoIdSessionMap = new HashMap<String, List<IoSession>>();

	public static IoSession putIoSessionIfAbsent(String seoId, IoSession session) {
		List<IoSession> sessionList = seoIdSessionMap.get(seoId);
		if (sessionList == null) {
			sessionList = new ArrayList<IoSession>();
			seoIdSessionMap.put(seoId, sessionList);
		}
		sessionList.add(session);
		return session;
	}

	public static List<IoSession> getSeoIdIoSessionList(String seoId) {
		return seoIdSessionMap.get(seoId);
	}

	public static void removeIoSession(String seoId, IoSession session) {
		seoIdSessionMap.get(seoId).remove(session);
	}

	private final static Map<Long, PlayerInfo> playerInfoMap = new HashMap<Long, PlayerInfo>();

	public static PlayerInfo putPlayerInfo(long accountId, int pic, String nickName, String seoId, IoSession playerInfoSession, byte accountType) {
		PlayerInfo playerInfo = playerInfoMap.get(accountId);
		if (playerInfo == null) {
			playerInfo = new PlayerInfo(pic, nickName, seoId, playerInfoSession);
			playerInfoMap.put(accountId, playerInfo);
		} else {
			playerInfo.setPic(pic);
			playerInfo.setNickName(nickName);
			playerInfo.setSeoId(seoId);
			playerInfo.setPlayerInfoSession(playerInfoSession);
			playerInfo.setAccountType(accountType);
		}
		return playerInfo;
	}

	public static PlayerInfo putPlayerInfo(long accountId, int pic, String nickName, byte accountType) {
		PlayerInfo playerInfo = playerInfoMap.get(accountId);
		if (playerInfo == null) {
			playerInfo = new PlayerInfo(pic, nickName);
			playerInfoMap.put(accountId, playerInfo);
		} else {
			playerInfo.setPic(pic);
			playerInfo.setNickName(nickName);
			playerInfo.setAccountType(accountType);
		}
		return playerInfo;
	}

	public static PlayerInfo putPlayerInfo(long accountId, int pic, String nickName) {
		PlayerInfo playerInfo = playerInfoMap.get(accountId);
		if (playerInfo == null) {
			playerInfo = new PlayerInfo(pic, nickName);
			playerInfoMap.put(accountId, playerInfo);
		} else {
			playerInfo.setPic(pic);
			playerInfo.setNickName(nickName);
		}
		return playerInfo;
	}
	
	public static PlayerInfo putPlayerInfo(long accountId, String seoId, byte accountType) {
		PlayerInfo playerInfo = playerInfoMap.get(accountId);
		playerInfo.setSeoId(seoId);
		playerInfo.setAccountType(accountType);
		return playerInfo;
	}

	public static PlayerInfo getPlayerInfo(long accountId) {
		return playerInfoMap.get(accountId);
	}

	public static void removePlayerInfo(long accountId) {
		playerInfoMap.remove(accountId);
	}

	private final static Map<String, Map<String, MachineInfo>> seoIdMachineInfoMap = new HashMap<String, Map<String, MachineInfo>>();

	public static MachineInfo getMachineInfo(String seoId, String machineId) {
		Map<String, MachineInfo> machineIdMap = seoIdMachineInfoMap.get(seoId);
		if (machineIdMap == null) {
			machineIdMap = new HashMap<String, MachineInfo>();
			seoIdMachineInfoMap.put(seoId, machineIdMap);
		}
		MachineInfo machineInfo = machineIdMap.get(machineId);
		if (machineInfo == null) {
			machineInfo = new MachineInfo();
			machineIdMap.put(machineId, machineInfo);
		}
		return machineInfo;
	}

	public static MachineInfo updateMachineInfo(String seoId, String machineId, int machineType, long accountId, Date stayTime) {
		Map<String, MachineInfo> machineIdMap = seoIdMachineInfoMap.get(seoId);
		if (machineIdMap == null) {
			machineIdMap = new HashMap<String, MachineInfo>();
			seoIdMachineInfoMap.put(seoId, machineIdMap);
		}
		MachineInfo machineInfo = machineIdMap.get(machineId);
		if (machineInfo == null) {
			machineInfo = new MachineInfo();
			machineIdMap.put(machineId, machineInfo);
		}
		machineInfo.setMachineType(machineType);
		machineInfo.setAccountId(accountId);
		machineInfo.setStayTime(stayTime);
		return machineInfo;
	}

	public static MachineInfo updateMachineInfo(String seoId, String machineId, int machineType, long accountId) {
		Map<String, MachineInfo> machineIdMap = seoIdMachineInfoMap.get(seoId);
		if (machineIdMap == null) {
			machineIdMap = new HashMap<String, MachineInfo>();
			seoIdMachineInfoMap.put(seoId, machineIdMap);
		}
		MachineInfo machineInfo = machineIdMap.get(machineId);
		if (machineInfo == null) {
			machineInfo = new MachineInfo();
			machineIdMap.put(machineId, machineInfo);
		}
		machineInfo.setMachineType(machineType);
		machineInfo.setAccountId(accountId);
		return machineInfo;
	}
}
