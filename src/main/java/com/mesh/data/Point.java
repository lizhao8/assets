package com.mesh.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Point {
	public List<Float> floatList = new ArrayList<Float>();

	public Texture texture;

	public Normal normal;

	public Index index;

	public List<Face> faceList = new ArrayList<Face>();

	public Map<Channel, List<Float>> floatMap = new HashMap<Channel, List<Float>>();

	public void addFace(Face face) {
		faceList.add(face);
	}

	public Point(String point) {
		super();
		String[] split = point.split(" ");
		floatList.add(new Float(split[0], -1));
		floatList.add(new Float(split[1]));
		floatList.add(new Float(split[2]));
	}

	public Point(Float x, Float y, Float z) {
		floatList.add(x);
		floatList.add(y);
		floatList.add(z);
	}

	public Float get(int index) {
		return floatList.get(index);
	}

	public Float x() {
		return get(0);
	}

	public Float y() {
		return get(1);
	}

	public Float z() {
		return get(2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(index);
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
		return Objects.equals(index, other.index);
	}

	@Override
	public String toString() {
		return x() + "," + y() + "," + z();
	}

}
