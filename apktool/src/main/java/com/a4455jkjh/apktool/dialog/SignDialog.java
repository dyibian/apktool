package com.a4455jkjh.apktool.dialog;

import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.ApktoolActivity;
import org.jf.util.Log;

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
		Log.info("正在签名");
		data.signTool.loadKey();
		data.signTool.sign(data.in,data.out);
		Log.info("签名成功");
	}

	@Override
	protected CharSequence getTitle (boolean success) {
		if(success)
			return "签名成功";
		return "签名失败";
	}

	
	
}
