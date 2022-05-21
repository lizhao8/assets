package com.assets.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.assets.ByteReader;

public class SerializedFile {
	public ByteReader reader;
	public SerializedFileHeader header = new SerializedFileHeader();
	boolean m_EnableTypeTree;
	public long bigIDEnabled = 0;
	public List<SerializedType> m_Types;
	public String userInformation;
	public List<ObjectInfo> m_Objects;
	public int[] version = { 0, 0, 0, 0 };
	public BuildType buildType;
	public List<Mesh> Objects = new ArrayList<Mesh>();
	public Map<Long, Mesh> ObjectsDic = new TreeMap<Long, Mesh>();

	public SerializedFile(ByteReader reader) {
		this.reader = reader;

		header.m_MetadataSize = reader.readUInt32();
		header.m_FileSize = reader.readUInt32();
		header.m_Version = reader.readUInt32();
		header.m_DataOffset = reader.readUInt32();
		header.m_Endianess = reader.readByte();
		header.m_Reserved = reader.readBytes(3);
		byte m_FileEndianess = header.m_Endianess;
		// ReadMetadata
		if (m_FileEndianess == 0) {
			this.reader.Endian = EndianType.LittleEndian;
		}
		String unityVersion = reader.readString();
		long m_TargetPlatform = reader.readInt32();
		m_EnableTypeTree = reader.readBoolean();
		long typeCount = reader.readInt32();
		m_Types = new ArrayList<SerializedType>((int) typeCount);
		for (int i = 0; i < typeCount; i++) {
			m_Types.add(readSerializedType(false));
		}

		if (header.m_Version >= SerializedFileFormatVersion.kUnknown_7
				&& header.m_Version < SerializedFileFormatVersion.kUnknown_14) {
			bigIDEnabled = reader.readInt32();
		}
		long objectCount = reader.readInt32();
		m_Objects = new ArrayList<ObjectInfo>((int) objectCount);
		// Objects = new List<Object>(objectCount);
		// ObjectsDic = new Dictionary<long, Object>(objectCount);

		for (int i = 0; i < objectCount; i++) {
			ObjectInfo objectInfo = new ObjectInfo();
			objectInfo.reader = reader;
			if (bigIDEnabled != 0) {
				objectInfo.m_PathID = reader.readInt64();
			} else if (header.m_Version < SerializedFileFormatVersion.kUnknown_14) {
				objectInfo.m_PathID = reader.readInt32();
			} else {
				reader.alignStream();
				objectInfo.m_PathID = reader.readInt64();
			}

			if (header.m_Version >= SerializedFileFormatVersion.kLargeFilesSupport) {
				objectInfo.byteStart = reader.readInt64();
			} else {
				objectInfo.byteStart = reader.readUInt32();
			}
			objectInfo.byteStart += header.m_DataOffset;
			objectInfo.byteSize = reader.readUInt32();
			objectInfo.typeID = reader.readInt32();
			if (header.m_Version < SerializedFileFormatVersion.kRefactoredClassId) {
				objectInfo.classID = reader.readUInt16();
				objectInfo.serializedType = m_Types.stream().filter(x -> x.classID == objectInfo.typeID).findFirst()
						.get();
			} else {
				SerializedType type = m_Types.get((int) objectInfo.typeID);
				objectInfo.serializedType = type;
				objectInfo.classID = type.classID;
			}
			if (header.m_Version < SerializedFileFormatVersion.kHasScriptTypeIndex) {
				objectInfo.isDestroyed = reader.readUInt16();
			}
			if (header.m_Version >= SerializedFileFormatVersion.kHasScriptTypeIndex
					&& header.m_Version < SerializedFileFormatVersion.kRefactorTypeData) {
				long m_ScriptTypeIndex = reader.readInt16();
				if (objectInfo.serializedType != null) {
					objectInfo.serializedType.m_ScriptTypeIndex = m_ScriptTypeIndex;
				}
			}
			if (header.m_Version == SerializedFileFormatVersion.kSupportsStrippedObject
					|| header.m_Version == SerializedFileFormatVersion.kRefactoredClassId) {
				objectInfo.stripped = reader.readByte();
			}
			m_Objects.add(objectInfo);

		}
		List<LocalSerializedObjectIdentifier> m_ScriptTypes;
		if (header.m_Version >= SerializedFileFormatVersion.kHasScriptTypeIndex) {
			long scriptCount = reader.readInt32();
			m_ScriptTypes = new ArrayList<LocalSerializedObjectIdentifier>((int) scriptCount);
			for (int i = 0; i < scriptCount; i++) {
				LocalSerializedObjectIdentifier m_ScriptType = new LocalSerializedObjectIdentifier();
				m_ScriptType.localSerializedFileIndex = reader.readInt32();
				if (header.m_Version < SerializedFileFormatVersion.kUnknown_14) {
					m_ScriptType.localIdentifierInFile = reader.readInt32();
				} else {
					reader.alignStream();
					m_ScriptType.localIdentifierInFile = reader.readInt64();
				}
				m_ScriptTypes.add(m_ScriptType);
			}
		}
		List<FileIdentifier> m_Externals;
		long externalsCount = reader.readInt32();
		m_Externals = new ArrayList<FileIdentifier>((int) externalsCount);
		for (int i = 0; i < externalsCount; i++) {
			FileIdentifier m_External = new FileIdentifier();
			if (header.m_Version >= SerializedFileFormatVersion.kUnknown_6) {
				String tempEmpty = reader.readString();
			}
			if (header.m_Version >= SerializedFileFormatVersion.kUnknown_5) {
				m_External.guid = new String(reader.readBytes(16));// ????
				m_External.type = reader.readInt32();
			}
			m_External.pathName = reader.readString();
			m_External.fileName = new File(m_External.pathName).getName();// ????
			m_Externals.add(m_External);
		}
		List<SerializedType> m_RefTypes;
		if (header.m_Version >= SerializedFileFormatVersion.kSupportsRefObject) {
			long refTypesCount = reader.readInt32();
			m_RefTypes = new ArrayList<SerializedType>((int) refTypesCount);
			for (int i = 0; i < refTypesCount; i++) {
				m_RefTypes.add(readSerializedType(true));
			}
		}

		if (header.m_Version >= SerializedFileFormatVersion.kUnknown_5) {
			userInformation = reader.readString();
		}

		System.out.println("end");
	}

	private SerializedType readSerializedType(boolean isRefType) {
		SerializedType type = new SerializedType();

		type.classID = reader.readInt32();

		if (header.m_Version >= SerializedFileFormatVersion.kRefactoredClassId) {
			type.m_IsStrippedType = reader.readBoolean();
		}

		if (header.m_Version >= SerializedFileFormatVersion.kRefactorTypeData) {
			type.m_ScriptTypeIndex = reader.readInt16();
		}

		if (header.m_Version >= SerializedFileFormatVersion.kHasTypeTreeHashes) {
			if (isRefType && type.m_ScriptTypeIndex >= 0) {
				type.m_ScriptID = reader.readBytes(16);
			} else if ((header.m_Version < SerializedFileFormatVersion.kRefactoredClassId && type.classID < 0)
					|| (header.m_Version >= SerializedFileFormatVersion.kRefactoredClassId && type.classID == 114)) {
				type.m_ScriptID = reader.readBytes(16);
			}
			type.m_OldTypeHash = reader.readBytes(16);
		}

		if (m_EnableTypeTree) {
			type.m_Type = new TypeTree();
			type.m_Type.m_Nodes = new ArrayList<TypeTreeNode>();
			if (header.m_Version >= SerializedFileFormatVersion.kUnknown_12
					|| header.m_Version == SerializedFileFormatVersion.kUnknown_10) {
				TypeTreeBlobRead(type.m_Type);
			} else {
				System.out.println(header.m_Version);
				// ReadTypeTree(type.m_Type);
			}
			if (header.m_Version >= SerializedFileFormatVersion.kStoresTypeDependencies) {
				System.out.println(header.m_Version);
				/*
				 * if (isRefType) { type.m_KlassName = reader.ReadStringToNull();
				 * type.m_NameSpace = reader.ReadStringToNull(); type.m_AsmName =
				 * reader.ReadStringToNull(); } else { type.m_TypeDependencies =
				 * reader.ReadInt32Array(); }
				 */
			}
		}

		return type;
	}

	private void TypeTreeBlobRead(TypeTree m_Type) {
		long numberOfNodes = reader.readInt32();
		long stringBufferSize = reader.readInt32();
		for (int i = 0; i < numberOfNodes; i++) {
			TypeTreeNode typeTreeNode = new TypeTreeNode();
			m_Type.m_Nodes.add(typeTreeNode);
			typeTreeNode.m_Version = reader.readUInt16();
			typeTreeNode.m_Level = reader.readByte();
			typeTreeNode.m_TypeFlags = reader.readByte();
			typeTreeNode.m_TypeStrOffset = reader.readUInt32();
			typeTreeNode.m_NameStrOffset = reader.readUInt32();
			typeTreeNode.m_ByteSize = reader.readInt32();
			typeTreeNode.m_Index = reader.readInt32();
			typeTreeNode.m_MetaFlag = reader.readInt32();
			if (header.m_Version >= SerializedFileFormatVersion.kTypeTreeNodeWithTypeFlags) {
				typeTreeNode.m_RefTypeHash = reader.readUInt64();
			}
		}
		m_Type.m_StringBuffer = reader.readBytes((int) stringBufferSize);

		ByteReader stringBufferReader = new ByteReader(new ByteArrayInputStream(m_Type.m_StringBuffer));
		for (int i = 0; i < numberOfNodes; i++) {
			TypeTreeNode m_Node = m_Type.m_Nodes.get(i);
			m_Node.m_Type = readString(stringBufferReader, m_Node.m_TypeStrOffset);
			m_Node.m_Name = readString(stringBufferReader, m_Node.m_NameStrOffset);
		}
	}

	public String readString(ByteReader stringBufferReader, long value) {
		boolean isOffset = (value & 0x80000000) == 0;
		if (isOffset) {
			stringBufferReader.stream.reset();
			stringBufferReader.stream.skip(value);
			return stringBufferReader.readString();
		}
		long offset = value & 0x7FFFFFFF;
		if (CommonString.StringBuffer.containsKey(offset)) {
			return CommonString.StringBuffer.get(offset);
		}
		return offset + "";
	}

	public void setVersion(String stringVersion) {
		String buildSplit = stringVersion.replaceAll("\\d", "").replaceAll("\\.", "");
		buildType = new BuildType(buildSplit);

		String[] split1 = stringVersion.split(buildSplit);
		String[] split2 = split1[0].split("\\.");
		version[0] = Integer.valueOf(split2[0]);
		version[1] = Integer.valueOf(split2[1]);
		version[2] = Integer.valueOf(split2[2]);
		version[3] = Integer.valueOf(split1[1]);
	}

	public void AddObject(Mesh obj) {
		Objects.add(obj);
		ObjectsDic.put(obj.objectInfo.m_PathID, obj);
	}
}
