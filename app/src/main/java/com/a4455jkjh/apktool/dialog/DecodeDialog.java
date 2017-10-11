package com.a4455jkjh.apktool.dialog;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import brut.util.Log;
import com.a4455jkjh.apktool.MainActivity;
import java.io.IOException;

public class DecodeDialog extends DialogCommon
{
	public DecodeDialog(MainActivity c,int theme){
		super(c,theme);
	}

	@Override
	public void show()
	{
		setTitle("正在反编译");
		super.show();
	}
	
	
	@Override
	protected void start() throws BuildException
	{
		if(input == null)
			throw new BuildException("没有设置输入文件");
		if(!(input instanceof ApkDecoder))
			throw new BuildException("错误");
		Log.info("开始");
		ApkDecoder decoder = (ApkDecoder)input;
		try
		{
			decoder.decode();
		}
		catch (AndrolibException|IOException|DirectoryException e)
		{
			throw new BuildException(e);
		}
	}
	
}
