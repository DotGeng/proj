package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO 服务器
 * @author GYK
 *
 */
public class NIOServerDemo {

	// 声明一个通道管理器
	private Selector selector;
	/**
	 * 一：获得一个ServerSocketChannel通道对象，并对该对象做一些初始化的工作
	 * @throws IOException 
	 * 
	 */
	public void initServer( int port) throws IOException {
		//获得一个serverSocket通道对象
		ServerSocketChannel serverChannel  = ServerSocketChannel.open();
		//这是通道为非阻塞方式
		serverChannel.configureBlocking(false);
		// 将该对象对应的serverSocket绑定到port端口上
		serverChannel.socket().bind(new InetSocketAddress(port));
		//获得一个管道管理器
		this.selector = Selector.open();
		/*将管道管理器与该通道绑定到port端口，并为该通道注册SelectorKey.OP_ACCEPT事件。当
		事件达到的时候，seletor.select() 方法会轮询到状态的改变。（打断点验证下是否会阻塞。）*/
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 *二： 采用轮询的方式监听是否有感兴趣的事件发生，如果有，则进行步骤4：进行事件处理
	 * @throws Exception 
	 */
	public void listen() throws Exception {
		System.out.println("服务器端启动成功");while(true) {
			
			//轮询各个信道
			if(selector.select(200) == 0) {
				//这里可以并发处理其他事件，我们这里输出。。。。
				System.out.print("。");
				continue;
			}
			//获得selector中通道的迭代器，选中的通道就是发生了感兴趣IO事件。
			Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
			while(ite.hasNext() ) {
				SelectionKey key = ite.next();
				//删除已经选中的Key以防止重复
				ite.remove();
				//客户端请求连接事件
				//通过key得到ServerSocketChannel
				if(key.isAcceptable()) {
					//获得该通道对应的serverChannnel
					ServerSocketChannel serverChannnel = (ServerSocketChannel)key.channel();
					//获得和客户端连接的通道
					SocketChannel clinChannel = serverChannnel.accept();  
					//设置成非阻塞
					clinChannel.configureBlocking(false);
					//在和客户端连接成功以后，为了可以从客户端接收到信息，那么就需要给客户端通道设置读的权限
					clinChannel.register(selector, SelectionKey.OP_READ);
				} else if(key.isReadable()) {
					int readRecd = 0;
					while((readRecd = read(key)) != -1);
					if((readRecd = read(key))== -1) {
						System.out.println("客户端已经关闭");
						key.channel().close();
					}
				}
			}
		}
	}
	/** 
	 * 三  处理客户端发送来的信息的事件
	 * @throws IOException 
	 */
	public int read(SelectionKey key) throws IOException {
		//服务器端可读信息：得到事件发生的Socket通道
		SocketChannel clinChannel = (SocketChannel) key.channel();
		//创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(10);
		int readRecd = clinChannel.read(buffer);
		byte[] data = buffer.array();
		String msg = new String(data).trim();
		System.out.println("服务端接收的消息是："+msg);
		ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
		clinChannel.write(outBuffer);//将消息返回到客户端
		return readRecd;
	}
	/**
	 * 四 启动服务器测试
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception {
		NIOServer server = new NIOServer();
		server.initServer(8000);
		server.listen();
	}

}
