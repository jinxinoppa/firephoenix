package com.mzm.firephoenix.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

public class GameCache {
	private static volatile Map<String, List<IoSession>> seoIdSessionMap = new ConcurrentHashMap<String, List<IoSession>>();

	public static synchronized IoSession putIoSessionIfAbsent(String seoId, IoSession session) {
		List<IoSession> sessionList = seoIdSessionMap.get(seoId);
		if (sessionList == null) {
			sessionList = new ArrayList<IoSession>();
			seoIdSessionMap.put(seoId, sessionList);
		}
		sessionList.add(session);
		return session;
	}

	public static synchronized List<IoSession> getSeoIdIoSessionList(String seoId) {
		return seoIdSessionMap.get(seoId);
	}

	public static synchronized void removeIoSession(String seoId, IoSession session) {
		seoIdSessionMap.get(seoId).remove(session);
	}

	private static volatile Map<Long, PlayerInfo> playerInfoMap = new ConcurrentHashMap<Long, PlayerInfo>();

	public static synchronized PlayerInfo putPlayerInfo(long accountId, int pic, String nickName, String seoId, IoSession playerInfoSession, byte accountType, String udid) {
		PlayerInfo playerInfo = playerInfoMap.get(accountId);
		if (playerInfo == null) {
			playerInfo = new PlayerInfo(pic, nickName, seoId, playerInfoSession, accountType, udid);
			playerInfoMap.put(accountId, playerInfo);
		} else {
			playerInfo.setPic(pic);
			playerInfo.setNickName(nickName);
			playerInfo.setSeoId(seoId);
			playerInfo.setPlayerInfoSession(playerInfoSession);
			playerInfo.setAccountType(accountType);
			playerInfo.setUdid(udid);
		}
		return playerInfo;
	}
	
	public static synchronized PlayerInfo putPlayerInfo(long accountId, int pic, String nickName, String seoId, IoSession playerInfoSession, byte accountType) {
		PlayerInfo playerInfo = playerInfoMap.get(accountId);
		if (playerInfo == null) {
			playerInfo = new PlayerInfo(pic, nickName, seoId, playerInfoSession, accountType);
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

	public static synchronized PlayerInfo putPlayerInfo(long accountId, int pic, String nickName, byte accountType) {
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

	public static synchronized PlayerInfo putPlayerInfo(long accountId, int pic, String nickName) {
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

	public static synchronized PlayerInfo putPlayerInfo(long accountId, String seoId, byte accountType) {
		PlayerInfo playerInfo = playerInfoMap.get(accountId);
		playerInfo.setSeoId(seoId);
		playerInfo.setAccountType(accountType);
		return playerInfo;
	}

	public static synchronized PlayerInfo getPlayerInfo(long accountId) {
		return playerInfoMap.get(accountId);
	}

	public static synchronized void removePlayerInfo(long accountId) {
		playerInfoMap.remove(accountId);
	}

//	private static volatile Map<String, Map<String, MachineInfo>> seoIdMachineInfoMap = new ConcurrentHashMap<String, Map<String, MachineInfo>>();
//
//	public static synchronized MachineInfo getMachineInfo(String seoId, String machineId) {
//		Map<String, MachineInfo> machineIdMap = seoIdMachineInfoMap.get(seoId);
//		if (machineIdMap == null) {
//			machineIdMap = new HashMap<String, MachineInfo>();
//			seoIdMachineInfoMap.put(seoId, machineIdMap);
//		}
//		MachineInfo machineInfo = machineIdMap.get(machineId);
//		if (machineInfo == null) {
//			machineInfo = new MachineInfo();
//			machineIdMap.put(machineId, machineInfo);
//		}
//		return machineInfo;
//	}
//
//	public static synchronized MachineInfo updateMachineInfo(String seoId, String machineId, int machineType, long accountId, Date stayTime) {
//		Map<String, MachineInfo> machineIdMap = seoIdMachineInfoMap.get(seoId);
//		if (machineIdMap == null) {
//			machineIdMap = new HashMap<String, MachineInfo>();
//			seoIdMachineInfoMap.put(seoId, machineIdMap);
//		}
//		MachineInfo machineInfo = machineIdMap.get(machineId);
//		if (machineInfo == null) {
//			machineInfo = new MachineInfo();
//			machineIdMap.put(machineId, machineInfo);
//		}
//		machineInfo.setMachineType(machineType);
//		machineInfo.setAccountId(accountId);
//		machineInfo.setStayTime(stayTime);
//		return machineInfo;
//	}
//
//	public static synchronized MachineInfo updateMachineInfo(String seoId, String machineId, int machineType, long accountId) {
//		Map<String, MachineInfo> machineIdMap = seoIdMachineInfoMap.get(seoId);
//		if (machineIdMap == null) {
//			machineIdMap = new HashMap<String, MachineInfo>();
//			seoIdMachineInfoMap.put(seoId, machineIdMap);
//		}
//		MachineInfo machineInfo = machineIdMap.get(machineId);
//		if (machineInfo == null) {
//			machineInfo = new MachineInfo();
//			machineIdMap.put(machineId, machineInfo);
//		}
//		machineInfo.setMachineType(machineType);
//		machineInfo.setAccountId(accountId);
//		return machineInfo;
//	}

	private static final Map<String, Map<Integer, List<Integer>>> prefabCardsPoolMap = new HashMap<String, Map<Integer, List<Integer>>>();

	public static List<Integer> addPrefabCardsPoolRandomSize(String machineId, int winType) {
		Map<Integer, List<Integer>> subMap = prefabCardsPoolMap.get(machineId);
		if (subMap == null) {
			subMap = new HashMap<Integer, List<Integer>>();
			prefabCardsPoolMap.put(machineId, subMap);
		}
		List<Integer> randomSizeList = subMap.get(winType);
		if (randomSizeList == null) {
			randomSizeList = new ArrayList<Integer>();
			subMap.put(winType, randomSizeList);
		}
		if (randomSizeList.size() >= 24) {
			randomSizeList.clear();
		}
		return randomSizeList;
	}
	
	private static final Map<String, Integer> forceSevenBetterMap = new HashMap<String, Integer>();

	public static Map<String, Integer> getForcesevenbettermap() {
		return forceSevenBetterMap;
	}
	
}
