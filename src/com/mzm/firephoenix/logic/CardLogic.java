package com.mzm.firephoenix.logic;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cardutils.CardResult;
import com.mzm.firephoenix.cardutils.CardUtil;
import com.mzm.firephoenix.constant.CardConstant;
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
		int startIndex = content.getCsCards().getStartIndex();
		int betScore = content.getCsCards().getBetScore();
		if (startIndex != 0 && startIndex != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		if (betScore != CardConstant.BETSCORE_100 && betScore != CardConstant.BETSCORE_500 && betScore != CardConstant.BETSCORE_1000 && betScore != CardConstant.BETSCORE_2000) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		CardResult cr = null;
		if (startIndex == 0) {
			cr = CardUtil.firstRandomCards();
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
			int score = winType * betScore;
			cr.setScore(score);
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
		int betScore = content.getCsCompareCard().getBetScore();
		if (bigSmall != 0 && bigSmall != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		long accountId = (long) session.getAttribute("accountId");
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		int compareCard = CardUtil.compareCard();
		int cardValue = CardUtil.getCardValue(compareCard);
		boolean isWin = false;
		if (bigSmall == 0 && cardValue > 7 || bigSmall == 0 && cardValue == 1) {
			isWin = true;
		} else if (bigSmall == 1 && cardValue < 7 && cardValue > 1) {
			isWin = true;
		}
		if (isWin) {
			cr.setScore(cr.getScore() + betScore);
		} else {
			cr.setScore(0);
		}
		fivepkPlayerInfo.firstInLastOut(compareCard);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore(cr.getScore()));
	}

	public Builder csWin(IoSession session, MessageContent content) {
		long accountId = (long) session.getAttribute("accountId");
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		int score = cr.getScore();
		if (score > 0) {
			fivepkPlayerInfo.setScore(score + fivepkPlayerInfo.getScore());
			jdbcDaoSupport.update(fivepkPlayerInfo);
		}
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccCoinScore(IoSession session, MessageContent content) {
		int coin = content.getCcCoinScore().getCoin();
		int score = content.getCcCoinScore().getScore();
		long accountId = (long) session.getAttribute("accountId");
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
//		if ((coin * 100 + score) != (fivepkPlayerInfo.getCoin() * 100 + fivepkPlayerInfo.getScore())) {
//			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
//		}
		fivepkPlayerInfo.setCoin(coin);
		fivepkPlayerInfo.setScore(score);
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0).setCcCoinScore(CCCoinScore.newBuilder().setCoin(coin).setScore(score));
	}
}