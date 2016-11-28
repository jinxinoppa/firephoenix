package com.mzm.firephoenix.logic;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.oppa.utils.cardutils.CardResult;
import org.oppa.utils.cardutils.CardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
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
		CardResult cr = null;
		if (startIndex == 0) {

			cr = CardUtil.firstRandomCards();
			cr.setBet(betScore);
			cr.setStartIndex(1);
			session.setAttribute("cardResult", cr);
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
			if (holdCards != null && !holdCards.isEmpty()) {
				String[] holdCardsArr = holdCards.split(",");
				byte[] keepCards = new byte[holdCardsArr.length];
				for (int i = 0; i < holdCardsArr.length; i++) {
					keepCards[i] = Byte.parseByte(holdCardsArr[i].trim());
				}
				cr.setKeepCards(keepCards);
			} else {
				cr.setKeepCards(null);
			}
			// if (cr.getKeepCards() != null && cr.getKeepCards().length == 5) {
			// return
			// MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(""));
			// }
			cr = CardUtil.secondRandomCards(cr);
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
			logger.info("------------------------------------------------------------------------------set win : " + score);
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
			session.setAttribute("cardResult", cr);
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
		if (betScore == 0) {
			compareCard = CardUtil.compareCard();
			session.setAttribute("compareCard", compareCard);
			return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore(0));
		} else {
			compareCard = CardUtil.compareCard();
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
				logger.info("------------------------------------------------------------------------------set win : " + betScore);
				isUpdate = true;
			} else if (betScore == cr.getWin() * 2) {
				score = -cr.getWin();
				isUpdate = true;
			}
			if (isWin) {
				if (betScore == cr.getWin() * 2) {
					cr.setWin(betScore * 2);
					logger.info("------------------------------------------------------------------------------set win : " + betScore * 2);
				} else {
					cr.setWin(cr.getWin() + betScore);
					logger.info("------------------------------------------------------------------------------set win : " + cr.getWin() + betScore);
				}
				if (compareCard == 53) {
					int randomTimes = RandomUtils.nextInt(3, 6);
					cr.setWin(cr.getWin() * randomTimes);
				}
				cr.setWinCount(cr.getWinCount() + 1);

				if (cr.getWin() >= 75000) {
					// cr.setWin(cr.getWin() + cr.getWinCount() * 4000);
					// score += cr.getWin() + cr.getWinCount() * 4000;
					cr.setGiftWin(cr.getWinCount() * 4000);
				}
				if (cr.getWin() >= 200000) {
					// cr.setWin(cr.getWin() + 50000);
					// score += cr.getWin() + 50000;
					cr.setGiftWin(cr.getGiftWin() + 50000);
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
				logger.info("------------------------------------------------------------------------------set win : " + 0);
				score = score - cr.getBet() + giftWin;
				isUpdate = true;
				cr.setBet(0);
				cr.setWinCount(0);
			}
		}
		session.setAttribute("cardResult", cr);
		session.removeAttribute("compareCard");
		fivepkPlayerInfo.firstInLastOut(compareCard);
		if (isUpdate) {
			fivepkPlayerInfo.setScore(fivepkPlayerInfo.getScore() + score);
			logger.info("------------------------------------------------------------------------得分: " + fivepkPlayerInfo.getScore());
		}
		jdbcDaoSupport.update(fivepkPlayerInfo);
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
		cr.reset();
		session.setAttribute("cardResult", cr);
		if (session.getAttribute("compareCard") != null) {
			fivepkPlayerInfo.firstInLastOut((Integer) session.getAttribute("compareCard"));
			session.removeAttribute("compareCard");
		}
		logger.info("------------------------------------------------------------------------得分: " + fivepkPlayerInfo.getScore());
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