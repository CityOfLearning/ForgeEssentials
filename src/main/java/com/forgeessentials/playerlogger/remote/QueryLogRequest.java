package com.forgeessentials.playerlogger.remote;

import java.util.Date;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.remote.RemoteMessageID;

public class QueryLogRequest {

	private static final int MAX_LIMIT = 100;

	public static final String ID = RemoteMessageID.PL_QUERY_LOG_COMMANDS;

	public Date startTime;

	public Date endTime;

	public Integer dimension;

	public Point point;

	public Point startPoint;

	public Point endPoint;

	protected int limit;

	public QueryLogRequest() {
	}

	public QueryLogRequest(Date startTime, Date endTime, int limit) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.limit = limit;
	}

	public WorldArea getArea() {
		return hasArea() ? new WorldArea(dimension, startPoint, endPoint) : null;
	}

	public int getLimit() {
		return ((limit > MAX_LIMIT) || (limit == 0)) ? MAX_LIMIT : limit;
	}

	public WorldPoint getPoint() {
		return (dimension != null) && (point != null) ? new WorldPoint(dimension, point) : null;
	}

	public boolean hasArea() {
		return (dimension != null) && (startPoint != null) && (endPoint != null);
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}