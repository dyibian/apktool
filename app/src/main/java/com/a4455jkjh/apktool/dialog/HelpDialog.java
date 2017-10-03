package com.a4455jkjh.apktool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import brut.androlib.Androlib;
import com.a4455jkjh.apktool.R;
import android.app.Activity;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class HelpDialog extends Dialog implements OnClickListener {
	Activity activity;
	public HelpDialog(Activity c, int style) {
		super(c, style);
		activity = c;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		setTitle("说明");
		SpannableStringBuilder ssb = new SpannableStringBuilder("Apktool版本：");
		ssb.append(Androlib.getVersion());
		ssb.append("\nApktool开源地址：");
		String apktool_url = "https://github.com/iBotPeaches/Apktool";
		ssb.append(apktool_url);
		int apktool_i=ssb.length() - apktool_url.length();
		URLSpan url = new URLSpan(apktool_url);
		ssb.setSpan(url, apktool_i, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.append("\n\n使用说明：\n").
			append("1：编译\n").
			append("点击apktool.yml可自动编译。\n").
			append("\n2:反编译\n").
			append("点击apk文件时可选择4种操作(反编译全部、反编译资源、反编译classes.dex和签名)。\n").
			append("\n3:关于签名文件\n").
			append("本软件支持3种类型的签名文件：\n").
			append("  1:JKS格式(后缀名为jks或keystore，默认)。\n").
			append("  2:PKCS#12格式(后缀名为p12或pk12)。\n").
			append("  3:PKCS#8格式(后缀名为pk8，使用本格式时会自动读取对应的x509.pem文件)。\n").
			append("\n4:本软件支持读取密钥文件的详细信息\n").
			append("点击密钥文件可显示详细信息(暂只支持JKS和PKCS#12格式)。\n").
			append("\n注：读取和创建JKS、PKCS#12的代码提取自OpenJDK 6。");

		TextView msg = (TextView)findViewById(R.id.message);
		msg.setText(ssb);
		findViewById(R.id.confirm).
			setOnClickListener(this);
	}

	@Override
	public void onClick(View p1) {
		dismiss();
	}


}
