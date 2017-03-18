package cn.gotz9;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Acceptor {

    EventLoop eventLoop;

    public static void main(String[] args) throws IOException {
        new Acceptor().run();
    }


    public void run() throws IOException {
        Selector selector = Selector.open();
        eventLoop = new EventLoop();
        eventLoop.start();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.keys().size() > 0) {
            System.out.println("keys number:" + selector.keys().size());
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            boolean exit = false;
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isValid() && key.isAcceptable()) {
                    SocketChannel socket = ((ServerSocketChannel) key.channel()).accept();
                    socket.configureBlocking(false);
                    eventLoop.register(socket);
                }
            }
        }
    }
}
