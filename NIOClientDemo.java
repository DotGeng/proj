package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * NIO客户端
 * @author GYK
 *
 */

public class NIOClientDemo {
	//声明通道管理器
	private Selector selector;
	//获得一个Socket通道，并对该通道做出一些初始化工作，包括IP地址，端口号，
	public void initClient(String ip,int port) throws IOException {
		//获得一个socket通道
		SocketChannel channel = SocketChannel.open();
		//设置通道为非阻塞方式
		channel.configureBlocking(false);
		//得到一个通道管理器
		this.selector = Selector.open();
		//客户端连接服务器，方法以异步方式完成连接
		channel.connect(new InetSocketAddress(ip,port));
		//将通道管理器与该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。
		channel.register(selector, SelectionKey.OP_CONNECT);
		 //在这里可以给服务器发送信息
		 //如果正在连接，则完成连接
		 while(channel.isConnectionPending()) {
			 //可以异步处理其他事件
			 System.out.println("正在连接");
			 if(channel.finishConnect())  {
				 System.out.println("完成连接");
			 }
		 }
		String data = new String("给服务器发送一条信息feafwfwafsefawafawfewafwafwafwa");
		 ByteBuffer writeBuffer = ByteBuffer.wrap(data.getBytes());
		 while(writeBuffer.hasRemaining()) {
			 System.out.println("正在写入数据");
			 channel.write(writeBuffer);
		 }
			 System.out.println("客户端关闭");
			 channel.close();
		 
	}
	 /**
	  * 启动客户端进行测试
	  * @param args
	 * @throws Exception 
	  */
	public static void main(String[] args) throws Exception {
		NIOClient client = new NIOClient();
		client.initClient("localhost", 8000);
	}
}






















