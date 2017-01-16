package com.mzm.firephoenix.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.CardsPoolUnit;
import com.mzm.firephoenix.constant.ErrorCode;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.Order;
import com.mzm.firephoenix.dao.QueryMeta;
import com.mzm.firephoenix.dao.entity.AccessPoints;
import com.mzm.firephoenix.dao.entity.FivepkDefault;
import com.mzm.firephoenix.dao.entity.FivepkPath;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.dao.entity.FivepkPrefab;
import com.mzm.firephoenix.dao.entity.FivepkPrefabRandom;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;
import com.mzm.firephoenix.dao.entity.MachineDefault;
import com.mzm.firephoenix.dao.entity.MachineGain;
import com.mzm.firephoenix.dao.entity.MachineMatch;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCCoinScore;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCCompareHistoryCards;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCards;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCompareCard;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCompareHistoryOneCard;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCFourKindTime;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCMachineAuto;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCNotice;
import com.mzm.firephoenix.protobuf.GameProtocol.SCPlayerHistoryCardsData;
import com.mzm.firephoenix.protobuf.GameProtocol.SCPlayerHistoryCompareData;
import com.mzm.firephoenix.protobuf.GameProtocol.SCPlayerHistoryData;
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
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		int startIndex = content.getCsCards().getStartIndex();
		int betScore = content.getCsCards().getBetScore();
		if (startIndex != 0 && startIndex != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_ONE_CARD_AND_NOT_TWO_CARD.getErrorCode());
		}
		if (betScore != GameConstant.BETSCORE_100 && betScore != GameConstant.BETSCORE_500 && betScore != GameConstant.BETSCORE_1000 && betScore != GameConstant.BETSCORE_2000) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_BET_SCORE.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
		if (betScore > fivepkPlayerInfo.getScore()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_BET_SCORE_NOT_ENOUGH.getErrorCode());
		}
		String machineId = (String) session.getAttribute("machineId");
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{machineId}, new String[]{"seoMachineId"});
		List<FivepkPrefabRandom> fivepkPrefabRandomList = jdbcDaoSupport.query(FivepkPrefabRandom.class);
		List<FivepkPrefab> fivepkPrefabList = jdbcDaoSupport.query(FivepkPrefab.class);
		CardResult cr = new CardResult();//(CardResult)session.getAttribute("cardResult");
		FivepkPrefab fivepkPrefab = null;
		double winCount = 0;
		int prefab = 0;
		int totalWinCount = 0;
		int plusWinCount = 0;
		int fourOfAKindJokerTwoFourteen = fivepkSeoId.getPrefabFourOfAKindJokerTwoFourteen();
		CardsPoolUnit cardsPoolUnit = null;
		if (startIndex == 0) {
			for (int i = 0; i < fivepkPrefabList.size(); i++) {
				fivepkPrefab = fivepkPrefabList.get(i);

				prefab = fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards());
				if (fivepkPrefab.getPrefabCards() == 78) {
					totalWinCount = (int) (1000 * (fivepkPrefab.getPrefabCards() + 2));
				} else if (fivepkPrefab.getPrefabCards() == 49) {
					totalWinCount = (int) (1000 * fivepkPrefab.getPrefabCards() + 1);
				} else {
					totalWinCount = (int) (1000 * fivepkPrefab.getPrefabCards());
				}
				if (fivepkPrefab.getPrefabCards() != 80 && fivepkPrefab.getPrefabCards() != 48) {
					prefab = Integer.parseInt(fivepkPrefab.getPrefab(prefab));
					if (prefab == 0) {
						continue;
					}
				} else {
					prefab = 5;
				}
				if (betScore == GameConstant.BETSCORE_500) {
					winCount = (double)prefab / 2;
				} else if (betScore == GameConstant.BETSCORE_2000) {
					winCount = prefab * 2;
				} else {
					winCount = prefab;
				}
				if (fivepkPrefab.getPrefabCards() == 48) {
					if (fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards()) == 7){
						fivepkSeoId.setPrefabFourOfAKindTwoTenContinue("");
						fivepkSeoId.setPrefabFourOfAKindTwoTenTwo((byte)7);
						continue;
					}
					String[] continueArr = null;
					if (fivepkSeoId.getPrefabFourOfAKindTwoTenContinue() == null || fivepkSeoId.getPrefabFourOfAKindTwoTenContinue().isEmpty()){
						prefab = fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards());
						continueArr = fivepkPrefab.getPrefab(prefab).split(",");
						int randomQuantity = RandomUtils.nextInt(Integer.parseInt(continueArr[0]), Integer.parseInt(continueArr[1]));
						StringBuffer sb = new StringBuffer();
						long randomSeoMachinePlayCount = RandomUtils.nextLong(fivepkSeoId.getSeoMachinePlayCount(), fivepkSeoId.getSeoMachinePlayCount() + 20);
						sb.append(randomSeoMachinePlayCount).append(",");
						for (int j = 1; j < randomQuantity; j++) {
							randomSeoMachinePlayCount = RandomUtils.nextLong(randomSeoMachinePlayCount + 1, randomSeoMachinePlayCount + 20);
							sb.append(randomSeoMachinePlayCount).append(",");
						}
						sb.deleteCharAt(sb.lastIndexOf(","));
						fivepkSeoId.setPrefabFourOfAKindTwoTenContinue(sb.toString());
					}
					boolean isBreak = false;
					continueArr = fivepkSeoId.getPrefabFourOfAKindTwoTenContinue().split(",");
					String win = null;
					for (int j = 0; j < continueArr.length; j++) {
						win = continueArr[j];
						if (j == continueArr.length - 1 && fivepkSeoId.getSeoMachinePlayCount() > Integer.parseInt(win)) {
							isBreak = true;
							fivepkSeoId.setPrefabFourOfAKindTwoTenContinue("");
							fivepkSeoId.setPrefabFourOfAKindTwoTenTwo((byte) 7);
							break;
						}
						if (fivepkSeoId.getSeoMachinePlayCount() == Integer.parseInt(win)) {
							cr.setWinType(fivepkPrefab.getPrefabCards() + 1);
							isBreak = true;
							if (j + 1 == continueArr.length){
								fivepkSeoId.setPrefabFourOfAKindTwoTenContinue("");
								fivepkSeoId.setPrefabFourOfAKindTwoTenTwo((byte)7);
							}
							break;
						}
					}
					if (isBreak){
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 49) {
					fivepkSeoId.setPrefabFourOfAKindTTCount(fivepkSeoId.getPrefabFourOfAKindTTCount() + winCount);
					if (fivepkSeoId.getPrefabFourOfAKindTTCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 78) {
					fivepkSeoId.setPrefabFourOfAKindJACount(fivepkSeoId.getPrefabFourOfAKindJACount() + winCount);
					if (fivepkSeoId.getPrefabFourOfAKindJACount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 80) {
					prefab = fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards());
					if (prefab == 7){
						fivepkSeoId.setPrefabFourOfAKindJokerCount(0);
						continue;
					}
					prefab = Integer.parseInt(fivepkPrefab.getPrefab(prefab).split(",")[0]);
					if (fivepkSeoId.getPrefabFourOfAKindJokerCount() == 0 && prefab != 0){
						fourOfAKindJokerTwoFourteen = Integer.parseInt(fivepkPrefab.getPrefab(fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards())).split(",")[RandomUtils.nextInt(0, 2)]);
						fivepkSeoId.setPrefabFourOfAKindJokerTwoFourteen(fourOfAKindJokerTwoFourteen);
						fivepkSeoId.setPrefabFourOfAKindJokerCount(fivepkSeoId.getSeoMachinePlayCount() + 10);
					}
					if (fivepkSeoId.getPrefabFourOfAKindJokerCount() == fivepkSeoId.getSeoMachinePlayCount()) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
					
//					prefab = fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards());
//					prefab = Integer.parseInt(fivepkPrefab.getPrefab(prefab).split(",")[0]);
//					if (prefab == 0) {
//						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
//						if (prefabBuffMap != null) {
//							prefabBuffMap.remove(fivepkPrefab.getPrefabCards());
//						}
//						fivepkSeoId.setPrefabFourOfAKindJokerTwoFourteen(0);
//						continue;
//					}
//					if (fourOfAKindJokerTwoFourteen == 0){
//						fourOfAKindJokerTwoFourteen = Integer.parseInt(fivepkPrefab.getPrefab(fivepkSeoId.getPrefab(fivepkPrefab.getPrefabCards())).split(",")[RandomUtils.nextInt(0, 2)]);
//						fivepkSeoId.setPrefabFourOfAKindJokerTwoFourteen(fourOfAKindJokerTwoFourteen);
//					}
//					Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
//					if (prefabBuffMap == null) {
//						prefabBuffMap = new HashMap<Integer, Double>();
//						GameConstant.RANDOMBUFF.put(machineId, prefabBuffMap);
//					}
//					Double randomBuff = prefabBuffMap.get(fivepkPrefab.getPrefabCards());
//					if (randomBuff == null) {
//						randomBuff = GameConstant.FIVEPREFAB2[RandomUtils.nextInt(0, 4)];
//						prefabBuffMap.put(fivepkPrefab.getPrefabCards(), randomBuff);
//						plusWinCount = (int) ((1000 * fivepkPrefab.getPrefabCards() * randomBuff * fourOfAKindJokerTwoFourteen));
//						fivepkSeoId.setPrefabFourOfAKindJokerCount(plusWinCount);
//					}
					
//					if (prefab == 5) {
//						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
//						if (prefabBuffMap == null) {
//							prefabBuffMap = new HashMap<Integer, Double>();
//							GameConstant.RANDOMBUFF.put(machineId, prefabBuffMap);
//						}
//						Double randomBuff = prefabBuffMap.get(fivepkPrefab.getPrefabCards());
//						if (randomBuff == null) {
//							randomBuff = GameConstant.FIVEPREFAB[RandomUtils.nextInt(0, 6)];
//							prefabBuffMap.put(fivepkPrefab.getPrefabCards(), randomBuff);
//							plusWinCount = (int) ((1000 * fivepkPrefab.getPrefabCards() * randomBuff));
//							fivepkSeoId.setPrefabFourOfAKindJokerCount(plusWinCount);
//						}
//					} else {
//						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
//						if (prefabBuffMap != null) {
//							prefabBuffMap.remove(fivepkPrefab.getPrefabCards());
//						}
//					}
//					fivepkSeoId.setPrefabFourOfAKindJokerCount(fivepkSeoId.getPrefabFourOfAKindJokerCount() + winCount);
//					if (fivepkSeoId.getPrefabFourOfAKindJokerCount() >= totalWinCount * fourOfAKindJokerTwoFourteen) {
//						cr.setWinType(fivepkPrefab.getPrefabCards());
//						break;
//					}
				} else if (fivepkPrefab.getPrefabCards() == 120) {
					if (prefab == 5) {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap == null) {
							prefabBuffMap = new HashMap<Integer, Double>();
							GameConstant.RANDOMBUFF.put(machineId, prefabBuffMap);
						}
						Double randomBuff = prefabBuffMap.get(fivepkPrefab.getPrefabCards());
						if (randomBuff == null) {
							randomBuff = GameConstant.FIVEPREFAB[RandomUtils.nextInt(0, 6)];
							prefabBuffMap.put(fivepkPrefab.getPrefabCards(), randomBuff);
							plusWinCount = (int) ((1000 * fivepkPrefab.getPrefabCards() * randomBuff));
							fivepkSeoId.setPrefabStraightFlushCount(plusWinCount);
						}
					} else {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap != null) {
							prefabBuffMap.remove(fivepkPrefab.getPrefabCards());
						}
					}
					fivepkSeoId.setPrefabStraightFlushCount(fivepkSeoId.getPrefabStraightFlushCount() + winCount);
					if (fivepkSeoId.getPrefabStraightFlushCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 250) {
					if (prefab == 5) {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap == null) {
							prefabBuffMap = new HashMap<Integer, Double>();
							GameConstant.RANDOMBUFF.put(machineId, prefabBuffMap);
						}
						Double randomBuff = prefabBuffMap.get(fivepkPrefab.getPrefabCards());
						if (randomBuff == null) {
							randomBuff = GameConstant.FIVEPREFAB[RandomUtils.nextInt(0, 6)];
							prefabBuffMap.put(fivepkPrefab.getPrefabCards(), randomBuff);
							plusWinCount = (int) ((1000 * fivepkPrefab.getPrefabCards() * randomBuff * fivepkSeoId.getPrefabFiveOfAKindCompare()));
							fivepkSeoId.setPrefabFiveOfAKindCount(plusWinCount);
						}
					} else {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap != null) {
							prefabBuffMap.remove(fivepkPrefab.getPrefabCards());
						}
					}
					fivepkSeoId.setPrefabFiveOfAKindCount(fivepkSeoId.getPrefabFiveOfAKindCount() + winCount);
					if (fivepkSeoId.getPrefabFiveOfAKindCount() >= totalWinCount * fivepkSeoId.getPrefabFiveOfAKindCompare()) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 500) {
					if (prefab == 5) {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap == null) {
							prefabBuffMap = new HashMap<Integer, Double>();
							GameConstant.RANDOMBUFF.put(machineId, prefabBuffMap);
						}
						Double randomBuff = prefabBuffMap.get(fivepkPrefab.getPrefabCards());
						if (randomBuff == null) {
							randomBuff = GameConstant.FIVEPREFAB[RandomUtils.nextInt(0, 6)];
							prefabBuffMap.put(fivepkPrefab.getPrefabCards(), randomBuff);
							plusWinCount = (int) ((1000 * fivepkPrefab.getPrefabCards() * randomBuff));
							fivepkSeoId.setPrefabRoyalFlushCount(plusWinCount);
						}
					} else {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap != null) {
							prefabBuffMap.remove(fivepkPrefab.getPrefabCards());
						}
					}
					fivepkSeoId.setPrefabRoyalFlushCount(fivepkSeoId.getPrefabRoyalFlushCount() + winCount);
					if (fivepkSeoId.getPrefabRoyalFlushCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				} else if (fivepkPrefab.getPrefabCards() == 1000) {
					if (prefab == 5) {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap == null) {
							prefabBuffMap = new HashMap<Integer, Double>();
							GameConstant.RANDOMBUFF.put(machineId, prefabBuffMap);
						}
						Double randomBuff = prefabBuffMap.get(fivepkPrefab.getPrefabCards());
						if (randomBuff == null) {
							randomBuff = GameConstant.FIVEPREFAB[RandomUtils.nextInt(0, 6)];
							prefabBuffMap.put(fivepkPrefab.getPrefabCards(), randomBuff);
							plusWinCount = (int) ((1000 * fivepkPrefab.getPrefabCards() * randomBuff));
							fivepkSeoId.setPrefabFiveBarsCount(plusWinCount);
						}
					} else {
						Map<Integer, Double> prefabBuffMap = GameConstant.RANDOMBUFF.get(machineId);
						if (prefabBuffMap != null) {
							prefabBuffMap.remove(fivepkPrefab.getPrefabCards());
						}
					}
					fivepkSeoId.setPrefabFiveBarsCount(fivepkSeoId.getPrefabFiveBarsCount() + winCount);
					if (fivepkSeoId.getPrefabFiveBarsCount() >= totalWinCount) {
						cr.setWinType(fivepkPrefab.getPrefabCards());
						break;
					}
				}
			}
			jdbcDaoSupport.update(fivepkSeoId);
			cr.setBet(betScore);
			cr.setStartIndex(1);
			session.setAttribute("cardResult", cr);
			byte[] cardArray = new byte[5];
			
			byte forceSevenBetterCount = fivepkSeoId.getPrefabForceSevenBetterCount();
			Integer loseCount = GameCache.getForcesevenbettermap().get(machineId);
			int randomSize = -1;
			List<CardsPoolUnit> winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType());
			if (cr.getWinType() == 49) {
				while(true){
					randomSize = RandomUtils.nextInt(0, winPoolList.size());
					List<Integer> repeatedRandomSizeList = GameCache.addPrefabCardsPoolRandomSize(machineId, cr.getWinType());
					if (repeatedRandomSizeList.contains(randomSize)){
						continue;
					}
					repeatedRandomSizeList.add(randomSize);
					break;
				}
				cardsPoolUnit = winPoolList.get(randomSize);
				cardArray = Arrays.copyOf(cardsPoolUnit.getFirst(), cardArray.length);
//
//				randomRemove = 4;// RandomUtils.nextInt(0, 4);
//				keepCard = cardArray[randomRemove];
//				int replaceCard = 0;
//				while (true) {
//					boolean isRepeat = false;
//					replaceCard = RandomUtils.nextInt(1, 53);
//					for (int i = 0; i < 4; i++) {
//						if (cardArray[i] == (byte) replaceCard) {
//							isRepeat = true;
//							break;
//						}
//					}
//					if (!isRepeat) {
//						break;
//					}
//				}
//				cardArray[randomRemove] = (byte) replaceCard;
//				cr.setReplaceCard(cardArray[randomRemove]);
//				List<Byte> keepList = new ArrayList<Byte>();
//				for (byte j = 0; j < 5; j++) {
//					if (j != (byte) randomRemove) {
//						keepList.add(j);
//					}
//				}
				cr.setWinType2(49);
				cr.setWinType(cardsPoolUnit.getWinType());
				cr.setCards(cardArray);
				byte[] keepArray = new byte[cardsPoolUnit.getKeep().length];
				cr.setKeepCards(Arrays.copyOf(cardsPoolUnit.getKeep(), keepArray.length));
				cr.setKeepCard(randomSize);
			} else if (cr.getWinType() == 78) {
				while(true){
					randomSize = RandomUtils.nextInt(0, winPoolList.size());
					List<Integer> repeatedRandomSizeList = GameCache.addPrefabCardsPoolRandomSize(machineId, cr.getWinType());
					if (repeatedRandomSizeList.contains(randomSize)){
						continue;
					}
					repeatedRandomSizeList.add(randomSize);
					if (repeatedRandomSizeList.size() >= winPoolList.size()) {
						repeatedRandomSizeList.clear();
					}
					break;
				}
				cardsPoolUnit = winPoolList.get(randomSize);
				cardArray = Arrays.copyOf(cardsPoolUnit.getFirst(), cardArray.length);
				cr.setWinType(cardsPoolUnit.getWinType());
				cr.setCards(cardArray);
				byte[] keepArray = new byte[cardsPoolUnit.getKeep().length];
				cr.setKeepCards(Arrays.copyOf(cardsPoolUnit.getKeep(), keepArray.length));
				cr.setKeepCard(randomSize);
				cr.setWinType2(78);
			} else if (cr.getWinType() == 80) {
				while(true){
					randomSize = RandomUtils.nextInt(0, winPoolList.size());
					List<Integer> repeatedRandomSizeList = GameCache.addPrefabCardsPoolRandomSize(machineId, cr.getWinType());
					if (repeatedRandomSizeList.contains(randomSize)){
						continue;
					}
					repeatedRandomSizeList.add(randomSize);
					break;
				}
				cardsPoolUnit = winPoolList.get(randomSize);
				cardArray = Arrays.copyOf(cardsPoolUnit.getFirst(), cardArray.length);
				cr.setWinType(cardsPoolUnit.getWinType());
				cr.setCards(cardArray);
				byte[] keepArray = new byte[cardsPoolUnit.getKeep().length];
				cr.setKeepCards(Arrays.copyOf(cardsPoolUnit.getKeep(), keepArray.length));
				cr.setKeepCard(randomSize);
				cr.setWinType2(80);
			} else if (cr.getWinType() == 120) {
				while(true){
					randomSize = RandomUtils.nextInt(0, winPoolList.size());
					List<Integer> repeatedRandomSizeList = GameCache.addPrefabCardsPoolRandomSize(machineId, cr.getWinType());
					if (repeatedRandomSizeList.contains(randomSize)){
						continue;
					}
					repeatedRandomSizeList.add(randomSize);
					break;
				}
				cardsPoolUnit = winPoolList.get(randomSize);
				cardArray = Arrays.copyOf(cardsPoolUnit.getFirst(), cardArray.length);
				
				cr.setWinType2(120);
				cr.setWinType(cardsPoolUnit.getWinType());
				cr.setCards(cardArray);
				byte[] keepArray = new byte[cardsPoolUnit.getKeep().length];
				cr.setKeepCards(Arrays.copyOf(cardsPoolUnit.getKeep(), keepArray.length));
				cr.setKeepCard(randomSize);
			} else if (cr.getWinType() == 250) {
				while(true){
					randomSize = RandomUtils.nextInt(0, winPoolList.size());
					List<Integer> repeatedRandomSizeList = GameCache.addPrefabCardsPoolRandomSize(machineId, cr.getWinType());
					if (repeatedRandomSizeList.contains(randomSize)){
						continue;
					}
					repeatedRandomSizeList.add(randomSize);
					break;
				}
				cardsPoolUnit = winPoolList.get(randomSize);
				cardArray = Arrays.copyOf(cardsPoolUnit.getFirst(), cardArray.length);
				cr.setWinType(cardsPoolUnit.getWinType());
				cr.setCards(cardArray);
				byte[] keepArray = new byte[cardsPoolUnit.getKeep().length];
				cr.setKeepCards(Arrays.copyOf(cardsPoolUnit.getKeep(), keepArray.length));
				cr.setKeepCard(randomSize);
				cr.setWinType2(250);
			} else if (cr.getWinType() == 500) {
				while(true){
					randomSize = RandomUtils.nextInt(0, winPoolList.size());
					List<Integer> repeatedRandomSizeList = GameCache.addPrefabCardsPoolRandomSize(machineId, cr.getWinType());
					if (repeatedRandomSizeList.contains(randomSize)){
						continue;
					}
					repeatedRandomSizeList.add(randomSize);
					break;
				}
				cardsPoolUnit = winPoolList.get(randomSize);
				cardArray = Arrays.copyOf(cardsPoolUnit.getFirst(), cardArray.length);
				cr.setWinType(cardsPoolUnit.getWinType());
				cr.setCards(cardArray);
				byte[] keepArray = new byte[cardsPoolUnit.getKeep().length];
				cr.setKeepCards(Arrays.copyOf(cardsPoolUnit.getKeep(), keepArray.length));
				cr.setKeepCard(randomSize);
				cr.setWinType2(500);
			} else if (cr.getWinType() == 1000) {
				while(true){
					randomSize = RandomUtils.nextInt(0, winPoolList.size());
					break;
				}
				cardsPoolUnit = winPoolList.get(randomSize);
				cardArray = Arrays.copyOf(cardsPoolUnit.getFirst(), cardArray.length);
				cr.setWinType(cardsPoolUnit.getWinType());
				cr.setCards(cardArray);
				byte[] keepArray = new byte[cardsPoolUnit.getKeep().length];
				cr.setKeepCards(Arrays.copyOf(cardsPoolUnit.getKeep(), keepArray.length));
				cr.setKeepCard(randomSize);
				cr.setWinType2(1000);
			} else if (
					loseCount != null && loseCount > forceSevenBetterCount
					){
				cr.setWinType(1);
				int randomSevenBetterCards = RandomUtils.nextInt(7, 14);
				int firstRandomSevenBetterCards = randomSevenBetterCards + 13 * RandomUtils.nextInt(0, 2);
				int secondRandomSevenBetterCards = randomSevenBetterCards + 13 * RandomUtils.nextInt(2, 4);
				int first = RandomUtils.nextInt(0, 2);
				int second = RandomUtils.nextInt(2, 5);
				byte[] keepRandomSevenBetterCards = new byte[]{(byte) first, (byte) second};
				cr.setKeepCards(keepRandomSevenBetterCards);
				byte[] randomSevenBetterCardsArr = new byte[5];
				randomSevenBetterCardsArr[first] = (byte) firstRandomSevenBetterCards;
				randomSevenBetterCardsArr[second] = (byte) secondRandomSevenBetterCards;
				List<Integer> list = new ArrayList<Integer>();
				for (int i = 0; i < randomSevenBetterCardsArr.length; i++) {
					if (randomSevenBetterCardsArr[i] == 0) {
						int randomValue = 0;
						int randomValue1 = 0;
						int randomValue2 = 0;
						while (true) {
							randomValue = RandomUtils.nextInt(1, 53);
							randomValue1 = CardUtil.getCardValue(randomValue);
							randomValue2 = CardUtil.getCardValue(firstRandomSevenBetterCards);
							if (randomValue1 != randomValue2) {
								if (!list.contains(randomValue1)){
									list.add(randomValue1);
									break;
								}
							}
						}
						randomSevenBetterCardsArr[i] = (byte) randomValue;
					}
				}
				cr.setCards(randomSevenBetterCardsArr);
				GameCache.getForcesevenbettermap().put(machineId, loseCount);
			} else {
				CardUtil.firstRandomCards(cr, fivepkSeoId, fivepkPrefabRandomList);
			}
			
//			cr.setCards(new byte[]{30,4,17,43,25});
//			cr.setKeepCards(new byte[]{0,1,2,3});
//			cr.setWinType(50);
//			cr.setWinType2(50);
			
			String cardsStr = Arrays.toString(cr.getCards());
			cardsStr = cardsStr.substring(1, cardsStr.length() - 1);
			cr.setOneCard(cardsStr);
			if (cr.getKeepCards() == null || cr.getKeepCards().length == 0) {
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(cardsStr));
			}
			String keepCards = Arrays.toString(cr.getKeepCards());
			keepCards = keepCards.substring(1, keepCards.length() - 1);
			cr.setGuardCard(keepCards);
			if (cr.getWinType2() == -5 || cr.getWinType() == 3 || cr.getWinType2() == 49 && cr.getWinType() == 3 || cr.getWinType2() == 78 && cr.getWinType() == 3|| cr.getWinType2() == 80 && cr.getWinType() == 3|| cr.getWinType2() == 120 || cr.getWinType2() == 500 && cr.getWinType() <= 1 || cr.getWinType2() == 1000){
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(cardsStr).setHoldCards(keepCards).setPreSound(1));
			}
			return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(cardsStr).setHoldCards(keepCards));
		} else {
			cr = (CardResult) session.getAttribute("cardResult");
			if (cr == null) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_CARD_RESULT.getErrorCode());
			}
			if (cr.getStartIndex() != 1) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_START_INDEX.getErrorCode());
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
			byte forceSevenBetterCount = fivepkSeoId.getPrefabForceSevenBetterCount();
			Integer loseCount = GameCache.getForcesevenbettermap().get(machineId);
			if (cr.getKeepCard() > -1 && prefabKeepCard) {
				List<CardsPoolUnit> winPoolList = null;
				if (cr.getWinType2() == 49) {
					winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType2());
					cardsPoolUnit = winPoolList.get(cr.getKeepCard());
					fivepkSeoId.setPrefabFourOfAKindTTCount(0);
					cr.setCards(cardsPoolUnit.getSecond());
					cr.setWinType(50);
				} else if (cr.getWinType2() == 78) {
					winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType2());
					cardsPoolUnit = winPoolList.get(cr.getKeepCard());
					cr.setCards(cardsPoolUnit.getSecond());
					fivepkSeoId.setPrefabFourOfAKindJACount(0);
					cr.setWinType(80);
					fourOfAKindJokerTwoFourteen = 0;
				} else if (cr.getWinType2() == 80) {
					winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType2());
					cardsPoolUnit = winPoolList.get(cr.getKeepCard());
					cr.setCards(cardsPoolUnit.getSecond());
					fivepkSeoId.setPrefabFourOfAKindJokerCount(0);
					fivepkSeoId.setPrefabFourOfAKindJoker((byte)7);
					cr.setWinType(80);
//					fivepkSeoId.setPrefabFourOfAKindJokerTwoFourteen(0);
				} else if (cr.getWinType2() == 120) {
					winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType2());
					cardsPoolUnit = winPoolList.get(cr.getKeepCard());
					fivepkSeoId.setPrefabStraightFlushCount(0);
					cr.setCards(cardsPoolUnit.getSecond());
					cr.setWinType(120);
				} else if (cr.getWinType2() == 250) {
					winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType2());
					cardsPoolUnit = winPoolList.get(cr.getKeepCard());
					cr.setCards(cardsPoolUnit.getSecond());
					fivepkSeoId.setPrefabFiveOfAKindCount(0);
					cr.setWinType(250);
				} else if (cr.getWinType2() == 500) {
					winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType2());
					cardsPoolUnit = winPoolList.get(cr.getKeepCard());
					cr.setCards(cardsPoolUnit.getSecond());
					fivepkSeoId.setPrefabRoyalFlushCount(0);
					cr.setWinType(500);
				} else if (cr.getWinType2() == 1000) {
					winPoolList = GameConstant.WINPOOLMAP.get(cr.getWinType2());
					cardsPoolUnit = winPoolList.get(cr.getKeepCard());
					cr.setCards(cardsPoolUnit.getSecond());
					fivepkSeoId.setPrefabFiveBarsCount(0);
					cr.setWinType(1000);
				}
//			} else if (loseCount != null && loseCount > forceSevenBetterCount && prefabKeepCard){
//				loseCount = 0;
//				fivepkSeoId.setPrefabForceSevenBetterCount((byte) 0);
//				cr.setWinType(1);
//				byte[] randomSevenBetterCardsArr = cr.getCards();
//				byte[] oldCards = Arrays.copyOf(randomSevenBetterCardsArr, 5);
//				List<Integer> list = new ArrayList<Integer>();
//				
////				if (prefabKeepCard){
//					for (int i = 0; i < randomSevenBetterCardsArr.length; i++) {
//						boolean isContinue = false;
//						for (int j = 0; j < cr.getKeepCards().length; j++) {
//							if (i == cr.getKeepCards()[j]) {
//								isContinue = true;
//								break;
//							}
//						}
//						if (isContinue) {
//							continue;
//						}
//						int randomValue = 0;
//						int randomValue1 = 0;
//						while (true) {
//							randomValue = RandomUtils.nextInt(1, 53);
//							randomValue1 = CardUtil.getCardValue(randomValue);
//							boolean isContinue1 = false;
//							for (int j = 0; j < oldCards.length; j++) {
//								if (randomValue == oldCards[j]){
//									isContinue1 = true;
//									break;
//								}
//							}
//							if (isContinue1) {
//								continue;
//							}
//							if (!list.contains(randomValue1)) {
//								list.add(randomValue1);
//								break;
//							}
//						}
//						randomSevenBetterCardsArr[i] = (byte) randomValue;
//					}
//				} else {
					
//					int maxCardValue = 0;
//					int maxCard = 0;
//					Map<Integer, Integer> sevenBetterMap = new HashMap<Integer, Integer>();
//					int compareMaxCardValue = 0;
//					boolean alreadySevenBetter = false;
//					for (int i = 0; i < randomSevenBetterCardsArr.length; i++) {
//						if (i == 0){
//							maxCardValue = CardUtil.getCardValue(randomSevenBetterCardsArr[i]);
//							maxCard = randomSevenBetterCardsArr[i];
//						}
//						compareMaxCardValue = CardUtil.getCardValue(randomSevenBetterCardsArr[i]);
//						if (compareMaxCardValue == 1 || compareMaxCardValue != 1 && maxCardValue != 1 && maxCardValue < compareMaxCardValue){
//							maxCardValue = compareMaxCardValue;
//							maxCard = randomSevenBetterCardsArr[i];
//						}
//					}
//
//					int newMaxCard = 0;
//					while (true) {
//						newMaxCard = maxCardValue + 13 * RandomUtils.nextInt(0, 4);
//						if (CardUtil.getCardValue(newMaxCard) == maxCardValue && newMaxCard != maxCard) {
//							maxCard = newMaxCard;
//							break;
//						}
//					}
//					
//					if (cr.getKeepCards() != null){
//						for (int j = 0; j < cr.getKeepCards().length; j++) {
//							compareMaxCardValue = CardUtil.getCardValue(randomSevenBetterCardsArr[cr.getKeepCards()[j]]);
//							if (compareMaxCardValue == 1 || compareMaxCardValue >= 7){
//								Integer count = sevenBetterMap.get(compareMaxCardValue);
//								if (count ==  null){
//									count = 0;
//								}
//								count++;
//								sevenBetterMap.put(compareMaxCardValue, count);
//								if (count > 1){
//									alreadySevenBetter = true;
//								}
//							}
//						}
//					}
//					if (alreadySevenBetter){
//						for (int i = 0; i < randomSevenBetterCardsArr.length; i++) {
//							if (cr.getKeepCards() != null){
//								boolean isContinue = false;
//								for (int j = 0; j < cr.getKeepCards().length; j++) {
//									if (i == cr.getKeepCards()[j]) {
//										isContinue = true;
//										break;
//									}
//								}
//								if (isContinue) {
//									continue;
//								}
//							}
//							int randomValue = 0;
//							while (true) {
//								boolean isEqual = false;
//								randomValue = RandomUtils.nextInt(1, 53);
//								for (int i1 = 0; i1 < oldCards.length; i1++) {
//									if (randomValue == oldCards[i1] || CardUtil.getCardValue(oldCards[i1]) == CardUtil.getCardValue(randomValue)) {
//										isEqual = true;
//									}
//								}
//								if (!isEqual) {
//									break;
//								}
//							}
//							randomSevenBetterCardsArr[i] = (byte) randomValue;
//						}
//					} else {
//						for (int i = 0; i < randomSevenBetterCardsArr.length; i++) {
//							if (cr.getKeepCards() != null){
//								boolean isContinue = false;
//								for (int j = 0; j < cr.getKeepCards().length; j++) {
//									if (i == cr.getKeepCards()[j]) {
//										isContinue = true;
//										break;
//									}
//								}
//								if (isContinue) {
//									continue;
//								}
//							}
//							int randomValue = 0;
//							while (true) {
//								boolean isEqual = false;
//								randomValue = RandomUtils.nextInt(1, 53);
//								for (int i1 = 0; i1 < randomSevenBetterCardsArr.length; i1++) {
//									if (randomValue == randomSevenBetterCardsArr[i1] || 
//											CardUtil.getCardValue(randomSevenBetterCardsArr[i1]) == CardUtil.getCardValue(randomValue) ||
//											CardUtil.getCardValue(randomValue) == CardUtil.getCardValue(maxCard)) {
//										isEqual = true;
//									}
//								}
//								if (!isEqual) {
//									break;
//								}
//							}
//							randomSevenBetterCardsArr[i] = (byte) randomValue;
//						}
//						
//						for (int i = 0; i < randomSevenBetterCardsArr.length; i++) {
//							if (cr.getKeepCards() != null){
//								boolean isContinue = false;
//								for (int j = 0; j < cr.getKeepCards().length; j++) {
//									if (i == cr.getKeepCards()[j]) {
//										isContinue = true;
//										break;
//									}
//								}
//								if (isContinue) {
//									continue;
//								}
//							}
//							if (!alreadySevenBetter){
//								int maxCard1 = 0;
//								int maxCard2 = 0;
//								while (true) {
//									maxCard1 = maxCardValue + 13 * RandomUtils.nextInt(0, 4);
//									if (CardUtil.getCardValue(maxCard1) == CardUtil.getCardValue(maxCard) && maxCard1 != maxCard) {
//										maxCard2 = maxCard;
//										maxCard = maxCard1;
//										break;
//									}
//								}
//								randomSevenBetterCardsArr[i] = (byte) maxCard1;
//								
//								for (int i1 = 0; i1 < randomSevenBetterCardsArr.length; i1++) {
//									if (CardUtil.getCardValue(randomSevenBetterCardsArr[i1]) == 1 || CardUtil.getCardValue(randomSevenBetterCardsArr[i1]) >= 7){
//										Integer count = sevenBetterMap.get(CardUtil.getCardValue(randomSevenBetterCardsArr[i1]));
//										if (count ==  null){
//											count = 0;
//										}
//										count++;
//										sevenBetterMap.put(CardUtil.getCardValue(randomSevenBetterCardsArr[i1]), count);
//										if (count > 1 && CardUtil.getCardValue(randomSevenBetterCardsArr[i1]) == CardUtil.getCardValue(maxCard1) &&
//												maxCard2 != randomSevenBetterCardsArr[i1] && 
//												CardUtil.getCardValue(randomSevenBetterCardsArr[i1]) == CardUtil.getCardValue(maxCard2)){
//											alreadySevenBetter = true;
//										}
//									}
//								}
//							}
//						}
////					}
//				}
//				cr.setCards(randomSevenBetterCardsArr);
			} else {
				cr = CardUtil.secondRandomCards(cr, fivepkSeoId, fivepkPrefabRandomList);
				if (loseCount != null && loseCount > forceSevenBetterCount){
					loseCount = 0;
					fivepkSeoId.setPrefabForceSevenBetterCount((byte) 0);
				}
				if (cr.getWinType() == 80){
					fourOfAKindJokerTwoFourteen = 0;
				}
			}
			int winType = cr.getWinType();
			if (winType == 0) {
				fivepkPlayerInfo.setScore(fivepkPlayerInfo.getScore() - cr.getBet());
				jdbcDaoSupport.update(fivepkPlayerInfo);
				cr.setBet(0);
				if (forceSevenBetterCount == 0){
					FivepkPrefabRandom fivepkPrefabRandom = fivepkPrefabRandomList.get(fivepkPrefabRandomList.size() - 2);
					String[] prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
					fivepkSeoId.setPrefabForceSevenBetterCount((byte)RandomUtils.nextInt(Integer.parseInt(prefabRandomArray[0]), Integer.parseInt(prefabRandomArray[1])));
					forceSevenBetterCount = fivepkSeoId.getPrefabForceSevenBetterCount();
				}
				if (loseCount == null){
					loseCount = 0;
				}
				loseCount = loseCount + 1;
				
				GameCache.getForcesevenbettermap().put(machineId, loseCount);
			} else {
				GameCache.getForcesevenbettermap().remove(machineId);
			}
			int score = winType * betScore;
			cr.setWin(score);
			if (cr.getWinType() == 50 || (cr.getWinType() == 80 && fourOfAKindJokerTwoFourteen == 0) || cr.getWinType() == 120 || cr.getWinType() == 250 || cr.getWinType() == 500 || cr.getWinType() == 1000) {
				PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
				List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(playerInfo.getSeoId());
				for (IoSession ioSession : sessionList) {
					if (!ioSession.isClosing() && ioSession.isConnected()) {
						MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
						returnMessagePack.setCmd(Cmd.CMD_NOTICE);
						returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScNotice(SCNotice.newBuilder().setNotice(fivepkPlayerInfo.getNickName() + "," + cr.getWinType())));
//						PlayerInfo playerInfo1 = GameCache.getPlayerInfo((Long)ioSession.getAttribute("accountId"));
//						logger.info("nickName : " + playerInfo1.getNickName() +"accountId:"+ accountId+"  |　sent message pack : " + returnMessagePack.toString());
						ioSession.write(returnMessagePack);
					} else {
						logger.error("ioSession accountId not send:　" + ioSession.getAttribute("accountId"));
					}
				}
			}
			fivepkSeoId.setSeoMachinePlayCount(fivepkSeoId.getSeoMachinePlayCount() + 1);
			jdbcDaoSupport.update(fivepkSeoId);
			try {
				session.setAttribute("cardResult", cr);
				java.sql.Date oneday = new java.sql.Date(new java.util.Date().getTime());
				if (startIndex == 1) {
					FivepkPath fivepkPath=(FivepkPath) session.getAttribute("fivepkPath");
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
					fivepkPath.setPlayNumber(fivepkPath.getPlayNumber()+1);
					session.setAttribute("fivepkPath", fivepkPath);
					//fivepkPath.setWinPoint(winPoint);
					//fivepkPath.setPlayPoint(fivepkPath.getPlayPoint()+betScore);
					machineDefault.setPlayNumber(machineDefault.getPlayNumber() + 1);
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
							if (cr.getWinType2() == 80 && fivepkSeoId.getPrefabFourOfAKindJokerTwoFourteen() > 1) {
								switch (fivepkSeoId.getPrefabFourOfAKindJokerTwoFourteen()) {
									case 2 :
										machineDefault.setFour2(machineDefault.getFour2() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + score);
										break;
									case 3 :
										machineDefault.setFour3(machineDefault.getFour3() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 2));
										break;
									case 4 :
										machineDefault.setFour4(machineDefault.getFour4() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 3));
										break;
									case 5 :
										machineDefault.setFour5(machineDefault.getFour5() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 4));
										break;
									case 6 :
										machineDefault.setFour6(machineDefault.getFour6() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 5));
										break;
									case 7 :
										machineDefault.setFour7(machineDefault.getFour7() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 6));
										break;
									case 8 :
										machineDefault.setFour8(machineDefault.getFour8() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 7));
										break;
									case 9 :
										machineDefault.setFour9(machineDefault.getFour9() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 8));
										break;
									case 10 :
										machineDefault.setFour10(machineDefault.getFour10() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 9));
										break;
									case 11 :
										machineDefault.setFour11(machineDefault.getFour11() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 10));
										break;
									case 12 :
										machineDefault.setFour12(machineDefault.getFour12() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 11));
										break;
									case 13 :
										machineDefault.setFour13(machineDefault.getFour13() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 12));
										break;
									case 14 :
										machineDefault.setFour14(machineDefault.getFour14() + 1);
										machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + (score * 13));
										break;
								}
							}
							break;
						case 120 :// 同花小顺
							machineDefault.setStrFlush(machineDefault.getStrFlush() + 1);
							break;
						case 250 :// 五梅
							machineDefault.setFiveKind(machineDefault.getFiveKind() + 1);
							if (fivepkSeoId.getPrefabFiveOfAKindCompare() == 1) {
								machineDefault.setFive1(machineDefault.getFive1() + 1);
							} else if (fivepkSeoId.getPrefabFiveOfAKindCompare() == 2) {
								machineDefault.setFive2(machineDefault.getFive2() + 1);
							}
							break;
						case 500 :// 同花大顺
							machineDefault.setRoyalFlush(machineDefault.getRoyalFlush() + 1);
							break;
						case 1000 :// 五鬼
							machineDefault.setFiveBars(machineDefault.getFiveBars() + 1);
							break;
					}
					machineDefault.setWinSumPoint(machineDefault.getWinSumPoint() + score);
					MachineMatch machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
					if (machineMatch == null) {
						jdbcDaoSupport.save(new MachineMatch((String) session.getAttribute("machineId"), GameCache.getPlayerInfo(accountId).getSeoId(), oneday));
						machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
					}
					//FivepkDefault fivepkDefault = new FivepkDefault();
					machineGain.setAddWinPoint((int) machineDefault.getWinSumPoint() + (int) machineMatch.getWinPoint());
					machineGain.setAddPlayPoint((int) machineMatch.getPlayPoint() + (int) machineDefault.getPlaySumPoint());
					jdbcDaoSupport.update(machineGain);
					jdbcDaoSupport.update(machineDefault);
					// jdbcDaoSupport.update(machineGain);
					session.setAttribute("machineGain", machineGain);
					session.setAttribute("machineDefault", machineDefault);
					// session.setAttribute("machineMatch", machineMatch);
				}
			} catch (Exception e) {
				logger.error(e, e);
			}
			
			String cardsStr = Arrays.toString(cr.getCards());
			try {
				cardsStr = cardsStr.substring(1, cardsStr.length() - 1);
				String defsult=fivepkPlayerInfo.getNickName()+"|"+machineId+"|"+(fivepkPlayerInfo.getScore()-betScore)+"|"+
				betScore+"|"+cr.getOneCard()+"|"+holdCards+"|"+cardsStr+"|"+cr.getWinType();
				session.setAttribute("defsult",defsult);
				FivepkPath fivepkPath=(FivepkPath) session.getAttribute("fivepkPath");
				fivepkPath.setPlayPoint(fivepkPath.getPlayPoint()+betScore);
				if(winType==0){
					FivepkDefault fivepkDefault=new FivepkDefault(fivepkPlayerInfo.getNickName(),machineId,fivepkPlayerInfo.getScore(),
							betScore,score,cr.getOneCard(),holdCards,cardsStr,
							cr.getWinType(),"0","");
					jdbcDaoSupport.save(fivepkDefault);
					session.removeAttribute("defsult");
				}
			} catch (Exception e) {
				logger.error(e, e);
			}
			
			/*if(cr.getWinType() == 80){
				int math = RandomUtils.nextInt(2, 15);
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(winType).setCards(cardsStr).setMultiple(math));
			}else{
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(winType).setCards(cardsStr).setMultiple(0));
			}*/
			if(cr.getWinType() == 80 && fourOfAKindJokerTwoFourteen > 0){
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(winType).setCards(cardsStr).setRealKind(100));
			}else{
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(winType).setCards(cardsStr));
			}
		}
	}
	public Builder ccCompareHistoryCards(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
//		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
//		if (fivepkPlayerInfo == null) {
//			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
//		}
		FivepkSeoId FivepkSeoId=jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{session.getAttribute("machineId")}, null, new String[]{"seoMachineId"});
		if(FivepkSeoId==null){
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_USER_EXIST.getErrorCode());
		}
		String compareHistoryCards = FivepkSeoId.getCompareHistoryCards();
		if (compareHistoryCards == null) {
			compareHistoryCards = "0,0,0,0,0,0";
//			fivepkPlayerInfo.setCompareHistoryCards(sb.toString());
			FivepkSeoId.setCompareHistoryCards(compareHistoryCards);
//			jdbcDaoSupport.update(fivepkPlayerInfo);
			jdbcDaoSupport.update(FivepkSeoId);
		}
		return MessageContent.newBuilder().setResult(0).setCcCompareHistoryCards(CCCompareHistoryCards.newBuilder().setCards(compareHistoryCards));
	}

	public Builder csCompareCard(IoSession session, MessageContent content) {
		int bigSmall = content.getCsCompareCard().getBigSmall();
		if (bigSmall != 0 && bigSmall != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER.getErrorCode());
		}
		int five = content.getCsCompareCard().getFive();
		if (five != 0 && five != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER.getErrorCode());
		}
		int betScore = content.getCsCompareCard().getBetScore();
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_CARD_RESULT.getErrorCode());
		}
		cr.setBetMath(cr.getBetMath()+1);
		if (betScore != cr.getWin() && betScore != cr.getWin() / 2 && betScore != cr.getWin() * 2) {
			System.out.println("betScore : " + betScore + " | win : " + cr.getWin());
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_BETSCORE.getErrorCode());
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
		if (betScore > fivepkPlayerInfo.getScore() + cr.getWin() - cr.getBet()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_COMPARE_CARD_BET_SCORE_NOT_ENOUGH.getErrorCode());
		}
		int compareCard = 0;
		int score = 0;
		boolean isUpdate = false;
		java.sql.Date oneday = new java.sql.Date(new java.util.Date().getTime());
		MachineMatch machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
		if (machineMatch == null) {
			jdbcDaoSupport.save(new MachineMatch((String) session.getAttribute("machineId"), GameCache.getPlayerInfo(accountId).getSeoId(), oneday));
			machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
		}
		machineMatch.setPlayNumber(machineMatch.getPlayNumber() + 1);
		String machineId = (String) session.getAttribute("machineId");
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{machineId}, new String[]{"seoMachineId"});
		FivepkPrefabRandom fivepkPrefabRandom = jdbcDaoSupport.queryOne(FivepkPrefabRandom.class, new Object[]{"9997"}, new String[]{"prefabCards"});
		String[] compareHistoryCardsArray = fivepkSeoId.getCompareHistoryCards().split(",");
		if (betScore == 0) {
			while (true) {
				boolean isRepeated = false;
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
			session.setAttribute("compareCard", compareCard);
			return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore(0));
		} else {
			if (fivepkSeoId.getPrefabCompareCutDown() == 7){
				fivepkSeoId.setPrefabCompareCutDownCount((byte)0);
			}
			if (fivepkSeoId.getPrefabCompareCutDownCount() == 0){
				String[] compareCutDownArr = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				fivepkSeoId.setPrefabCompareCutDownCount((byte)RandomUtils.nextInt(Integer.parseInt(compareCutDownArr[0]), Integer.parseInt(compareCutDownArr[1]) + 1));
			}
			
			while (true) {
				boolean isRepeated = false;
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
			if (bigSmall == 0 && cardValue > 7) {
				isWin = true;
			} else if (bigSmall == 1 && cardValue < 7 && cardValue >= 1) {
				isWin = true;
			}
			int prefabCompareSevenJoker = fivepkSeoId.getPrefabCompareSevenJoker();
			int additionBetScore = 0;
			if (isWin){
				if (betScore * 2 >= 200000) {
					additionBetScore = additionBetScore + 50000;
				}
				if (betScore * 2 >= 75000) {
					additionBetScore = additionBetScore + cr.getWinCount() * 4000 + 4000;
				}
			}
			if (cr.getWinCount() >= fivepkSeoId.getPrefabCompareCutDownCount() || betScore + additionBetScore > fivepkSeoId.getPrefabCompareBuff()){
				if (bigSmall == 0){
					while (true) {
						boolean isRepeated = false;
						compareCard = RandomUtils.nextInt(1, 7) + 13 * RandomUtils.nextInt(0, 4);
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
				} else {
					while (true) {
						boolean isRepeated = false;
						compareCard = RandomUtils.nextInt(8, 14) + 13 * RandomUtils.nextInt(0, 4);
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
				}
				isWin = false;
			} else if (prefabCompareSevenJoker > 0 && betScore * (prefabCompareSevenJoker - 1) + additionBetScore <= fivepkSeoId.getPrefabCompareBuff()) {
				if (prefabCompareSevenJoker >= 3 && prefabCompareSevenJoker <= 5) {
					compareCard = 53;
				} else {
					while (true) {
						boolean isRepeated = false;
						compareCard = 7 + 13 * RandomUtils.nextInt(0, 4);
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
				}
				isWin = true;
				fivepkSeoId.setPrefabCompareSevenJoker((byte) 0);
			}
			
			if (five == 1){
				if (fivepkSeoId.getPrefabFiveOfAKindCompare() == 1){
					if (bigSmall == 0){
						while (true) {
							boolean isRepeated = false;
							compareCard = RandomUtils.nextInt(1, 7) + 13 * RandomUtils.nextInt(0, 4);
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
					} else {
						while (true) {
							boolean isRepeated = false;
							compareCard = RandomUtils.nextInt(8, 14) + 13 * RandomUtils.nextInt(0, 4);
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
					}
					isWin = false;
				} else {
					if (bigSmall == 0){
						while (true) {
							boolean isRepeated = false;
							compareCard = RandomUtils.nextInt(8, 14) + 13 * RandomUtils.nextInt(0, 4);
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
					} else {
						while (true) {
							boolean isRepeated = false;
							compareCard = RandomUtils.nextInt(1, 7) + 13 * RandomUtils.nextInt(0, 4);
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
					}
					isWin = true;
				}
			}
			int a = 0;
			if (betScore == cr.getWin() / 2) {
				a=1;
				score = betScore;
				cr.setWin(betScore);
				cr.setBetType(cr.getBetType()+"0-");
				isUpdate = true;
				if(cr.getBetMath()>1){
					cr.getHalfWin().add(betScore);
				}
			} else if (betScore == cr.getWin() * 2) {
				a=2;
				score = -cr.getWin();
				cr.setBetType(cr.getBetType()+"2-");
				isUpdate = true;
			}else{
				cr.setBetType(cr.getBetType()+"1-");
			}
			cr.setBetType(cr.getBetType()+betScore+"-");
			if(bigSmall==0){
				cr.setBetType(cr.getBetType()+"1-");
			}else if(bigSmall==1){
				cr.setBetType(cr.getBetType()+"0-");
			}else{
				cr.setBetType(cr.getBetType()+"2-");
			}
			if(compareCard==53){
				cr.setBetType(cr.getBetType()+53);
			}else{
				cardValue = CardUtil.getCardValue(compareCard);
				cr.setBetType(cr.getBetType()+cardValue);
			}
			cr.setBetType(cr.getBetType()+",");
			if (isWin) {
				cr.setPassScore(cr.getPassScore()+4000);
				machineMatch.setWinNumber(machineMatch.getWinNumber() + 1);
				//machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
				if (compareCard == 53) {
					machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
					int randomTimes = prefabCompareSevenJoker;
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
					if (a==2) {
						if(cr.getWinCount() == 0){
							machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
						}else{
							machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore/2);
						}
						cr.setWin(betScore * 2);
					} else {
						cr.setWin(cr.getWin() + betScore);
						if (cr.getWinCount() == 0) {
							machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
						}
					}
				}
//				if (prefabCompareSevenJoker > 0){
//					fivepkSeoId.setPrefabCompareBuff(fivepkSeoId.getPrefabCompareBuff() - betScore * (prefabCompareSevenJoker - 1) - additionBetScore <= 0 ? 0 : fivepkSeoId.getPrefabCompareBuff() - betScore * (prefabCompareSevenJoker - 1) - additionBetScore);
//				} else {
//					fivepkSeoId.setPrefabCompareBuff(fivepkSeoId.getPrefabCompareBuff() - betScore <= 0 ? 0 : fivepkSeoId.getPrefabCompareBuff() - betScore - additionBetScore);
//				}
				//TODO
				if (five == 0){
					cr.setWinCount(cr.getWinCount() + 1);
				}
				
				if (cr.getWin() >= 200000) {
					// cr.setWin(cr.getWin() + 50000);
					// score += cr.getWin() + 50000;
					cr.setGiftWin(cr.getGiftWin() + 50000);
					machineMatch.setOriderMachineNumber(machineMatch.getOriderMachineNumber() + 1);
					machineMatch.setOriderMachineMoney(machineMatch.getOriderMachineMoney() + 50000);// 爆机彩金
					machineMatch.setWinPoint(machineMatch.getWinPoint() + 50000);
				}
				if (cr.getWin() >= 75000) {
					// cr.setWin(cr.getWin() + cr.getWinCount() * 4000);
					// score += cr.getWin() + cr.getWinCount() * 4000;
					cr.setWinSevenFive(1);
//					cr.setGiftWin(cr.getGiftWin() + cr.getWinCount() * 4000);
					machineMatch.setPassNumber(machineMatch.getPassNumber() + 1);// 过关次数
					if(cr.getPassMath()==0){
						machineMatch.setPassMoney(machineMatch.getPassMoney() + cr.getPassScore());// 过关彩金
					//	machineMatch.setWinPoint(machineMatch.getWinPoint()+cr.getPassScore());
						cr.setPassMath(1);
					}else{
						machineMatch.setPassMoney(machineMatch.getPassMoney() + 4000);// 过关彩金
					//	machineMatch.setWinPoint(machineMatch.getWinPoint() + 4000);
					}
				}
				int subCompareBuff = betScore;
				if (prefabCompareSevenJoker > 0){
					subCompareBuff = subCompareBuff * (prefabCompareSevenJoker - 1);
				}
				if (betScore * 2 >= 200000) {
					subCompareBuff = subCompareBuff + 50000;
				}
				if (betScore * 2 >= 75000) {
					if (cr.getWinSevenFive() == 1 && !cr.isSubCompareWinCount()){
						subCompareBuff = subCompareBuff + cr.getWinCount() * 4000;
						cr.setSubCompareWinCount(true);
					} else if(cr.isSubCompareWinCount()){
						subCompareBuff = subCompareBuff + 4000;
					}
				}
				fivepkSeoId.setPrefabCompareBuff(fivepkSeoId.getPrefabCompareBuff() - subCompareBuff <= 0 ? 0 : fivepkSeoId.getPrefabCompareBuff() - subCompareBuff);
			} else {
				if (five == 0){
					int addtionalBetScore = 0;
					if (cr.getWin() >= 75000){
						addtionalBetScore = cr.getWinCount() * 4000;
					} else if (cr.getWinSevenFive() > 0) {
						addtionalBetScore = cr.getWinSevenFive() * 4000;
					}
					fivepkSeoId.setPrefabCompareBuff((int)(fivepkSeoId.getPrefabCompareBuff() + betScore * 0.9 - addtionalBetScore));
					int giftWin = 0;
					if (cr.getWin() >= 75000){
						giftWin = cr.getWinCount() * 4000;
					} else if (cr.getWinSevenFive() > 0) {
						giftWin = cr.getWinCount() * 4000;
					}
					// if (cr.getWin() >= 200000) {
					// giftWin += 50000;
					// }
					if (cr.getWin() >= 75000){
						machineMatch.setWinPoint(machineMatch.getWinPoint()+(cr.getWinCount() * 4000));
					} else if (cr.getWinSevenFive() > 0) {
						machineMatch.setWinPoint(machineMatch.getWinPoint()+(cr.getWinCount() * 4000));
					}
	
					String defsultStr = (String)session.getAttribute("defsult");
					if(defsultStr != null && !defsultStr.isEmpty()){
						String defsult[]= defsultStr.split("\\|");
						FivepkDefault fivepkDefault=null;
						fivepkDefault=new FivepkDefault(defsult[0],defsult[1],Integer.parseInt(defsult[2]),Integer.parseInt(defsult[3]),0,defsult[4],defsult[5],defsult[6],Integer.parseInt(defsult[7]),cr.getBetScore(),cr.getBetType());
						jdbcDaoSupport.save(fivepkDefault);
						session.removeAttribute("defsult");
						cr.setBetType("");
					}
										
					cr.setWin(0);
					

					if (a==2) {
						if(cr.getWinCount() == 0){
							machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
						}else{
							machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore/2);
						}
					} else {
						if (cr.getWinCount() == 0) {
							machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
						}
					}
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
				String defsultStr = (String)session.getAttribute("defsult");
				
				if(defsultStr != null && !defsultStr.isEmpty()){
					String defsult[]= defsultStr.split("\\|");
					FivepkDefault fivepkDefault=new FivepkDefault(defsult[0],defsult[1],Integer.parseInt(defsult[2]),Integer.parseInt(defsult[3]),0,defsult[4],defsult[5],defsult[6],Integer.parseInt(defsult[7]),cr.getBetScore(),cr.getBetType());
					jdbcDaoSupport.save(fivepkDefault);
					session.removeAttribute("defsult");
					cr.setBetType("");
				}
				cr.setBetMath(0);
			}

		}
		session.setAttribute("cardResult", cr);
		session.removeAttribute("compareCard");
//		fivepkPlayerInfo.firstInLastOut(compareCard);
		fivepkSeoId.firstInLastOut(compareCard);
		if (isUpdate) {
			fivepkPlayerInfo.setScore(fivepkPlayerInfo.getScore() + score);
		}

		jdbcDaoSupport.update(fivepkPlayerInfo);
		jdbcDaoSupport.update(fivepkSeoId);
		// machineMatch.setPlayPoint(machineMatch.getPlayPoint() + betScore);
		jdbcDaoSupport.update(machineMatch);
		MachineGain machineGain = (MachineGain) session.getAttribute("machineGain");
		MachineDefault machineDefault = (MachineDefault) session.getAttribute("machineDefault");
		machineGain.setAddWinPoint((int) machineDefault.getWinSumPoint() + (int) machineMatch.getWinPoint());
		machineGain.setAddPlayPoint((int) machineMatch.getPlayPoint() + (int) machineDefault.getPlaySumPoint());
		session.setAttribute("machineMatch",machineMatch);
		jdbcDaoSupport.update(machineGain);
		cr.setBetScore(cr.getBetScore()+betScore+",");
		return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore(cr.getWin()));
	}

	public Builder csWin(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_CARD_RESULT.getErrorCode());
		}
		if (cr.getStartIndex() != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_START_INDEX.getErrorCode());
		}
		fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet() + cr.getGiftWin() + cr.getWinSevenFive() * cr.getWinCount() * 4000);
		java.sql.Date oneday = new java.sql.Date(new java.util.Date().getTime());
		MachineMatch machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
		if (machineMatch == null) {
			jdbcDaoSupport.save(new MachineMatch((String) session.getAttribute("machineId"), GameCache.getPlayerInfo(accountId).getSeoId(), oneday));
			machineMatch = jdbcDaoSupport.queryOne(MachineMatch.class, new Object[]{session.getAttribute("machineId"), oneday}, null, new String[]{"seoMachineId", "oneday"});
		}
		if (cr.getBetType() != null && cr.getBetType()!="" && cr.getWin() > 0) {
			machineMatch.setWinPoint(machineMatch.getWinPoint() + cr.getWin());
		}
		if (!cr.getHalfWin().isEmpty()) {
			int totalHalfWin = 0;
			for (int i = 0; i < cr.getHalfWin().size(); i++) {
				totalHalfWin += cr.getHalfWin().get(i);
			}
			machineMatch.setWinPoint(machineMatch.getWinPoint() + totalHalfWin);
		}
		if (cr.getWin() >= 75000) {
			machineMatch.setWinPoint(machineMatch.getWinPoint()+cr.getPassScore());
		}
		jdbcDaoSupport.update(machineMatch);
		session.removeAttribute("machineMatch");
		FivepkPath fivepkPath=(FivepkPath) session.getAttribute("fivepkPath");
		fivepkPath.setWinPoint(fivepkPath.getWinPoint()+cr.getWin());
		String defsultStr = (String)session.getAttribute("defsult");
		if(defsultStr != null && !defsultStr.isEmpty()){
			String defsult[]= defsultStr.split("\\|");
			FivepkDefault fivepkDefault=new FivepkDefault(defsult[0],defsult[1],Integer.parseInt(defsult[2]),Integer.parseInt(defsult[3]),cr.getWin(),defsult[4],defsult[5],defsult[6],Integer.parseInt(defsult[7]),cr.getBetScore(),cr.getBetType());
			jdbcDaoSupport.save(fivepkDefault);
			session.removeAttribute("defsult");
			cr.setBetType("");
		}
		cr.reset();
		session.setAttribute("cardResult", cr);
		if (session.getAttribute("compareCard") != null) {
			String machineId = (String) session.getAttribute("machineId");
			FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{machineId}, new String[]{"seoMachineId"});
			fivepkSeoId.firstInLastOut((Integer) session.getAttribute("compareCard"));
			jdbcDaoSupport.update(fivepkSeoId);
			session.removeAttribute("compareCard");
		}
		jdbcDaoSupport.update(fivepkPlayerInfo);
		fivepkPath.setEndPoint(fivepkPlayerInfo.getScore());
		fivepkPath.setWinNumber(fivepkPath.getWinNumber()+1);
		session.setAttribute("fivepkPath", fivepkPath);
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccCoinScore(IoSession session, MessageContent content) {
		int coin = content.getCcCoinScore().getCoin();
		int score = content.getCcCoinScore().getScore();
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		if (playerInfo.getAccountType() == GameConstant.ACCOUNT_TYPE_GUEST) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_GUEST_COINSCORE.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			if ((coin * 100 + score) != (fivepkPlayerInfo.getCoin() * 100 + fivepkPlayerInfo.getScore())) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_COIN_SCORE.getErrorCode());
			}
		} else {
			if ((coin * 100 + score) != (fivepkPlayerInfo.getCoin() * 100 + fivepkPlayerInfo.getScore() - cr.getBet())) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_COIN_SCORE.getErrorCode());
			}
			cr.setBet(0);
			session.setAttribute("cardResult", cr);
		}
		FivepkPath fivepkPath=(FivepkPath) session.getAttribute("fivepkPath");
		if (fivepkPath != null){
			fivepkPath.setAccessPoint(fivepkPath.getAccessPoint()+(fivepkPlayerInfo.getScore()-score));
			session.setAttribute("fivepkPath", fivepkPath);
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
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		String machineId = (String) session.getAttribute("machineId");
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{machineId}, new String[]{"seoMachineId"});
		if (fivepkSeoId == null){
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_MACHINE.getErrorCode());
		}
		String[] compareHistoryCardsArray = fivepkSeoId.getCompareHistoryCards().split(",");
		int compareCard = CardUtil.compareCard();
		while (true) {
			boolean isRepeated = false;
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
		session.setAttribute("compareCard", compareCard);
		return MessageContent.newBuilder().setResult(0).setSCCompareHistoryOneCard(SCCompareHistoryOneCard.newBuilder().setCompareCard(compareCard));
	}
	
	public Builder csFourKindTime(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null){
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		String machineId = (String) session.getAttribute("machineId");
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{machineId}, new String[]{"seoMachineId"});
		int fourOfAKindJokerTwoFourteen = fivepkSeoId.getPrefabFourOfAKindJokerTwoFourteen();
		if (fourOfAKindJokerTwoFourteen == 0){
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_FOUR_OF_A_KIND_JOKER_TWO_FOURTEEN_ZERO.getErrorCode());
		}   
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
		cr.setWin(cr.getWin() * fourOfAKindJokerTwoFourteen);
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		fivepkSeoId.setPrefabFourOfAKindJokerTwoFourteen(0);
		jdbcDaoSupport.update(fivepkSeoId);
		MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
		returnMessagePack.setCmd(Cmd.CMD_FOUR_KIND_TIME);
		returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScFourKindTime(SCFourKindTime.newBuilder().setMultiple(fourOfAKindJokerTwoFourteen)));
		session.write(returnMessagePack);
		
		List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(playerInfo.getSeoId());
		for (IoSession ioSession : sessionList) {
			if (!ioSession.isClosing() && ioSession.isConnected()) {
				MessagePack.Builder returnMessagePack1 = MessagePack.newBuilder();
				returnMessagePack1.setCmd(Cmd.CMD_NOTICE);
				returnMessagePack1.setContent(MessageContent.newBuilder().setResult(0).setScNotice(SCNotice.newBuilder().setNotice(fivepkPlayerInfo.getNickName() + "," + cr.getWinType() + "," + fourOfAKindJokerTwoFourteen)));
//				PlayerInfo playerInfo1 = GameCache.getPlayerInfo((Long) ioSession.getAttribute("accountId"));
//				logger.info("nickName : " + playerInfo1.getNickName() + "accountId:" + accountId + "  |　sent message pack : " + returnMessagePack1.toString());
				ioSession.write(returnMessagePack1);
			} else {
				logger.info("ioSession accountId not send:　" + ioSession.getAttribute("accountId"));
			}
		}
		return MessageContent.newBuilder().setResult(0).setScFourKindTime(SCFourKindTime.newBuilder().setMultiple(fourOfAKindJokerTwoFourteen));
	}
	
	public Builder csMachineAuto(IoSession session, MessageContent content){
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		int auto=content.getCsMachineAuto().getAuto();
		FivepkSeoId fivepkSeoId=jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{session.getAttribute("machineId")}, null, new String[]{"seoMachineId"});
		fivepkSeoId.setMachineAuto(auto);
		jdbcDaoSupport.update(fivepkSeoId);
		return MessageContent.newBuilder().setResult(0).setScMachineAuto(SCMachineAuto.newBuilder().setAuto(auto));
	}
	
	public Builder csPlayerCardsData(IoSession session, MessageContent content){
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		String machineId = (String) session.getAttribute("machineId");
		List<FivepkDefault> fivepkDefaultList=jdbcDaoSupport.query(FivepkDefault.class, new Object[]{machineId}, new QueryMeta(0, 10, "id", Order.DESC), new String[]{"machineId"});
		SCPlayerHistoryData.Builder listBuilder = SCPlayerHistoryData.newBuilder();
		for (FivepkDefault fivepkDefault : fivepkDefaultList) {
			SCPlayerHistoryCardsData.Builder infoBuilder = SCPlayerHistoryCardsData.newBuilder();
			infoBuilder.setCredit(fivepkDefault.getCredit());
			infoBuilder.setBet(fivepkDefault.getBet());
			infoBuilder.setWin(fivepkDefault.getWin());
			infoBuilder.setOneCard(fivepkDefault.getOneCard());
			infoBuilder.setGuardCard(fivepkDefault.getGuardCard());
			infoBuilder.setTwoCard(fivepkDefault.getTwoCard());
			if(!fivepkDefault.getGuessType().equals("")){
				for(String string:fivepkDefault.getGuessType().split(",")){
					SCPlayerHistoryCompareData.Builder playerHistoryCompareData=SCPlayerHistoryCompareData.newBuilder();
					String str[]=string.split("-");
					playerHistoryCompareData.setContrast(Integer.parseInt(str[0]));					
					playerHistoryCompareData.setPoint(Integer.parseInt(str[1]));
					playerHistoryCompareData.setGuess(Integer.parseInt(str[2]));
					playerHistoryCompareData.setOpenCard(Integer.parseInt(str[3]));
					infoBuilder.addScPlayerHistoryCompareData(playerHistoryCompareData);
				}
			}
			
			//infoBuilder.setScPlayerHistoryCompareData(index, value)
			listBuilder.addScPlayerHistoryCardsData(infoBuilder);
		}
		return MessageContent.newBuilder().setResult(0).setScPlayerHistoryData(listBuilder);
	}
	
	public Builder csCardsBet(IoSession session, MessageContent content){
//		Long accountId = (Long) session.getAttribute("accountId");
//		if (accountId == null) {
//			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
//		}
//		int betScore = content.getCsCardsBet().getBetScore();
//		if (betScore <= 0 || betScore > 2000){
//			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER.getErrorCode());
//		}
//		CardResult cr = (CardResult)session.getAttribute("cardResult");;
//		if (cr == null){
//			cr = new CardResult();
//		}
//		cr.setBet(betScore);
//		session.setAttribute("cardResult", cr);
		return MessageContent.newBuilder().setResult(0);
	}
}