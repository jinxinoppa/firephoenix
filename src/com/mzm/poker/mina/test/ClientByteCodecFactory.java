package com.mzm.poker.mina.test;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * code factory
 * 
 * @author oppa
 * 
 */
public class ClientByteCodecFactory implements ProtocolCodecFactory {

	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public ProtocolEncoder getEncoder() {
		return encoder;
	}

	public void setEncoder(ProtocolEncoder encoder) {
		this.encoder = encoder;
	}

	public ProtocolDecoder getDecoder() {
		return decoder;
	}

	public void setDecoder(ProtocolDecoder decoder) {
		this.decoder = decoder;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}
}
