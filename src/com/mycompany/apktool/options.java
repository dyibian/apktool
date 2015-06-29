package com.mycompany.apktool;
import java.util.*;

public class options
{
	public static List<String> cmd;
	public static String action,path,target,x509,pk8,aapt,framework="";
	public static boolean verbose = false,usekey=false,qiangzhi=false;
	public static int loglevel=0;
	public static void add(String c)
	{
		cmd.add(c);
	}
	public static void reset()
	{
		cmd.removeAll(cmd);
	}
	public static String[] get()
	{
		List<String> ls=new ArrayList<String>();
		ls.add(action);
		ls.add(path);
		ls.addAll(cmd);
		return ls.toArray(new String[0]);
	}
	static{
		cmd = new ArrayList<String>();
		System.loadLibrary("abi");
	}
	public static native String abi();
}
