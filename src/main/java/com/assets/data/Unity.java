package com.assets.data;

import lombok.Data;

@Data
public class Unity {
	public String signature;// 签名，文件类型
	public long version;// 版本
	public String unityVersion;// unity版本
	public String unityRevision;// unity修订版本
	public long size;// 文件大小
	public long compressedBlocksInfoSize;// 压缩块大小
	public long uncompressedBlocksInfoSize;// 未压缩块大小
	public long flags;

}
