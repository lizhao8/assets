package com.assets.data;

import com.assets.ByteReader;
import com.assets.max.SubMesh.Vector3;

public class BlendShapeData {
	public BlendShapeVertex[] vertices;
	public MeshBlendShape[] shapes;
	public MeshBlendShapeChannel[] channels;
	public float[] fullWeights;

	public BlendShapeData(ByteReader reader) {
		int[] version = reader.version;

		if (version[0] > 4 || (version[0] == 4 && version[1] >= 3)) // 4.3 and up
		{
			long numVerts = reader.readInt32();
			vertices = new BlendShapeVertex[(int) numVerts];
			for (int i = 0; i < numVerts; i++) {
				vertices[i] = new BlendShapeVertex(reader);
			}

			long numShapes = reader.readInt32();
			shapes = new MeshBlendShape[(int) numShapes];
			for (int i = 0; i < numShapes; i++) {
				shapes[i] = new MeshBlendShape(reader);
			}

			long numChannels = reader.readInt32();
			channels = new MeshBlendShapeChannel[(int) numChannels];
			for (int i = 0; i < numChannels; i++) {
				channels[i] = new MeshBlendShapeChannel(reader);
			}

			fullWeights = reader.readSingleArray();
		} else {
			/*var m_ShapesSize = reader.ReadInt32();
			var m_Shapes = new MeshBlendShape[m_ShapesSize];
			for (int i = 0; i < m_ShapesSize; i++) {
				m_Shapes[i] = new MeshBlendShape(reader);
			}
			reader.AlignStream();
			var m_ShapeVerticesSize = reader.ReadInt32();
			var m_ShapeVertices = new BlendShapeVertex[m_ShapeVerticesSize]; // MeshBlendShapeVertex
			for (int i = 0; i < m_ShapeVerticesSize; i++) {
				m_ShapeVertices[i] = new BlendShapeVertex(reader);
			}*/
		}
	}

	public class BlendShapeVertex {
		public Vector3 vertex;
		public Vector3 normal;
		public Vector3 tangent;
		public long index;

		public BlendShapeVertex(ByteReader reader) {
			vertex = reader.readVector3();
			normal = reader.readVector3();
			tangent = reader.readVector3();
			index = reader.readUInt32();
		}
	}

	public class MeshBlendShape {
		public long firstVertex;
		public long vertexCount;
		public boolean hasNormals;
		public boolean hasTangents;

		public MeshBlendShape(ByteReader reader) {
			int[] version = reader.version;

			if (version[0] == 4 && version[1] < 3) // 4.3 down
			{
				String name = reader.readAlignedString();
			}
			firstVertex = reader.readUInt32();
			vertexCount = reader.readUInt32();
			if (version[0] == 4 && version[1] < 3) // 4.3 down
			{
				Vector3 aabbMinDelta = reader.readVector3();
				Vector3 aabbMaxDelta = reader.readVector3();
			}
			hasNormals = reader.readBoolean();
			hasTangents = reader.readBoolean();
			if (version[0] > 4 || (version[0] == 4 && version[1] >= 3)) // 4.3 and up
			{
				reader.alignStream();
			}
		}
	}

	public class MeshBlendShapeChannel {
		public String name;
		public long nameHash;
		public long frameIndex;
		public long frameCount;

		public MeshBlendShapeChannel(ByteReader reader) {
			name = reader.readAlignedString();
			nameHash = reader.readUInt32();
			frameIndex = reader.readInt32();
			frameCount = reader.readInt32();
		}
	}

}
