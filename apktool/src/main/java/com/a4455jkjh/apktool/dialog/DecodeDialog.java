package com.a4455jkjh.apktool.dialog;

import android.content.Context;
import brut.androlib.ApkDecoder;
import com.a4455jkjh.apktool.ApktoolActivity;

public class DecodeDialog extends ProcessDialog<ApkDecoder>
{
	public DecodeDialog(ApktoolActivity a,CharSequence t){
		super(a,t);
		notify=true;
	}

	@Override
	protected boolean appendInfo () {
		return true;
	}

	@Override
	protected void start () throws Exception {
		data.decode();
	}

	@Override
	protected CharSequence getTitle (boolean success) {
		if(success)
			return "反编译完成";
		return "反编译失败";
	}


	
}
