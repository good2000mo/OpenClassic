package ch.spacebase.openclassic.server.network.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import ch.spacebase.openclassic.api.network.msg.LevelInitializeMessage;


public class LevelInitializeCodec extends MessageCodec<LevelInitializeMessage> {

	public LevelInitializeCodec() {
		super(LevelInitializeMessage.class, (byte) 0x02);
	}

	@Override
	public ChannelBuffer encode(LevelInitializeMessage message) throws IOException {
		return ChannelBuffers.EMPTY_BUFFER;
	}

	@Override
	public LevelInitializeMessage decode(ChannelBuffer buffer) throws IOException {
		return new LevelInitializeMessage();
	}
	
}
