package com.mesh.data;

import java.util.ArrayList;
import java.util.List;

import com.mesh.Data;
import com.tool.BitConverter;

public class Float {
	public List<Data> dataList = new ArrayList<Data>();
	public float value;

	public Float(List<Data> dataList) {
		super();
		this.dataList = dataList;
		byte[] bytes = new byte[4];
		for (int i = 0; i < dataList.size(); i++) {
			bytes[i] = dataList.get(i).byteValue();
		}
		value = BitConverter.toFloat(bytes);
	}

	public Float(float value) {
		super();
		this.value = value;
		byte[] bytes = BitConverter.getBytes(value);

		for (byte b : bytes) {
			Data data = new Data(null);
			data.index = "0";
			data.type = "UInt8";
			data.name = "data";
			data.value = ((int) b) + "";
			dataList.add(data);
		}
	}

	@Override
	public String toString() {
		return value + "";
	}

}
