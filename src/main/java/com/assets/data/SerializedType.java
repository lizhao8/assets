package com.assets.data;

public class SerializedType {
	public long classID;
	public boolean m_IsStrippedType;
	public long m_ScriptTypeIndex = -1;
	public TypeTree m_Type;
	public byte[] m_ScriptID; // Hash128
	public byte[] m_OldTypeHash; // Hash128
	public int[] m_TypeDependencies;
	public String m_KlassName;
	public String m_NameSpace;
	public String m_AsmName;
}
