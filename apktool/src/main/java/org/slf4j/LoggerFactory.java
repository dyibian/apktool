package org.slf4j;
import jadx.core.codegen.CodeWriter;

public class LoggerFactory
{


	public static Logger getLogger(Class<?> clazz)
	{
		// TODO: Implement this method
		return new Logger(clazz);
	}
}
