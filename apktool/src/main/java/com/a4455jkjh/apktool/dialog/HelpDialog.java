package com.a4455jkjh.apktool.dialog;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import brut.androlib.Androlib;
import brut.util.Log;
import com.a4455jkjh.apktool.ApktoolActivity;

public class HelpDialog extends ProcessDialog{
	public HelpDialog (ApktoolActivity a, CharSequence t) {
		super(a, t);
	}
	@Override
	protected boolean appendInfo () {
		return false;
	}

	@Override
	protected void start () throws Exception {
		String apktool_url = "https://github.com/iBotPeaches/Apktool";
		String apksig_url="https://android.googlesource.com/platform/tools/apksig";
		Log.info("本软件使用了一下开源代码：");
		Log.info("·Apktool:版本 " + Androlib.getVersion());
		SpannableString apktool = new SpannableString("    " + apktool_url);
		URLSpan apktool_span = new URLSpan(apktool_url);
		apktool.setSpan(apktool_span, 4, apktool.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		Log.info(apktool);
		Log.info("·apksig:版本未知");
		SpannableString apksig = new SpannableString("    " + apksig_url);
		URLSpan apksig_span = new URLSpan(apksig_url);
		apksig.setSpan(apksig_span, 4, apksig.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		Log.info(apksig);
		Log.info("\n使用说明：");
		Log.info("1：编译");
		Log.info("点击apktool.yml可自动编译。");
		Log.info("\n2:反编译");
		Log.info("点击apk文件时可选择5种操作(反编译全部、反编译资源、反编译classes.dex、签名和校验签名)。");
		Log.info("\n3:关于签名文件");
		Log.info("本软件支持4种类型的签名文件：");
		Log.info("  1:JKS格式");
		Log.info("  2:PKCS#12格式");
		Log.info("  3:BKS格式");
		Log.info("  4:PKCS#8(未加密) + X.509");
		Log.info("\n4:本软件支持读取密钥文件的详细信息");
		Log.info("点击密钥文件可显示详细信息。");
		Log.info("\n注：读取和创建JKS、PKCS#12的代码提取自OpenJDK 6。");
	}

}
