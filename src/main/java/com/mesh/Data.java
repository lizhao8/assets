package com.mesh;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import com.assets.data.EndianType;
import com.tool.BitConverter;

public class Data {

	public int deep;
	public String index;
	public String type;
	public String name;
	public String value;

	public byte byteValue() {
		return (byte) Integer.valueOf(value).intValue();
	}

	public int intValue() {
		return Integer.valueOf(value);
	}

	public int longValue() {
		return Integer.valueOf(value);
	}

	public float floatValue() {
		return Float.valueOf(value);
		// return BitConverter.toFloat(longToBytes(Integer.valueOf(value), 4));
	}

	/*
	 * public byte[] longToBytes(long value, int length) { String hexString =
	 * Long.toHexString(value); byte[] byteArray = new byte[length]; for (int i = 0;
	 * i < length; i++) { String hex = ""; if (hexString.length() == 0) { break; }
	 * else if (hexString.length() > 1) { hex =
	 * hexString.substring(hexString.length() - 2, hexString.length()); if
	 * (hexString.length() == 2) { hexString = ""; } else { hexString =
	 * hexString.substring(0, hexString.length() - 2); } } else { hex = hexString;
	 * hexString = ""; } byteArray[i] = (byte) Integer.parseInt(hex, 16); }
	 * 
	 * return byteArray; }
	 */
	public Data parent;

	public List<Data> childList = new ArrayList<Data>();

	public Data(Data parent) {
		super();
		this.parent = parent;
		if (parent != null) {
			deep = parent.deep + 1;
			parent.add(this);
		}
	}

	public void remove(Data data) {
		childList.remove(data);
	}

	public void add(Data data) {
		childList.add(data);
	}

	public Data get(int index) {
		return childList.get(index);
	}

	@Override
	public String toString() {
		return value + "";
	}

	public void save(BufferedWriter writer) throws Exception {
		save(writer, null, 0);
	}

	public void save(BufferedWriter writer, Data parent, int deepAdd) throws Exception {
		if (deep == 0 && parent != null) {
			deep = parent.deep + 1;
		}
		if (this instanceof Array) {
			((Array) this).save(writer);
			return;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < deep + deepAdd; i++) {
			stringBuilder.append(" ");
		}
		stringBuilder.append(index);
		stringBuilder.append(" ");
		stringBuilder.append(type);
		stringBuilder.append(" ");
		stringBuilder.append(name);

		if (value != null && !"".equals(value)) {
			if (value.startsWith("-")&&"UInt8".equals(type)) {
				try {
					byte b = Byte.valueOf(value);
					if (((int) b) < 0) {
						value = (b & 0xff) + "";
					}
				} catch (Exception e) {
					//System.out.println(value);
				}
			}
			stringBuilder.append(" = ");
			stringBuilder.append(value);
		}

		writer.append(stringBuilder);
		writer.newLine();
		for (Data data : childList) {
			data.save(writer, this, deepAdd);
		}
	}

	public Data getByType(String type) {
		return childList.stream().filter(x -> type.equals(x.type)).findFirst().get();
	}

	public Data getByName(String name) {
		return childList.stream().filter(x -> name.equals(x.name)).findFirst().get();
	}

}
