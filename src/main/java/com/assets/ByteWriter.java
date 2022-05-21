package com.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
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

public class ByteWriter {
	public ByteArrayOutputStream stream=new ByteArrayOutputStream();
	public Charset charset = Charset.forName("UTF-8");
	public List<Base> dataList = new ArrayList<Base>();
	public long position = 0;
	public long byteStart = 0;
	public EndianType Endian = EndianType.BigEndian;
	public int[] version = { 0, 0, 0, 0 };

	
	
	public void writeUInt8Array(byte[] bytes) {
		writeInt32(bytes.length);
		for (byte b : bytes) {
			writeByte(b);
		}		
	}

	public void writeInt32Array(long[] longs) {
		writeInt32(longs.length);
		for (long l : longs) {
			writeInt32(l);
		}
	}
	public void writeUInt32Array(long[] longs) {
		writeInt32(longs.length);
		for (long l : longs) {
			writeUInt32(l);
		}
	}
	public void writeMatrixArray(Matrix4x4[] matrix4x4s) {
		writeInt32(matrix4x4s.length);
		for (Matrix4x4 matrix4x4 : matrix4x4s) {
			writeSingleArray(matrix4x4.floats,false);
		}
	}

	public void writeSingleArray(float[] floats,boolean writeLength) {
		if(writeLength) {
			writeInt32(floats.length);	
		}
		for (int i = 0; i < floats.length; i++) {
			writeSingle(floats[i]);;
		}
	}

	public void writeSingle(float value) {
		int length = 4;
		byte[] bytes = BitConverter.getBytes(value);
		writeBytes(bytes);
	}

	public void writeVector3(Vector3 vector3) {
		writeSingle(vector3.X);
		writeSingle(vector3.Y);
		writeSingle(vector3.Z);
	}

	public void writeAlignedString(String string) {
		writeInt32(string.length());
		int length = string.length();
		if (length > 0) {
			writeBytes(string.getBytes(charset));
			alignStream();
		}
	}
	public void alignStream() {
		alignStream(4);
	}
	public void alignStream(long alignment) {
		//long mod = (position+byteStart) % alignment;
		long mod = position % alignment;
		//21015056
		if (mod != 0) {
			writeBytes(new byte[(int) (alignment - mod)]);
		}
	}
	public void skip(int length) {
		byte[] bytes= new byte[length];
		writeBytes(bytes);
	}
/*
	public void reset() {
		stream.reset();
	}



	public void reset(long position) {
		stream.reset();
		stream.skip(position);
		this.position = position;
	}




*/
	public void writeBoolean(boolean bool) {
		//readByte() != 0;		
		writeByte(bool ? (byte)0x01 :(byte)0x00);
	}
/*
	public byte[] readBytes(int length) {
		byte[] bytes = new byte[length];
		Base base = readBytes(bytes, 0, length);
		dataList.add(new com.assets.data.Byte(base));
		return bytes;
	}
*/
	public void writeByte(byte b) {
		byte[] bytes = {b};
		writeBytes(bytes);
	}
/*
	public void read(byte[] b, int off, int len) {
		Base base = readBytes(b, off, len);
		dataList.add(new com.assets.data.Byte(base));
	}*/
	//base
	public void writeBytes(byte[] b) {
		stream.write(b, 0, b.length);
		position += b.length;
		//System.out.println(byteStart+position);
	}
/*
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
*/
	public void writeInt64(long value) {
		writeInt(value,8);
	}

	public void writeUInt64(long value) {
		writeUInt(value,8);
	}

	public void writeInt32(long value) {
		writeInt(value,4);
	}

	public void writeUInt32(long value) {
		writeUInt(value,4);
	}
	
	public void writeInt16(long value) {
		writeInt(value,2);
	}

	public void writeUInt16(long value) {
		writeUInt(value,2);
	}

	public void writeUInt(long value,int length) {
		writeInt(value,length, false);
	}

	public void writeInt(long value,int length) {
		writeInt(value,length,true);
	}

	public void writeInt(long value,int length, boolean u) {
		byte[] bytes = longToBytes(value,length, u);
		writeBytes(bytes);
	}

	public byte[] longToBytes(long value,int length, boolean u) {
		if (Endian == EndianType.BigEndian) {
			String hexString = Long.toHexString(value);
			byte[] byteArray = new byte[length];
			for (int i = 0; i < length; i++) {
				String hex="";
				if(hexString.length()==0) {
					break;
				}else if(hexString.length()>1) {
					hex= hexString.substring(hexString.length()-2,hexString.length());
					if(hexString.length()==2) {
						hexString="";
					}else {
						hexString=hexString.substring(0,hexString.length()-2);
					}
				}else {
					hex=hexString;
					hexString="";
				}
				byteArray[i] = (byte) Integer.parseInt(hex, 16);
			}
			
			
			return byteArray;
		}
		return _longToBytes(value, u);
	}

	public String encodeHexString(byte[] data, boolean u) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public static byte[] _longToBytes(long values, boolean u) {
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
			int offset = 64 - (i + 1) * 8;
			buffer[i] = (byte) ((values >> offset) & 0xff);
		}
		return buffer;
	}
/*
	public static float[] toArray(List<Float> data) {
		float[] floats = new float[data.size()];
		for (int i = 0; i < data.size(); i++) {
			floats[i] = data.get(i);
		}
		return floats;
	}

	private void println(Object value) {
		// System.out.println(value);
	}*/
/*
	public static void main(String[] args) {
		ByteWriter byteWrite=new ByteWriter();
		byteWrite.longToBytes(95220L,4, false);
	}
*/
}
