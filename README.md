# ģ��Netty��NIOʵ��
���Demoּ������Javaԭ��APIģ��Netty��NIO�߳�ģ�͵����˼��, �Լ���ص���ܹ�. Ŀǰֻ��Acceptor, EventLoop�ļ򵥲���, ���Ǵ���ṹ�Ѿ���������.
## �ṹ����
### ����
___Acceptor___�����ڽ��տͻ�����������߳�. �ڽ��յ��ͻ������Ӻ�, ������ע�ᵽ**���߳�**EventLoop��selector��. Ȼ����EventLoop�ж��ڸ����첽IO�¼�������Ӧ. ��Ӧ��Ϊ��EventLoopί��ConnectionWrapperִ��.
��Ӧ��ϵ
| Netty | Demo |
| ----- | ---- |
| ___EventLoop___ | ___EventLoop___ |
| (io.netty.channel)___Channel___ | ___ConnectionWrapper___ |
| ___AbstractUnsafe.outboundBuffer___ | ___ConnectionWrapper.outbound___ |
### EventLoop
ÿ��EventLoop���ǵ�����һ���߳�, ������߳���, EventLoop������Selector��ѯ����Ӧ��IO�¼�. Acceptor��ȡ���Ŀͻ������Ӷ���EventLoop����, ����һ������ֻע��һ��EventLoop. �����������ӵ�����IO�¼�������ע���EventLoop���߳̽��д���, ʵ����ÿ�����ӵ�IO���л�, һ���̶��ϼ�С�˱���Ѷ�.
### ConnectionWrapper
���Ӱ�װ����IO��������, ������EventLoop�е���. ��Ҫ��ʵ��read, write�Ȳ���. ��ר�ŵ������洢�����Ҫд��������, ���ʵ���ʱ�����в���.
### inbound & outbound
�����д�����ݵĻ�������, �����Ǽ���List�洢ByteBuffer. ��Netty��, ����Netty��ByteBuf, ���и���Ч��ʵ��.