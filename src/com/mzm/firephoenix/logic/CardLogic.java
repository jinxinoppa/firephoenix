package com.mzm.firephoenix.logic;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cardutils.CardResult;
import com.mzm.firephoenix.cardutils.CardUtil;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCCoinScore;
import com.mzm.firephoenix.protobuf.CoreProtocol.CCCompareHistoryCards;
import com.mzm.firephoenix.protobuf.CoreProtocol.ErrorCode;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCards;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCCompareCard;

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

	public static void main(String[] args) {
		String holdCards = "";
		if (holdCards != null && !holdCards.isEmpty()) {
			holdCards.trim();
			String[] holdCardsArr = holdCards.split(",");
			byte[] keepCards = new byte[holdCardsArr.length];
			for (int i = 0; i < holdCardsArr.length; i++) {
				keepCards[i] = Byte.parseByte(holdCardsArr[i].trim());
			}
		}
		byte[] b = new byte[]{1, 2, 3, 4, 5};
		String s = Arrays.toString(b);
		System.out.println(s.substring(1, s.length() - 1));
	}

	public Builder csCards(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		int startIndex = content.getCsCards().getStartIndex();
		int betScore = content.getCsCards().getBetScore();
		if (startIndex != 0 && startIndex != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		if (betScore != GameConstant.BETSCORE_100 && betScore != GameConstant.BETSCORE_500 && betScore != GameConstant.BETSCORE_1000 && betScore != GameConstant.BETSCORE_2000) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_BET_SCORE_0_VALUE);
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
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
			}
			if (cr.getKeepCards() != null && cr.getKeepCards().length == 5) {
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(""));
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
			cr = CardUtil.secondRandomCards(cr);
			int winType = cr.getWinType();
			int score = 0;
			if (winType != 0) {
				score = winType * betScore;
			} else {
				score = 0;
				fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet());
				jdbcDaoSupport.update(fivepkPlayerInfo);
				cr.setBet(0);
			}
			cr.setWin(score);
			session.setAttribute("cardResult", cr);
			String cardsStr = Arrays.toString(cr.getCards());
			cardsStr = cardsStr.substring(1, cardsStr.length() - 1);
			return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(winType).setCards(cardsStr));
		}
	}

	public Builder ccCompareHistoryCards(IoSession session, MessageContent content) {
		long accountId = (long) session.getAttribute("accountId");
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
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		if (betScore != 0 && betScore != cr.getWin() && betScore != cr.getWin() / 2 && betScore != cr.getWin() * 2) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		if (betScore > fivepkPlayerInfo.getScore()) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_COMPARE_CARD_BET_SCORE_NOT_ENOUGH_VALUE);
		}
		int compareCard = 0;
		int score = 0;
		boolean isUpdate = false;
		if (betScore == 0) {
			compareCard = CardUtil.compareCard();
			session.setAttribute("compareCard", compareCard);
			return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore(cr.getWin()));
		} else {
			compareCard = CardUtil.compareCard();
			int cardValue = CardUtil.getCardValue(compareCard);
			boolean isWin = true;
			if (bigSmall == 0 && cardValue > 7 || bigSmall == 0 && cardValue == 1) {
				isWin = true;
			} else if (bigSmall == 1 && cardValue < 7 && cardValue > 1) {
				isWin = true;
			}
			if (betScore == cr.getWin() / 2) {
				score = betScore;
				cr.setWin(betScore);
				isUpdate = true;
			} else if (betScore == cr.getWin() * 2) {
				score = -cr.getWin();
				isUpdate = true;
			}
			if (isWin) {
				if (betScore == cr.getWin() * 2) {
					cr.setWin(betScore * 2);
				} else {
					cr.setWin(cr.getWin() + betScore);
				}
			} else {
				cr.setWin(0);
				score += cr.getWin() - cr.getBet();
				isUpdate = true;
				cr.setBet(0);
			}
		}
		session.setAttribute("cardResult", cr);
		fivepkPlayerInfo.firstInLastOut(compareCard);
		if (isUpdate) {
			fivepkPlayerInfo.setScore(fivepkPlayerInfo.getScore() + score);
		}
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(2).setWinScore(cr.getWin()));
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
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet());
		cr.setBet(0);
		cr.setWin(0);
		session.setAttribute("cardResult", cr);
		if (session.getAttribute("compareCard") != null) {
			fivepkPlayerInfo.firstInLastOut((Integer) session.getAttribute("compareCard"));
			session.removeAttribute("compareCard");
		}
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccCoinScore(IoSession session, MessageContent content) {
		Byte accountType = (Byte) session.getAttribute(GameConstant.SESSION_ACCOUNT_TYPE);
		if (accountType == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		if (accountType == GameConstant.ACCOUNT_TYPE_GUEST) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_CARD_GUEST_COINSCORE_VALUE);
		}
		int coin = content.getCcCoinScore().getCoin();
		int score = content.getCcCoinScore().getScore();
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
			if ((coin * 100 + score) != (fivepkPlayerInfo.getCoin() * 100 + fivepkPlayerInfo.getScore())) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
			}
		} else {
			if ((coin * 100 + score) != (fivepkPlayerInfo.getCoin() * 100 + fivepkPlayerInfo.getScore() - cr.getBet())) {
				return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
			}
			cr.setBet(0);
			session.setAttribute("cardResult", cr);
		}
		fivepkPlayerInfo.setCoin(coin);
		fivepkPlayerInfo.setScore(score);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0).setCcCoinScore(CCCoinScore.newBuilder().setCoin(coin).setScore(score));
	}
}