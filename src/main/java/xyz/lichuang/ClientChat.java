package xyz.lichuang;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by tttppp606 on 2019/5/12.
 */
public class ClientChat {
	private void start() throws IOException {
		/**
		 * 创建一个SocketChannel
		 */
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));

        /**
		 * 接受控制台的数据，放入socketChannel中
		 */
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()){
			String request = scanner.nextLine();
			if (StringUtils.isNotBlank(request)){
				socketChannel.write(Charset.forName("UTF-8").encode(request));
			}
		}

	}

	public static void main(String[] args) throws IOException {
		ClientChat clientChat = new ClientChat();
		clientChat.start();
	}
}
