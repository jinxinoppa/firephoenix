package com.mzm.firephoenix.logic;

import java.util.Arrays;
import java.util.List;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cardutils.CardResult;
import com.mzm.firephoenix.cardutils.CardUtil;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.protobuf.CoreProtocol.CompareHistoryCards;
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
		byte[] b = new byte[]{1, 2, 3, 4, 5};
		String s = Arrays.toString(b);
		System.out.println(s);
		System.out.println(s.substring(1, s.length() - 1));
	}
	public Builder csCards(IoSession session, MessageContent content) {
		int startIndex = content.getCsCards().getStartIndex();
		int betScore = content.getCsCards().getBetScore();
		if (startIndex != 0 && startIndex != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		if (betScore != 100 && betScore != 500 && betScore != 1000 && betScore != 2000) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		CardResult cr = null;
		if (startIndex == 0) {
			cr = CardUtil.firstRandomCards();
			session.setAttribute("cardResult", cr);
			String cardsStr = Arrays.toString(cr.getCards());
			cardsStr = cardsStr.substring(1, cardsStr.length() - 1);
			if (cr.getKeepCards() == null) {
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
			if (cr.getKeepCards() != null && cr.getKeepCards().length == 5){
				return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(""));
			}
			cr = CardUtil.secondRandomCards(cr);
			String cardsStr = Arrays.toString(cr.getCards());
			cardsStr = cardsStr.substring(1, cardsStr.length() - 1);
			return MessageContent.newBuilder().setResult(0).setScCards(SCCards.newBuilder().setCardRate(cr.getWinType()).setCards(cardsStr));
		}
	}

	public Builder compareHistoryCards(IoSession session, MessageContent content) {
		long accountId = (long) session.getAttribute("accountId");
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		String compareHistoryCards = fivepkPlayerInfo.getCompareHistoryCards();
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0).setCompareHistoryCards(CompareHistoryCards.newBuilder().setCards(compareHistoryCards));
	}

	public Builder csCompareCard(IoSession session, MessageContent content) {
		int bigSmall = content.getCsCompareCard().getBigSmall();
		int betScore = content.getCsCompareCard().getBetScore();
		if (bigSmall != 0 || bigSmall != 1) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		int compareCard = CardUtil.compareCard();
		int cardValue = CardUtil.getCardValue(compareCard);
		long accountId = (long) session.getAttribute("accountId");
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT_VALUE);
		}
		boolean isWin = false;
		if (bigSmall == 0 && cardValue > 7 || bigSmall == 0 && cardValue == 1) {
			isWin = true;
		} else if (bigSmall == 1 && cardValue < 7 && cardValue > 1) {
			isWin = true;
		}
		List<Integer> list = fivepkPlayerInfo.getCompareHistoryCardsList();
		list.add(0, compareCard);
		list.remove(list.size() - 1);
		fivepkPlayerInfo.setCompareHistoryCardsList();
		jdbcDaoSupport.update(fivepkPlayerInfo);
		return MessageContent.newBuilder().setResult(0).setScCompareCard(SCCompareCard.newBuilder().setCompareCard(compareCard).setWinScore("0"));
	}

}