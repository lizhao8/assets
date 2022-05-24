package com.mesh.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Texture {
	public List<Float> floatList = new ArrayList<Float>();

	public Texture(String texture) {
		String[] split = texture.split(" ");
		floatList.add(new Float(split[0]));
		floatList.add(new Float(split[1]));
	}
	public Texture(Float x, Float y) {
		floatList.add(x);
		floatList.add(y);
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
	
}
