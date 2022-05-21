package com.assets.data;

public class Object {
	public SerializedFile assetsFile;
	// public ObjectReader reader;
	public long m_PathID;
	public int[] version;
	protected BuildType buildType;
	//public BuildTarget platform;
	//public ClassIDType type;
	public SerializedType serializedType;
	public long byteSize;

	public Object(ObjectReader reader) {
		this.reader = reader;
		reader.Reset();
		assetsFile = reader.assetsFile;
		type = reader.type;
		m_PathID = reader.m_PathID;
		version = reader.version;
		buildType = reader.buildType;
		platform = reader.platform;
		serializedType = reader.serializedType;
		byteSize = reader.byteSize;

		if (platform == BuildTarget.NoTarget) {
			var m_ObjectHideFlags = reader.ReadUInt32();
		}
	}
}
