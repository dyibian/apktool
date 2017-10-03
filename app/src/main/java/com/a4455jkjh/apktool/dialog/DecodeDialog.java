package com.a4455jkjh.apktool.dialog;
import android.content.Context;
import brut.androlib.ApkDecoder;
import brut.androlib.AndrolibException;
import java.io.IOException;
import brut.directory.DirectoryException;
import java.util.logging.Logger;
import com.a4455jkjh.apktool.MainActivity;

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
		LOGGER.info("开始");
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

	private static Logger LOGGER = Logger.getLogger(DecodeDialog.class.getName());
	
}
