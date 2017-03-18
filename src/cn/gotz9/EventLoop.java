package cn.gotz9;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventLoop implements Closeable, Runnable {

    Selector selector;

    ExecutorService loop = Executors.newSingleThreadExecutor();

    public void run() {
        try {
            selector = Selector.open();
            while (true) {
//                System.out.println("eventloop number:" + selector.keys().size());
                selector.select(50);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                boolean exit = false;
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isValid() && key.isReadable()) {
                        ConnectionWrapper attachment = (ConnectionWrapper) key.attachment();
                        attachment.doRead();
                    }

                    if (key.isValid() && key.isWritable()) {
                        ConnectionWrapper attachment = (ConnectionWrapper) key.attachment();
                        attachment.doWrite();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void start() {
        loop.execute(this);
    }

    @Override
    public void close() throws IOException {
        if (!loop.isTerminated())
            loop.shutdownNow();
        if (selector != null) {
            selector.close();
        }
    }

    public void register(SocketChannel socket) {
        if (selector != null && !selector.isOpen())
            throw new IllegalArgumentException();
        try {
            socket.configureBlocking(false);
            SelectionKey key = socket.register(selector, 0);
            ConnectionWrapper wrapper = new ConnectionWrapper(socket, key);
            key.attach(wrapper);
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
