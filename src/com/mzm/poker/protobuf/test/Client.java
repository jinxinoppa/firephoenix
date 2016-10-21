package com.mzm.poker.protobuf.test;

import java.net.Socket;

import com.mzm.poker.protobuf.test.Protocal.Msg;
import com.mzm.poker.protobuf.test.Protocal.Msg.Builder;


public class Client {
	public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        Socket socket = new Socket("127.0.0.1",3030);
        Protocal.Msg.Builder msgBuilder = Protocal.Msg.newBuilder();
        msgBuilder.setLength(1);
        msgBuilder.setAutoNum(2);
        msgBuilder.setChecksum(3);
        msgBuilder.setMsgType(4);
        byte[] messageBody = msgBuilder.build().toByteArray();
    
        int headerLen = 1;
        byte[] message = new byte[headerLen+messageBody.length];
        message[0] = (byte)messageBody.length;
        System.arraycopy(messageBody, 0,  message, 1, messageBody.length);
        System.out.println("msg len:"+message.length);
        socket.getOutputStream().write(message);
    }
}
