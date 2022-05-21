package com.assets.data;

public class TypeTreeNode {
	public String m_Type;
	public String m_Name;
	public long m_ByteSize;
	public long m_Index;
	public long m_TypeFlags; // m_IsArray
	public long m_Version;
	public long m_MetaFlag;
	public long m_Level;
	public long m_TypeStrOffset;
	public long m_NameStrOffset;
	public long m_RefTypeHash;

	public TypeTreeNode() {
	}

	public TypeTreeNode(String type, String name, int level, boolean align) {
		m_Type = type;
		m_Name = name;
		m_Level = level;
		m_MetaFlag = align ? 0x4000 : 0;
	}
}