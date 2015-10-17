package handserver;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class CustomTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	
	private DirectionListener dl;
	private final ChannelGroup channels;
	
    public CustomTextFrameHandler(DirectionListener dl, ChannelGroup channels) {
    	this.channels = channels;
    	this.dl = dl;
	}
    
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channels.add(ctx.channel());
	}
    
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		channels.remove(ctx.channel());
	}

	@Override
    protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        String request = frame.text();
        String returnMsg = "";
        float mult = dl.getMultiplier();
        switch (request.toUpperCase()) {
		case "W": dl.valuesChanged(0, mult, 0, 0, 0); returnMsg = "stretch +" + mult + "!"; break;
		case "S": dl.valuesChanged(0, -mult, 0, 0, 0); returnMsg = "stretch -" + mult + "!"; break;
		
		case "D": dl.valuesChanged(mult, 0, 0, 0, 0); returnMsg = "rotate +" + mult + "!"; break;
		case "A": dl.valuesChanged(-mult, 0, 0, 0, 0); returnMsg = "rotate -" + mult + "!"; break;

		case "K": dl.valuesChanged(0, 0, mult, 0, 0); returnMsg = "height +" + mult + "!"; break;
		case "I": dl.valuesChanged(0, 0, -mult, 0, 0); returnMsg = "height -" + mult + "!"; break;
		
		case "L": dl.valuesChanged(0,0,0, mult, 0); returnMsg = "grab_rot +" + mult + "!"; break;
		case "J": dl.valuesChanged(0,0,0,-mult, 0); returnMsg = "grab_rot -" + mult + "!"; break;
		
		case " ": dl.valuesChanged(0, 0, 0, 0, mult); break;

		default:
			System.out.println(request);	
			break;
		}
        
        ctx.channel().writeAndFlush(new TextWebSocketFrame(dl.getValues() + ", \"echo\": \"" + returnMsg + "\"}"));
        
    }
}
