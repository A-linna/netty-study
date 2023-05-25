<h2> Nio基础</h2>

<h3>1. 三大组件</h3>

nio编程包含三个组件 channel、buffer、selector
<ol>
<li>channel</li>
    channel是一个读写数据的双向通道，可以从channel将数据读到buffer，
也可以将buffer中的数据写到channel。
    常见的channel有：
    <ul>
        <li>FileChannel : 文件传输通道</li>
        <li>DatagramChannel : UDP传输通道</li>
        <li>SocketChannel ：TPC传输通道 </li>
        <li>ServerSocketChannel : TCP传输通道 专用与服务器端</li>
    </ul>
<li>buffer</li>
    buffer用来缓冲读写数据，常见的buffer有：
    <ul>
         <li>
            ByteBuffer
             <ul>
                  <li>MappedByteBuffer</li>
                  <li>DirectByteBuffer</li>
                 <li>HeapByteBuffer</li>
        	</ul>
        </li>
        <li>ShortBuffer</li>
        <li>IntBuffer</li>
        <li>LongBuffer</li>
        <li>FloatBuffer</li>
        <li>DoubleBuffer</li>
        <li>CharBuffer</li>
    </ul>
    <li>Selector</li>
    selector能够检测一到多个NIO通道，并能够知晓channel是否为诸如读写事件做好准备 的组件。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接
</ol>

<h2>2. ByteBuffer</h2>
<h3> 2.1 ByteBuffer使用方法</h3>
<ol>
<li>向buffer写入数据，例如调用channel.read(buffer) </li>
<li>调用flip()方法切换至读模式 </li>
<li>从buffer中读取数据，例如调用buffer.get()</li>
<li>调用clear()或compat()切换至写模式</li>
</ol>
当向buffer写入数据时，buffer会记录下写了多少数据。一旦要读取数据，需要通过flip()
方法将Buffer从写模式切换到读模式。在读模式下，可以读取之前写入到buffer的所有数据。

一旦读完了所有的数据，就需要清空缓冲区，让它可以再次被写入。有两种方式能清空缓冲区：调用clear()或compact()方法。clear()
方法会清空整个缓冲区。compact()方法只会清除已经读过的数据。任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读数据的后面。
<h3>2.2 ByteBuffer结构</h3>
<ul>
<li>capacity:</li>
作为一个内存块，Buffer有一个固定的大小值，也叫“capacity”.你只能往里写capacity个byte、long，char等类型。一旦Buffer满了，需要将其清空（通过读数据或者清除数据）才能继续写数据往里写数据
<li>position</li>
当你写数据到Buffer中时，position表示当前的位置。初始的position值为0.当一个byte、long等数据写到Buffer后，
position会向前移动到下一个可插入数据的Buffer单元。position最大可为capacity – 1.
<li>limit</li>
在写模式下，Buffer的limit表示你最多能往Buffer里写多少数据。 写模式下，limit等于Buffer的capacity。

当切换Buffer到读模式时，
limit表示你最多能读到多少数据。因此，当切换Buffer到读模式时，limit会被设置成写模式下的position值。换句话说，你能读到之前写入的所有数据（limit被设置成已写数据的数量，这个值在写模式下就是position）
</ul>

<h3>2.3 Buffer的分配</h3>
要想获得一个Buffer对象首先要进行分配。 每一个Buffer类都有一个allocate方法。下面是一个分配48字节capacity的ByteBuffer的例子
```
    ByteBuffer buf = ByteBuffer.allocate(48);
```
<h3>2.4 向Buffer中写数据</h3>

写数据到Buffer有两种方式：
<ul>
<li> 从Channel写到Buffer。 channel.read(buf)</li>
<li>通过Buffer的put()方法写到Buffer里。 buf.put(127);</li>
<ul>

<h4>flip()方法</h4>
flip方法将Buffer从写模式切换到读模式。调用flip()方法会将position设回0，并将limit设置成之前position的值。
换句话说，position现在用于标记读的位置，limit表示之前写进了多少个byte、char等 —— 现在能读取多少个byte、char等。

<h3>从Buffer中读取数据 </h3>
==
从Buffer读取数据到Channel。
使用get()方法从Buffer中读取数据。

<h4>rewind()方法</h4>
==
Buffer.rewind()将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变，仍然表示能从Buffer中读取多少个元素（byte、char等）

<h4>clear()与compact()方法</h4>
==
一旦读完Buffer中的数据，需要让Buffer准备好再次被写入。可以通过clear()或compact()方法来完成。

如果调用的是clear()方法，position将被设回0，limit被设置成 capacity的值。换句话说，Buffer
被清空了。Buffer中的数据并未清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。

如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。

如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先先写些数据，那么使用compact()方法。

compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()
方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。

<h4>mark()与reset()方法</h4>
通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。

<h3>3. channel网络编程 </h3>

<h4>3.1 阻塞模式下服务器代码</h4>
```
ByteBuffer buffer = ByteBuffer.allocate(512);
        //1 获取一个ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //2 绑定本机端口
        ssc.bind(new InetSocketAddress("localhost", 8888));
       List<SocketChannel> scList = new ArrayList<>();
        while (true) {
            //3监听客户端连接，若没有连接则会阻塞
            log.info("connecting--------");
            SocketChannel sc = ssc.accept(); //没有连接时  线程会在此阻塞
            log.info("connected---------:{}",sc);
            scList.add(sc);
            for (SocketChannel socketChannel : scList) {
                log.info("before read-----");
                socketChannel.read(buffer);
                buffer.flip();
                log.info("read data:{}", StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
                log.info("read after=======");
            }
        }


```
- ServerSocketChannel.accept() 会一直阻塞 直到有客户端连接进来
- ServerChannel.read() 方法也会一直阻塞，直到读取到数据

#### 非阻塞修改

```
    //设置为非阻塞，调用accept方法时 不在阻塞，没有连接 返回null
    serverSocketChannel.configureBlocking(false)
    SocketChannel sc= serverSocketChannel.accept()

    //socketChannel 设置非阻塞，read方法 没读取到数据 返回0
    socketChannel.configureBlocking(false)
    int len=  socketChannel.read()
```
问题点：线程一直在循环，当没有客户端连接以及写数据的时候，线程也在占用cpu资源。

###4.selector
多路复用：
    单线程配合selector完成对多个channel可读写事件的监控，称之为多路复用

- 	多路复用仅针对网络io，文件io没法使用
-	如果不使用selector的非阻塞模式，线程大部分时间都在做无用功。而selector能保证：
    1.有可连接事件才去连接
	2.有可读事件才去读取
 	3.有可写事件才去写入，限于网络传输能力，channel未必时时可写，一旦channel可写 会触发channel的可写事件
```java
    ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", 8888));
        //注册selector 需要设置为非阻塞
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT, null);
        while (true) {
            //在没有事件时，该方法会阻塞，
            //select 在事件未处理时，它不会阻塞，事件发生后 要么处理 要么取消
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                log.info("key:{}", selectionKey);
                //连接事件
                if (selectionKey.isAcceptable()) {
                    //读事件的channel只会是ServerSocketChannel
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel sc = channel.accept();
                    //注册selector 需要设置为非阻塞
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ, null);
                } else if (selectionKey.isReadable()) {
                    try {
                        SocketChannel sc = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(124);
                        //如果为-1 表示客户端断开连接
                        int read = sc.read(buffer);
                        if (read == -1) {
                            selectionKey.cancel();
                        }
                        buffer.flip();
                        log.info("readMessage:{}", StandardCharsets.UTF_8.decode(buffer));
                    } catch (IOException e) {
                        log.error("e:", e);
                        selectionKey.cancel();
                    }
                }
                iterator.remove();
            }
        }
    }
```
 需要将channel注册到selector上，返回一个selectionKey，设置这个key关心的事件。
 selector会维护selectionKey的set集合。每次处理完事件，selector不会主动删除，处理完事件后 需要主动删除。
 客户端主动或中断 导致断开连接的 服务端会收到一个read事件，主动断开连接的 read放读取到的字节数为-1. 需要调用canal来取消事件

### 文件零拷贝
```java
	RandomAccessFile file=new RandomAccessFile(new File("/home/xxx.txt"),"r");
	byte[] buf=new byte[(int)file.length];
    Socket socket=.....;
    socket.getOutputStream().write(buf);
```
内部工作流程：
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/fileCopy.png?raw=true)

1. java本身不具备IO读写能力，因此read方法调用后，要从java程序的==**用户态**==切换到==**内核态**==，去调用操作系统的读写能力，将数据读入内核缓冲区。这期间用户线程阻塞，操作系统使用DMA(Direct Memory Access)来实现文件读写，期间也不会使用CPU
	`DMA可以理解为硬件单元，用来解放CPU完成文件IO`
2. 从==**内核态**==切换到==**用户态**==，将数据从==**内核缓冲区**==读取到==**用户缓冲区**==(即byte[]buf),这期间cpu会参与拷贝，无法利用DMA
3. 调用write方法，这是将数据从==**用户缓冲区**==(即byte[]buf)写入==**Socket缓冲区**==，cpu会参与拷贝
4. 接下来要向网卡写数据，这项能力java也不具备，因此又要从用户态切换到内核态，调用操作系统的写能力，使用DMA将socket缓冲区的数据写入网卡，不会使用CPU

java的IO实际不是物理设备级别的读写,而是缓存的复制，底层真正的读写是操作系统来完成的
- 	用户态与内核态的切换发生了3次，这个操作比较重量级
- 	数据拷贝了4次

##### NIO优化
通过DirectByteBuf
- 	ByteBuffer.allocate(10)  HeapByteBuffer 使用的还是java的内存
- 	ByteBuffer.allocateDirect(10)  DirectByteBuff 使用的是操作系统的内存
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/WX20230525-111215@2x.png?raw=true)

java使用directByteBuf 将堆外内存映射到jvm内存来直接访问
-  这块内存不收jvm垃圾回收影响，因此内存地址固定，有助于IO读写
-  java中的directByteBuffer对象仅维护了内存的虚引用，内存回收分成2部
	1. directByteBuffer对象被垃圾回收，将虚引用加入引用队列
	2. 通过专门线程访问引用队列，根据虚引用释放堆外内存
-	减少了一次数据拷贝，用户态与内核态切换次数没有减少

进一步优化(底层采用lunix2.1后提供的方法sendFile) java对应着2个channel的调用transferTO/transferFrom方法拷贝数据。
![avatar][transferTo/transferFrom]

1. java调用transferTo方法后，要从java态切换到内核态，使用DMA将数据读入内核缓冲区，不会使用CPU
2. 数据从内核缓冲区传输到socket缓冲区，cpu会参与拷贝
3. 最后使用DMA将socket缓冲区的数据写入网卡，不会使用CPU

可以看到：
-	只发生了一次用户态与内核态的切换
-	数据拷贝了3次

进一步优化(lunix2.4)
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/transfer_2.png?raw=true)
1.	java调用transferTo方法后，要从java态切换到内核态，使用DMA将数据读取到内核缓冲区，不会使用CPU
2.	只会将一些offset和length信息拷贝到socket缓冲区，几乎无消耗
3.	使用DMA将内核缓冲区的数据写入网卡，不会使用cpu

整个过程仅只发生了一次用户态到内核态的切换，数据拷贝了2次。零拷贝并不是指无拷贝，而是不会拷贝重复数据到jvm内存中
零拷贝的优点：
- 更少的用户态与内核态切换
- 不利用cpu计算，减少cpu缓存伪共享
- 零拷贝适合小文件传输




