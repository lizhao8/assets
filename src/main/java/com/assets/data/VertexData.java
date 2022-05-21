package com.assets.data;

import java.util.stream.Stream;

import com.assets.ByteReader;
import com.assets.ByteWriter;

public class VertexData {
	public long m_CurrentChannels;
	public long m_VertexCount;
	public ChannelInfo[] m_Channels;
	public StreamInfo[] m_Streams;
	public byte[] m_DataSize;

	public VertexData(ByteReader reader) {
		int[] version = reader.version;

		if (version[0] < 2018)// 2018 down
		{
			m_CurrentChannels = reader.readUInt32();
		}

		m_VertexCount = reader.readUInt32();

		if (version[0] >= 4) // 4.0 and up
		{
			long m_ChannelsSize = reader.readInt32();
			m_Channels = new ChannelInfo[(int) m_ChannelsSize];
			for (int i = 0; i < m_ChannelsSize; i++) {
				m_Channels[i] = new ChannelInfo(reader);
			}
		}

		if (version[0] < 5) // 5.0 down
		{
			if (version[0] < 4) {
				m_Streams = new StreamInfo[4];
			} else {
				m_Streams = new StreamInfo[(int) reader.readInt32()];
			}

			for (int i = 0; i < m_Streams.length; i++) {
				m_Streams[i] = new StreamInfo(reader);
			}

			if (version[0] < 4) // 4.0 down
			{
				throw new RuntimeException("GetChannels");
				// GetChannels(version);
			}
		} else // 5.0 and up
		{
			GetStreams(version);
		}

		m_DataSize = reader.readUInt8Array();
		reader.alignStream();
	}

	private void GetStreams(int[] version) {

		long streamCount = Stream.of(m_Channels).mapToInt(x -> x.stream).max().getAsInt() + 1;
		m_Streams = new StreamInfo[(int) streamCount];
		long offset = 0;
		for (int s = 0; s < streamCount; s++) {
			long chnMask = 0;
			long stride = 0;
			for (int chn = 0; chn < m_Channels.length; chn++) {
				ChannelInfo m_Channel = m_Channels[chn];
				if (m_Channel.stream == s) {
					if (m_Channel.dimension > 0) {
						chnMask |= 1L << chn;
						stride += m_Channel.dimension
								* MeshHelper.GetFormatSize(MeshHelper.ToVertexFormat(m_Channel.format, version));
					}
				}
			}
			m_Streams[s] = new StreamInfo(chnMask, offset, stride, 0L, (byte) 0, 0L);

			offset += m_VertexCount * stride;
			// static size_t AlignStreamSize (size_t size) { return (size +
			// (kVertexStreamAlign-1)) & ~(kVertexStreamAlign-1); }
			offset = (offset + (16L - 1L)) & ~(16L - 1L);
		}
	}

//	private void GetChannels(int[] version)
//    {
//        m_Channels = new ChannelInfo[6];
//        for (int i = 0; i < 6; i++)
//        {
//            m_Channels[i] = new ChannelInfo();
//        }
//        for (int s = 0; s < m_Streams.length; s++)
//        {
//        	StreamInfo m_Stream = m_Streams[s];
//            var channelMask = new BitArray(new[] { (int)m_Stream.channelMask });
//            byte offset = 0;
//            for (int i = 0; i < 6; i++)
//            {
//                if (channelMask.Get(i))
//                {
//                    var m_Channel = m_Channels[i];
//                    m_Channel.stream = (byte)s;
//                    m_Channel.offset = offset;
//                    switch (i)
//                    {
//                        case 0: //kShaderChannelVertex
//                        case 1: //kShaderChannelNormal
//                            m_Channel.format = 0; //kChannelFormatFloat
//                            m_Channel.dimension = 3;
//                            break;
//                        case 2: //kShaderChannelColor
//                            m_Channel.format = 2; //kChannelFormatColor
//                            m_Channel.dimension = 4;
//                            break;
//                        case 3: //kShaderChannelTexCoord0
//                        case 4: //kShaderChannelTexCoord1
//                            m_Channel.format = 0; //kChannelFormatFloat
//                            m_Channel.dimension = 2;
//                            break;
//                        case 5: //kShaderChannelTangent
//                            m_Channel.format = 0; //kChannelFormatFloat
//                            m_Channel.dimension = 4;
//                            break;
//                    }
//                    offset += (byte)(m_Channel.dimension * MeshHelper.GetFormatSize(MeshHelper.ToVertexFormat(m_Channel.format, version)));
//                }
//            }
//        }
//    }

	public class ChannelInfo {
		public byte stream;
		public byte offset;
		public byte format;
		public byte dimension;

		public ChannelInfo() {
		}

		public ChannelInfo(ByteReader reader) {
			stream = reader.readByte();
			offset = reader.readByte();
			format = reader.readByte();
			dimension = (byte) (reader.readByte() & 0xF);
		}
	}

	public class StreamInfo {
		public long channelMask;
		public long offset;
		public long stride;
		public long align;
		public byte dividerOp;
		public long frequency;

		public StreamInfo() {

		}

		public StreamInfo(long channelMask, long offset, long stride, long align, byte dividerOp, long frequency) {
			super();
			this.channelMask = channelMask;
			this.offset = offset;
			this.stride = stride;
			this.align = align;
			this.dividerOp = dividerOp;
			this.frequency = frequency;
		}

		public StreamInfo(ByteReader reader) {
			int[] version = reader.version;

			channelMask = reader.readUInt32();
			offset = reader.readUInt32();

			if (version[0] < 4) // 4.0 down
			{
				stride = reader.readUInt32();
				align = reader.readUInt32();
			} else {
				stride = reader.readByte();
				dividerOp = reader.readByte();
				frequency = reader.readUInt16();
			}
		}
	}
	
	
	public void write(ByteWriter writer) {
		int[] version = writer.version;
		writer.writeUInt32(m_VertexCount);

		if (version[0] >= 4) // 4.0 and up
		{
			writer.writeInt32(m_Channels.length);
			for (ChannelInfo channel : m_Channels) {				
				writer.writeByte(channel.stream);
				writer.writeByte(channel.offset);
				writer.writeByte(channel.format);
				writer.writeByte(channel.dimension);//reader.readByte() & 0xF
			}
		}
		writeStreams(version);
		writer.writeUInt8Array(m_DataSize);
		writer.alignStream();		
	}

	private void writeStreams(int[] version) {	
		//???
		/*long streamCount = m_Streams.length;
		long offset = 0;
		for (int s = 0; s < streamCount; s++) {
			long chnMask = 0;
			long stride = 0;
			for (int chn = 0; chn < m_Channels.length; chn++) {
				ChannelInfo m_Channel = m_Channels[chn];
				if (m_Channel.stream == s) {
					if (m_Channel.dimension > 0) {
						chnMask |= 1L << chn;
						stride += m_Channel.dimension
								* MeshHelper.GetFormatSize(MeshHelper.ToVertexFormat(m_Channel.format, version));
					}
				}
			}
			m_Streams[s] = new StreamInfo(chnMask, offset, stride, 0L, (byte) 0, 0L);

			offset += m_VertexCount * stride;
			offset = (offset + (16L - 1L)) & ~(16L - 1L);
		}*/
	}

}