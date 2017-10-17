package com.a4455jkjh.apktool.dialog;

import android.content.Context;
import brut.androlib.ApkDecoder;
import com.a4455jkjh.apktool.ApktoolActivity;

public class DecodeDialog extends ProcessDialog<ApkDecoder>
{
	public DecodeDialog(ApktoolActivity a,CharSequence t){
		super(a,t);
	}

	@Override
	protected boolean appendInfo () {
		return true;
	}

	@Override
	protected void start () throws Exception {
		data.decode();
	}

	
}
