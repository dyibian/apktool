package com.a4455jkjh.apktool.utils;
import android.graphics.Color;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.Spannable;
import android.graphics.Typeface;
import android.text.style.TypefaceSpan;
import android.text.style.StyleSpan;

public class HighLight {
	public static void smali (Editable text) {
		ForegroundColorSpan[] fores = text.getSpans(0, text.length(), ForegroundColorSpan.class);
		for (ForegroundColorSpan fore:fores)
			text.removeSpan(fore);
		Matcher start = HighLight.start.matcher(text);
		while (start.find()) {
			ForegroundColorSpan fore = new ForegroundColorSpan(Color.GRAY);
			text.setSpan(fore, start.start(),
						 start.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		Matcher key = keyWords.matcher(text);
		while (key.find()) {
			StyleSpan style = new StyleSpan(Typeface.BOLD);
			text.setSpan(style, key.start(), key.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		Matcher c = comment.matcher(text);
		while (c.find()) {
			ForegroundColorSpan fore = new ForegroundColorSpan(Color.CYAN);
			text.setSpan(fore, c.start(),
						 c.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	private static final Pattern start;
	private static final Pattern keyWords;
	private static final Pattern comment;
	static{
		start = Pattern.compile(
			"\\.((class)|(super)|(source)|(field)|(method)|(end)|(((locals)|(register)) \\d*)"
			+ "|(line)|(implements)|(annotation)).*");
		keyWords = Pattern.compile(
			"\\b(public|private|protected|final|static|synchronized|constructor"
			+"|abstract|interface)\\b");
		comment = Pattern.compile(
			"#.*");
	}
}
