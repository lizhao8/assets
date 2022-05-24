package com.mesh.data;

import java.util.Objects;

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
	public Index(Long index) {
		this.v = index.intValue();
		this.vt = index.intValue();
		this.vn = index.intValue();
	}

	@Override
	public String toString() {
		return v + "/" + vt + "/" + vn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(v, vn, vt);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Index other = (Index) obj;
		return v == other.v && vn == other.vn && vt == other.vt;
	}

}