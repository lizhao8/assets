package com.mesh.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mesh.Data;
import com.tool.BitConverter;

public class Long {
	public List<Data> dataList = new ArrayList<Data>();
	public long value;

	public Long(List<Data> dataList) {
		super();
		this.dataList = dataList;
		byte[] bytes = new byte[dataList.size()];
		for (int i = 0; i < dataList.size(); i++) {
			bytes[i] = (byte) dataList.get(i).intValue();
		}
		value = _bytesToLong(bytes, false);
	}

	public int intValue() {
		return (int) value;
	}

	public Long(long value) {
		super();
		this.value = value;
		byte[] bytes = _longToBytes(value, false);

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

	public static long _bytesToLong(byte[] bytes, boolean u) {
		long value = 0;
		for (int i = 0; i < bytes.length; i++) {
			value <<= 8;
			byte b = bytes[bytes.length - 1 - i];
			long a = b;
			if (!u || a < 0) {
				a = (b & 0xff);
			}
			value |= a;
		}
		return value;
	}

	public static byte[] _longToBytes(long values, boolean u) {
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
			int offset = 64 - (i + 1) * 8;
			buffer[i] = (byte) ((values >> offset) & 0xff);
		}
		return buffer;
	}

}
