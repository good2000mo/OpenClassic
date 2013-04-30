package ch.spacebase.openclassic.server.network;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import ch.spacebase.openclassic.api.network.msg.Message;
import ch.spacebase.openclassic.server.network.codec.MessageCodec;

public class ClassicEncoder extends OneToOneEncoder {

	@SuppressWarnings("unchecked")
	@Override
	protected Object encode(ChannelHandlerContext context, Channel channel, Object msg) throws Exception {
		if (msg instanceof Message) {
			Message message = (Message) msg;

			Class<? extends Message> clazz = message.getClass();
			MessageCodec<Message> codec = (MessageCodec<Message>) CodecLookupService.find(clazz);
			
			if (codec == null) {
				throw new IOException("Unknown message type: " + clazz + ".");
			}

			ChannelBuffer opcodeBuf = ChannelBuffers.buffer(1);
			opcodeBuf.writeByte(message.getOpcode());
			
			return ChannelBuffers.wrappedBuffer(opcodeBuf, codec.encode(message));
		}
		return msg;
	}
	
}
