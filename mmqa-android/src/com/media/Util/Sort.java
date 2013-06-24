package com.media.Util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

public class Sort {

	public static String[] sortByChina(String[] array){
		Comparator cmp=Collator.getInstance(java.util.Locale.CHINA);
		Arrays.sort(array, cmp);
		return array;
	}
	
	public static List<String> sortByChina(List<String> list){
		int len = list.size();
		String[] array = new String[len];
		for(int i = 0; i< len;i++)
		{
			array[i] = list.get(i);
		}
		array = sortByChina(array);
		list.clear();
		for(int i = 0; i<len;i++)
		{
			list.add(array[i]);
		}
		return list;
	}
}
