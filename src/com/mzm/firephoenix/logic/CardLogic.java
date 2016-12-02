package com.mzm.firephoenix.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.AccessPoints;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.dao.entity.MachineDefault;
import com.mzm.firephoenix.dao.entity.MachineGain;
import com.mzm.firephoenix.dao.entity.FivepkPrefab;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;
import com.mzm.firephoenix.dao.entity.MachineMatch;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCCoinScore;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCCompareHistoryCards;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.ErrorCode;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCards;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCompareCard;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCompareHistoryOneCard;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCNotice;
import com.mzm.firephoenix.utils.CardResult;
import com.mzm.firephoenix.utils.CardUtil;

/**
 * 
 * @author oppa
 *
 */
@Component
public class CardLogic {
	public final static Log logger = LogFactory.getLog(CardLogic.class);
	@Autowired
	JdbcDaoSupport jdbcDaoSupport;

	public Builder csCards(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		int startIndex = content.getCsCards().getStartIndex();
		int betScore = content.getCsCards().getBetScore();
		if (startIndex != 0 && startIndex != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_ONE_CARD_AND_NOT_TWO_CARD_VALUE);
		}
		if (betScore != GameConstant.BETSCORE_100 && betScore != GameConstant.BETSCORE_500 && betScore != GameConstant.BETSCORE_1000 && betScore != GameConstant.BETSCORE_2000) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_BET_SCORE_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		if (betScore > fivepkPlayerInfo.getScore()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_BET_SCORE_NOT_ENOUGH_VALUE);
		}
		String machineId = (String)session.getAttribute("machineId");
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{machineId}, new String[]{"seoMachineId"});
		CardResult cr = new CardResult();
		int winCount = 0;
		byte keepCard = 0;
		int randomRemove = 0;
		int prefab = 0;
		int sameColor = 0;
		int sameColor2 = 0;
		int totalWinCount = 0;
		if (startIndex == 0) {
			List<FivepkPrefab> fivepkPrefabList = jdbcDaoSupport.query(FivepkPrefab.class);
			for (int i = 0; i < fivepkPrefabList.size(); i++) {
				FivepkPrefab fivepkPrefab = fivepkPrefabList.get(i);
				prefab = fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards());
				prefab = fivepkPrefab.getPrefab(prefab);
				if (prefab == 0) {
					continue;
				}
				if (prefab == 5) {
					Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
					if (prefabBuffMap == null){
						prefabBuffMap = new HashMap<Integer, Double>();
						GameConstant.RANDOMBUFF.put(machineId, prefabBuffMap);
					}
					Double randomBuff = prefabBuffMap.get(fivepkPrefab.getPrefabCards());
					if (randomBuff == null){
						randomBuff = GameConstant.FIVEPREFAB[RandomUtils.nextInt(0, 6)];
						prefabBuffMap.put(fivepkPrefab.getPrefabCards(), randomBuff);
					}
					totalWinCount = (int) ((1000 * fivepkPrefab.getPrefabCards() * (1 - randomBuff)) / prefab);
				} else {
					totalWinCount = (int) (1000 * fivepkPrefab.getPrefabCards() / prefab);
				}

				
				if (betScore == GameConstant.BETSCORE_500) {
					prefab = prefab / 2;
				} else if (betScore != GameConstant.BETSCORE_2000) {
					prefab += prefab;
				}
				winCount = prefab;
				if (fivepkPrefab.getPrefabCards() == 80){
					//FIXME
					fivepkSeoId.setPrefabFourOfAKindJOKERCount(fivepkSeoId.getPrefabFourOfAKindJOKERCount() + winCount);
					if (fivepkSeoId.getPrefabFourOfAKindJOKERCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 120){
					fivepkSeoId.setPrefabStraightFlushCount(fivepkSeoId.getPrefabStraightFlushCount() + winCount);
					if (fivepkSeoId.getPrefabStraightFlushCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 250){
					fivepkSeoId.setPrefabFiveOfAKindCount(fivepkSeoId.getPrefabFiveOfAKindCount() + winCount);
					if (fivepkSeoId.getPrefabFiveOfAKindCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 500){
					fivepkSeoId.setPrefabRoyalFlushCount(fivepkSeoId.getPrefabRoyalFlushCount() + winCount);
					if (fivepkSeoId.getPrefabRoyalFlushCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 1000){
					fivepkSeoId.setPrefabFiveBarsCount(fivepkSeoId.getPrefabFiveBarsCount() + winCount);
					if (fivepkSeoId.getPrefabFiveBarsCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				}
			}
			cr.setBet(betScore);
			cr.setStartIndex(1);
			session.setAttribute("cardResult", cr);
			byte[] cardArray = new byte[5];
			if (cr.getWinType() == 80) {
				cardArray = Arrays.copyOf(GameConstant.WINPOOLFOUROFAKINDJOKER[RandomUtils.nextInt(0, GameConstant.WINPOOLFOUROFAKINDJOKER.length)], cardArray.length);

				randomRemove = 4;// RandomUtils.nextInt(0, 4);
				keepCard = cardArray[randomRemove];
				int replaceCard = 0;
				boolean isRepeat = false;
				while (true) {
					replaceCard = RandomUtils.nextInt(0, 53);
					for (int i = 0; i < 4; i++) {
						if (cardArray[i] == (byte) replaceCard) {
							isRepeat = true;
							break;
						}
					}
					if (!isRepeat) {
						break;
					}
				}
				cardArray[randomRemove] = (byte) replaceCard;
				cr.setReplaceCard(cardArray[randomRemove]);
				List<Byte> keepList = new ArrayList<Byte>();
				for (byte j = 0; j < 5; j++) {
					if (j != (byte) randomRemove) {
						keepList.add(j);
					}
				}
				cr.setCards(cardArray);
				cr.setKeepCards(ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				cr.setKeepCard(keepCard);
			} else if (cr.getWinType() == 120) {
				cardArray = Arrays.copyOf(GameConstant.WINPOOLSTRAIGHTFLUSH[RandomUtils.nextInt(0, GameConstant.WINPOOLSTRAIGHTFLUSH.length)], cardArray.length);
				sameColor = 13 * RandomUtils.nextInt(0, 4);
				randomRemove = RandomUtils.nextInt(0, 5);
				while (true) {
					sameColor2 = 13 * RandomUtils.nextInt(0, 4);
					if (sameColor != sameColor2) {
						break;
					}
				}
				keepCard = cardArray[randomRemove];
				cardArray[randomRemove] = (byte) (sameColor2 + RandomUtils.nextInt(2, 14));
				cr.setReplaceCard(cardArray[randomRemove]);
				List<Byte> keepList = new ArrayList<Byte>();
				for (byte j = 0; j < 5; j++) {
					if (j != (byte) randomRemove) {
						keepList.add(j);
					}
				}
				cr.setCards(cardArray);
				cr.setKeepCards(ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				cr.setKeepCard(keepCard);
			} else if (cr.getWinType() == 250) {
				cardArray = Arrays.copyOf(GameConstant.WINPOOLFIVEOFAKIND[RandomUtils.nextInt(0, GameConstant.WINPOOLFIVEOFAKIND.length)], cardArray.length);
				Map<Byte, List<Byte>> indexMap = new HashMap<Byte, List<Byte>>();
				for (byte j = 0; j < cardArray.length; j++) {
					byte cardValue = (byte) CardUtil.getCardValue(cardArray[j]);
					List<Byte> subList = indexMap.get(cardValue);
					if (subList == null) {
						subList = new ArrayList<Byte>();
						indexMap.put(cardValue, subList);
					}
					subList.add(j);
				}
				cr.setCards(cardArray);
				for (Byte key : indexMap.keySet()) {
					if (indexMap.get(key).size() >= 3) {
						cr.setKeepCards(ArrayUtils.toPrimitive(indexMap.get(key).toArray(new Byte[indexMap.get(key).size()])));
					}
				}
			} else if (cr.getWinType() == 500) {
				cardArray = Arrays.copyOf(GameConstant.WINPOOLROYALFLUSH[RandomUtils.nextInt(0, GameConstant.WINPOOLROYALFLUSH.length)], cardArray.length);
				sameColor = 13 * RandomUtils.nextInt(0, 4);
				randomRemove = RandomUtils.nextInt(0, 5);
				while (true) {
					sameColor2 = 13 * RandomUtils.nextInt(0, 4);
					if (sameColor != sameColor2) {
						break;
					}
				}
				keepCard = cardArray[randomRemove];
				cardArray[randomRemove] = (byte) (sameColor2 + RandomUtils.nextInt(2, 14));
				cr.setReplaceCard(cardArray[randomRemove]);
				List<Byte> keepList = new ArrayList<Byte>();
				for (byte j = 0; j < 5; j++) {
					if (j != (byte) randomRemove) {
						keepList.add(j);
					}
				}
				cr.setCards(cardArray);
				cr.setKeepCards(ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				cr.setKeepCard(keepCard);
			} else if (cr.getWinType() == 1000) {
				cardArray = Arrays.copyOf(GameConstant.WINPOOLFIVEBARS, cardArray.length);
				randomRemove = RandomUtils.nextInt(0, 5);
				keepCard = cardArray[randomRemove];
				cardArray[randomRemove] = (byte) (RandomUtils.nextInt(1, 53));
				cr.setReplaceCard(cardArray[randomRemove]);
				List<Byte> keepList = new ArrayList<Byte>();
				for (byte j = 0; j < 5; j++) {
					if (j != (byte) randomRemove) {
						keepList.add(j);
					}
				}
				cr.setCards(cardArray);
				cr.setKeepCards(ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				cr.setKeepCard(keepCard);
			} else {
				CardUtil.firstRandomCards(cr);
			}
			String cardsStr = Arrays.toString(cr.getCards());
			cardsStr = cardsStr.substring(1, cardsStr.length() - 1);
			if (cr.getKeepCards() == null || cr.getKeepCards().length == 0) {
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(cardsStr));
			}
			String keepCards = Arrays.toString(cr.getKeepCards());
			keepCards = keepCards.substring(1, keepCards.length() - 1);
			return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(cardsStr).setHoldCards(keepCards));
		} else {
			cr = (CardResult) session.getAttribute("cardResult");
			if (cr == null) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_CARD_RESULT_VALUE);
			}
			if (cr.getStartIndex() != 1) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_START_INDEX_VALUE);
			}
			String holdCards = content.getCsCards().getHoldCards();
			boolean prefabKeepCard = false;
			if (holdCards != null && !holdCards.isEmpty()) {
				String[] holdCardsArr = holdCards.split(",");
				byte[] keepCards = new byte[holdCardsArr.length];
				for (int i = 0; i < holdCardsArr.length; i++) {
					keepCards[i] = Byte.parseByte(holdCardsArr[i].trim());
				}
				if (cr.getKeepCards() != null) {
					Arrays.sort(cr.getKeepCards());
					Arrays.sort(keepCards);
					if (Arrays.equals(cr.getKeepCards(), keepCards)) {
						prefabKeepCard = true;
					}
				}
				cr.setKeepCards(keepCards);
			} else {
				cr.setKeepCards(null);
			}
			if (cr.getKeepCard() != 0) {
				if (cr.getWinType() == 80 && prefabKeepCard) {
					byte[] cardArray2 = cr.getCards();
					for (int j = 0; j < cardArray2.length; j++) {
						if (cardArray2[j] == cr.getReplaceCard()) {
							randomRemove = j;
							break;
						}
					}
					cardArray2[randomRemove] = cr.getKeepCard();
				} else if (cr.getWinType() == 120 && prefabKeepCard) {
					byte[] cardArray2 = cr.getCards();
					for (int j = 0; j < cardArray2.length; j++) {
						if (cardArray2[j] == cr.getReplaceCard()) {
							randomRemove = j;
							break;
						}
					}
					cardArray2[randomRemove] = cr.getKeepCard();
				} else if (cr.getWinType() == 250 && prefabKeepCard) {
					cr.getCards()[4] = 53;
				} else if (cr.getWinType() == 500 && prefabKeepCard) {
					byte[] cardArray2 = cr.getCards();
					for (int j = 0; j < cardArray2.length; j++) {
						if (cardArray2[j] == cr.getReplaceCard()) {
							randomRemove = j;
							break;
						}
					}
					cardArray2[randomRemove] = cr.getKeepCard();
				} else if (cr.getWinType() == 1000 && prefabKeepCard) {
					byte[] cardArray2 = cr.getCards();
					for (int j = 0; j < cardArray2.length; j++) {
						if (cardArray2[j] == cr.getReplaceCard()) {
							randomRemove = j;
							break;
						}
					}
					cardArray2[randomRemove] = cr.getKeepCard();
				}
			} else {
				cr = CardUtil.secondRandomCards(cr);
			}
			int winType = cr.getWinType();
			int score = 0;
			if (winType != 0) {
				score = winType * betScore;
			} else {
				fivepkPlayerInfo.setScore(fivepkPlayerInfo.getScore() - cr.getBet());
				jdbcDaoSupport.update(fivepkPlayerInfo);
				cr.setBet(0);
			}
			cr.setWin(score);
			if (cr.getWinType() == 50 || cr.getWinType() == 80 || cr.getWinType() == 120 || cr.getWinType() == 500 || cr.getWinType() == 1000) {
				PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
				List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(playerInfo.getSeoId());
				for (IoSession ioSession : sessionList) {
					if (!ioSession.isClosing() && ioSession.isConnected()) {
						MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
						returnMessagePack.setCmd(Cmd.CMD_NOTICE);
						returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScNotice(SCNotice.newBuilder().setNotice(fivepkPlayerInfo.getNickName() + "," + cr.getWinType())));
						logger.info("sent message pack : " + returnMessagePack.toString());
						ioSession.write(returnMessagePack);
					}
				}
			}
			fivepkSeoId.setSeoMachinePlayCount(fivepkSeoId.getSeoMachinePlayCount() + 1);
			jdbcDaoSupport.update(fivepkSeoId);
			session.setAttribute("cardResult", cr);
			java.sql.Date oneday = new java.sql.Date(new java.util.Date().getTime());
			if (startIndex == 1) {
				MachineGain machineGain = jdbcDaoSupport.queryOne(MachineGain.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
				if (machineGain == null) {
					jdbcDaoSupport.save(new MachineGain((String) session.getAttribute("machineId"), GameCache.getPlayerInfo(accountId).getSeoId(), oneday));
					machineGain = jdbcDaoSupport.queryOne(MachineGain.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
				}
				machineGain.setAddPlayNumber(machineGain.getAddPlayNumber() + 1);
				MachineDefault machineDefault = jdbcDaoSupport.queryOne(MachineDefault.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
				if (machineDefault == null) {
					jdbcDaoSupport.save(new MachineDefault((String) session.getAttribute("machineId"), GameCache.getPlayerInfo(accountId).getSeoId(), oneday));
					machineDefault = jdbcDaoSupport.queryOne(MachineDefault.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
				}
				if (score > 0) {
					machineDefault.setWinNumber(machineDefault.getWinNumber() + 1);
				}
				machineDefault.setPlayNumber(machineDefault.getPlayNumber() + 1);
				machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + score);
				machineDefault.setPlaySumPoint(machineDefault.getPlaySumPoint() + betScore);
				switch (cr.getWinType()) {
					case 1 :// 一对
						machineDefault.setSevenBetter(machineDefault.getSevenBetter() + 1);
						break;
					case 2 :// 两对
						machineDefault.setTwoPairs(machineDefault.getTwoPairs() + 1);
						break;
					case 3 :// 三条
						machineDefault.setThreeKind(machineDefault.getThreeKind() + 1);
						break;
					case 5 :// 顺子
						machineDefault.setStraight(machineDefault.getStraight() + 1);
						break;
					case 7 :// 同花
						machineDefault.setFlush(machineDefault.getFlush() + 1);
						break;
					case 10 :// 葫芦
						machineDefault.setFullHouse(machineDefault.getFullHouse() + 1);
						break;
					case 50 :// 小四梅
						machineDefault.setLittleFourKind(machineDefault.getLittleFourKind() + 1);
						break;
					case 80 :// 大四梅
						machineDefault.setBigFourKind(machineDefault.getBigFourKind() + 1);
						break;
					case 81 :// 正宗大四梅
						machineDefault.setBigFourKind(machineDefault.getBigFourKind() + 1);
						break;
					case 120 :// 同花小顺
						machineDefault.setStrFlush(machineDefault.getStrFlush() + 1);
						break;
					case 121 :// 四张同花小顺
						machineDefault.setStrFlush(machineDefault.getStrFlush() + 1);
						break;
					case 250 :// 五梅
						machineDefault.setFiveKind(machineDefault.getFiveKind() + 1);
						break;
					case 500 :// 同花大顺
						machineDefault.setRoyalFlush(machineDefault.getRoyalFlush() + 1);
						break;
					case 501 :// 四张同花大顺
						machineDefault.setRoyalFlush(machineDefault.getRoyalFlush() + 1);
						break;
					case 1000 :// 五鬼
						machineDefault.setFiveBars(machineDefault.getFiveBars() + 1);
						break;
				}
				MachineMatch machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
				if (machineMatch == null) {
					jdbcDaoSupport.save(new MachineMatch((String) session.getAttribute("machineId"), GameCache.getPlayerInfo(accountId).getSeoId(), oneday));
					machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
				}
				machineGain.setAddWinPoint((int) machineDefault.getWinSumPoint() + (int) machineMatch.getWinPoint());
				machineGain.setAddPlayPoint((int) machineMatch.getPlayPoint() + (int) machineDefault.getPlaySumPoint());
				jdbcDaoSupport.update(machineGain);
				jdbcDaoSupport.update(machineDefault);
				jdbcDaoSupport.update(machineGain);
				session.setAttribute("machineGain", machineGain);
				session.setAttribute("machineDefault", machineDefault);
				session.setAttribute("machineMatch", machineMatch);
			}

			String cardsStr = Arrays.toString(cr.getCards());
			cardsStr = cardsStr.substring(1, cardsStr.length() - 1);
			return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(winType).setCards(cardsStr));
		}
	}
	public Builder ccCompareHistoryCards(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		String compareHistoryCards = fivepkPlayerInfo.getCompareHistoryCards();
		if (compareHistoryCards == null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 6; i++) {
				sb.append(CardUtil.compareCard());
				sb.append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			fivepkPlayerInfo.setCompareHistoryCards(sb.toString());
			jdbcDaoSupport.update(fivepkPlayerInfo);
			compareHistoryCards = fivepkPlayerInfo.getCompareHistoryCards();
		}
		return MessageContent.newBuilder().setResult(0).setCcCompareHistoryCards(CCCompareHistoryCards.newBuilder().setCards(compareHistoryCards));
	}

	public Builder csCompareCard(IoSession session, MessageContent content) {
		int bigSmall = content.getCsCompareCard().getBigSmall();
		if (bigSmall != 0 && bigSmall != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		int betScore = content.getCsCompareCard().getBetScore();
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_CARD_RESULT_VALUE);
		}
		if (betScore != cr.getWin() && betScore != cr.getWin() / 2 && betScore != cr.getWin() * 2) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_BETSCORE_VALUE);
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		if (betScore > fivepkPlayerInfo.getScore() + cr.getWin() - cr.getBet()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_COMPARE_CARD_BET_SCORE_NOT_ENOUGH_VALUE);
		}
		int compareCard = 0;
		int score = 0;
		boolean isUpdate = false;

		MachineMatch machineMatch = null;
		if (!session.containsAttribute("machineMatch")) {
			machineMatch = new MachineMatch();
			session.setAttribute("machineMatch", machineMatch);
		}
		machineMatch = (MachineMatch) session.getAttribute("machineMatch");
		machineMatch.setPlayNumber(machineMatch.getPlayNumber() + 1);
		if (betScore == 0) {
			compareCard = CardUtil.compareCard();
			session.setAttribute("compareCard", compareCard);
			return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore(0));
		} else {
			String[] compareHistoryCardsArray = fivepkPlayerInfo.getCompareHistoryCards().split(",");
			boolean isRepeated = false;
			while (true) {
				compareCard = CardUtil.compareCard();
				for (int i = 0; i < compareHistoryCardsArray.length; i++) {
					if (compareHistoryCardsArray[i].equals(String.valueOf(compareCard))) {
						isRepeated = true;
						break;
					}
				}
				if (isRepeated) {
					isRepeated = false;
					continue;
				}
				break;
			}
			int cardValue = CardUtil.getCardValue(compareCard);
			boolean isWin = false;
			// boolean isWin = true;
			if (compareCard == 53) {
				isWin = true;
			} else if (bigSmall == 0 && cardValue > 7
			// || bigSmall == 0 && cardValue == 1
			) {
				isWin = true;
			} else if (bigSmall == 1 && cardValue < 7 && cardValue >= 1) {
				isWin = true;
			} else if (cardValue == 7) {
				isWin = true;
			}
			if (betScore == cr.getWin() / 2) {
				score = betScore;
				cr.setWin(betScore);
				isUpdate = true;
				cr.getHalfWin().add(betScore);
			} else if (betScore == cr.getWin() * 2) {
				score = -cr.getWin();
				isUpdate = true;
			}

			if (isWin) {
				machineMatch.setWinNumber(machineMatch.getWinNumber() + 1);
				if (compareCard == 53) {
					int randomTimes = RandomUtils.nextInt(3, 6);
					switch (randomTimes) {
						case 3 :
							machineMatch.setThree(machineMatch.getThree() + 1);
							break;
						case 4 :
							machineMatch.setFour(machineMatch.getFour() + 1);
							break;
						case 5 :
							machineMatch.setFive(machineMatch.getFive() + 1);
							break;
					}
					if (betScore == cr.getWin() * 2) {
						cr.setWin(betScore * randomTimes);
					} else {
						cr.setWin(cr.getWin() * randomTimes);
					}
				} else {
					if (betScore == cr.getWin() * 2) {
						cr.setWin(betScore * 2);
					} else {
						cr.setWin(cr.getWin() + betScore);
					}
				}
				cr.setWinCount(cr.getWinCount() + 1);

				if (cr.getWin() >= 75000) {
					// cr.setWin(cr.getWin() + cr.getWinCount() * 4000);
					// score += cr.getWin() + cr.getWinCount() * 4000;
					cr.setGiftWin(cr.getWinCount() * 4000);
					machineMatch.setPassNumber(machineMatch.getPassNumber() + 1);// 过关次数
					machineMatch.setPassMoney(machineMatch.getPassMoney() + cr.getWinCount() * 4000);// 过关彩金
				}
				if (cr.getWin() >= 200000) {
					// cr.setWin(cr.getWin() + 50000);
					// score += cr.getWin() + 50000;
					cr.setGiftWin(cr.getGiftWin() + 50000);
					machineMatch.setOriderMachineMoney(machineMatch.getOriderMachineMoney() + cr.getGiftWin() + 50000);// 爆机彩金
				}

			} else {
				int giftWin = 0;
				if (cr.getWin() >= 75000) {
					giftWin = cr.getWinCount() * 4000;
				}
				// if (cr.getWin() >= 200000) {
				// giftWin += 50000;
				// }
				cr.setWin(0);

				// machineMatch.setPlayPoint(machineMatch.getPlayPoint()+java.lang.Math.abs(score));

				score = score - cr.getBet() + giftWin;
				isUpdate = true;
				cr.setBet(0);
				cr.setWinCount(0);
				if (!cr.getHalfWin().isEmpty()) {
					int totalHalfWin = 0;
					for (int i = 0; i < cr.getHalfWin().size(); i++) {
						totalHalfWin += cr.getHalfWin().get(i);
					}
					machineMatch.setWinPoint(machineMatch.getWinPoint() + totalHalfWin);
					cr.getHalfWin().clear();
				}
				// if (machineMatch.getWinPoint() > 0){
				// jdbcDaoSupport.update(machineMatch);
				// }
			}

		}
		session.setAttribute("cardResult", cr);
		session.removeAttribute("compareCard");
		fivepkPlayerInfo.firstInLastOut(compareCard);
		if (isUpdate) {
			fivepkPlayerInfo.setScore(fivepkPlayerInfo.getScore() + score);
		}

		jdbcDaoSupport.update(fivepkPlayerInfo);

		machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
		jdbcDaoSupport.update(machineMatch);
		MachineGain machineGain = (MachineGain) session.getAttribute("machineGain");
		MachineDefault machineDefault = (MachineDefault) session.getAttribute("machineDefault");
		machineGain.setAddWinPoint((int) machineDefault.getWinSumPoint() + (int) machineMatch.getWinPoint());
		machineGain.setAddPlayPoint((int) machineMatch.getPlayPoint() + (int) machineDefault.getPlaySumPoint());
		jdbcDaoSupport.update(machineGain);

		return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore(cr.getWin()));
	}

	public Builder csWin(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_CARD_RESULT_VALUE);
		}
		if (cr.getStartIndex() != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_START_INDEX_VALUE);
		}
		fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet() + cr.getGiftWin());
		java.sql.Date oneday = new java.sql.Date(new java.util.Date().getTime());
		MachineMatch machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
		if (machineMatch == null) {
			jdbcDaoSupport.save(new MachineMatch((String) session.getAttribute("machineId"), GameCache.getPlayerInfo(accountId).getSeoId(), oneday));
			machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
		}
		if (cr.getWinCount() > 0 && (cr.getWin() > 0 || cr.getGiftWin() > 0)) {
			machineMatch.setWinPoint(machineMatch.getWinPoint() + cr.getWin() + cr.getGiftWin());
		}
		if (!cr.getHalfWin().isEmpty()) {
			int totalHalfWin = 0;
			for (int i = 0; i < cr.getHalfWin().size(); i++) {
				totalHalfWin += cr.getHalfWin().get(i);
			}
			machineMatch.setWinPoint(machineMatch.getWinPoint() + totalHalfWin);
		}
		jdbcDaoSupport.update(machineMatch);
		cr.reset();
		session.setAttribute("cardResult", cr);
		if (session.getAttribute("compareCard") != null) {
			fivepkPlayerInfo.firstInLastOut((Integer) session.getAttribute("compareCard"));
			session.removeAttribute("compareCard");
		}
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccCoinScore(IoSession session, MessageContent content) {
		int coin = content.getCcCoinScore().getCoin();
		int score = content.getCcCoinScore().getScore();
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		if (playerInfo.getAccountType() == GameConstant.ACCOUNT_TYPE_GUEST) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_GUEST_COINSCORE_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			if ((coin * 100 + score) != (fivepkPlayerInfo.getCoin() * 100 + fivepkPlayerInfo.getScore())) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_COIN_SCORE_VALUE);
			}
		} else {
			if ((coin * 100 + score) != (fivepkPlayerInfo.getCoin() * 100 + fivepkPlayerInfo.getScore() - cr.getBet())) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_COIN_SCORE_VALUE);
			}
			cr.setBet(0);
			session.setAttribute("cardResult", cr);
		}
		AccessPoints accessPoints = new AccessPoints();
		accessPoints.setNickName(playerInfo.getNickName());
		accessPoints.setSeoid(playerInfo.getSeoId());
		accessPoints.setOnCoin(fivepkPlayerInfo.getCoin());
		accessPoints.setOnScore(fivepkPlayerInfo.getScore());
		accessPoints.setUpCoin(coin);
		accessPoints.setUpScore(score);
		jdbcDaoSupport.save(accessPoints);

		fivepkPlayerInfo.setCoin(coin);
		fivepkPlayerInfo.setScore(score);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0).setCcCoinScore(CCCoinScore.newBuilder().setCoin(coin).setScore(score));
	}

	public Builder csCompareHistoryOneCard(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		int compareCard = CardUtil.compareCard();
		session.setAttribute("compareCard", compareCard);
		return MessageContent.newBuilder().setResult(0).setSCCompareHistoryOneCard(SCCompareHistoryOneCard.newBuilder().setCompareCard(compareCard));
	}
}