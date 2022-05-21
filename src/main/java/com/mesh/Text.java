package com.mesh;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Text {
	public int deep;
	private String text;
	public Text parent;

	public List<Text> childList = new ArrayList<Text>();

	public Text(Text parentText) {
		super();
		this.parent = parentText;
		this.parent.addChild(this);
		this.deep = parentText.deep + 1;

	}

	public void addChild(Text text) {
		childList.add(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		//print();
	}

	public void print() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < deep; i++) {
			stringBuilder.append(" ");
		}
		stringBuilder.append(text);
		System.out.println(stringBuilder);
	}

	public Text(int deep) {
		super();
		this.deep = deep;
	}

	public void save(BufferedWriter writer) throws Exception {

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < deep; i++) {
			stringBuilder.append(" ");
		}
		stringBuilder.append(text);
		writer.append(stringBuilder);
		writer.newLine();

		for (Text text : childList) {
			text.save(writer);
		}
	}

	@Override
	public String toString() {
		return "Text [deep=" + deep + ", text=" + text + ", childList=" + childList + "]";
	}

}
