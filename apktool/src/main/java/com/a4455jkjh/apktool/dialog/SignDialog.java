package com.a4455jkjh.apktool.dialog;

import android.content.Context;
import brut.androlib.ApkOptions;
import brut.util.Log;
import com.a4455jkjh.apktool.ApktoolActivity;

public class SignDialog extends ProcessDialog<ApkOptions>
{
	public SignDialog(ApktoolActivity a,CharSequence t){
		super(a,t);
	}

	@Override
	protected boolean appendInfo () {
		return false;
	}

	@Override
	protected void start () throws Exception {
		data.signTool.loadKey();
		data.signTool.sign(data.in,data.out);
		Log.info("签名成功");
	}
	
}
