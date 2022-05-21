package com.assets.max;

import java.util.Objects;

import com.jogl.base.Point;
import com.jogl.base.Texture;

public class Buiding{
	public Point point;
	public Texture texture;
	public Point normal;

	public Buiding(Point point, Texture texture, Point normal) {
		super();
		this.point = point;
		this.texture = texture;
		this.normal = normal;
	}

	@Override
	public int hashCode() {
		return Objects.hash(normal, point, texture);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Buiding other = (Buiding) obj;
		return Objects.equals(normal, other.normal) && Objects.equals(point, other.point)
				&& Objects.equals(texture, other.texture);
	}

}
