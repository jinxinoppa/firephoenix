package com.mzm.firephoenix.utils;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.mina.core.session.IoSession;

import com.mzm.firephoenix.mina.ByteMessage;


public class CommonUtil {
	
	public static long myTime;
	
//	public static void sendToRole(String roleId,ByteMessage bm){
//		IoSession session = SessionCache.getRoleSession(roleId);
//		if (session == null || !session.isConnected()) {
//			return;
//		}
//		session.write(bm);
//	}
	
	/**
	 * 检查时间间隔
	 * @param ssn
	 * @param key
	 * @param times
	 * @return
	 */
	public static boolean checkTimeStamp(IoSession ssn, Object key, long times) {
		Long oldtime = null;
		if(ssn==null) return false;
		if (ssn.getAttribute(key) == null) {
			ssn.setAttribute(key, new Long(System.currentTimeMillis()));
			return true;
		} else {
			oldtime = (Long) ssn.getAttribute(key);
		}
		long timespace = System.currentTimeMillis() - oldtime.longValue();
		if (timespace < times) {
			return false;
		} else {
			ssn.setAttribute(key, new Long(System.currentTimeMillis()));
			return true;
		}
	}
	public static boolean checkTimeStamp(Map<Object,Long> map, Object key, long times) {
		Long oldtime = null;
		if(map==null) return false;
		if (map.get(key) == null) {
			map.put(key, new Long(System.currentTimeMillis()));
			return true;
		} else {
			oldtime = (Long) map.get(key);
		}
		long timespace = System.currentTimeMillis() - oldtime.longValue();
		if (timespace < times) {
			return false;
		} else {
			map.put(key, new Long(System.currentTimeMillis()));
			return true;
		}
	}
	
	public static String getIp(IoSession session) {
		SocketAddress sa = session.getRemoteAddress();
		String loginIp;
		if (sa != null) {
			loginIp = sa.toString();
			String[] arr = loginIp.substring(1).split(":");
			if (arr.length > 1) {
				loginIp = arr[0];
			}
		} else {
			loginIp = "errorIp";
		}
		return loginIp;
	}

	/**
	 * 获取servlet 访问ip地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 将number向上舍入为最接近的整数,或最接近的指定基数的倍数
	 * 
	 * @param number
	 * @param significance
	 *            用于向上舍入的基数
	 * 
	 * @return 比如significance=50，则number=49，result=50;number=50,result=100
	 */
	public static int ceiling(double number, int significance) {
		if (Math.abs(number) < Math.abs(significance)) {
			return significance;
		} else {
			int multiple = ((Double) (number / significance)).intValue() + 1;// 倍数
			return significance * multiple;
		}
	}

	public static String genMapId(String mapType, int objId) {
		return mapType + "_" + objId;
	}

	public static String getExploreKey(int reqType, int reqAttrObj) {
		return reqType + "_" + reqAttrObj;
	}
	
	public static List<Byte> getByteListByByteMessage(ByteMessage data) {
		List<Byte> list = new ArrayList<Byte>();
		int size = data.readArrLength();
		for (int i = 0; i < size; i++) {
			list.add(data.readByte());
		}
		return list;
	}
	
//	public static String genNextSeq(String tableName) {
//		long nextSeqId = RoleSeqCache.getSeqMap(tableName).incrementAndGet();
//		return ServerConfData.SERVER_ID + "_" + RoleSeqCache.getPreBaseSeq()
//				+ "_" + nextSeqId;
//	}
//	
//	public static int checkLoginDiff(Role role, Date lastLoginTime){
//		int loginDiff = DateUtil.dayDiff(lastLoginTime.getTime(), System.currentTimeMillis());
//		int logoutDiff = 0;
//		if(role.getLastLogoutTime()!=null){
//			logoutDiff = DateUtil.dayDiff(role.getLastLogoutTime().getTime(), System.currentTimeMillis());
//		}
//		int diff = loginDiff<logoutDiff?loginDiff:logoutDiff;
//		return diff;
//	}
}
