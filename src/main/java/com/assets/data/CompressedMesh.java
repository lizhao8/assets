package com.assets.data;

import java.util.ArrayList;
import java.util.List;

import com.assets.ByteReader;
import com.assets.ByteWriter;

public class CompressedMesh {
	public PackedFloatVector m_Vertices;
	public PackedFloatVector m_UV;
	public PackedFloatVector m_BindPoses;
	public PackedFloatVector m_Normals;
	public PackedFloatVector m_Tangents;
	public PackedIntVector m_Weights;
	public PackedIntVector m_NormalSigns;
	public PackedIntVector m_TangentSigns;
	public PackedFloatVector m_FloatColors;
	public PackedIntVector m_BoneIndices;
	public PackedIntVector m_Triangles;
	public PackedIntVector m_Colors;
	public long m_UVInfo;

	public CompressedMesh(ByteReader reader) {
		int[] version = reader.version;

		m_Vertices = new PackedFloatVector(reader);
		m_UV = new PackedFloatVector(reader);
		if (version[0] < 5) {
			m_BindPoses = new PackedFloatVector(reader);
		}
		m_Normals = new PackedFloatVector(reader);
		m_Tangents = new PackedFloatVector(reader);
		m_Weights = new PackedIntVector(reader);
		m_NormalSigns = new PackedIntVector(reader);
		m_TangentSigns = new PackedIntVector(reader);
		if (version[0] >= 5) {
			m_FloatColors = new PackedFloatVector(reader);
		}
		m_BoneIndices = new PackedIntVector(reader);
		m_Triangles = new PackedIntVector(reader);
		if (version[0] > 3 || (version[0] == 3 && version[1] >= 5)) // 3.5 and up
		{
			if (version[0] < 5) {
				m_Colors = new PackedIntVector(reader);
			} else {
				m_UVInfo = reader.readUInt32();
			}
		}
	}

	public class PackedFloatVector {
		public long m_NumItems;
		public float m_Range;
		public float m_Start;
		public byte[] m_Data;
		public byte m_BitSize;

		public PackedFloatVector(ByteReader reader) {
			m_NumItems = reader.readUInt32();
			m_Range = reader.readSingle();
			m_Start = reader.readSingle();

			long numData = reader.readInt32();
			m_Data = reader.readBytes((int) numData);
			reader.alignStream();

			m_BitSize = reader.readByte();
			reader.alignStream();
		}
		public void write(ByteWriter writer) {
			writer.writeUInt32(m_NumItems);
			writer.writeSingle(m_Range);
			writer.writeSingle(m_Start);

			writer.writeInt32(m_Data.length);
			writer.writeBytes(m_Data);
			writer.alignStream();

			writer.writeByte(m_BitSize);
			writer.alignStream();
			
		}

		public float[] UnpackFloats(long itemCountInChunk, long chunkStride) {
			return UnpackFloats(itemCountInChunk, chunkStride, 0, -1);
		}

		public float[] UnpackFloats(long itemCountInChunk, long chunkStride, long start, long numChunks) {
			// int start = 0;
			// long numChunks = -1;
			long bitPos = m_BitSize * start;
			long indexPos = bitPos / 8;
			bitPos %= 8;

			float scale = 1.0f / m_Range;
			if (numChunks == -1) {
				numChunks = m_NumItems / itemCountInChunk;
			}
			long end = chunkStride * numChunks / 4;
			List<Float> data = new ArrayList<Float>();
			for (long index = 0; index != end; index += chunkStride / 4) {
				for (int i = 0; i < itemCountInChunk; ++i) {
					long x = 0;

					int bits = 0;
					while (bits < m_BitSize) {
						x |= (m_Data[(int) indexPos] >> bitPos) << bits;
						long num = Math.min(m_BitSize - bits, 8 - bitPos);
						bitPos += num;
						bits += num;
						if (bitPos == 8) {
							indexPos++;
							bitPos = 0;
						}
					}
					x &= (1 << m_BitSize) - 1L;
					data.add(x / (scale * ((1 << m_BitSize) - 1)) + m_Start);
				}
			}

			return ByteReader.toArray(data);
		}


		
	}

	public class PackedIntVector {
		public long m_NumItems;
		public byte[] m_Data;
		public byte m_BitSize;

		public PackedIntVector(ByteReader reader) {
			m_NumItems = reader.readUInt32();

			long numData = reader.readInt32();
			m_Data = reader.readBytes((int) numData);
			reader.alignStream();

			m_BitSize = reader.readByte();
			reader.alignStream();
		}

		public void write(ByteWriter writer) {
			writer.writeUInt32(m_NumItems);

			writer.writeInt32(m_Data.length);
			writer.writeBytes(m_Data);
			writer.alignStream();

			writer.writeByte(m_BitSize);
			writer.alignStream();
			
		}

		public long[] UnpackInts() {
			long[] data = new long[(int) m_NumItems];
			int indexPos = 0;
			int bitPos = 0;
			for (int i = 0; i < m_NumItems; i++) {
				int bits = 0;
				data[i] = 0;
				while (bits < m_BitSize) {
					data[i] |= (m_Data[indexPos] >> bitPos) << bits;
					int num = Math.min(m_BitSize - bits, 8 - bitPos);
					bitPos += num;
					bits += num;
					if (bitPos == 8) {
						indexPos++;
						bitPos = 0;
					}
				}
				data[i] &= (1 << m_BitSize) - 1;
			}
			return data;
		}

	}

	public void write(ByteWriter writer) {
		int[] version = writer.version;

		m_Vertices.write(writer);
		m_UV.write(writer);
		if (version[0] < 5) {
			m_BindPoses.write(writer);
		}
		m_Normals.write(writer);
		m_Tangents.write(writer);
		m_Weights.write(writer);
		m_NormalSigns.write(writer);
		m_TangentSigns.write(writer);
		if (version[0] >= 5) {
			m_FloatColors.write(writer);
		}
		m_BoneIndices.write(writer);
		m_Triangles.write(writer);
		if (version[0] > 3 || (version[0] == 3 && version[1] >= 5)) // 3.5 and up
		{
			if (version[0] < 5) {
				m_Colors.write(writer);
			} else {
				writer.writeUInt32(m_UVInfo);
			}
		}
		
	}

}