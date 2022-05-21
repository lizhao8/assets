package com.assets.data;

import lombok.Data;

@Data
public class Base {
	protected long start;
	protected long end;
	protected long length;
	protected byte[] bytes = new byte[0];

	public Base(Base base) {
		super();
		this.start = base.start;
		this.end = base.end;
		this.length = base.length;
		this.bytes = base.bytes;
	}

	public Base(long start, long end, long length, byte[] bytes) {
		super();
		this.start = start;
		this.end = end;
		this.length = length;
		this.bytes = bytes;
	}

	public void set() {

	}

}
