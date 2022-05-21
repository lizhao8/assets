package com.assets.data;

public class BitArray {
	private int length;
	private int[] m_array;
	private int m_length;

	/*
	 * public BitArray(int numBits) { data = new byte[(int) Math.ceil((double)
	 * numBits / 8)]; this.length = numBits; }
	 */

	public BitArray(int[] values) {
		m_array = new int[values.length];
		m_length = values.length * 32;
		for (int i = 0; i < values.length; i++) {
			m_array[i] = values[i];
		}
	}

	public boolean get(int index) {
		return (m_array[index / 32] & (1 << index % 32)) != 0;
	}
}
