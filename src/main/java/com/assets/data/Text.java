package com.assets.data;

public class Text extends Base {

	public Text(long start, long end, long length, byte[] bytes, String value) {
		super(start, end, length, bytes);
		this.value = value;
	}

	private String value;

}