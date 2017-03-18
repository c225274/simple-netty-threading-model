# 模仿Netty的NIO实现
这个Demo旨在利用Java原生API模仿Netty的NIO线程模型的设计思想, 以及相关的类架构. 目前只有Acceptor, EventLoop的简单部分, 但是大体结构已经很清晰了.
## 结构讲解
### 概述
___Acceptor___是用于接收客户连接请求的线程. 在接收到客户端连接后, 将连接注册到**单线程**EventLoop的selector中. 然后在EventLoop中对于各种异步IO事件进行响应. 响应行为由EventLoop委托ConnectionWrapper执行.
对应关系:

| Netty | Demo |
| ----- | ---- |
| ___EventLoop___ | ___EventLoop___ |
| (io.netty.channel)___Channel___ | ___ConnectionWrapper___ |
| ___AbstractUnsafe.outboundBuffer___ | ___ConnectionWrapper.outbound___ |

### EventLoop
每个EventLoop都是单独的一个线程, 在这个线程中, EventLoop负责用Selector轮询可响应的IO事件. Acceptor获取到的客户端连接都由EventLoop管理, 并且一个连接只注册一个EventLoop. 这样这条连接的所有IO事件都会由注册的EventLoop单线程进行处理, 实现了每条连接的IO串行化, 一定程度上减小了编程难度.
### ConnectionWrapper
连接包装器将IO操作屏蔽, 便于在EventLoop中调用. 主要是实现read, write等操作. 用专门的容器存储读入或要写出的数据, 在适当的时机进行操作.
### inbound & outbound
读入或写出数据的缓存容器, 这里是简单用List存储ByteBuffer. 在Netty中, 借助Netty的ByteBuf, 会有更高效的实现.
