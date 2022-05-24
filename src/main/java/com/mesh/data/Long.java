package com.mesh.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.mesh.Data;
import com.tool.BitConverter;

public class Long implements Comparable<Long> {
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
		byte[] bytes = _longToBytes(value, 2, false);

		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[bytes.length - i - 1];
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

	public static byte[] _longToBytes(long values, int length, boolean u) {
		byte[] buffer = new byte[length];
		for (int i = 0; i < length; i++) {
			int offset = 8 * length - (i + 1) * 8;
			buffer[i] = (byte) ((values >> offset) & 0xff);
		}
		return buffer;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Long other = (Long) obj;
		return value == other.value;
	}

	@Override
	public int compareTo(Long o) {

		return (int) (this.value - o.value);
	}

}
