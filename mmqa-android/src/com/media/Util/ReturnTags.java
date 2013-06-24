package com.media.Util;

import java.util.ArrayList;
import java.util.List;

public class ReturnTags {

	private String inString;

	public ReturnTags(String inString) {
		this.inString = inString;
	}

	public List<String> convertString() {
		List<String> tags = new ArrayList<String>();
		int i = 1; // i记录每个开始位置
		int j = 1; // j记录每个结束位置
		while (!inString.substring(j).equals("]")) {

			if (String.valueOf(inString.charAt(j)).equals(",")) {
				String temp = inString.substring(i, j);
				tags.add(temp);
				i = j + 2;
			}

			j++;
		}
		tags.add(inString.substring(i, j));
		return tags;
	}
}