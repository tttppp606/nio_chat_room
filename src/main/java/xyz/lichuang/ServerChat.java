package xyz.lichuang;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by tttppp606 on 2019/5/8.
 */
@Slf4j
public class ServerChat {
	/**
	 * 启动
	 * @throws IOException
	 */
	public void start() throws IOException {
		/**
		 * 1.创建Selector
		 */
		Selector selector = Selector.open();
		/**
		 * 2.创建服务端serverSocketChannel，绑定端口
		 */
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(8000));
		log.info("创建serverSocketChannel成功,并绑定8000端口");
//		System.out.println("创建serverSocketChannel成功,并绑定8000端口");
		/**
		 * 3.serverSocketChannel设置为非阻塞模式
		 */
		serverSocketChannel.configureBlocking(false);
		/**
		 * 3.serverSocketChannel注册到Selector上，并且指定Selector监听指定事件
		 */
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		/**
		 * 4.循环检查监听事件是否就绪
		 */
		log.info("开始循环检测事件");
//		System.out.println("开始循环检测事件");
		for (;;) {//相当于while(true)
			/**
			 * 判断监听事件是否就绪
			 */
			int selectNum = selector.select();
			/**
			 * 没有就绪，循环监听
			 */
			if (selectNum == 0) continue;
			/**
			 * 存在就绪事件，获取事件set集合
			 */
			Set<SelectionKey> selectionKeys = selector.selectedKeys();

			Iterator<SelectionKey> iterator = selectionKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				/**
				 * 移除Set中当前的selectionKey
				 */
				iterator.remove();
				/**
				 * 可连接事件就绪,处理
				 */
				if (selectionKey.isAcceptable()) {
					acceptHandler(serverSocketChannel,selector);
				}
				/**
				 * 可读事件就绪，处理
				 */
				if (selectionKey.isReadable()) {
					readHandler(selectionKey,selector);
				}
			}
		}
	}

	/**
	 * 1、连接事件处理器
	 */
	private void acceptHandler(ServerSocketChannel serverSocketChannel , Selector selector) throws IOException {
		/**
		 * 若是连接事件，再创建一个与客户端连接的serverChannel
		 */
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		/**
		 * 将通道serverChannel注册到Selector上，监听接下来的可读事件
		 */
		socketChannel.register(selector,SelectionKey.OP_READ);
		/**
		 * 响应客户端
		 */
		//获取名字为UTF-8的Charset对象
		Charset charset = Charset.forName("UTF-8");
		//用charset指定解码方式，将字符串编译为字节byteufferB
		ByteBuffer byteBuffer = charset.encode("已连接聊天室");
		//往通道里写进byteBuffer的内容
		socketChannel.write(byteBuffer);
	}

	/**
	 * 2、可读事件处理器
	 */
	private void readHandler(SelectionKey selectionKey,Selector selector) throws IOException {
		/**
		 * 从SelectionKey中获取已经处于就绪状态的SocketChannel
		 */
		SocketChannel channel = (SocketChannel) selectionKey.channel();

		/**
		 * 创建ByteBuffer，接受channel中的数据
		 */
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		/**
		 * 将数据读取并拼接到request中
		 */
		String request = "";
		if (channel.read(buffer) > 0){
			buffer.flip();
			CharBuffer charbuffer = Charset.forName("UTF-8").decode(buffer);
			request += charbuffer;
		}

		/**
		 * 将channel重新注册到Selector上，监视可读状态
		 */
        channel.register(selector,SelectionKey.OP_READ);

		/**
		 * 将接受到客户端的数据，广播发送给其他客户端，实现聊天室的功能
		 */
        //TODO **
		System.out.println("" + request);
	}

	public static void main(String[] args) throws IOException {
		ServerChat serverChat = new ServerChat();
		serverChat.start();
	}

}