package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * NIO�ͻ���
 * @author GYK
 *
 */

public class NIOClientDemo {
	//����ͨ��������
	private Selector selector;
	//���һ��Socketͨ�������Ը�ͨ������һЩ��ʼ������������IP��ַ���˿ںţ�
	public void initClient(String ip,int port) throws IOException {
		//���һ��socketͨ��
		SocketChannel channel = SocketChannel.open();
		//����ͨ��Ϊ��������ʽ
		channel.configureBlocking(false);
		//�õ�һ��ͨ��������
		this.selector = Selector.open();
		//�ͻ������ӷ��������������첽��ʽ�������
		channel.connect(new InetSocketAddress(ip,port));
		//��ͨ�����������ͨ���󶨣���Ϊ��ͨ��ע��SelectionKey.OP_CONNECT�¼���
		channel.register(selector, SelectionKey.OP_CONNECT);
		 //��������Ը�������������Ϣ
		 //����������ӣ����������
		 while(channel.isConnectionPending()) {
			 //�����첽���������¼�
			 System.out.println("��������");
			 if(channel.finishConnect())  {
				 System.out.println("�������");
			 }
		 }
		String data = new String("������������һ����Ϣfeafwfwafsefawafawfewafwafwafwa");
		 ByteBuffer writeBuffer = ByteBuffer.wrap(data.getBytes());
		 while(writeBuffer.hasRemaining()) {
			 System.out.println("����д������");
			 channel.write(writeBuffer);
		 }
			 System.out.println("�ͻ��˹ر�");
			 channel.close();
		 
	}
	 /**
	  * �����ͻ��˽��в���
	  * @param args
	 * @throws Exception 
	  */
	public static void main(String[] args) throws Exception {
		NIOClient client = new NIOClient();
		client.initClient("localhost", 8000);
	}
}






















