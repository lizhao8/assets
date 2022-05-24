package com.mesh.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Face {
	public List<Long> indexList = new ArrayList<Long>();
	public List<Index> _indexList = new ArrayList<Index>();

	public void addIndex(Long index) {
		indexList.add(index);
	}

	public List<Point> pointList = new ArrayList<Point>();

	public void addPoint(Point point) {
		point.addFace(this);
		pointList.add(point);
	}

	public Point getPoint(int index) {
		return pointList.get(index);
	}

	public Face() {
		super();
	}

	public Face(String index, SubMesh subMesh) {
		String[] split = index.split(" ");
		_indexList.add(new Index(split[2]));
		_indexList.add(new Index(split[1]));
		_indexList.add(new Index(split[0]));
	}

	@Override
	public String toString() {
		return indexList.toString();
	}

	

}
