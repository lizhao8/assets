package com.jogl.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Point {
	public float x;
	public float y;
	public float z;

	public List<Face> faceList = new ArrayList<Face>();

	public void addFace(Face face) {
		faceList.add(face);
	}

	public Point(String point) {
		super();
		String[] split = point.split(" ");
		this.x = -Float.parseFloat(split[0]);
		this.y = Float.parseFloat(split[1]);
		this.z = Float.parseFloat(split[2]);
	}

	public Point(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return x + " " + y + " " + z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		return Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
				&& Float.floatToIntBits(y) == Float.floatToIntBits(other.y)
				&& Float.floatToIntBits(z) == Float.floatToIntBits(other.z);
	}

}
