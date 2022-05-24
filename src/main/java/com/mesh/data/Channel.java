package com.mesh.data;

import java.util.List;

import com.mesh.Data;

public class Channel {
	public int index;
	public int stream;
	public int offset;
	public int format;
	public int dimension;
	public Data data;
	List<Float> floatList;
	public String type;
	public int componentByteSize;


	public Channel(Data data, int index) {
		this.data = data;
		this.index = index;
		stream = data.getByName("stream").intValue();
		offset = data.getByName("offset").intValue();
		format = data.getByName("format").intValue();
		dimension = data.getByName("dimension").intValue();
	}

	public int getStream() {
		return stream;
	}

}