package com.jogl.base;

public class Point2D {
	public float x;
	public float y;

	public Point2D(String point) {
		super();
		String[] split = point.split(" ");
		this.x = Float.parseFloat(split[0]);
		this.y = Float.parseFloat(split[1]);
	}

	public Point2D(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

}
