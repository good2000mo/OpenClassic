package ch.spacebase.openclassic.server.network.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;

import ch.spacebase.openclassic.api.network.msg.Message;


public abstract class MessageCodec<T extends Message> {

	private final Class<T> clazz;
	private final byte opcode;

	public MessageCodec(Class<T> clazz, byte opcode) {
		this.clazz = clazz;
		this.opcode = opcode;
	}

	public final Class<T> getType() {
		return clazz;
	}
	
	public byte getOpcode() {
		return this.opcode;
	}

	public abstract ChannelBuffer encode(T message) throws IOException;

	public abstract T decode(ChannelBuffer buffer) throws IOException;
	
}
