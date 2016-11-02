package com.mzm.firephoenix.protobuf.test;

import java.net.ServerSocket;
import java.net.Socket;

import com.mzm.firephoenix.protobuf.test.Protocal.Msg;

public class Server {
	  public static void main(String[] args) throws Exception {
	        // TODO Auto-generated method stub
	        ServerSocket serverSock = new ServerSocket(3030);
	        Socket sock = serverSock.accept();
	        byte[] msg = new byte[256];
	        sock.getInputStream().read(msg);
	        int msgBodyLen = msg[0];
	        System.out.println("msg body len:"+msgBodyLen);
	        byte[] msgbody = new byte[msgBodyLen];
	        System.arraycopy(msg, 1, msgbody, 0, msgBodyLen);
	        Msg protocalMsg = Protocal.Msg.parseFrom(msgbody);
	        System.out.println("Receive:" + protocalMsg.toString());
	    }
}
