package com.assets.data;

public class Matrix4x4 {
	public float[] floats;

	public float M00;
	public float M10;
	public float M20;
	public float M30;

	public float M01;
	public float M11;
	public float M21;
	public float M31;

	public float M02;
	public float M12;
	public float M22;
	public float M32;

	public float M03;
	public float M13;
	public float M23;
	public float M33;

	public Matrix4x4(float[] values) {
		this.floats=values;
		if (values == null)
			throw new RuntimeException("values");
		if (values.length != 16)
			throw new RuntimeException("values");

		M00 = values[0];
		M10 = values[1];
		M20 = values[2];
		M30 = values[3];

		M01 = values[4];
		M11 = values[5];
		M21 = values[6];
		M31 = values[7];

		M02 = values[8];
		M12 = values[9];
		M22 = values[10];
		M32 = values[11];

		M03 = values[12];
		M13 = values[13];
		M23 = values[14];
		M33 = values[15];
	}
}
