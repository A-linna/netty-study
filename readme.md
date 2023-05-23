<h2># Nio基础</h1>

<h2>1. 三大组件</h2>

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
当向buffer写入数据时，buffer会记录下写了多少数据。一旦要读取数据，需要通过flip()方法将Buffer从写模式切换到读模式。在读模式下，可以读取之前写入到buffer的所有数据。

一旦读完了所有的数据，就需要清空缓冲区，让它可以再次被写入。有两种方式能清空缓冲区：调用clear()或compact()方法。clear()方法会清空整个缓冲区。compact()方法只会清除已经读过的数据。任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读数据的后面。
 <h3>2.2 ByteBuffer结构</h3>
    <ul>
        <li>capacity:</li>
作为一个内存块，Buffer有一个固定的大小值，也叫“capacity”.你只能往里写capacity个byte、long，char等类型。一旦Buffer满了，需要将其清空（通过读数据或者清除数据）才能继续写数据往里写数据
        <li>position</li>
当你写数据到Buffer中时，position表示当前的位置。初始的position值为0.当一个byte、long等数据写到Buffer后， position会向前移动到下一个可插入数据的Buffer单元。position最大可为capacity – 1.
        <li>limit</li>
在写模式下，Buffer的limit表示你最多能往Buffer里写多少数据。 写模式下，limit等于Buffer的capacity。

当切换Buffer到读模式时， limit表示你最多能读到多少数据。因此，当切换Buffer到读模式时，limit会被设置成写模式下的position值。换句话说，你能读到之前写入的所有数据（limit被设置成已写数据的数量，这个值在写模式下就是position）
    </ul>

<h3>2.3 Buffer的分配</h3>
要想获得一个Buffer对象首先要进行分配。 每一个Buffer类都有一个allocate方法。下面是一个分配48字节capacity的ByteBuffer的例子
   
 ```
    ByteBuffer buf = ByteBuffer.allocate(48);
```

<h3>2.4 向Buffer中写数据</h3>

  写数据到Buffer有两种方式：
    <ul>
    <li> 从Channel写到Buffer。  channel.read(buf)</li>
    <li>通过Buffer的put()方法写到Buffer里。 buf.put(127);</li>
    <ul>
####    
  
<h4>flip()方法:</h4>  

flip方法将Buffer从写模式切换到读模式。调用flip()方法会将position设回0，并将limit设置成之前position的值。

换句话说，position现在用于标记读的位置，limit表示之前写进了多少个byte、char等 —— 现在能读取多少个byte、char等。

<h3>从Buffer中读取数据 </h3>
从Buffer读取数据到Channel。    
使用get()方法从Buffer中读取数据。

<h4>rewind()方法</h4>  
Buffer.rewind()将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变，仍然表示能从Buffer中读取多少个元素（byte、char等）

<h4>clear()与compact()方法</h4>     
一旦读完Buffer中的数据，需要让Buffer准备好再次被写入。可以通过clear()或compact()方法来完成。

如果调用的是clear()方法，position将被设回0，limit被设置成 capacity的值。换句话说，Buffer 被清空了。Buffer中的数据并未清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。

如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。

如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先先写些数据，那么使用compact()方法。

compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。

<h4>mark()与reset()方法</h4> 
通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。
