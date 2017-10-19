package brut.androlib;

import java.io.File;

public abstract class SignTool
{
	public String keystore;
	public int type;
	public String storepass;
	public String keypass;
	public String alias;
	public abstract void sign(File in,File out) throws Exception;
	public abstract void loadKey() throws Exception;
}
