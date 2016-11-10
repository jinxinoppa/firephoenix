package com.mzm.firephoenix.dao;

public final class QueryMeta {

	public static final QueryMeta DEFAULT_QUERY_META = new QueryMeta(0, 0);

	private final String sort;

	private final Order dir;

	private final int firstResult;

	private final int lastResult;

	private final int maxResults;

	private final String groupBy;

	public QueryMeta(int first, int max) {
		this.firstResult = first;
		this.lastResult = first + max;
		this.maxResults = max;
		this.sort = null;
		this.dir = null;
		this.groupBy = null;
	}

	public QueryMeta(int first, int max, String sort, Order order) {
		this.firstResult = first;
		this.lastResult = first + max;
		this.maxResults = max;
		this.sort = sort;
		this.dir = order;
		this.groupBy = null;
	}

	public QueryMeta(String sort, Order order) {
		this.sort = sort;
		this.dir = order;
		this.firstResult = 0;
		this.lastResult = 0;
		this.maxResults = 0;
		this.groupBy = null;
	}

	public QueryMeta(String groupBy) {
		this.firstResult = 0;
		this.lastResult = 0;
		this.maxResults = 0;
		this.sort = null;
		this.dir = null;
		this.groupBy = groupBy;
	}

	public String getSort() {
		return this.sort;
	}

	public Order getDir() {
		return this.dir;
	}

	public int getFirstResult() {
		return this.firstResult;
	}

	public int getMaxResults() {
		return this.maxResults;
	}

	public int getLastResult() {
		return lastResult;
	}

	public String getGroupBy() {
		return groupBy;
	}
}
