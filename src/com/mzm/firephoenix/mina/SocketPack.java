package com.mzm.firephoenix.mina;

public class SocketPack {
	private String executorName;
	private String methodName;
	private short actId;

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public short getActId() {
		return actId;
	}

	public void setActId(short actId) {
		this.actId = actId;
	}

	public String toString() {
		return executorName + "_" + methodName + "-" + actId;
	}
}
