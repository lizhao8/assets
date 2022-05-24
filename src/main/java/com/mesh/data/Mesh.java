package com.mesh.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;

import com.mesh.Data;

public class Mesh {

	public List<Float> m_Vertices = new ArrayList<Float>();
	public List<Float> m_Normals = new ArrayList<Float>();

	public List<Float> m_Colors = new ArrayList<Float>();
	public List<Float> m_UV0 = new ArrayList<Float>();
	public List<Float> m_UV1 = new ArrayList<Float>();
	public List<Float> m_UV2 = new ArrayList<Float>();
	public List<Float> m_UV3 = new ArrayList<Float>();
	public List<Float> m_UV4 = new ArrayList<Float>();
	public List<Float> m_UV5 = new ArrayList<Float>();
	public List<Float> m_UV6 = new ArrayList<Float>();
	public List<Float> m_UV7 = new ArrayList<Float>();
	public List<Float> m_Tangents = new ArrayList<Float>();
	public List<Float> m_Skin_weight = new ArrayList<Float>();
	public List<Float> m_Skin_boneIndex = new ArrayList<Float>();
	public List<Long> m_Indices = new ArrayList<Long>();

	public String name = "";
	List<Data> m_Channels;

	public int getVertexCount() {
		return m_Vertices.size() / 3;
	}

	public List<SubMesh> subMeshList = new ArrayList<SubMesh>();
	public List<Face> faceList = new ArrayList<Face>();
	public List<Element> elementList = new ArrayList<Element>();
	public Data data;
	public List<Channel> channelList = new ArrayList<Channel>();
	public Map<Integer, List<Channel>> streamChannelMap = new HashedMap<Integer, List<Channel>>();

	public List<Point> pointList = new ArrayList<Point>();

	public Mesh(Data _data) {
		this.data = _data;
		name = _data.getByName("m_Name").value;
		Data m_VertexData = _data.getByType("VertexData");
		int m_VertexCount = m_VertexData.getByName("m_VertexCount").intValue();
		m_Channels = m_VertexData.getByName("m_Channels").get(0).childList;
		List<Data> m_DataSize = m_VertexData.getByName("m_DataSize").childList;
		List<Data> m_IndexBuffer = _data.getByName("m_IndexBuffer").get(0).childList;
		List<Data> m_SubMeshes = _data.getByName("m_SubMeshes").get(0).childList;
		for (int i = 0; i < m_Channels.size(); i++) {
			Data data = m_Channels.get(i);
			Channel channel = new Channel(data, i);
			channelList.add(channel);

			switch (i) {
			case 0:
				channel.floatList = m_Vertices;
				channel.type = "m_Vertices";
				break;
			case 1:
				channel.floatList = m_Normals;
				channel.type = "m_Normals";
				break;
			case 2:
				channel.floatList = m_Tangents;
				channel.type = "m_Tangents";
				break;
			case 3:
				channel.floatList = m_Colors;
				channel.type = "m_Colors";
				break;
			case 4:
				channel.floatList = m_UV0;
				channel.type = "m_UV0";
				break;
			case 5:
				channel.floatList = m_UV1;
				channel.type = "m_UV1";
				break;
			case 6:
				channel.floatList = m_UV2;
				channel.type = "m_UV2";
				break;
			case 7:
				channel.floatList = m_UV3;
				channel.type = "m_UV3";
				break;
			case 8:
				channel.floatList = m_UV4;
				channel.type = "m_UV4";
				break;
			case 9:
				channel.floatList = m_UV5;
				channel.type = "m_UV5";
				break;
			case 10:
				channel.floatList = m_UV6;
				channel.type = "m_UV6";
				break;
			case 11:
				channel.floatList = m_UV7;
				channel.type = "m_UV7";
				break;
			// 2018.2 and up
			case 12:
				channel.floatList = m_Skin_weight;
				channel.type = "m_Skin_weight";
				break;
			case 13:
				channel.floatList = m_Skin_boneIndex;
				channel.type = "m_Skin_boneIndex";
				break;
			default:
				throw new RuntimeException("m_Channel");
			}
		}
		for (Iterator<Data> iterator = m_IndexBuffer.iterator(); iterator.hasNext();) {
			List<Data> list = new ArrayList<Data>();
			list.add(iterator.next());
			list.add(iterator.next());
			m_Indices.add(new Long(list));
		}

		for (int i = 0; i < m_SubMeshes.size(); i++) {
			subMeshList.add(new SubMesh(m_SubMeshes.get(i), i));
		}

		streamChannelMap = channelList.stream()
				.collect(Collectors.groupingBy(Channel::getStream, TreeMap::new, Collectors.toList()));

		int dataSizeIndex = 0;
		for (Integer stream : streamChannelMap.keySet()) {
			List<Channel> _channelList = streamChannelMap.get(stream);
			for (int v = 0; v < m_VertexCount; v++) {
				for (Channel channel : _channelList) {
					if (channel.dimension == 0) {
						continue;
					}
					int componentByteSize = 0;
					switch (channel.format) {
					case 0:
					case 11:
						componentByteSize = 4;
						break;
					case 2:
						componentByteSize = 1;
						break;
					default:
						throw new RuntimeException("format");
					}
					channel.componentByteSize = componentByteSize;

					List<Float> floatList = channel.floatList;
					List<Data> dataList = new ArrayList<Data>();
					for (int d = 0; d < channel.dimension; d++) {
						List<Data> list = new ArrayList<Data>();
						for (int c = 0; c < componentByteSize; c++) {
							list.add(m_DataSize.get(dataSizeIndex + c));
							dataList.add(m_DataSize.get(dataSizeIndex + c));
						}
						Float float1 = new Float(list);
						floatList.add(float1);
						dataSizeIndex += componentByteSize;
					}
				}
			}
		}
		pointList = new ArrayList<Point>();
		int sum = 0;
		for (int i = 0; i < subMeshList.size(); i++) {
			SubMesh subMesh = subMeshList.get(i);
			List<Face> faceList = new ArrayList<Face>();

			int indexCount = (int) subMesh.indexCount;
			int end = sum + indexCount;
			for (int f = sum; f < end; f += 3) {
				Face face = new Face();
				face.addIndex(m_Indices.get(f + 0));
				face.addIndex(m_Indices.get(f + 1));
				face.addIndex(m_Indices.get(f + 2));

				for (Long index : face.indexList) {
					Float x = m_Vertices.get(index.intValue() * 3 + 0);
					Float y = m_Vertices.get(index.intValue() * 3 + 1);
					Float z = m_Vertices.get(index.intValue() * 3 + 2);

					Point point = new Point(x, y, z);
					point.index = new Index(index);
					int pointIndex = pointList.indexOf(point);
					if (pointIndex >= 0) {
						point = pointList.get(pointIndex);
					} else {
						pointList.add(point);
					}
					for (Channel channel : channelList) {
						if (channel.dimension == 0) {
							continue;
						}
						if (channel.floatList.isEmpty()) {
							continue;
						}

						List<Float> list = new ArrayList<Float>();
						for (int d = 0; d < channel.dimension; d++) {
							list.add(channel.floatList.get(index.intValue() * channel.dimension + d));
							// System.out.println(channel.type+","+channel.floatList.size()+","+(index.intValue()*channel.dimension
							// + d));
						}
						// m_Normals m_UV0
						if ("m_Normals".equals(channel.type)) {
							point.normal = new Normal(list.get(0), list.get(1), list.get(2));
						} else if ("m_UV0".equals(channel.type)) {
							point.texture = new Texture(list.get(0), list.get(1));
						}
						point.floatMap.put(channel, list);
					}
					// System.out.println("-------------------------------");
					face.addPoint(point);
				}
				faceList.add(face);
				this.faceList.add(face);
			}
			subMesh.faceList = faceList;

			sum = end;
		}

		printChannal("Mesh over");
	}

	private void printChannal(String log) {
		System.out.println("----------" + log + " printChannal begin----------");
		System.out.println("pointSize=" + pointList.size());
		System.out.println("faceSize=" + faceList.size());
		System.out.println("VertexCount=" + getVertexCount());
		for (Channel channel : channelList) {
			if (channel.dimension == 0) {
				continue;
			}
			int componentByteSize = channel.componentByteSize;
			if (channel.floatList.isEmpty()) {
				continue;
			}
			System.out.println(channel.type + "," + channel.floatList.size() + "," + channel.dimension + ","
					+ componentByteSize + "," + (getVertexCount() * channel.dimension));
		}
		int indexCount = 0;
		for (SubMesh subMesh : subMeshList) {
			System.out.println("subMesh," + subMesh.indexCount);
			indexCount += subMesh.indexCount;
		}
		System.out.println("subMesh,count=" + indexCount + "," + indexCount / 3);

		System.out.println("----------" + log + " printChannal end----------");

	}

	public void reset() {

		/*
		 * for (int i = 0; i < 5000; i++) { Face face = faceList.remove(0);
		 * m_Indices.removeAll(face.indexList); for (Point point : face.pointList) {
		 * point.faceList.remove(face); if (point.faceList.isEmpty()) { for (Channel
		 * channel : channelList) { if (channel.dimension == 0) { continue; } if
		 * (channel.floatList.isEmpty()) { continue; } List<Float>
		 * floatList=point.floatMap.remove(channel);
		 * channel.floatList.removeAll(floatList); } pointList.remove(point); } } }
		 */

		printChannal("reset begin");

		// data.getByName("m_Name").value=name;
		Data m_VertexData = data.getByType("VertexData");
		m_VertexData.getByName("m_VertexCount").value = getVertexCount() + "";
		List<Data> m_DataSize = new ArrayList<Data>();
		m_VertexData.getByName("m_DataSize").childList = m_DataSize;

		List<Data> m_IndexBuffer = new ArrayList<Data>();
		data.getByName("m_IndexBuffer").get(0).childList = m_IndexBuffer;

		// List<Data> m_SubMeshes = new ArrayList<Data>();
		// data.getByName("m_SubMeshes").get(0).childList = m_SubMeshes;

		for (Iterator<Long> iterator = m_Indices.iterator(); iterator.hasNext();) {
			m_IndexBuffer.addAll(iterator.next().dataList);
		}

		/*
		 * for (int i = 0; i < 3000; i++) { m_IndexBuffer.remove(m_IndexBuffer.size() -
		 * 1); }
		 * 
		 * subMeshList.get(1).setIndexCount(subMeshList.get(1).indexCount - 3000);
		 */

		for (Channel channel : channelList) {

			switch (channel.index) {
			case 0:
				channel.floatList = m_Vertices;
				// channel.floatList = new ArrayList<Float>();
				break;
			case 1:
				channel.floatList = m_Normals;
				// channel.floatList = new ArrayList<Float>();
				break;
			case 2:
				channel.floatList = m_Tangents;
				break;
			case 3:
				channel.floatList = m_Colors;
				break;
			case 4:
				channel.floatList = m_UV0;
				// channel.floatList = new ArrayList<Float>();
				break;
			case 5:
				channel.floatList = m_UV1;
				break;
			case 6:
				channel.floatList = m_UV2;
				break;
			case 7:
				channel.floatList = m_UV3;
				break;
			case 8:
				channel.floatList = m_UV4;
				break;
			case 9:
				channel.floatList = m_UV5;
				break;
			case 10:
				channel.floatList = m_UV6;
				break;
			case 11:
				channel.floatList = m_UV7;
				break;
			// 2018.2 and up
			case 12:
				channel.floatList = m_Skin_weight;
				break;
			case 13:
				channel.floatList = m_Skin_boneIndex;
				break;
			default:
				throw new RuntimeException("m_Channel");
			}
		}
		for (Integer stream : streamChannelMap.keySet()) {
			List<Channel> _channelList = streamChannelMap.get(stream);
			
			for (Point point : pointList) {
				for (Channel channel : _channelList) {
					if (channel.dimension == 0) {
						continue;
					}
					List<Float> floatList = point.floatMap.get(channel);
					for (Float float1 : floatList) {
						m_DataSize.addAll(float1.dataList);
					}
				}

			}
			long size = m_DataSize.size();
			size = (size + (16L - 1L)) & ~(16L - 1L);

			for (int i = m_DataSize.size(); i < size; i++) {
				Data data = new Data(null);
				data.index = "0";
				data.type = "UInt8";
				data.name = "data";
				data.value = "0";
				m_DataSize.add(data);
			}
		}

		resetSubMesh();

		printChannal("reset over");

	}

	public void resetSubMesh() {
		long firstByte = 0;
		long firstVertex = 0;
		for (SubMesh subMesh : subMeshList) {
			subMesh.setIndexCount(subMesh.faceList.size() * 3);
			subMesh.setFirstByte(firstByte);
			long vertexCount = getVertexCount(subMesh.faceList);
			subMesh.setVertexCount(vertexCount);
			subMesh.setFirstVertex(firstVertex);
			firstByte = subMesh.faceList.size() * 3 * 2;
			firstVertex += vertexCount;
		}
	}

	public long getVertexCount(List<Face> faceList) {
		List<Point> allPointList = new ArrayList<>();
		for (Face face : faceList) {
			List<Point> pointList = face.pointList;
			pointList.removeAll(allPointList);
			allPointList.addAll(pointList);
		}
		return allPointList.size();
	}
}
