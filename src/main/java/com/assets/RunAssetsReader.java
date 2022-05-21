package com.assets;

import com.assets.data.Mesh;
import com.assets.max.SubMesh;
import com.jogl.Draw;

public class RunAssetsReader {

	public static void main(String[] args) throws Exception {
		AssetsReader reader = new AssetsReader();
		reader.reader();
		Mesh mesh = reader.assetsFileList.get(0).Objects.get(0);
		System.out
				.println("v =" + mesh.m_VertexCount + "," + mesh.m_Vertices.length + "," + mesh.m_Vertices.length / 3);
		System.out.println("vt=" + mesh.m_UV0.length + "," + mesh.m_UV0.length / 2);
		System.out.println("vn=" + mesh.m_Normals.length + "," + mesh.m_Normals.length / 3);
		System.out.println("f =" + mesh.m_Indices.size());
		for (SubMesh subMesh : mesh.m_SubMeshes) {
			System.out.println("subMesh=" + subMesh.indexCount + "," + subMesh.firstByte + "," + subMesh.vertexCount);
		}

		System.out.println("");
		System.out.println("----------------------");
		System.out.println("");

		//AssetsReader.ExportMesh(mesh, "D:\\data\\123\\" + "body.obj");
		// reader.repace(mesh, "D:\\data\\123\\body.obj");
		//reader.repaceMax(mesh, "D:\\data\\123\\max.obj");
		//reader.repaceMax(mesh, "D:\\data\\123\\modify.obj");

		//mesh.save("D:\\data\\123\\body_new.mesh");
		//reader.ExportMesh(mesh, "D:\\data\\123\\" + "body_new.obj");
		/*
		 * System.out .println("v =" + mesh.m_VertexCount + "," + mesh.m_Vertices.length
		 * + "," + mesh.m_Vertices.length / 3); System.out.println("vt=" +
		 * mesh.m_UV0.length + "," + mesh.m_UV0.length / 2); System.out.println("vn=" +
		 * mesh.m_Normals.length + "," + mesh.m_Normals.length / 3);
		 * System.out.println("f =" + mesh.m_Indices.size());
		 * 
		 * for (SubMesh subMesh : mesh.m_SubMeshes) { System.out.println("subMesh=" +
		 * subMesh.indexCount + "," + subMesh.firstByte + "," + subMesh.vertexCount); }
		 * 
		 * Draw draw = new Draw(mesh); draw.starting();
		 */

	}

}
