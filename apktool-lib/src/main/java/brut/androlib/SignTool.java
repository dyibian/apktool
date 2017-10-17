package brut.androlib;

import java.io.File;

public interface SignTool extends CharSequence
{
	public void sign(File in,File out) throws Exception;
	public void loadKey() throws Exception;
}
