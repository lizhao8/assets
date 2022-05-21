package com.assets.data;

public class FileIdentifier {
	public String guid;	//Guid ????
	public long type; // enum { kNonAssetType = 0, kDeprecatedCachedAssetType = 1,
						// kSerializedAssetType = 2, kMetaAssetType = 3 };
	public String pathName;

	// custom
	public String fileName;
}