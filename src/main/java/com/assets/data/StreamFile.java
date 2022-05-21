package com.assets.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import lombok.Data;

@Data
public class StreamFile {
	private String path;
	private String fileName;
	private ByteArrayInputStream stream;
	private byte[] srcBytes;

}