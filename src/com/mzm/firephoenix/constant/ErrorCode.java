package com.mzm.firephoenix.constant;

public enum ErrorCode {
	ERROR_ACCOUNT_EXIT(1001), // 账号存在
	ERROR_MUTI_LOGIN(1002), // 重复登陆
	ERROR_PWD_WRONG(1003), // 密码错误
	ERROR_PLAYER_NOT_EXIT(1004), // 玩家不存在

	ERROR_SMS_INVALID_PARAMETER(1017), // 传递的参数不正确
	ERROR_ACCOUNT_RECONNECT(1018), // 请重新登录

	ERROR_CARD_BET_SCORE_0(2000), // 比倍分数不能为0
	ERROR_CARD_GUEST_COINSCORE(2001), // 游客不能存取分
	ERROR_CARD_BET_SCORE_NOT_ENOUGH(2002), // 发牌下分分数不足
	ERROR_CARD_COMPARE_CARD_BET_SCORE_NOT_ENOUGH(2003), // 比倍下分分数不足
	ERROR_NO_BUILDING(2004), // 已经绑定的玩家不能再次绑定
	ERROR_NICK_NAME_ILLEGAL(2005), // 昵称不能为空
	ERROR_NO_LOGIN_SEOID(2006), // 代理商没有注册
	ERROR_NICK_NAME_COUNT(2007), // 昵称只能修改一次
	ERROR_USER_NAME(2008), // 用户名必须为6~18位字母或数字
	ERROR_USER_EXIST(2009), // 被绑定的用户已存在
	ERROR_YOUR_ACCOUNT_HAS_BEEN_LANDED(2010), // 您的帐号已经被登陆
	ERROR_LOGIN_OTHER_DEVICES(2011), // 您的帐号在其它设备登录,请从新登录

	ERROR_NOT_ONE_CARD_AND_NOT_TWO_CARD(2012), // 既不是第一手牌也不是第二手牌
	ERROR_CARD_BET_SCORE(2013), // 比倍分数不正确

	ERROR_UNAME_UPASS_USEOID(2014), // 用户名密码代理商不可以为空
	ERROR_NOT_MACHINE(2015), // 该机器不存在
	ERROR_MACHINE_STAY(2016), // 留机时异常
	ERROR_NOT_CARD_RESULT(2017), // 请从新开始下注
	ERROR_BETSCORE(2018), // 下注分数不正确
	ERROR_COIN_SCORE(2019), // 存取分计算错误
	ERROR_HEAD_PIC_NOT_EXIST(2020), // 没有这个头像
	ERROR_MACHINE_TYPE_ONLINE(2021), // 该机台有玩家
	ERROR_START_INDEX(2022), // 发牌顺序不对不是第二手牌得分
	ERROR_NICK_NAME_REPEATED(2023), // 该昵称已被注册,请更换其他昵称

	ERROR_NICK_NAME_LAW(2024),// 非法昵称！！
	ERROR_FOUR_OF_A_KIND_JOKER_TWO_FOURTEEN_ZERO(2025),//正宗大四梅彩蛋倍数为0
	ERROR_NET_WORK_ERROR(2026);//服务器断开连接

	private int errorCode;

	private ErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
