package com.test;

import com.tool.BitConverter;

public class test {
	/*
	    0 UInt8 data = 254
   [786805]
    0 UInt8 data = 57
   [786806]
    0 UInt8 data = 159
   [786807]
    0 UInt8 data = 61
	 
	 */
	public static void main(String[] args) {
		float a = (float) 0.3941699;
		byte[] bytes = BitConverter.getBytes(a);
		for (byte b : bytes) {
			System.out.print(b + " ");
		}
		bytes[0] = (byte) 254;
		float b = BitConverter.toFloat(bytes);
		System.out.println(b);
	}
	

}
