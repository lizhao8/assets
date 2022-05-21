package com.test;

public class test {

	public static void main(String[] args) {
		byte[] bytes=new byte[]{
				0,0,0,0,
				0,0,0,1
				};
		long a=(0xffL & (long) bytes[7]) 
		        | (0xff00L & ((long) bytes[6] << 8)) 
		        | (0xff0000L & ((long) bytes[5] << 16))
		        | (0xff000000L & ((long) bytes[4] << 24)) 
		        | (0xff00000000L & ((long) bytes[3] << 32))
		        | (0xff0000000000L & ((long) bytes[2] << 40)) 
		        | (0xff000000000000L & ((long) bytes[1] << 48))
		        | (0xff00000000000000L & ((long) bytes[0] << 56));
		        System.out.println(a);
	}

}
