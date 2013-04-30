package ch.spacebase.openclassic.server.network.codec.custom;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import ch.spacebase.openclassic.api.network.msg.custom.audio.AudioPlayMessage;
import ch.spacebase.openclassic.server.network.codec.MessageCodec;
import ch.spacebase.openclassic.server.util.ChannelBufferUtils;

public class AudioPlayCodec extends MessageCodec<AudioPlayMessage> {

	public AudioPlayCodec() {
		super(AudioPlayMessage.class, (byte) 0x17);
	}

	@Override
	public ChannelBuffer encode(AudioPlayMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.writeString(buffer, message.getIdentifier());
		buffer.writeFloat(message.getX());
		buffer.writeFloat(message.getY());
		buffer.writeFloat(message.getZ());
		buffer.writeFloat(message.getVolume());
		buffer.writeFloat(message.getPitch());
		buffer.writeByte(message.isMusic() ? 1 : 0);
		buffer.writeByte(message.isLooping() ? 1 : 0);
		return buffer;
	}

	@Override
	public AudioPlayMessage decode(ChannelBuffer buffer) throws IOException {
		String identifier = ChannelBufferUtils.readString(buffer);
		float x = buffer.readFloat();
		float y = buffer.readFloat();
		float z = buffer.readFloat();
		float volume = buffer.readFloat();
		float pitch = buffer.readFloat();
		boolean music = buffer.readByte() == 1;
		boolean looping = buffer.readByte() == 1;
		return new AudioPlayMessage(identifier, x, y, z, volume, pitch, music, looping);
	}
	
}
