package cn.gotz9;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConnectionWrapper {

    private static int MAX_READ = 6;
    private static int MAX_WRITE = 8;

    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    private boolean closed = true;

    private List<ByteBuffer> inbound = new LinkedList<>();

    private List<ByteBuffer> outbound = new LinkedList<>();

    public ConnectionWrapper(SocketChannel channel, SelectionKey key) {
        if (channel != key.channel() || !key.isValid())
            throw new IllegalArgumentException();
        selectionKey = key;
        socketChannel = channel;

    }

    public void doRead() throws IOException {
        if (!selectionKey.isValid())
            throw new IllegalArgumentException();

        for (int i = 0; i < MAX_READ; i++) {
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            int read = socketChannel.read(allocate);
            if (read <= 0) {
                break;
            }
            allocate.limit(allocate.position());
            allocate.rewind();
            inbound.add(allocate);
        }
        outbound.add(ByteBuffer.wrap("read finish".getBytes()));
        if (outbound.size() > 6) {
            selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
        }
    }

    public void doWrite() throws IOException {
        if (!selectionKey.isValid())
            throw new IllegalArgumentException();

        int size = outbound.size();
        Iterator<ByteBuffer> iterator = outbound.iterator();
        boolean writeable = true;
        boolean writefinish = true;
        for (; size > 0 && iterator.hasNext(); size--) {
            ByteBuffer buffer = iterator.next();
            int write = 0;
            if (buffer.remaining() > 0) {
                write = socketChannel.write(buffer);
                if (write <= 0) {
                    writeable = false;
                    writefinish = false;
                    break;
                }
            }
        }
        if (size > 0 && !writefinish) {
            iterator.remove();
            selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
        } else //
            selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
    }

}
