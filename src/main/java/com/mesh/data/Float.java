package com.mesh.data;

import java.util.ArrayList;
import java.util.List;

import com.mesh.Data;
import com.tool.BitConverter;

public class Float {
	static int accuracy = 6;
	static String zero = "0.";
	static long multiple = 1;
	static {
		for (int i = 0; i < accuracy; i++) {
			zero += "0";
			multiple *= 10;
		}
	}
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
		value = (float) (Math.round(value * multiple)) / multiple;
	}

	public String stringValue() {
		String value = String.format("%." + accuracy + "f", this.value);
		if (("-" + zero).equals(value)) {
			value = zero;
		}
		return value;
	}

	public String stringValue(int a) {
		String value = String.format("%." + accuracy + "f", this.value * a);
		if (("-" + zero).equals(value)) {
			value = zero;
		}
		return value;
	}

	public Float(String value) {
		this(java.lang.Float.parseFloat(value));
	}

	public Float(String value, int a) {
		this(java.lang.Float.parseFloat(value) * a);
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
