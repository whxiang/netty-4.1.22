/*
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.string;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Apply a line separator to the requested {@link String} and encode it into a {@link ByteBuf}.
 * A typical setup for a text-based line protocol in a TCP/IP socket would be:
 * <pre>
 * {@link ChannelPipeline} pipeline = ...;
 *
 * // Decoders
 * pipeline.addLast("frameDecoder", new {@link LineBasedFrameDecoder}(80));
 * pipeline.addLast("stringDecoder", new {@link StringDecoder}(CharsetUtil.UTF_8));
 *
 * // Encoder
 * pipeline.addLast("lineEncoder", new {@link LineEncoder}(LineSeparator.UNIX, CharsetUtil.UTF_8));
 * </pre>
 * and then you can use a {@link String} instead of a {@link ByteBuf}
 * as a message:
 * <pre>
 * void channelRead({@link ChannelHandlerContext} ctx, {@link String} msg) {
 *     ch.write("Did you say '" + msg + "'?");
 * }
 * </pre>
 * 对所请求的字符串应用行分隔符，并将其编码为ByteBuf。TCP/IP套接字中基于文本的行协议的典型设置是:
 * 然后你可以用字符串代替ByteBuf作为消息:
 */
@Sharable
public class LineEncoder extends MessageToMessageEncoder<CharSequence> {

    private final Charset charset;
    private final byte[] lineSeparator;

    /**
     * Creates a new instance with the current system line separator and UTF-8 charset encoding.使用当前系统行分隔符和UTF-8字符集编码创建一个新实例。
     */
    public LineEncoder() {
        this(LineSeparator.DEFAULT, CharsetUtil.UTF_8);
    }

    /**
     * Creates a new instance with the specified line separator and UTF-8 charset encoding.使用指定的行分隔符和UTF-8字符集编码创建一个新实例。
     */
    public LineEncoder(LineSeparator lineSeparator) {
        this(lineSeparator, CharsetUtil.UTF_8);
    }

    /**
     * Creates a new instance with the specified character set.使用指定的字符集创建一个新实例。
     */
    public LineEncoder(Charset charset) {
        this(LineSeparator.DEFAULT, charset);
    }

    /**
     * Creates a new instance with the specified line separator and character set.使用指定的行分隔符和字符集创建一个新实例。
     */
    public LineEncoder(LineSeparator lineSeparator, Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.lineSeparator = ObjectUtil.checkNotNull(lineSeparator, "lineSeparator").value().getBytes(charset);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
//        编码成字符串
        ByteBuf buffer = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), charset, lineSeparator.length);
//        在字符串末尾加上分隔符
        buffer.writeBytes(lineSeparator);
        out.add(buffer);
    }
}
