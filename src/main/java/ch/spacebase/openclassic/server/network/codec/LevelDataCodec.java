package ch.spacebase.openclassic.server.network.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import ch.spacebase.openclassic.api.network.msg.LevelDataMessage;


public class LevelDataCodec extends MessageCodec<LevelDataMessage> {

	public LevelDataCodec() {
		super(LevelDataMessage.class, (byte) 0x03);
	}

	@Override
	public ChannelBuffer encode(LevelDataMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		
		buffer.writeShort(message.getLength());
		buffer.writeBytes(message.getData());
		buffer.writeByte(message.getPercent());
		
		return buffer;
	}

	@Override
	public LevelDataMessage decode(ChannelBuffer buffer) throws IOException {
		short length = buffer.readShort();
		
		byte[] data = new byte[length];
		buffer.readBytes(data);
		
		byte percent = buffer.readByte();
		
		return new LevelDataMessage(length, data, percent);
	}
	
}
