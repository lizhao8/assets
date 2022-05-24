package com.mesh.data;

import java.util.ArrayList;
import java.util.List;

import com.mesh.Data;

public class SubMesh {
	public int index;
	public long firstByte;
	public long indexCount;
	public long topology;// GfxPrimitiveType
	public long triangleCount;
	public long baseVertex;
	public long firstVertex;
	public long vertexCount;
	// public AABB localAABB;
	public Data data;

	public List<Face> faceList = new ArrayList<Face>();

	public SubMesh(int index) {
		this.index = index;
	}

	public SubMesh(Data data, int index) {
		this.data = data;
		firstByte = data.getByName("firstByte").intValue();
		indexCount = data.getByName("indexCount").intValue();
		topology = data.getByName("topology").intValue();
		baseVertex = data.getByName("baseVertex").intValue();
		firstVertex = data.getByName("firstVertex").intValue();
		vertexCount = data.getByName("vertexCount").intValue();
	}

	public SubMesh() {
	}

	public void setFirstByte(long firstByte) {
		this.firstByte = firstByte;
		data.getByName("firstByte").value = firstByte + "";

	}

	public void setIndexCount(long indexCount) {
		this.indexCount = indexCount;
		data.getByName("indexCount").value = indexCount + "";
	}

	public void setFirstVertex(long firstVertex) {
		this.firstVertex = firstVertex;
		data.getByName("firstVertex").value = firstVertex + "";

	}

	public void setVertexCount(long vertexCount) {
		this.vertexCount = vertexCount;
		data.getByName("vertexCount").value = vertexCount + "";

	}

	public class AABB {
		public Vector3 m_Center;
		public Vector3 m_Extent;
	}

	public static class Vector3 {
		public float X;
		public float Y;
		public float Z;

		public Vector3(float x, float y, float z) {
			X = x;
			Y = y;
			Z = z;
		}

		private float kEpsilon = 0.00001F;

		public void Normalize() {
			float length = Length();
			if (length > kEpsilon) {
				float invNorm = 1.0f / length;
				X *= invNorm;
				Y *= invNorm;
				Z *= invNorm;
			} else {
				X = 0;
				Y = 0;
				Z = 0;
			}
		}

		public float Length() {
			return (float) Math.sqrt(LengthSquared());
		}

		public float LengthSquared() {
			return X * X + Y * Y + Z * Z;
		}

	}
}
