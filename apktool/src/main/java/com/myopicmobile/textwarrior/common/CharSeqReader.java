package com.myopicmobile.textwarrior.common;

import java.io.IOException;
import java.io.Reader;

public class CharSeqReader extends Reader 
{
	int offset = 0;
	CharSequence src;

	CharSeqReader(CharSequence src)
	{
		this.src = src;
	}

	@Override
	public void close() throws IOException
	{
		src = null;
		offset = 0;
	}

	@Override
	public int read(char[] chars, int i, int i1) throws IOException
	{
		int len = Math.min(src.length() - offset, i1);
		for (int n = 0; n < len; n++)
		{
			chars[i++] = src.charAt(offset++);
		}
		if (len <= 0)
			return  -1;
		return len;
	}
}
