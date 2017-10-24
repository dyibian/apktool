package com.a4455jkjh.apktool.views;

import com.myopicmobile.textwarrior.common.Language;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.Opcode;

public class LanguageSmali extends Language {
	private static Language theOne=null;
	public static synchronized Language getInstance() {
		if (theOne == null)
			theOne = new LanguageSmali();
		return theOne;
	}
	private LanguageSmali() {
		Opcode[] codes =Opcode.values();
		String[] names = new String[codes.length];
		for (int i=0;i < codes.length;i++)
			names[i] = codes[i].name + " ";
		setNames(names);
		AccessFlags[] flags = AccessFlags.values();
		String[] keywords = new String[flags.length];
		for (int i=0;i < flags.length;i++)
			keywords[i] = flags[i].toString() + " ";
		setKeywords(keywords);
	}
}
