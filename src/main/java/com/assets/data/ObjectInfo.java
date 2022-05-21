package com.assets.data;

import com.assets.ByteReader;

public class ObjectInfo {
	public long byteStart;
	public long byteSize;
	public long typeID;
	public long classID;
	public long isDestroyed;
	public byte stripped;

	public long m_PathID;
	public SerializedType serializedType;
	
	public ByteReader reader;

}