package com.a4455jkjh.apktool.dialog;
import android.app.Activity;
import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import brut.util.Log;
import com.a4455jkjh.apktool.ApktoolActivity;
import java.io.File;
import android.content.Context;

public class BuildDialog extends ProcessDialog<ApkOptions>
{
	public BuildDialog(ApktoolActivity a,CharSequence t){
		super(a,t);
	}

	@Override
	protected boolean appendInfo () {
		return true;
	}

	@Override
	protected void start () throws Exception {
		data.signTool.loadKey();
		data.tmp = File.createTempFile("apktool-","-"+System.currentTimeMillis(),data.in.getParentFile());
		Androlib lib = new Androlib(data);
		lib.build();
		data.sign();
		data.tmp.delete();
		Log.info("输出文件为："+data.out);
	}

	@Override
	protected void finish () {
		setNeutralButton("安装");
	}

	@Override
	protected void onNeutralButtonClicked () {
		((ApktoolActivity)context).install(data.out);
	}
	
	
	
}
