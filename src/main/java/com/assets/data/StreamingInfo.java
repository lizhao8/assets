package com.assets.data;

import com.assets.ByteReader;
import com.assets.ByteWriter;

public class StreamingInfo {
	public long offset; // ulong
	public long size;
	public String path;

	public StreamingInfo(ByteReader reader) {
		int[] version = reader.version;

		if (version[0] >= 2020) // 2020.1 and up
		{
			offset = reader.readInt64();
		} else {
			offset = reader.readUInt32();
		}
		size = reader.readUInt32();
		path = reader.readAlignedString();
	}
	public void write(ByteWriter writer) {
		int[] version = writer.version;

		if (version[0] >= 2020) // 2020.1 and up
		{
			  writer.writeInt64(offset);
		} else {
			 writer.writeUInt32(offset);
		}
		writer.writeUInt32(size);
		writer.writeAlignedString(path);
	}
}