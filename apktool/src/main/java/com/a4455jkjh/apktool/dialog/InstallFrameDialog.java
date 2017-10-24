package com.a4455jkjh.apktool.dialog;

import android.content.Context;
import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.ApktoolActivity;

public class InstallFrameDialog extends ProcessDialog<ApkOptions>
{
	public InstallFrameDialog(ApktoolActivity a,CharSequence t){
		super(a,t);
	}
	@Override
	protected boolean appendInfo () {
		return false;
	}
	@Override
	protected CharSequence getTitle (boolean success) {
		return "安装完成";
	}
	@Override
	protected void start () throws Exception {
		Androlib lib = new Androlib(data);
		lib.installFramework();
	}
	
}
