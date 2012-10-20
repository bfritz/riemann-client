/*

 Copyright 2012 David Rusek

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

*/

package org.robobninjas.riemann;

import com.aphyr.riemann.Proto;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;

class TcpClientPipelineFactory implements ChannelPipelineFactory {
  @Override
  public ChannelPipeline getPipeline() throws Exception {
    final ChannelPipeline pipeline = Channels.pipeline();
    pipeline.addLast("frame-encoder", new LengthFieldPrepender(4));
    pipeline.addLast("frame-decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
    pipeline.addLast("message-encoder", new ProtobufEncoder());
    pipeline.addLast("message-decoder", new ProtobufDecoder(Proto.Msg.getDefaultInstance()));
    pipeline.addLast("returnable-handler", new ReturnableHandler());
    return pipeline;
  }
}