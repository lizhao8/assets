package com.assets.data;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.assets.AssetsReader;
import com.assets.ByteReader;
import com.assets.ByteWriter;
import com.assets.data.MeshHelper.VertexFormat;
import com.assets.data.VertexData.ChannelInfo;
import com.assets.data.VertexData.StreamInfo;
import com.assets.max.SubMesh;
import com.assets.max.SubMesh.Vector3;
import com.jogl.base.Element;
import com.jogl.base.Face;

public class Mesh extends java.lang.Object {
	public ByteReader reader;
	public SubMesh[] m_SubMeshes;
	public String m_Name;
	public BlendShapeData m_Shapes;
	public AssetsReader assetsReader;
	public Matrix4x4[] m_BindPose;
	public long[] m_BoneNameHashes;
	public BuildType buildType;
	public boolean m_Use16BitIndices = true;
	public int[] version = { 0, 0, 0, 0 };
	public long[] m_IndexBuffer;
	public VertexData m_VertexData;
	public CompressedMesh m_CompressedMesh;
	public StreamingInfo m_StreamData;
	public long m_VertexCount;

	public BoneWeights4[] m_Skin;

	public float[] m_Vertices;
	public float[] m_Normals;

	public float[] m_Colors;
	public float[] m_UV0;
	public float[] m_UV1;
	public float[] m_UV2;
	public float[] m_UV3;
	public float[] m_UV4;
	public float[] m_UV5;
	public float[] m_UV6;
	public float[] m_UV7;
	public float[] m_Tangents;
	public ObjectInfo objectInfo;
	public List<Long> m_Indices = new ArrayList<Long>();
	public List<Face> faceList = new ArrayList<Face>();
	public List<Element> elementList = new ArrayList<Element>();

	public long m_RootBoneNameHash;
	public byte m_MeshCompression;

	public boolean m_IsReadable;
	public boolean m_KeepVertices;
	public boolean m_KeepIndices;
	long m_IndexFormat;
	long m_IndexBuffer_size;
	byte[] skipBytes;
	long m_MeshUsageFlags;
	byte[] m_BakedConvexCollisionMesh;
	byte[] m_BakedTriangleCollisionMesh;
	float[] m_MeshMetrics;

	public Mesh() {

	}

	public Mesh(SerializedFile assetsFile, ObjectInfo objectInfo, AssetsReader assetsReader) {
		reader = assetsFile.reader;
		reader.version = assetsFile.version;
		version = assetsFile.version;
		buildType = assetsFile.buildType;
		this.objectInfo = objectInfo;
		reader.reset(objectInfo.byteStart);
		m_Name = reader.readAlignedString();
		long m_SubMeshesSize = reader.readInt32();
		m_SubMeshes = new SubMesh[(int) m_SubMeshesSize];

		for (int i = 0; i < m_SubMeshesSize; i++) {
			m_SubMeshes[i] = new SubMesh(reader);
		}
		m_Shapes = new BlendShapeData(reader);

		if (version[0] > 4 || (version[0] == 4 && version[1] >= 3)) // 4.3 and up
		{
			m_BindPose = reader.readMatrixArray();
			m_BoneNameHashes = reader.readUInt32Array();
			m_RootBoneNameHash = reader.readUInt32();
		}

		if (version[0] > 2 || (version[0] == 2 && version[1] >= 6)) // 2.6.0 and up
		{
//            if (version[0] >= 2019) //2019 and up
//            {
//                long m_BonesAABBSize = reader.readInt32();
//                long m_BonesAABB = new MinMaxAABB[m_BonesAABBSize];
//                for (int i = 0; i < m_BonesAABBSize; i++)
//                {
//                    m_BonesAABB[i] = new MinMaxAABB(reader);
//                }
//
//                var m_VariableBoneCountWeights = reader.ReadUInt32Array();
//            }

			m_MeshCompression = reader.readByte();
			if (version[0] >= 4) {
//                if (version[0] < 5)
//                {
//                    var m_StreamCompression = reader.ReadByte();
//                }
				m_IsReadable = reader.readBoolean();
				m_KeepVertices = reader.readBoolean();
				m_KeepIndices = reader.readBoolean();
			}
			reader.alignStream();

			// Unity fixed it in 2017.3.1p1 and later versions
			if ((version[0] > 2017 || (version[0] == 2017 && version[1] >= 4)) || // 2017.4
					((version[0] == 2017 && version[1] == 3 && version[2] == 1) && buildType.IsPatch) || // fixed after
																											// 2017.3.1px
					((version[0] == 2017 && version[1] == 3) && m_MeshCompression == 0))// 2017.3.xfx with no
																						// compression
			{
				m_IndexFormat = reader.readInt32();
				m_Use16BitIndices = m_IndexFormat == 0;
			}

			m_IndexBuffer_size = reader.readInt32();
			if (m_Use16BitIndices) {
				m_IndexBuffer = new long[(int) (m_IndexBuffer_size / 2)];
				for (int i = 0; i < m_IndexBuffer_size / 2; i++) {
					m_IndexBuffer[i] = reader.readUInt16();					
				}
				reader.alignStream();
			} else {
				m_IndexBuffer = reader.readUInt32Array(m_IndexBuffer_size / 4);
			}
		}
		m_VertexData = new VertexData(reader);

		if (version[0] > 2 || (version[0] == 2 && version[1] >= 6)) // 2.6.0 and later
		{
			m_CompressedMesh = new CompressedMesh(reader);
		}
		skipBytes = reader.skip(24); // AABB m_LocalAABB
//		if (version[0] < 3 || (version[0] == 3 && version[1] <= 4)) //3.4.2 and earlier
//        {
//            int m_Colors_size = reader.ReadInt32();
//            m_Colors = new float[m_Colors_size * 4];
//            for (int v = 0; v < m_Colors_size * 4; v++)
//            {
//                m_Colors[v] = (float)reader.ReadByte() / 0xFF;
//            }
//
//            int m_CollisionTriangles_size = reader.ReadInt32();
//            reader.Position += m_CollisionTriangles_size * 4; //UInt32 indices
//            int m_CollisionVertexCount = reader.ReadInt32();
//        }

		m_MeshUsageFlags = reader.readInt32();
		if (version[0] >= 5) // 5.0 and up
		{
			m_BakedConvexCollisionMesh = reader.readUInt8Array();
			reader.alignStream();
			m_BakedTriangleCollisionMesh = reader.readUInt8Array();
			reader.alignStream();
		}
		if (version[0] > 2018 || (version[0] == 2018 && version[1] >= 2)) // 2018.2 and up
		{
			m_MeshMetrics = new float[2];
			m_MeshMetrics[0] = reader.readSingle();
			m_MeshMetrics[1] = reader.readSingle();
		}
		if (version[0] > 2018 || (version[0] == 2018 && version[1] >= 3)) // 2018.3 and up
		{
			reader.alignStream();
			m_StreamData = new StreamingInfo(reader);
		}

		ProcessData();
	}

	private void ProcessData() {
		/*
		 * if (m_StreamData != null && !m_StreamData.equals("")) { if
		 * (m_VertexData.m_VertexCount > 0) { var resourceReader = new
		 * ResourceReader(m_StreamData.path, assetsFile, m_StreamData.offset,
		 * m_StreamData.size); m_VertexData.m_DataSize = resourceReader.GetData(); } }
		 */
		if (version[0] > 3 || (version[0] == 3 && version[1] >= 5)) // 3.5 and up
		{
			ReadVertexData();
		}

		if (version[0] > 2 || (version[0] == 2 && version[1] >= 6)) // 2.6.0 and later
		{
			DecompressCompressedMesh();
		}

		GetTriangles();
	}

	private void GetTriangles() {
		for (SubMesh m_SubMesh : m_SubMeshes) {
			long firstIndex = m_SubMesh.firstByte / 2;
			if (!m_Use16BitIndices) {
				firstIndex /= 2;
			}
			long indexCount = m_SubMesh.indexCount;
			long topology = m_SubMesh.topology;
			if (topology == GfxPrimitiveType.kPrimitiveTriangles.ordinal()) {
				for (int i = 0; i < indexCount; i += 3) {
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + i)]);
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + i + 1)]);
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + i + 2)]);
				}
			} else if (version[0] < 4 || topology == GfxPrimitiveType.kPrimitiveTriangleStrip.ordinal()) {
				// de-stripify :
				long triIndex = 0;
				for (int i = 0; i < indexCount - 2; i++) {
					long a = m_IndexBuffer[(int) (firstIndex + i)];
					long b = m_IndexBuffer[(int) (firstIndex + i + 1)];
					long c = m_IndexBuffer[(int) (firstIndex + i + 2)];

					// skip degenerates
					if (a == b || a == c || b == c)
						continue;

					// do the winding flip-flop of strips :
					if ((i & 1) == 1) {
						m_Indices.add(b);
						m_Indices.add(a);
					} else {
						m_Indices.add(a);
						m_Indices.add(b);
					}
					m_Indices.add(c);
					triIndex += 3;
				}
				// fix indexCount
				m_SubMesh.indexCount = triIndex;
			} else if (topology == GfxPrimitiveType.kPrimitiveQuads.ordinal()) {
				for (int q = 0; q < indexCount; q += 4) {
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + q)]);
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + q + 1)]);
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + q + 2)]);
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + q)]);
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + q + 2)]);
					m_Indices.add(m_IndexBuffer[(int) (firstIndex + q + 3)]);
				}
				// fix indexCount
				m_SubMesh.indexCount = indexCount / 2 * 3;
			} else {
				throw new RuntimeException("Failed getting triangles. Submesh topology is lines or points.");
			}
		}
	}

	private void DecompressCompressedMesh() {
		// Vertex
		if (m_CompressedMesh.m_Vertices.m_NumItems > 0) {
			m_VertexCount = (int) m_CompressedMesh.m_Vertices.m_NumItems / 3;
			m_Vertices = m_CompressedMesh.m_Vertices.UnpackFloats(3, 3 * 4);
		}
		// UV
		if (m_CompressedMesh.m_UV.m_NumItems > 0) {
			long m_UVInfo = m_CompressedMesh.m_UVInfo;
			if (m_UVInfo != 0) {
				int kInfoBitsPerUV = 4;
				int kUVDimensionMask = 3;
				int kUVChannelExists = 4;
				int kMaxTexCoordShaderChannels = 8;

				int uvSrcOffset = 0;
				for (int uv = 0; uv < kMaxTexCoordShaderChannels; uv++) {
					long texCoordBits = m_UVInfo >> (uv * kInfoBitsPerUV);
					texCoordBits &= (1L << kInfoBitsPerUV) - 1L;
					if ((texCoordBits & kUVChannelExists) != 0) {
						long uvDim = 1 + (int) (texCoordBits & kUVDimensionMask);
						float[] m_UV = m_CompressedMesh.m_UV.UnpackFloats(uvDim, uvDim * 4, uvSrcOffset, m_VertexCount);
						SetUV(uv, m_UV);
						uvSrcOffset += uvDim * m_VertexCount;
					}
				}
			} else {
				m_UV0 = m_CompressedMesh.m_UV.UnpackFloats(2, 2 * 4, 0, m_VertexCount);
				if (m_CompressedMesh.m_UV.m_NumItems >= m_VertexCount * 4) {
					m_UV1 = m_CompressedMesh.m_UV.UnpackFloats(2, 2 * 4, m_VertexCount * 2, m_VertexCount);
				}
			}
		}
		// BindPose
		if (version[0] < 5) {
			if (m_CompressedMesh.m_BindPoses.m_NumItems > 0) {
				m_BindPose = new Matrix4x4[(int) (m_CompressedMesh.m_BindPoses.m_NumItems / 16)];
				float[] m_BindPoses_Unpacked = m_CompressedMesh.m_BindPoses.UnpackFloats(16, 4 * 16);
				float[] buffer = new float[16];
				for (int i = 0; i < m_BindPose.length; i++) {
					m_BindPoses_Unpacked = Arrays.copyOfRange(buffer, i * 16, 16);
					m_BindPose[i] = new Matrix4x4(buffer);
				}
			}
		}
		// Normal
		if (m_CompressedMesh.m_Normals.m_NumItems > 0) {
			float[] normalData = m_CompressedMesh.m_Normals.UnpackFloats(2, 4 * 2);
			long[] signs = m_CompressedMesh.m_NormalSigns.UnpackInts();
			m_Normals = new float[(int) (m_CompressedMesh.m_Normals.m_NumItems / 2 * 3)];
			for (int i = 0; i < m_CompressedMesh.m_Normals.m_NumItems / 2; ++i) {
				float x = normalData[i * 2 + 0];
				float y = normalData[i * 2 + 1];
				float zsqr = 1 - x * x - y * y;
				float z;
				if (zsqr >= 0f)
					z = (float) Math.sqrt(zsqr);
				else {
					z = 0;
					Vector3 normal = new Vector3(x, y, z);
					normal.Normalize();
					x = normal.X;
					y = normal.Y;
					z = normal.Z;
				}
				if (signs[i] == 0)
					z = -z;
				m_Normals[i * 3] = x;
				m_Normals[i * 3 + 1] = y;
				m_Normals[i * 3 + 2] = z;
			}
		}
		// Tangent
		if (m_CompressedMesh.m_Tangents.m_NumItems > 0) {
			float[] tangentData = m_CompressedMesh.m_Tangents.UnpackFloats(2, 4 * 2);
			long[] signs = m_CompressedMesh.m_TangentSigns.UnpackInts();
			m_Tangents = new float[(int) (m_CompressedMesh.m_Tangents.m_NumItems / 2 * 4)];
			for (int i = 0; i < m_CompressedMesh.m_Tangents.m_NumItems / 2; ++i) {
				float x = tangentData[i * 2 + 0];
				float y = tangentData[i * 2 + 1];
				float zsqr = 1 - x * x - y * y;
				float z;
				if (zsqr >= 0f)
					z = (float) Math.sqrt(zsqr);
				else {
					z = 0;
					Vector3 vector3f = new Vector3(x, y, z);
					vector3f.Normalize();
					x = vector3f.X;
					y = vector3f.Y;
					z = vector3f.Z;
				}
				if (signs[i * 2 + 0] == 0)
					z = -z;
				float w = signs[i * 2 + 1] > 0 ? 1.0f : -1.0f;
				m_Tangents[i * 4] = x;
				m_Tangents[i * 4 + 1] = y;
				m_Tangents[i * 4 + 2] = z;
				m_Tangents[i * 4 + 3] = w;
			}
		}
		// FloatColor
		if (version[0] >= 5) {
			if (m_CompressedMesh.m_FloatColors.m_NumItems > 0) {
				m_Colors = m_CompressedMesh.m_FloatColors.UnpackFloats(1, 4);
			}
		}
		// Skin
		if (m_CompressedMesh.m_Weights.m_NumItems > 0) {
			long[] weights = m_CompressedMesh.m_Weights.UnpackInts();
			long[] boneIndices = m_CompressedMesh.m_BoneIndices.UnpackInts();

			InitMSkin();

			int bonePos = 0;
			int boneIndexPos = 0;
			int j = 0;
			int sum = 0;

			for (int i = 0; i < m_CompressedMesh.m_Weights.m_NumItems; i++) {
				// read bone index and weight.
				m_Skin[bonePos].weight[j] = weights[i] / 31.0f;
				m_Skin[bonePos].boneIndex[j] = boneIndices[boneIndexPos++];
				j++;
				sum += weights[i];

				// the weights add up to one. fill the rest for this vertex with zero, and
				// continue with next one.
				if (sum >= 31) {
					for (; j < 4; j++) {
						m_Skin[bonePos].weight[j] = 0;
						m_Skin[bonePos].boneIndex[j] = 0;
					}
					bonePos++;
					j = 0;
					sum = 0;
				}
				// we read three weights, but they don't add up to one. calculate the fourth
				// one, and read
				// missing bone index. continue with next vertex.
				else if (j == 3) {
					m_Skin[bonePos].weight[j] = (31 - sum) / 31.0f;
					m_Skin[bonePos].boneIndex[j] = boneIndices[boneIndexPos++];
					bonePos++;
					j = 0;
					sum = 0;
				}
			}
		}
		// IndexBuffer
		if (m_CompressedMesh.m_Triangles.m_NumItems > 0) {
			m_IndexBuffer = m_CompressedMesh.m_Triangles.UnpackInts();
		}
		// Color
		if (m_CompressedMesh.m_Colors != null && m_CompressedMesh.m_Colors.m_NumItems > 0) {
			m_CompressedMesh.m_Colors.m_NumItems *= 4;
			m_CompressedMesh.m_Colors.m_BitSize /= 4;
			long[] tempColors = m_CompressedMesh.m_Colors.UnpackInts();
			m_Colors = new float[(int) m_CompressedMesh.m_Colors.m_NumItems];
			for (int v = 0; v < m_CompressedMesh.m_Colors.m_NumItems; v++) {
				m_Colors[v] = tempColors[v] / 255f;
			}
		}
	}

	private void SetUV(int uv, float[] m_UV) {
		switch (uv) {
		case 0:
			m_UV0 = m_UV;
			break;
		case 1:
			m_UV1 = m_UV;
			break;
		case 2:
			m_UV2 = m_UV;
			break;
		case 3:
			m_UV3 = m_UV;
			break;
		case 4:
			m_UV4 = m_UV;
			break;
		case 5:
			m_UV5 = m_UV;
			break;
		case 6:
			m_UV6 = m_UV;
			break;
		case 7:
			m_UV7 = m_UV;
			break;
		default:
			throw new RuntimeException("SetUV");
		}
	}

	private void ReadVertexData() {
		m_VertexCount = (int) m_VertexData.m_VertexCount;

		for (int chn = 0; chn < m_VertexData.m_Channels.length; chn++) {
			ChannelInfo m_Channel = m_VertexData.m_Channels[chn];
			if (m_Channel.dimension > 0) {
				StreamInfo m_Stream = m_VertexData.m_Streams[m_Channel.stream];
				BitArray channelMask = new BitArray(new int[] { (int) m_Stream.channelMask });
				if (channelMask.get(chn)) {
					VertexFormat vertexFormat = MeshHelper.ToVertexFormat(m_Channel.format, version);
					long componentByteSize = (int) MeshHelper.GetFormatSize(vertexFormat);
					byte[] componentBytes = new byte[(int) (m_VertexCount * m_Channel.dimension * componentByteSize)];
					for (int v = 0; v < m_VertexCount; v++) {
						long vertexOffset = m_Stream.offset + m_Channel.offset + m_Stream.stride * v;
						for (int d = 0; d < m_Channel.dimension; d++) {
							long componentOffset = vertexOffset + componentByteSize * d;
							System.out.println(componentOffset);
							System.out.println(componentByteSize * (v * m_Channel.dimension + d));
							System.out.println(componentByteSize);
							System.out.println("------------");

							Buffer.BlockCopy(m_VertexData.m_DataSize, componentOffset, componentBytes,
									componentByteSize * (v * m_Channel.dimension + d), componentByteSize);
						}
					}

					if (reader.Endian == EndianType.BigEndian && componentByteSize > 1) // swap bytes
					{
						for (long i = 0; i < componentBytes.length / componentByteSize; i++) {
							byte[] buff = new byte[(int) componentByteSize];
							Buffer.BlockCopy(componentBytes, i * componentByteSize, buff, 0, componentByteSize);
							buff = Buffer.Reverse(buff);
							Buffer.BlockCopy(buff, 0, componentBytes, i * componentByteSize, componentByteSize);
						}
					}

					int[] componentsIntArray = null;
					float[] componentsFloatArray = null;
					if (MeshHelper.IsIntFormat(vertexFormat))
						componentsIntArray = MeshHelper.BytesToIntArray(componentBytes, vertexFormat);
					else
						componentsFloatArray = MeshHelper.BytesToFloatArray(componentBytes, vertexFormat);

					if (version[0] >= 2018) {
						switch (chn) {
						case 0: // kShaderChannelVertex
							m_Vertices = componentsFloatArray;
							break;
						case 1: // kShaderChannelNormal
							m_Normals = componentsFloatArray;
							break;
						case 2: // kShaderChannelTangent
							m_Tangents = componentsFloatArray;
							break;
						case 3: // kShaderChannelColor
							m_Colors = componentsFloatArray;
							break;
						case 4: // kShaderChannelTexCoord0
							m_UV0 = componentsFloatArray;
							break;
						case 5: // kShaderChannelTexCoord1
							m_UV1 = componentsFloatArray;
							break;
						case 6: // kShaderChannelTexCoord2
							m_UV2 = componentsFloatArray;
							break;
						case 7: // kShaderChannelTexCoord3
							m_UV3 = componentsFloatArray;
							break;
						case 8: // kShaderChannelTexCoord4
							m_UV4 = componentsFloatArray;
							break;
						case 9: // kShaderChannelTexCoord5
							m_UV5 = componentsFloatArray;
							break;
						case 10: // kShaderChannelTexCoord6
							m_UV6 = componentsFloatArray;
							break;
						case 11: // kShaderChannelTexCoord7
							m_UV7 = componentsFloatArray;
							break;
						// 2018.2 and up
						case 12: // kShaderChannelBlendWeight
							if (m_Skin == null) {
								InitMSkin();
							}
							for (int i = 0; i < m_VertexCount; i++) {
								for (int j = 0; j < m_Channel.dimension; j++) {
									m_Skin[i].weight[j] = componentsFloatArray[i * m_Channel.dimension + j];
								}
							}
							break;
						case 13: // kShaderChannelBlendIndices
							if (m_Skin == null) {
								InitMSkin();
							}
							for (int i = 0; i < m_VertexCount; i++) {
								for (int j = 0; j < m_Channel.dimension; j++) {
									m_Skin[i].boneIndex[j] = componentsIntArray[i * m_Channel.dimension + j];
								}
							}
							break;
						}
					} else {
						switch (chn) {
						case 0: // kShaderChannelVertex
							m_Vertices = componentsFloatArray;
							break;
						case 1: // kShaderChannelNormal
							m_Normals = componentsFloatArray;
							break;
						case 2: // kShaderChannelColor
							m_Colors = componentsFloatArray;
							break;
						case 3: // kShaderChannelTexCoord0
							m_UV0 = componentsFloatArray;
							break;
						case 4: // kShaderChannelTexCoord1
							m_UV1 = componentsFloatArray;
							break;
						case 5:
							if (version[0] >= 5) // kShaderChannelTexCoord2
							{
								m_UV2 = componentsFloatArray;
							} else // kShaderChannelTangent
							{
								m_Tangents = componentsFloatArray;
							}
							break;
						case 6: // kShaderChannelTexCoord3
							m_UV3 = componentsFloatArray;
							break;
						case 7: // kShaderChannelTangent
							m_Tangents = componentsFloatArray;
							break;
						}
					}
				}
			}
		}
	}

	private void InitMSkin() {
		m_Skin = new BoneWeights4[(int) m_VertexCount];
		for (int i = 0; i < m_VertexCount; i++) {
			m_Skin[i] = new BoneWeights4();
		}
	}

	public class BoneWeights4 {
		public float[] weight;
		public long[] boneIndex;

		public BoneWeights4() {
			weight = new float[4];
			boneIndex = new long[4];
		}

		public BoneWeights4(ByteReader reader) {
			weight = reader.readSingleArray(4);
			boneIndex = reader.readInt32Array(4);
		}
	}

	public enum GfxPrimitiveType {
		kPrimitiveTriangles, kPrimitiveTriangleStrip, kPrimitiveQuads, kPrimitiveLines, kPrimitiveLineStrip,
		kPrimitivePoints;
	};

	public void save(String filePath) throws Exception {
		ByteWriter writer = new ByteWriter();
		writer.version = version;
		writer.byteStart = objectInfo.byteStart;
		writer.writeAlignedString(m_Name);
		writer.writeInt32(m_SubMeshes.length);
		for (SubMesh subMesh : m_SubMeshes) {
			writer.writeUInt32(subMesh.firstByte);
			writer.writeUInt32(subMesh.indexCount);
			writer.writeInt32(subMesh.topology);
			writer.writeUInt32(subMesh.baseVertex);

			writer.writeUInt32(subMesh.firstVertex);
			writer.writeUInt32(subMesh.vertexCount);
			writer.writeVector3(subMesh.localAABB.m_Center);
			writer.writeVector3(subMesh.localAABB.m_Extent);
		}

		writer.writeInt32(m_Shapes.vertices.length);
		writer.writeInt32(m_Shapes.shapes.length);
		writer.writeInt32(m_Shapes.channels.length);
		writer.writeSingleArray(m_Shapes.fullWeights, true);

		if (version[0] > 4 || (version[0] == 4 && version[1] >= 3)) // 4.3 and up
		{
			writer.writeMatrixArray(m_BindPose);
			writer.writeUInt32Array(m_BoneNameHashes);
			writer.writeUInt32(m_RootBoneNameHash);
		}
		if (version[0] > 2 || (version[0] == 2 && version[1] >= 6)) // 2.6.0 and up
		{
			writer.writeByte(m_MeshCompression);
		}
		if (version[0] >= 4) {
			writer.writeBoolean(m_IsReadable);
			writer.writeBoolean(m_KeepVertices);
			writer.writeBoolean(m_KeepIndices);
		}
		writer.alignStream();
		if ((version[0] > 2017 || (version[0] == 2017 && version[1] >= 4)) || // 2017.4
				((version[0] == 2017 && version[1] == 3 && version[2] == 1) && buildType.IsPatch) || // fixed after
																										// 2017.3.1px
				((version[0] == 2017 && version[1] == 3) && m_MeshCompression == 0))// 2017.3.xfx with no
																					// compression
		{
			writer.writeInt32(m_IndexFormat);
			m_Use16BitIndices = m_IndexFormat == 0;
		}
		writer.writeInt32(m_IndexBuffer_size);

		if (m_Use16BitIndices) {

			for (long l : m_IndexBuffer) {
				writer.writeUInt16(l);
			}

			writer.alignStream();
		} else {
			m_IndexBuffer = reader.readUInt32Array(m_IndexBuffer_size / 4);
		}
		m_VertexData.write(writer);

		if (version[0] > 2 || (version[0] == 2 && version[1] >= 6)) // 2.6.0 and later
		{
			m_CompressedMesh.write(writer);
		}

		writer.writeBytes(skipBytes);// 24

		writer.writeInt32(m_MeshUsageFlags);

		if (version[0] >= 5) // 5.0 and up
		{
			writer.writeUInt8Array(m_BakedConvexCollisionMesh);
			writer.alignStream();
			writer.writeUInt8Array(m_BakedTriangleCollisionMesh);
			writer.alignStream();
		}
		if (version[0] > 2018 || (version[0] == 2018 && version[1] >= 2)) // 2018.2 and up
		{
			writer.writeSingle(m_MeshMetrics[0]);
			writer.writeSingle(m_MeshMetrics[1]);
		}
		if (version[0] > 2018 || (version[0] == 2018 && version[1] >= 3)) // 2018.3 and up
		{
			writer.alignStream();
			m_StreamData.write(writer);
		}

		// writeProcessData(writer);

		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		writer.stream.writeTo(fileOutputStream);
		fileOutputStream.close();

	}

	private void writeProcessData(ByteWriter writer) {
		if (version[0] > 3 || (version[0] == 3 && version[1] >= 5)) // 3.5 and up
		{
			writeVertexData(writer);// ???
		}

		if (version[0] > 2 || (version[0] == 2 && version[1] >= 6)) // 2.6.0 and later
		{
			// DecompressCompressedMesh();
		}

		// GetTriangles();

	}

	private void writeVertexData(ByteWriter writer) {
		m_VertexCount = (int) m_VertexData.m_VertexCount;

		for (int chn = 0; chn < m_VertexData.m_Channels.length; chn++) {
			ChannelInfo m_Channel = m_VertexData.m_Channels[chn];
			if (m_Channel.dimension > 0) {
				StreamInfo m_Stream = m_VertexData.m_Streams[m_Channel.stream];
				BitArray channelMask = new BitArray(new int[] { (int) m_Stream.channelMask });
				if (channelMask.get(chn)) {
					if (version[0] < 2018 && chn == 2 && m_Channel.format == 2) // kShaderChannelColor &&
																				// kChannelFormatColor
					{
						m_Channel.dimension = 4;
					}

					VertexFormat vertexFormat = MeshHelper.ToVertexFormat(m_Channel.format, version);
					long componentByteSize = (int) MeshHelper.GetFormatSize(vertexFormat);
					byte[] componentBytes = new byte[(int) (m_VertexCount * m_Channel.dimension * componentByteSize)];
					for (int v = 0; v < m_VertexCount; v++) {
						long vertexOffset = m_Stream.offset + m_Channel.offset + m_Stream.stride * v;
						for (int d = 0; d < m_Channel.dimension; d++) {
							long componentOffset = vertexOffset + componentByteSize * d;

							Buffer.BlockCopy(m_VertexData.m_DataSize, componentOffset, componentBytes,
									componentByteSize * (v * m_Channel.dimension + d), componentByteSize);
						}
					}

					if (reader.Endian == EndianType.BigEndian && componentByteSize > 1) // swap bytes
					{
						for (long i = 0; i < componentBytes.length / componentByteSize; i++) {
							byte[] buff = new byte[(int) componentByteSize];
							Buffer.BlockCopy(componentBytes, i * componentByteSize, buff, 0, componentByteSize);
							buff = Buffer.Reverse(buff);
							Buffer.BlockCopy(buff, 0, componentBytes, i * componentByteSize, componentByteSize);
						}
					}

					int[] componentsIntArray = null;
					float[] componentsFloatArray = null;
					if (MeshHelper.IsIntFormat(vertexFormat))
						componentsIntArray = MeshHelper.BytesToIntArray(componentBytes, vertexFormat);
					else
						componentsFloatArray = MeshHelper.BytesToFloatArray(componentBytes, vertexFormat);

					if (version[0] >= 2018) {
						switch (chn) {
						case 0: // kShaderChannelVertex
							m_Vertices = componentsFloatArray;
							break;
						case 1: // kShaderChannelNormal
							m_Normals = componentsFloatArray;
							break;
						case 2: // kShaderChannelTangent
							m_Tangents = componentsFloatArray;
							break;
						case 3: // kShaderChannelColor
							m_Colors = componentsFloatArray;
							break;
						case 4: // kShaderChannelTexCoord0
							m_UV0 = componentsFloatArray;
							break;
						case 5: // kShaderChannelTexCoord1
							m_UV1 = componentsFloatArray;
							break;
						case 6: // kShaderChannelTexCoord2
							m_UV2 = componentsFloatArray;
							break;
						case 7: // kShaderChannelTexCoord3
							m_UV3 = componentsFloatArray;
							break;
						case 8: // kShaderChannelTexCoord4
							m_UV4 = componentsFloatArray;
							break;
						case 9: // kShaderChannelTexCoord5
							m_UV5 = componentsFloatArray;
							break;
						case 10: // kShaderChannelTexCoord6
							m_UV6 = componentsFloatArray;
							break;
						case 11: // kShaderChannelTexCoord7
							m_UV7 = componentsFloatArray;
							break;
						// 2018.2 and up
						case 12: // kShaderChannelBlendWeight
							if (m_Skin == null) {
								InitMSkin();
							}
							for (int i = 0; i < m_VertexCount; i++) {
								for (int j = 0; j < m_Channel.dimension; j++) {
									m_Skin[i].weight[j] = componentsFloatArray[i * m_Channel.dimension + j];
								}
							}
							break;
						case 13: // kShaderChannelBlendIndices
							if (m_Skin == null) {
								InitMSkin();
							}
							for (int i = 0; i < m_VertexCount; i++) {
								for (int j = 0; j < m_Channel.dimension; j++) {
									m_Skin[i].boneIndex[j] = componentsIntArray[i * m_Channel.dimension + j];
								}
							}
							break;
						}
					} else {
						switch (chn) {
						case 0: // kShaderChannelVertex
							m_Vertices = componentsFloatArray;
							break;
						case 1: // kShaderChannelNormal
							m_Normals = componentsFloatArray;
							break;
						case 2: // kShaderChannelColor
							m_Colors = componentsFloatArray;
							break;
						case 3: // kShaderChannelTexCoord0
							m_UV0 = componentsFloatArray;
							break;
						case 4: // kShaderChannelTexCoord1
							m_UV1 = componentsFloatArray;
							break;
						case 5:
							if (version[0] >= 5) // kShaderChannelTexCoord2
							{
								m_UV2 = componentsFloatArray;
							} else // kShaderChannelTangent
							{
								m_Tangents = componentsFloatArray;
							}
							break;
						case 6: // kShaderChannelTexCoord3
							m_UV3 = componentsFloatArray;
							break;
						case 7: // kShaderChannelTangent
							m_Tangents = componentsFloatArray;
							break;
						}
					}
				}
			}
		}
	}

}
