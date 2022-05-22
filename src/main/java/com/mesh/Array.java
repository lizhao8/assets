package com.mesh;

import java.io.BufferedWriter;

public class Array extends Data {

	public Array(Data data) {
		super(data.parent);
		super.index = data.index;
		super.name = data.name;
		super.type = data.type;
	}

	@Override
	public void save(BufferedWriter writer) throws Exception {
		StringBuilder spaceBuilder = new StringBuilder();
		for (int i = 0; i < deep; i++) {
			spaceBuilder.append(" ");
		}
		StringBuilder stringBuilder = new StringBuilder(spaceBuilder);
		stringBuilder.append(index);
		stringBuilder.append(" ");
		stringBuilder.append(type);
		stringBuilder.append(" ");
		stringBuilder.append(name);
		if (value != null && !"".equals(value)) {
			stringBuilder.append(" = ");
			stringBuilder.append(value);
		}
		int size = childList.size();
		stringBuilder.append(" (" + size + " items)");
		writer.append(stringBuilder);
		writer.newLine();
		spaceBuilder.append(" ");
		stringBuilder = new StringBuilder(spaceBuilder);
		stringBuilder.append("0");
		stringBuilder.append(" int size = ");
		stringBuilder.append(size);
		writer.append(stringBuilder);
		writer.newLine();

		for (int i = 0; i < childList.size(); i++) {
			Data data = childList.get(i);
			stringBuilder = new StringBuilder(spaceBuilder);
			stringBuilder.append("[" + i + "]");
			writer.append(stringBuilder);
			writer.newLine();
			data.save(writer,this, 1);
		}
	}

	@Override
	public String toString() {
		return "Array [size=" + childList.size() + ", index=" + index + ", type=" + type + ", name=" + name + ", value="
				+ value + ", childList=" + childList + "]";
	}

}
