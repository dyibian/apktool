package com.a4455jkjh.apktool.dialog;
import android.content.Context;
import brut.androlib.ApkOptions;
import brut.androlib.Androlib;
import brut.common.BrutException;

public class BuildDialog extends DialogCommon
{
	public BuildDialog(Context c){
		super(c);
	}
	@Override
	protected void start() throws BuildException
	{
		if(input == null)
			throw new BuildException("没有设置输入文件");
		if(!(input instanceof ApkOptions))
			throw new BuildException("错误");
		ApkOptions option = (ApkOptions)input;
		Androlib builder = new Androlib(option);
		try
		{
			builder.build();
		}
		catch (BrutException e)
		{
			throw new BuildException(e);
		}
	}
	
}
