package com.assets.data;

public class Buffer {
	public static void BlockCopy(byte[] src, long srcOffset, byte[] dst, long dstOffset, long count) {
		for (int i = 0; i < count; i++, srcOffset++, dstOffset++) {
			dst[(int) dstOffset] = src[(int) srcOffset];
		}
	}

	public static byte[] Reverse(byte[] src) {
		int length = src.length;
		byte temp = 0;
		for (int i = 0; i < length / 2; i++) {
			temp = src[i];
			src[i] = src[length - i - 1];
			src[length - i - 1] = temp;
		}
		return src;
	}
}
