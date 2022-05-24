package com.mesh.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Normal {
	public List<Float> floatList = new ArrayList<Float>();
	public Index index;

	public Normal(String normal) {
		String[] split = normal.split(" ");
		floatList.add(new Float(split[0], -1));
		floatList.add(new Float(split[1]));
		floatList.add(new Float(split[2]));
	}

	public Normal(Float x, Float y, Float z) {
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
	public String toString() {
		return "{" + x() + "," + y() + "," + z() + "}";
	}

	@Override
	public int hashCode() {
		return Objects.hash(x(), y());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Normal other = (Normal) obj;
		return x() == other.x() && y() == other.y() && z() == other.z();
	}

}
