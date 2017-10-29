package org.slf4j;

import java.io.File;

public class Logger
{
	private final Class<?> clazz;

	public Logger(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	public boolean isDebugEnabled()
	{
		// TODO: Implement this method
		return false;
	}

	public void info(String m, Object... p)
	{
		out("I", String.format(m.replace("{}","%s"), p));
	}

	public void error(String m, Object... p)
	{
		out("E", String.format(m.replace("{}","%s"), p));
	}

	public void warn(String m, Object... p)
	{
		out("W", String.format(m.replace("{}","%s"), p));
	}
	public void debug(String m, Object... p)
	{
		out("D", String.format(m.replace("{}","%s"), p));
	}
	private void out(String p, String m)
	{
		System.out.printf("%s:\n", clazz.getName());
		System.out.printf("  %s:%s\n", p, m);
	}
}
