package com.assets;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.assets.data.Base;
import com.assets.data.EndianType;
import com.assets.data.Int16;
import com.assets.data.Int32;
import com.assets.data.Int64;
import com.assets.data.Matrix4x4;
import com.assets.data.Single;
import com.tool.BitConverter;
import com.assets.data.Text;
import com.assets.max.SubMesh.Vector3;

public class ByteReader {
	public ByteArrayInputStream stream;
	public Charset charset = Charset.forName("UTF-8");
	public List<Base> dataList = new ArrayList<Base>();
	public long position = 0;
	public EndianType Endian = EndianType.BigEndian;
	public int[] version = { 0, 0, 0, 0 };

	public byte[] readUInt8Array() {
		return readUInt8Array(readInt32());
	}

	public byte[] readUInt8Array(long length) {
		byte[] array = new byte[(int) length];
		for (int i = 0; i < length; i++) {
			array[i] = readByte();
		}
		return array;
	}

	public long[] readInt32Array() {
		return readInt32Array(readInt32());
	}

	public long[] readInt32Array(long length) {
		long[] array = new long[(int) length];
		for (int i = 0; i < length; i++) {
			array[i] = readInt32();
		}
		return array;
	}

	public long[] readUInt32Array() {
		return readUInt32Array(readInt32());
	}

	public long[] readUInt32Array(long length) {
		long[] array = new long[(int) length];
		for (int i = 0; i < length; i++) {
			array[i] = readUInt32();
		}
		return array;
	}

	public Matrix4x4[] readMatrixArray() {
		long length = readInt32();
		Matrix4x4[] array = new Matrix4x4[(int) length];
		for (int i = 0; i < length; i++) {
			array[i] = new Matrix4x4(readSingleArray(16));
		}
		return array;
	}

	public float[] readSingleArray() {
		return readSingleArray(readInt32());
	}

	public float[] readSingleArray(long length) {
		float[] array = new float[(int) length];
		for (int i = 0; i < length; i++) {
			array[i] = readSingle();
		}
		return array;
	}

	public <T> float readSingle() {
		int length = 4;
		byte[] bytes = new byte[length];
		Base base = readBytes(bytes, 0, length);
		float value = BitConverter.toFloat(bytes);
		dataList.add(new Single(base, value));
		return value;
	}

	public Vector3 readVector3() {
		return new Vector3(readSingle(), readSingle(), readSingle());
	}

	public String readAlignedString() {
		long length = readInt32();
		if (length > 0) {
			byte[] bytes = readBytes((int) length);
			alignStream(4);
			return new String(bytes, charset);
		}
		return "";
	}

	public void reset() {
		stream.reset();
	}

	public byte[] skip(long length) {
		return readBytes((int) length);
	}

	public void reset(long position) {
		stream.reset();
		stream.skip(position);
		this.position = position;
	}

	public void alignStream() {
		alignStream(4);
	}

	public void alignStream(long alignment) {
		long mod = position % alignment;
		if (mod != 0) {
			position += alignment - mod;
			stream.skip(alignment - mod);
		}
	}

	public ByteReader(ByteArrayInputStream stream) {
		super();
		this.stream = stream;
	}

	public boolean readBoolean() {
		return readByte() != 0;
	}

	public byte[] readBytes(int length) {
		byte[] bytes = new byte[length];
		Base base = readBytes(bytes, 0, length);
		dataList.add(new com.assets.data.Byte(base));
		return bytes;
	}

	public byte readByte() {
		Class<com.assets.data.Byte> clazz = com.assets.data.Byte.class;
		byte[] bytes = new byte[1];
		Base base = readBytes(bytes, 0, bytes.length);
		dataList.add(new com.assets.data.Byte(base));
		return bytes[0];
	}

	public void read(byte[] b, int off, int len) {
		Base base = readBytes(b, off, len);
		dataList.add(new com.assets.data.Byte(base));
	}

	/**
	 * base
	 * 
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	private Base readBytes(byte[] b, int off, int len) {
		long start = position;
		stream.read(b, off, len);
		position += len;
		long end = position;
		println(new String(b, charset));
		return new Base(start, end, len, b);
	}

	public String readString() {
		List<Byte> byteList = new ArrayList<Byte>();
		long start = position;
		long data = 0;
		while ((data = stream.read()) != 0) {
			byteList.add((byte) data);
			position++;
		}
		position++;
		long end = position;

		byte[] bytes = new byte[byteList.size()];
		for (int i = 0; i < byteList.size(); i++) {
			bytes[i] = byteList.get(i);
		}
		String value = new String(bytes, charset);
		println(value);
		dataList.add(new Text(start, end, bytes.length, bytes, value));
		return value;
	}

	public long readInt64() {
		return readInt(8);
	}

	public long readUInt64() {
		return readUInt(8);
	}

	public long readInt32() {
		return readInt(4);
	}

	public long readUInt32() {
		return readUInt(4);
	}

	public long readInt16() {
		return (long) readInt(2);
	}

	public long readUInt16() {
		return (long) readUInt(2);
	}

	public long readUInt(int length) {
		return readInt(length, false);
	}

	public long readInt(int length) {
		return readInt(length, true);
	}

	public long readInt(int length, boolean u) {
		byte[] bytes = new byte[length];
		Base _base = readBytes(bytes, 0, length);
		long value = bytesToLong(bytes, u);
		Base base = null;
		if (length == 2) {
			base = new Int16(_base, (short) value);
		} else if (length == 4) {
			base = new Int32(_base, (long) value);
		} else if (length == 8) {
			base = new Int64(_base, value);
		} else {
			assert true : "readInt闀垮害涓嶆敮鎸�-" + length;
		}
		dataList.add(base);
		return value;
	}

	public long bytesToLong(byte[] bytes, boolean u) {
		if (Endian == EndianType.BigEndian) {
			return Long.parseLong(encodeHexString(bytes, u), 16);
		}
		return _bytesToLong(bytes, u);
	}

	public String encodeHexString(byte[] data, boolean u) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
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

	public static float[] toArray(List<Float> data) {
		float[] floats = new float[data.size()];
		for (int i = 0; i < data.size(); i++) {
			floats[i] = data.get(i);
		}
		return floats;
	}
	
	private void println(Object value) {
		// System.out.println(value);
	}

	/*
	 * public static void main(String[] args) { byte[] bytes = { -68, 113, 58, 63 };
	 * // byte[] bytes = { 0, 0, 0, 0, -68, 113, 58, 63 };
	 * 
	 * ByteReader byteReader = new ByteReader(null); // byteReader.Endian =
	 * EndianType.BigEndian; // byteReader.Endian = EndianType.LittleEndian; //
	 * double value = BitConverter.toDouble(bytes); float value =
	 * BitConverter.toFloat(bytes); System.out.println(new BigDecimal(value));
	 * System.out.println(new BigDecimal(value + 0.0000000000000000000001));
	 * 
	 * }
	 */

}
