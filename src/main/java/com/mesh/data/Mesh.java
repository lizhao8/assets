package com.mesh.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.assets.max.SubMesh;
import com.jogl.base.Element;
import com.jogl.base.Face;
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
	public List<Channel> channelList = new ArrayList<Channel>();
	List<Data> m_Channels;
	public int m_VertexCount;

	public List<SubMesh> subMeshList = new ArrayList<SubMesh>();
	public List<Face> faceList = new ArrayList<Face>();
	public List<Element> elementList = new ArrayList<Element>();
	public Data data;

	public Mesh(Data _data) {
		this.data = _data;
		name = _data.getByName("m_Name").value;
		Data m_VertexData = _data.getByType("VertexData");
		m_VertexCount = m_VertexData.getByName("m_VertexCount").intValue();
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
				break;
			case 1:
				channel.floatList = m_Normals;
				break;
			case 2:
				channel.floatList = m_Tangents;
				break;
			case 3:
				channel.floatList = m_Colors;
				break;
			case 4:
				channel.floatList = m_UV0;
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
		for (Iterator<Data> iterator = m_IndexBuffer.iterator(); iterator.hasNext();) {
			List<Data> list = new ArrayList<Data>();
			list.add(iterator.next());
			list.add(iterator.next());
			m_Indices.add(new Long(list));
		}

		for (int i = 0; i < m_SubMeshes.size(); i++) {
			subMeshList.add(new SubMesh(m_SubMeshes.get(i), i));
		}

		for (int i = 0; i < m_Channels.size(); i++) {
			Data data = m_Channels.get(i);
			Channel channel = new Channel(data, i);
			channelList.add(channel);

			switch (i) {
			case 0:
				channel.floatList = m_Vertices;
				break;
			case 1:
				channel.floatList = m_Normals;
				break;
			case 2:
				channel.floatList = m_Tangents;
				break;
			case 3:
				channel.floatList = m_Colors;
				break;
			case 4:
				channel.floatList = m_UV0;
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
		
		Map<Integer, List<Channel>> streamChannelMap = channelList.stream()
				.collect(Collectors.groupingBy(Channel::getStream, TreeMap::new, Collectors.toList()));
		
		
		
		
		int index = 0;
		System.out.println(m_DataSize.size());
		for (Integer stream : streamChannelMap.keySet()) {
			List<Channel> channelList = streamChannelMap.get(stream);
			for (int v = 0; v < m_VertexCount; v++) {
				for (Channel channel : channelList) {
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

					List<Float> floatList = channel.floatList;
					for (int d = 0; d < channel.dimension; d++) {

						List<Data> list = new ArrayList<Data>();
						for (int c = 0; c < componentByteSize; c++) {
							list.add(m_DataSize.get(index + c));
							System.out.println(index + c);
						}
						Float float1 = new Float(list);
						floatList.add(float1);
						index += componentByteSize;

					}
				}
			}
		}

	}

	public void reset() {
		// data.getByName("m_Name").value=name;
		Data m_VertexData = data.getByType("VertexData");
		m_VertexData.getByName("m_VertexCount").value = m_VertexCount + "";

		List<Data> m_DataSize = new ArrayList<Data>();
		data.getByName("m_DataSize").childList = m_DataSize;

		List<Data> m_IndexBuffer = new ArrayList<Data>();
		data.getByName("m_IndexBuffer").get(0).childList = m_IndexBuffer;

		List<Data> m_SubMeshes = new ArrayList<Data>();
		data.getByName("m_SubMeshes").get(0).childList = m_IndexBuffer;

		for (Iterator<Long> iterator = m_Indices.iterator(); iterator.hasNext();) {
			m_IndexBuffer.addAll(iterator.next().dataList);
		}

		Map<Integer, List<Channel>> streamChannelMap = channelList.stream()
				.collect(Collectors.groupingBy(Channel::getStream, TreeMap::new, Collectors.toList()));

		int index = 0;

		for (Integer stream : streamChannelMap.keySet()) {
			List<Channel> channelList = streamChannelMap.get(stream);
			for (int v = 0; v < m_VertexCount; v++) {
				for (Channel channel : channelList) {
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
					List<Float> floatList = channel.floatList;

					System.out.println(channel.dimension+","+componentByteSize+","+floatList.size());
					
					for (int d = 0; d < channel.dimension; d++) {

						/*List<Data> list = new ArrayList<Data>();

						for (int c = 0; c < componentByteSize; c++) {
							list.add(m_DataSize.get(index + c));
						}
						Float float1 = new Float(list);
						floatList.add(float1);
						index += componentByteSize;
						 */
					}
				}
			}
		}

	}
}
