package com.assets.data;

public class BuildType {
	private String buildType;

	public BuildType(String type) {
		buildType = type;
	}

	public boolean IsAlpha = buildType == "a";
	public boolean IsPatch = buildType == "p";
}