package com.medialab.lejuju.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONObject;

public class UTextUtils {
	
	public static String getPingYin(String src)
	{
		if (src == null || src.equals(""))
		{
			return "";
		}
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try
		{
			for (int i = 0; i < t0; i++)
			{
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+"))
				{
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					if (t2 == null)
					{
						return "";
					}
					t4 += t2[0];
				}
				else
				{
					t4 += java.lang.Character.toString(t1[i]);
				}
			}
			return t4;
		}
		catch (BadHanyuPinyinOutputFormatCombination e1)
		{
			e1.printStackTrace();
		}
		return t4;
	}

	// 返回中文的首字母
	public static String getPinYinHeadChar(String str)
	{
		if (str == null || str.equals(""))
		{
			return "";
		}
		String convert = "";
		for (int j = 0; j < str.length(); j++)
		{
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null)
			{
				convert += pinyinArray[0].charAt(0);
				break;
			}
			else
			{
				convert += word;
				break;
			}
		}
		return convert.toUpperCase();
	}
	
	public static boolean isNormalName(String name)
	{
		Pattern p = Pattern.compile("([A-Za-z0-9\u4E00-\u9FA5]+)");
		Matcher m = p.matcher(name);
		if (m.matches())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean isSuccess(JSONObject json)
	{
		String result = json.optString("result","");
		if(result.equals("success"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
