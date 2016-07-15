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
 * NIO ������
 * @author GYK
 *
 */
public class NIOServerDemo {

	// ����һ��ͨ��������
	private Selector selector;
	/**
	 * һ�����һ��ServerSocketChannelͨ�����󣬲��Ըö�����һЩ��ʼ���Ĺ���
	 * @throws IOException 
	 * 
	 */
	public void initServer( int port) throws IOException {
		//���һ��serverSocketͨ������
		ServerSocketChannel serverChannel  = ServerSocketChannel.open();
		//����ͨ��Ϊ��������ʽ
		serverChannel.configureBlocking(false);
		// ���ö����Ӧ��serverSocket�󶨵�port�˿���
		serverChannel.socket().bind(new InetSocketAddress(port));
		//���һ���ܵ�������
		this.selector = Selector.open();
		/*���ܵ����������ͨ���󶨵�port�˿ڣ���Ϊ��ͨ��ע��SelectorKey.OP_ACCEPT�¼�����
		�¼��ﵽ��ʱ��seletor.select() ��������ѯ��״̬�ĸı䡣����ϵ���֤���Ƿ����������*/
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 *���� ������ѯ�ķ�ʽ�����Ƿ��и���Ȥ���¼�����������У�����в���4�������¼�����
	 * @throws Exception 
	 */
	public void listen() throws Exception {
		System.out.println("�������������ɹ�");while(true) {
			
			//��ѯ�����ŵ�
			if(selector.select(200) == 0) {
				//������Բ������������¼����������������������
				System.out.print("��");
				continue;
			}
			//���selector��ͨ���ĵ�������ѡ�е�ͨ�����Ƿ����˸���ȤIO�¼���
			Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
			while(ite.hasNext() ) {
				SelectionKey key = ite.next();
				//ɾ���Ѿ�ѡ�е�Key�Է�ֹ�ظ�
				ite.remove();
				//�ͻ������������¼�
				//ͨ��key�õ�ServerSocketChannel
				if(key.isAcceptable()) {
					//��ø�ͨ����Ӧ��serverChannnel
					ServerSocketChannel serverChannnel = (ServerSocketChannel)key.channel();
					//��úͿͻ������ӵ�ͨ��
					SocketChannel clinChannel = serverChannnel.accept();  
					//���óɷ�����
					clinChannel.configureBlocking(false);
					//�ںͿͻ������ӳɹ��Ժ�Ϊ�˿��Դӿͻ��˽��յ���Ϣ����ô����Ҫ���ͻ���ͨ�����ö���Ȩ��
					clinChannel.register(selector, SelectionKey.OP_READ);
				} else if(key.isReadable()) {
					int readRecd = 0;
					while((readRecd = read(key)) != -1);
					if((readRecd = read(key))== -1) {
						System.out.println("�ͻ����Ѿ��ر�");
						key.channel().close();
					}
				}
			}
		}
	}
	/** 
	 * ��  ����ͻ��˷���������Ϣ���¼�
	 * @throws IOException 
	 */
	public int read(SelectionKey key) throws IOException {
		//�������˿ɶ���Ϣ���õ��¼�������Socketͨ��
		SocketChannel clinChannel = (SocketChannel) key.channel();
		//������ȡ�Ļ�����
		ByteBuffer buffer = ByteBuffer.allocate(10);
		int readRecd = clinChannel.read(buffer);
		byte[] data = buffer.array();
		String msg = new String(data).trim();
		System.out.println("����˽��յ���Ϣ�ǣ�"+msg);
		ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
		clinChannel.write(outBuffer);//����Ϣ���ص��ͻ���
		return readRecd;
	}
	/**
	 * �� ��������������
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception {
		NIOServer server = new NIOServer();
		server.initServer(8000);
		server.listen();
	}

}
