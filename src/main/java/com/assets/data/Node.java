package com.assets.data;

import lombok.Data;

@Data
public class Node {
	private long offset;
	private long size;
	private long flags;
	private String path;

	public Node(long offset, long size, long flags, String path) {
		super();
		this.offset = offset;
		this.size = size;
		this.flags = flags;
		this.path = path;
	}

}
