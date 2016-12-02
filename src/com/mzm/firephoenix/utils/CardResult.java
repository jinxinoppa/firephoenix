package com.mzm.firephoenix.utils;

import java.util.ArrayList;
import java.util.List;


public class CardResult {
	private int winType = 0;
	private boolean isWin = false;
	private byte[] cards = null;
	private byte[] keepCards = null;
	private int physicalNumberOfRow;
	private int bet = 0;
	private int win = 0;
	private int winCount;
	private int giftWin = 0;
	private int startIndex;
	private int jokerCount = 0;
	private byte keepCard = 0;
	private byte replaceCard = 0;
	private List<Integer> halfWin = new ArrayList<Integer>();

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public void reset() {
		setBet(0);
		setWin(0);
		setWinCount(0);
		setGiftWin(0);
		halfWin.clear();
	}

	public int getWinType() {
		return winType;
	}

	public void setWinType(int winType) {
		this.winType = winType;
	}

	public byte[] getCards() {
		return cards;
	}

	public void setCards(byte[] cards) {
		this.cards = cards;
	}

	public byte[] getKeepCards() {
		return keepCards;
	}

	public void setKeepCards(byte[] keepCards) {
		this.keepCards = keepCards;
	}

	void setAfterWin(boolean win, byte[] keepCards) {
		this.isWin = win;
		this.keepCards = keepCards;
	}

	int getPhysicalNumberOfRow() {
		return physicalNumberOfRow;
	}

	void setPhysicalNumberOfRow(int physicalNumberOfRow) {
		this.physicalNumberOfRow = physicalNumberOfRow;
	}

	boolean isWin() {
		return isWin;
	}

	void setWin(boolean isWin) {
		this.isWin = isWin;
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public int getGiftWin() {
		return giftWin;
	}

	public void setGiftWin(int giftWin) {
		this.giftWin = giftWin;
	}

	public int getJokerCount() {
		return jokerCount;
	}

	public void setJokerCount(int jokerCount) {
		this.jokerCount = jokerCount;
	}

	public byte getKeepCard() {
		return keepCard;
	}

	public void setKeepCard(byte keepCard) {
		this.keepCard = keepCard;
	}

	public byte getReplaceCard() {
		return replaceCard;
	}

	public void setReplaceCard(byte replaceCard) {
		this.replaceCard = replaceCard;
	}

	public List<Integer> getHalfWin() {
		return halfWin;
	}

	public void setHalfWin(List<Integer> halfWin) {
		this.halfWin = halfWin;
	}
}
