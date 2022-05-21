package com.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.assets.data.Block;
import com.assets.data.Mesh;
import com.assets.data.Node;
import com.assets.data.ObjectInfo;
import com.assets.data.SerializedFile;
import com.assets.data.StreamFile;
import com.assets.data.Unity;
import com.assets.max.SubMesh;
import com.assets.max.SubMesh.AABB;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class AssetsReader {
	String sourceFile = "/4ddec4f0d2ffb3b6e02ac570ce400beb.ys";
	ByteReader reader;
	Unity unity = new Unity();
	List<Node> nodeList = new ArrayList<Node>();
	List<StreamFile> fileList = new ArrayList<StreamFile>();
	String sourceFilePath = "";
	List<SerializedFile> assetsFileList = new ArrayList<SerializedFile>();

	public void reader() {
		sourceFilePath = AssetsReader.class.getResource(sourceFile).getPath();
		File file = new File(sourceFilePath);
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			byte[] buffer = new byte[fileInputStream.available()];
			fileInputStream.read(buffer);
			reader = new ByteReader(new ByteArrayInputStream(buffer));
		} catch (Exception e) {
			e.printStackTrace();
		}
		unity.signature = reader.readString();
		unity.version = reader.readUInt32();
		unity.unityVersion = reader.readString();
		unity.unityRevision = reader.readString();
		if ("UnityFS".equals(unity.signature)) {
			unity.size = reader.readInt64();
			unity.compressedBlocksInfoSize = reader.readUInt32();
			unity.uncompressedBlocksInfoSize = reader.readUInt32();
			unity.flags = reader.readUInt32();
		}
		byte[] compressedBlocksInfoBytes = new byte[(int) unity.compressedBlocksInfoSize];
		reader.read(compressedBlocksInfoBytes, 0, (int) unity.compressedBlocksInfoSize);

		byte[] uncompressedBlocksInfoBytes = new byte[(int) unity.uncompressedBlocksInfoSize];

		LZ4Factory factory = LZ4Factory.fastestInstance();
		LZ4FastDecompressor decompressor = factory.fastDecompressor();
		int numWrite = decompressor.decompress(compressedBlocksInfoBytes, uncompressedBlocksInfoBytes);

		ByteArrayInputStream blocksInfoUncompresseddStream = new ByteArrayInputStream(uncompressedBlocksInfoBytes);

		ByteReader blocksInfoReader = new ByteReader(blocksInfoUncompresseddStream);
		byte[] uncompressedDataHash = blocksInfoReader.readBytes(16);
		long blocksInfoCount = blocksInfoReader.readInt32();
		List<Block> blockInfoList = new ArrayList<Block>();
		for (int i = 0; i < blocksInfoCount; i++) {
			blockInfoList.add(new Block(blocksInfoReader.readInt32(), blocksInfoReader.readInt32(),
					blocksInfoReader.readInt16()));
		}
		long nodesCount = blocksInfoReader.readInt32();
		for (int i = 0; i < nodesCount; i++) {
			nodeList.add(new Node(blocksInfoReader.readInt64(), blocksInfoReader.readInt64(),
					blocksInfoReader.readInt32(), blocksInfoReader.readString()));
		}
		long uncompressedSizeSum = blockInfoList.stream().mapToLong(Block::getUncompressedSize).sum();
		ByteArrayOutputStream blocksStream = new ByteArrayOutputStream((int) uncompressedSizeSum);
		for (Block block : blockInfoList) {
			long compressedSize = block.getCompressedSize();
			byte[] compressedBytes = new byte[(int) compressedSize];
			reader.read(compressedBytes, 0, (int) compressedSize);
			long uncompressedSize = block.getUncompressedSize();
			byte[] uncompressedBytes = new byte[(int) uncompressedSize];
			// int numWrite =
			decompressor.decompress(compressedBytes, uncompressedBytes, (int) uncompressedSize);
			blocksStream.write(uncompressedBytes, 0, (int) uncompressedSize);
		}
		ByteArrayInputStream _blocksStream = new ByteArrayInputStream(blocksStream.toByteArray());

		for (Node node : nodeList) {
			StreamFile streamFile = new StreamFile();
			fileList.add(streamFile);
			streamFile.setPath(node.getPath());
			streamFile.setFileName(new File(streamFile.getPath()).getName());
			byte[] bytes = new byte[(int) node.getSize()];
			_blocksStream.skip(node.getOffset());
			_blocksStream.read(bytes, 0, (int) node.getSize());
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			streamFile.setStream(stream);
			_blocksStream.reset();
		}

		readAssets();

		// dataList.stream().forEach(x -> System.out.println(x.get()));
		System.out.println(unity);

//		try {
//			FileOutputStream fileOutputStream = 
//					new FileOutputStream("D:\\data\\123\\" + objectInfo.m_PathID);
//			fileOutputStream.write(bytes, 0, (int) objectInfo.byteSize);
//			fileOutputStream.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		/*
		 * try { FileOutputStream fileOutputStream = new FileOutputStream(sourceFilePath
		 * + ".new"); int sum = 0; for (Base base : reader.dataList) {
		 * fileOutputStream.write(base.getBytes()); sum += base.getLength(); if (base
		 * instanceof Text) { fileOutputStream.write(0); sum++; }
		 * System.out.println(sum); } fileOutputStream.close(); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}

	public void readAssets() {

		for (StreamFile streamFile : fileList) {
			ByteReader reader = new ByteReader(streamFile.getStream());
			SerializedFile assetsFile = new SerializedFile(reader);
			assetsFileList.add(assetsFile);
			assetsFile.setVersion(unity.unityRevision);
			for (ObjectInfo objectInfo : assetsFile.m_Objects) {
				if (objectInfo.m_PathID != 4834823273777773367L) {
					continue;
				}
				/*
				 * reader.reset(objectInfo.byteStart); byte[] bytes = new byte[(int)
				 * objectInfo.byteSize]; reader.stream.read(bytes, 0, (int)
				 * objectInfo.byteSize);
				 * 
				 * try { FileOutputStream fileOutputStream = new FileOutputStream( "D:\\data\\"
				 * + objectInfo.m_PathID); fileOutputStream.write(bytes, 0, (int)
				 * objectInfo.byteSize); fileOutputStream.close(); } catch (Exception e) {
				 * e.printStackTrace(); }
				 * 
				 * reader.stream.reset();
				 */
			}

		}

		for (SerializedFile assetsFile : assetsFileList) {
			for (ObjectInfo objectInfo : assetsFile.m_Objects) {
				if (objectInfo.m_PathID != 4834823273777773367L) {
					continue;
				}
				assetsFile.AddObject(new Mesh(assetsFile, objectInfo, this));
				System.out.println(assetsFile.Objects);
			}
		}
	}

	StringBuilder sb;

	private void appendLine(String string) {
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	private void appendFormat(String string, float... params) {
		Float[] floats = new Float[params.length];
		for (int i = 0; i < params.length; i++) {
			floats[i] = params[i];
		}
		string = String.format(string, floats);
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	public void appendFormat(String string, long... params) {
		Long[] longs = new Long[params.length];
		for (int i = 0; i < params.length; i++) {
			longs[i] = params[i];
		}
		string = String.format(string, longs);
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	public boolean ExportMesh(Mesh m_Mesh, String exportPath) {
		if (m_Mesh.m_VertexCount <= 0)
			return false;
		sb = new StringBuilder();
		appendLine("g " + m_Mesh.m_Name);

		// region Vertices
		if (m_Mesh.m_Vertices == null || m_Mesh.m_Vertices.length == 0) {
			return false;
		}
		int c = 3;
		if (m_Mesh.m_Vertices.length == m_Mesh.m_VertexCount * 4) {
			c = 4;
		}
		for (int v = 0; v < m_Mesh.m_VertexCount; v++) {
			appendFormat("v %s %s %s", -m_Mesh.m_Vertices[v * c], m_Mesh.m_Vertices[v * c + 1],
					m_Mesh.m_Vertices[v * c + 2]);
		}
		// endregion

		// region UV
		if (m_Mesh.m_UV0 != null && m_Mesh.m_UV0.length > 0) {
			c = 2;
			if (m_Mesh.m_UV0.length == m_Mesh.m_VertexCount * 4) {
				c = 4;
			} else if (m_Mesh.m_UV0.length == m_Mesh.m_VertexCount * 3) {
				c = 3;
			}
			//for (int v = 0; v < m_Mesh.m_VertexCount; v++) {
			for (int v = 0; v < m_Mesh.m_UV0.length/c; v++) {
				appendFormat("vt %s %s", m_Mesh.m_UV0[v * c], m_Mesh.m_UV0[v * c + 1]);
			}
		}
		// endregion

		// region Normals
		if (m_Mesh.m_Normals != null && m_Mesh.m_Normals.length > 0) {
			if (m_Mesh.m_Normals.length == m_Mesh.m_VertexCount * 3) {
				c = 3;
			} else if (m_Mesh.m_Normals.length == m_Mesh.m_VertexCount * 4) {
				c = 4;
			}
			for (int v = 0; v < m_Mesh.m_VertexCount; v++) {
				appendFormat("vn %s %s %s", -m_Mesh.m_Normals[v * c], m_Mesh.m_Normals[v * c + 1],
						m_Mesh.m_Normals[v * c + 2]);
			}
		}
		// endregion

		// region Face
		int sum = 0;
		for (int i = 0; i < m_Mesh.m_SubMeshes.length; i++) {
			appendLine("g " + m_Mesh.m_Name + "_" + i);
			int indexCount = (int) m_Mesh.m_SubMeshes[i].indexCount;
			int end = sum + indexCount / 3;
			for (int f = sum; f < end; f++) {
				long long1 = m_Mesh.m_Indices.get(f * 3 + 2) + 1;
				long long2 = m_Mesh.m_Indices.get(f * 3 + 1) + 1;
				long long3 = m_Mesh.m_Indices.get(f * 3) + 1;
				appendFormat("f %s/%s/%s %s/%s/%s %s/%s/%s", long1, long1, long1, long2, long2, long2, long3, long3,
						long3);
			}
			sum = end;
		}
		// endregion

		try {
			String result = sb.toString();
			result = result.replaceAll("NaN", "0");
			File file = new File(exportPath);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(result);
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public Mesh repace(Mesh mesh, String objectPath) throws Exception {
		ObjToMesh objToMesh = new ObjToMesh();
		objToMesh.setM_Mesh(mesh);
		objToMesh.setObjectPath(objectPath);
		objToMesh.run();
		return mesh;
	}

	public Mesh repaceMax(Mesh mesh, String objectPath) throws Exception {
		ObjToMesh objToMesh = new ObjToMesh();
		objToMesh.setM_Mesh(mesh);
		objToMesh.setObjectPath(objectPath);
		objToMesh.runMax();
		return mesh;
	}	
}
