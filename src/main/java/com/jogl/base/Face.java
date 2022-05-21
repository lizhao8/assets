package com.jogl.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.assets.max.Buiding;
import com.assets.max.SubMesh;

public class Face {
	public Index index1;
	public Index index2;
	public Index index3;

	public long a;
	public long b;
	public long c;

	public List<Buiding> buidingList = new ArrayList<Buiding>();

	public void addBuiding(Buiding buiding) {
		buidingList.add(buiding);
	}

	public Buiding getBuiding(int index) {
		return buidingList.get(index);
	}

	public List<Point> pointList = new ArrayList<Point>();

	public void addPoint(Point point) {
		point.addFace(this);
		pointList.add(point);
	}

	public Point getPoint(int index) {
		return pointList.get(index);
	}

	public List<Texture> textureList = new ArrayList<Texture>();

	public void addTexture(Texture texture) {
		textureList.add(texture);
	}

	public Texture getTexture(int index) {
		return textureList.get(index);
	}

	public List<Normal> normalList = new ArrayList<Normal>();

	public void addNormal(Normal normal) {
		normalList.add(normal);
	}

	public Normal getNormal(int index) {
		return normalList.get(index);
	}

	public Normal normal1;
	public Point normal2;
	public Point normal3;

	public Face(String index, SubMesh subMesh) {
		String[] split = index.split(" ");
		this.index1 = new Index(split[0]);
		this.index2 = new Index(split[1]);
		this.index3 = new Index(split[2]);
	}

	@Override
	public String toString() {
		return index1 + " " + index2 + " " + index3;
	}

	public class Index {
		public int v;
		public int vt;
		public int vn;

		public Index(String index) {
			String[] splits = index.split("/");
			this.v = Integer.parseInt(splits[0]) - 1;
			this.vt = Integer.parseInt(splits[1]) - 1;
			this.vn = Integer.parseInt(splits[2]) - 1;
		}

		@Override
		public String toString() {
			return v + "/" + vt + "/" + vn;
		}

	}

}
