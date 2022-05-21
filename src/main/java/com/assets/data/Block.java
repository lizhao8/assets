package com.assets.data;

import lombok.Data;

@Data
public class Block {
	private long compressedSize;
	private long uncompressedSize;
	private long flags;

	public Block(long uncompressedSize, long compressedSize, long flags) {
		super();
		this.compressedSize = compressedSize;
		this.uncompressedSize = uncompressedSize;
		this.flags = flags;
	}

}
