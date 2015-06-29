package com.mycompany.apktool;
import android.os.*;
import brut.androlib.*;
import java.io.*;
import org.jf.baksmali.*;
import java.util.zip.*;
import out.out;
import android.text.*;

public class print extends out
{
	private int fine=-1,
	info=0,
	warning=1,
	error=2;

	private String newMessageInfo = "<font color='%s'><b>%s</b></font>";
	@Override
	public void fine(String p1)
	{
		// TODO: Implement this method
		if (bj(fine))
			out(format("F: " + p1,"gray"),10);
	}
private Spanned format(String s,String color){
	String m=String.format(newMessageInfo,color,s);
	return Html.fromHtml(m);
}
	@Override
	public void severe(String p1)
	{
		// TODO: Implement this method
		if (bj(error))
			out(format("E: " + p1,"red"),10);
	}
	private boolean bj(int b)
	{
		return options.loglevel >= b;
	}
	@Override
	public void warning(String p1)
	{
		// TODO: Implement this method
		if (bj(warning))
			out(format("W: " + p1,"blue"),10);
	}

	@Override
	public void out(String s)
	{
		// TODO: Implement this method
		out(s,0);
	}

	@Override
	public void decode(File p1, File p2, String p3, boolean p4, String p5, boolean p6, int p7) throws AndrolibException
	{
		// TODO: Implement this method
		try
		{
			main.main(new String[]{p1.getAbsolutePath(),"-o",p2.getAbsolutePath()});
		}
		catch (IOException e)
		{}
	}


	@Override
	public File getFile(File p1)
	{
		// TODO: Implement this method
		if (p1.getName().equals("1.apk") & Build.VERSION.SDK_INT >= 21)
		{
			p1 = new File("/system/framework/framework-res.apk");
		}
		else
		{
			p1 = new File("/sdcard/apktool/" + p1.getName());
		}
		return p1;
	}

	handler h;
	public print(handler m)
	{
		h = m;
	}

	@Override
	public void info(String p1)
	{
		// TODO: Implement this method
		if (bj(info))
			out(format("I: " + p1,"black"),10);
	}
	@Override
	public void out(Object p1,int i)
	{
		// TODO: Implement this method
		if(p1 instanceof String)
		if (((String)p1).contains("installed to"))
		{
			String[] strr=((String)p1).split("/");
			options.framework = "/sdcard/apktool/" + strr[strr.length - 1];
		}
		Message msg=new Message();
		//if (p1.endsWith(".smali"))p1 = " 编译 " + p1;
		msg.obj = p1;
		msg.what=i;
		h.sendMessage(msg);
	}

}
