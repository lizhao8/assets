package com.assets.data;

public class SerializedFileHeader {
	public long m_MetadataSize;
	public long m_FileSize;
	public long m_Version;
	public long m_DataOffset;
	public byte m_Endianess;
	public byte[] m_Reserved;
}
