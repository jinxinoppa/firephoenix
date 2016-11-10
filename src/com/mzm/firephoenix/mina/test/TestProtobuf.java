package com.mzm.firephoenix.mina.test;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.mzm.firephoenix.protobuf.CoreProtocol;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;

public class TestProtobuf {
	public static void main(String[] args) {
		CoreProtocol.MessagePack.Builder messagePackBuilder = CoreProtocol.MessagePack.newBuilder();
		messagePackBuilder.setCmd(Cmd.CMD_BET_LIST);
		CoreProtocol.MessageContent.Builder messageContentBuilder = CoreProtocol.MessageContent.newBuilder();
		messageContentBuilder.setResult(0);
		CoreProtocol.LCRetServer.Builder lcRetServerBuilder = CoreProtocol.LCRetServer.newBuilder();
		lcRetServerBuilder.setServerip("192.168.0.0");
		lcRetServerBuilder.setPort(8080);
		messageContentBuilder.setLcRetServer(lcRetServerBuilder);
		CoreProtocol.CSDepositDraw.Builder csDepositDrawBuilder = CoreProtocol.CSDepositDraw.newBuilder();
		csDepositDrawBuilder.setDrawGold(1);
		messagePackBuilder.setContent(messageContentBuilder);
		System.out.println(messagePackBuilder.getContent().toString());
		String name = null;
		for (FieldDescriptor string : messagePackBuilder.getContent().getAllFields().keySet()) {
			if (string.getName().startsWith("sc") || string.getName().startsWith("cs")) {
				name = string.getName().substring(2);
			}
		}
		FieldDescriptor scfd = (FieldDescriptor) messagePackBuilder.getContent().getField(MessageContent.getDescriptor().findFieldByName("sc" + name));
	}
}
