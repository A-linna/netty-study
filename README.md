## 一 Nio基础

### 1. 三大组件

nio编程包含三个组件 channel、buffer、selector
1. channel

channel是一个读写数据的双向通道，可以从channel将数据读到buffer,也可以将buffer中的数据写到channel。常见的channel有
- FileChannel : 文件传输通道
- DatagramChannel : UDP传输通道
- SocketChannel ：TPC传输通道
- ServerSocketChannel : TCP传输通道 专用与服务器端

2. buffer

   buffer用来缓冲读写数据，常见的buffer有：
- ByteBuffer
	- MappedByteBuffer
	- DirectByteBuffer
	- HeapByteBuffer
- ShortBuffer
- IntBuffer
- LongBuffer
- FloatBuffer
- DoubleBuffer
- CharBuffer

3. Selector
   selector能够检测一到多个NIO通道，并能够知晓channel是否为诸如读写事件做好准备 的组件。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接

_ _ _


### 2. ByteBuffer
#### 2.1 ByteBuffer使用方法
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
####2.4 向Buffer中写数据

写数据到Buffer有两种方式：
<ul>
<li> 从Channel写到Buffer。 channel.read(buf)</li>
<li>通过Buffer的put()方法写到Buffer里。 buf.put(127);</li>
<ul>

<h4>flip()方法</h4>
flip方法将Buffer从写模式切换到读模式。调用flip()方法会将position设回0，并将limit设置成之前position的值。
换句话说，position现在用于标记读的位置，limit表示之前写进了多少个byte、char等 —— 现在能读取多少个byte、char等。

####从Buffer中读取数据
从Buffer读取数据到Channel。
使用get()方法从Buffer中读取数据。

####rewind()方法
Buffer.rewind()将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变，仍然表示能从Buffer中读取多少个元素（byte、char等）

####clear()与compact()方法
一旦读完Buffer中的数据，需要让Buffer准备好再次被写入。可以通过clear()或compact()方法来完成。
如果调用的是clear()方法，position将被设回0，limit被设置成 capacity的值。换句话说，Buffer
被清空了。Buffer中的数据并未清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。
如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。
如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先先写些数据，那么使用compact()方法。
compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()
方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。
####mark()与reset()方法
通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。

- - -


### 3. channel网络编程

#### 3.1 阻塞模式下服务器代码
```
		ByteBuffer buffer = ByteBuffer.allocate(512);
        //1 获取一个ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //2 绑定本机端口
        ssc.bind(new InetSocketAddress("localhost", 8888));
       List<SocketChannel> scList = new ArrayList<SocketChannel>();
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


- - -

### 4.selector
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

- - -


### 5.文件零拷贝
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
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/transfer_1.png?raw=true)

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


* * *
## 二 netty入门
### 1概述：
#### 1.1netty是什么？
  	netty是一个异步的，基于事件驱动的网络框架，用于快速开发可维护，高性能的网络服务器和客户端
#### 2.入门案例：
server:
```java
  //1.启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2.BoosEventLoop,WorkEventLoop(selector,thread) group 组
                .group(new NioEventLoopGroup())
                //3.选择ServerSocketChannel的实现
                .channel(NioServerSocketChannel.class)
                //4.boos 负责连接，work(child)负责读写，决定了word(child)能执行哪些操作(handler)
                .childHandler(
                        //5.channel代表和客户端进行数据读写的通道，Initializer初始化，负责添加别的handler
                        //连接建立后会执行initChannel方法
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new StringDecoder());//将byteBuf转换为字符串
                                socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {//自定义handler
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("message:{}", msg);
                                    }
                                });
                            }
                            //6 绑定端口
                        }).bind(9999);

```
client:
```java
 //1启动类
        new Bootstrap()
                //添加eventLoop
                .group(new NioEventLoopGroup())
                //选择客户端channel实现
                .channel(NioSocketChannel.class)
                //添加处理器
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    //在连接建立后调用
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8888))
                .sync()//阻塞方法 直到连接建立
                .channel()//代表连接对象
                .writeAndFlush("hello");

```
-	channel可以理解为数据通道，可以从channel读取数据 或写入channel
-	msg 可以理解为流动的数据，最开始是输入的byteBuf，经过pipeline的加工，会变成其他类型对象，最后输出又变成byteBuf
-	handler 可以理解为数据的处理工序
	 -	工序有多道，合在一起就是pipeline，pipeline负责发布事件传播给每个handler，handler对自己感兴趣的事件进行处理(重写相应事件处理方法)
	 - handler分为Inbound和outBound两类
- 把eventLoop理解为处理数据的工人
	- 工人可以管理多个channel的io操作，并且一旦工人负责了某个channel就要负责到底(绑定)
	- 工人既可执行io操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个channel的待处理任务，任务分为普通任务、定时任务
	- 工人按照pipeline顺序，依次按照handler的规划(代码)处理数据，可以为每道工序指定不同的工人


### 3 组件
#### 3.1 EventLoop
EventLoop本质是一个单线程执行器 (同时维护了一个 Selector)，里面有 run 方法处理 Channel 上源源不断的io 事件。
它的继承关系比较复杂
- 一条线是继承自j.u.c.ScheduledExecutorService 因此包含了线程池中所有的方法。
- 另一条线是继承自 netty 自己的 OrderedEventExecutor提供了 boolean inEventLoop(Thread thread)方法判断一个线程是否属于此 EventLoopo 提供了 parent 方法来看看自己属于哪个 EventLoopGroup

EventLoopGroup 是一组EventLoop，Channel一般会调用EventLoopGroup 的register 方法来绑定其中一个EventLoop，后续这个 Channel上的 io 事件都由此EventLoop 来处理(保证了 io  事件处理时的线程安全)
-	继承自 netty 自己的 EventExecutorGroup
	 - 实现了 Iterable 接口提供遍历 EventLoop 的能力
	 - 另有 next 方法获取集合中下一个 EventLoop

#### 3.2 channel
**channel** 的主要作用
-  close0可以用来关闭 channel。
-  closeFuture0) 用来处理 channel的关闭
	-   sync 方法作用是同步等待 channel关闭
	-   而 addListener 方法是异步等待 channel 关闭
-  pipeline0)方法添加处理器
-  write0方法将数据写入
-  writeAndFlush0方法将数据写入并刷出

**channelFeature**
```
//2
 ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //1
                .connect(new InetSocketAddress("localhost", 9999));
                //2.1
       			 channelFuture.sync()
                .channel()
                .writeAndFlush("hello");
                //
              //2.2
               channelFuture.addListener(new ChannelFutureListener() {
            	@Override
           		 public void operationComplete(ChannelFuture future) throws Exception {
             	   Channel channel = future.channel();
                	channel.writeAndFlush("hello");
            }
        });

    }
```
1 connect连接是一个异步非阻塞方法，main线程发起调用，真正执行连接的是nioEventGroup中的线程

2 带有feature、promise的类型都是和异步方法配套使用，用来处理结果

2.1 chanenlFeature.sync(),此方法会阻塞住当前线程，直到nio线程连接建立完毕 能才继续执行。

2.2  channelFuture.addListener 这个方法是提供一个回调方法，当nio线程连接建立完毕后，会执行该listner里的逻辑。

#### 3.3 Feature 、 Promis

首先要说明 netty 中的 Future 与jdk 中的 Future 同名，但是是两个接口，netty的 Future 继承自jdk的Future，而 Promise 又对 netty Future 进行了扩展
-  jdk Future 只能同步等待任务结束 (或成功、或失败) 才能得到结果
-  netty Future 可以同步等待任务结束得到结果，也可以异步方式得到结果，但都是要等任务结束
-  netty Promise 不仅有 netty Future.的功能，而且脱离了任务独立存在，只作为两个线程间传递结果的容器

jdk Feature 常用api
-	canal 取消任务
-	isCanaled 任务是否取消
-	idDone 任务是否完成，无法区分成功or失败
-	get 获取任务结果，阻塞等待

netty Feature 部分APi

-	getNow 获取任务结果，非阻塞，还未产生结果时返回 null
-	await 等待任务结束，如果任务失败，不会抛异常，而是通过 isSuccess 判断
-	sync 等待任务结束，如果任务失败，抛出异常
-	isSuccess  判断任务是否成功
-	cause 获取失败信息，非阻塞，如果没有失败，返回null
-	addLinstener 添加回调，.导步接收结果

Promise Api
-  setSuccess 设置成功结果
-  setFailure 设置失败结果

#### 3.4 Handler 、Pipeline
ChannelHandler 用来处理Channel 上的各种事件，分为入站、出站两种。所有ChannelHandler 被连成一串,就是 Pipeline

-  入站处理器通常是 ChannelInboundHandlerAdapter的子类，主要用来读取客户端数据，写回结果
-  出站处理器通常是 ChannelOutboundHandlerAdapter 的子类，主要对写回结果进行加工

打个比喻，每个 Channel 是一个产品的加工车间，Pipeline 是车间中的流水线，ChannelHandler 就是流水线上的各道工序，而后面要讲的 BvteBuf 是原材料，经过很多工序的加工: 先经过一道道入站工序，再经过一道道出站工序最终变成产品

入站和出站是相对而言的，对于服务器来说，从客户端发来的数据叫入站，会依次执行Pipeline中head到tail的 inBoundHandler，当服务器向客户端写数据时，会依次执行tail到head的outBoundHandler

ChannelHandlerContext.writeAndFlush()与socketChannel.writeAndFlush的区别是：  
ChannelHandlerContext会从当前handler往head依次寻找outBoundHandler，而socketChannel的会从tail往head依次找outBoundHandler执行

#### 3.5 ByteBuf
是对字节数据的封装
##### 1 创建
`ByteBuf buffer = ByteBufA11ocator .DEFAULT.buffer(10);`
创建了一个默认的 ByteBuf (池化基于直接内存的 ByteBuf)，初始容量是 10
```
PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 10)
//初始容量为10，读写指针为0

```
##### 2 直接内存、堆内存
创建池化基于堆的ByteBuf
` ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();`
创建池化基于直接内存的ByteBuf
`ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer();`
-  直接内存创建和销毁的代价昂贵，但读写性能高(少一次内存复制)，适合配合池化功能一起用
-  直接内存对 GC 压力小，因为这部分内存不受JVM 垃圾回收的管理，但也要注意及时主动释放

##### 3 池化 VS 非池化
池化的最大意义在于可以重用 ByteBuf，优点有
-  没有池化，则每次都得创建新的 ByteBuf 实例，这个操作对直接内存代价昂贵，就算是堆内存，也会增加 GC压力
-  有了池化，则可以重用池中 ByteBuf 实例，并且采用了与 jemalloc 类似的内存分配算法提升分配效率
-  高并发时，池化功能更节约内存，减少内存溢出的可能  
  
  
池化功能是否开启，可以通过下面的系统环境变量来设置
`-Dio.netty.allocator .type={unpooledlpooled}`
-  4.1以后，非 Android 平台默认启用池化实现，Android 平台启用非池化实现
-  4.1 之前，池化功能还不成熟，默认是非池化实现

##### 4 组成
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/byteBuf.png?raw=true)

1. capacity byteByf(容量) 可以容纳的字节数
``ByteBufAllocator.DEFAULT.buffer(10);//指定容量为10，可以动态扩容``
2. maxCapacity (最大容量) 容量与最大容量的区域 称为可扩容区域
``ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10,100);//最大容量为100``
3. 写指针,写指针到容量区域 称为 可写区域
4. 读指针，读指针到写指针的区域称为 可读区域，已读的部分称为废弃区域

##### 5 扩容
容量不够时 会触发扩容  
扩容规则：  
-  如果写入后数据大小未超过 512，则选择下一个 16 的整数倍，例如写入后大小为 12，则扩容后 capacity 是16
-  如果写入后数据大小超过 512，则选择下一个2^n，例如写入后大小为 513，则扩容后 capacity 是2^10=1024 (2^9=512 已经不够了)
-  扩容不能超过 max capacity 否则会报错

##### 6 retain、release
由于 Netty 中有堆外内存的 ByteBuf 实现，堆外内存最好是手动来释放，而不是等 GC 垃圾回收。
-  UnpooledHeapByteBuf 使用的是JVM 内存，只需等 GC 回收内存即可
-  UnpooledDirectByteBuf 使用的就是直接内存了，需要特殊的方法来回收内存
-  PooledBvteBuf 和它的子类使用了池化机制，需要更复杂的规则来回收内存
> 回收内存的源码实现，请关注下面方法的不同实现
> protected abstract void dea1locate()  

Netty 这里采用了引用计数法来控制回收内存，每个 ByteBuf都实现了 ReferenceCounted 接口
- 每个 ByteBuf 对象的初始计数为 1
- 调用 release 方法计数减 1，如果计数为 0，ByteBuf 内存被回收
- 调用 retain 方法计数加 1，表示调用者没用完之前，其它 handler 即使调用了 release 也不会造成回收
- 当计数为 0时，底层内存会被回收，这时即使 ByteBuf 对象还在，其各个方法均无法正常使用

因为 pipeline 的存在，一般需要将BvteBuf 传递给下一个ChannelHandler，如果在 finally中release了，就失去了传递性(当然，如果在这个 ChannelHandler 内这个ByteBuf 已完成了它的使命，那么便无须再传递)
基本规则是，谁是最后使用者，谁负责 release  

##### 7 slice
[零拷贝] 的体现之一，对原始 BvteBuf 进行切片成多个 ByteBuf，切片后的 ByteBuf 并没有发生内存复制，还是使用原始 ByteBuf 的内存，切片后的 ByteBuf 维护独立的 read，write 指针
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/slice.png?raw=true)
```
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
	buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
     //在切片过程中，没有发生数据复制
    ByteBuf buf1 = buf.slice(0, 5);
```
注意点：  
- 切面后获得的byteBuf无法增加容量
- 原始byteBuf调用release方法释放内存后，切片获得的byteBuf无法使用,若需要使用slice后的buf 可调用retain  

##### 8 duplicate
[零拷贝] 的体现之一，就好比截取了原始 ByteBuf所有内容，并且没有 max capacity 的限制，也是与原始BvteBuf 使用同一块底层内存，只是读写指针是独立的
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/duplicate.png?raw=true)

##### 9 copy
会将底层内存数据进行深拷贝，因此无论读写，都与原始 ByteBuf 无关

##### 10 composite
将多个byteBuf数据汇总到一个byteBuf中，且没有发生数据复制。
```
		ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        ByteBuf buf2= ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1,2, 3, 4, 5});
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        CompositeByteBuf byteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        byteBuf.addComponents(true, buf1, buf2);
```

##### ByteBuf的优势
-  池化-可以重用池中 ByteBuf 实例，更节约内存，减少内存溢出的可能
-  读写指针分离，不需要像 ByteBuffer 一样切换读写模式可以自动扩容
-  支持链式调用，使用更流畅
-  很多地方体现零拷贝，例如 slice、duplicate、CompositeByteBuf


* * *
## 三 netty进阶
### 粘包 拆包
#### 1 TCP协议
- TCP 以一个段 (segment) 为单位，每发送一个段就需要进行一次确认应答 (ack) 处理，但如果这么做，缺点是包的往返时间越长性能就越差
- 为了解决此问题，引入了窗口概念，窗口大小即决定了无需等待应答而可以继续发送的数据最大值
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/tpc%E6%BB%91%E5%8A%A8%E7%AA%97%E5%8F%A3.png?raw=true)

- 窗口实际就起到一个缓冲区的作用，同时也能起到流量控制的作用
- 当应答未到达前，窗口必须停止滑动
- 如果 1001~2000 这个段的数据 ack 回来了，窗口就可以向前滑动
- 接收方也会维护一个窗口，只有落在窗口内的数据才能允许接收  

本质原因是因为TCP是流式协议，消息无边界  

#### 2.协议的设计与解析
**redis协议：**
```java
package com.mikasa.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 规范格式
 * 1、间隔符号，在Linux下是\r\n，在Windows下是\n;
 * 2、简单字符串 Simple Strings, 以 "+"加号 开头;
 * 3、错误 Errors, 以"-"减号 开头;
 * 4、整数型 Integer， 以 ":" 冒号开头;
 * 5、大字符串类型 Bulk Strings, 以 "$"美元符号开头，长度限制512M;
 * 6、数组类型 Arrays，以 "*"星号开头。
 * @author aiLun
 * @date 2023/5/30-10:05
 */
public class TestRedis {
    /**
     *  set key value
     *  传输数组类型 先传数组个数 然后发送每个命令以及键值的长度,每个之间要用间隔符号分割
     *  *3 数组的个数
     *  $3
     *  set
     *  $key的长度
     *  key
     *  $value的长度
     *  value
     */

    public static void main(String[] args)  {
        final byte[] LINE = {13, 10}; //回车 + 换行
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(work)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 发送 set name zhangsan
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    buffer.writeBytes("*3".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$3".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("set".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$4".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("name".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$8".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("zhangsan".getBytes());
                                    buffer.writeBytes(LINE);
                                    ctx.writeAndFlush(buffer);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf)msg;
                                    System.out.println(buf.toString(StandardCharsets.UTF_8));
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 6379)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {

        }
    }
}

```  
**http协议**
```java
package com.mikasa.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author aiLun
 * @date 2023/5/30-10:36
 */
@Slf4j
public class TestHttp {
    public static void main(String[] args) {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup word = new NioEventLoopGroup();
        try {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boos, word)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        //添加http协议 编解码器
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<DefaultHttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest msg) throws Exception {
                                String uri = msg.uri();
                                log.info("uri:{}", uri);
                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                byte[] bytes = "<h1>hello word</h1>".getBytes();
                                response.content().writeBytes(bytes);
                                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                });

            ChannelFuture future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            boos.shutdownGracefully();
            word.shutdownGracefully();
        }
    }
}


```

##### 2.2 自定义协议要素
-  魔数，用来在第一时间判定是否是无效数据包
-  版本号，可以支持协议的升级
-  序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如: ison、protobuf、hessian、jdk
-  指令类型，是登录、注册、单聊、群聊...跟业务相关
-  请求序号，为了双工通信，提供异步能力
-  正文长度
-  消息正文  

```java
package com.mikasa.netty.protocol;

import com.mikasa.netty.protocol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author aiLun
 * @date 2023/5/30-15:25
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //1  4个字节的魔数
        out.writeBytes(new byte[]{'a', 'b', 'c', 'd'});
        // 2  1字节的协议版本号
        out.writeByte(1);
        //3  1字节的序列化算法  0 jdk序列化 1 json  2
        out.writeByte(0);
        //4  1字节的消息指令类型
        out.writeByte(msg.getMessageType());
        //5  4个字节的请求序号
        out.writeInt(msg.getSequenceId());

        //无意义的字节填充，保持2的整数倍
        out.writeByte(0xff);

        //6 将msg转换为byte[]
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] msgByte = bos.toByteArray();
        //7 4字节的 消息长度
        out.writeInt(msgByte.length);
        //8 写入内容

        out.writeBytes(msgByte);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializableType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        byte b = in.readByte();//无意义的填充字节
        int length = in.readInt(); //字节长度
        byte[] bytes = new byte[length];
         in.readBytes(bytes,0,length);
        //jdk序列化
        Message msg =null;
        if (0 == serializableType) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
             msg = (Message) ois.readObject();
        }
        log.info("magicNum:{},version:{},serializableType:{},messageType:{}," +
                "sequenceId:{},length:{}", magicNum, version, serializableType, messageType, sequenceId, length);
        log.info("message:{}",msg);
        out.add(msg);
    }
}

```  

##### 2.3 存活检测
`IdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds)`
- readerIdleTimeSeconds 读的空闲时间 当指定时间段内未执行读取时触发 IdleState#READER_IDLE 事件
- writerIdleTimeSeconds 写的空闲时间 当指定时间段内未执行写入时触发 IdleState#WRITER_IDLE
- allIdleTimeSeconds 读写空闲时间  当在指定的时间段内未执行读取或写入时 触发 IdleState#ALL_IDLE

实用：在服务端加入一个监听读空闲的handler，超时的断开连接，客户端加入一个写空闲的handler，当触发写空闲时 主动向服务器发送数据，注意点：客户端的空闲时间 要比服务器的短。

服务器： 
```
  //6秒内没收到channel的数据 会触发一个IdleState#READER_IDLE 事件
	ch.pipeline().addLast(new IdleStateHandler(6, 0, 0));
    ch.pipeline().addLast(new ChannelDuplexHandler() {
    //用来触发特殊事件
 	  @Override
      public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (IdleState.READER_IDLE == event.state()) {
            ctx.channel().close();
         }
  	  }
	 });

```

客户端：  
```
//3秒内channel没有写数据 会触发一个IdleState#WRITER_IDLE 事件
	ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
	ch.pipeline().addLast(new ChannelDuplexHandler() {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
       		 IdleStateEvent event = (IdleStateEvent) evt;
       		 if (IdleState.WRITER_IDLE == event.state()) {
       		     PingMessage pingMessage = new PingMessage();
         	     ctx.writeAndFlush(pingMessage);
        	}
        }
 	 });
```

## 4.优化
### 1.参数优化
##### 1.CONNECT_TIMEOUT_MILLIS
- 属于 SocketChannal参数
- 用在客户端建立连接时，如果在指定毫秒内无法连接，会抛出 timeout 异常
- SO TIMEOUT 主要用在阻塞IO，阻塞IO中accept,read等都是无限等待的,如果不希望永远阻塞,使用它调整超时时间
  
```java
	public static void main(String[] args) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(eventLoopGroup)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler());

            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8888)).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
           log.debug("timeout");
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

```
##### 2.SO_BACKLOG
-	属于serverSocketChannel参数
![](https://github.com/A-linna/netty-study/blob/main/src/main/resources/image/tcp.png?raw=true)
1. 第一次握手，cient发送SYN 到server,状态修改为 SYN_SEND，server 收到，状态改变为 SYN_REVD，并将该请求放入 sync queue 队列
2. 第二次握手，server 回复SYN +ACK给client，cient收到，状态改变为 ESTABLISHED，并发送ACK给server
3. 第三次握手，server 收到ACK，状态改变为 ESTABLISHED，将该请求从 sync queue 放入 accept queue

- Lunix2.2以后分别用一下2个参数来控制：
- sync queue - 半连接队列
	-  大小通过/proc/sys/net/ipv4/tcp_max_syn_backlog指定，在 syncookies 启用的情况下，逻辑上没有最大值限制，这个设置便被忽略
- accept queue-全连接队列
	- 其大小通过/proc/sys/net/core/somaxconn 指定，在使用listen 函数时，内核会根据传入的 backlog参数与系统参数，取二者的较小值
	- 如果 accpet queue 队列满了，server 将发送一个拒绝连接的错误信息到 client

netty 中可以通过 option(ChannelOption.SO_BACKLOG,值)来设置全连接队列大小

##### 3 ALLOCATOR
- 属于 SocketChannal 参数
- 用来分配 ByteBuf，ctx.alloc() 的byteBuf类型

