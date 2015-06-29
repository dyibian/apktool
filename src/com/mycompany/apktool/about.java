package com.mycompany.apktool;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import brut.androlib.*;
import org.bouncycastle.jce.provider.*;

public class about extends Activity
{
	String help="说明：\n" + 
	"点击'apk'文件时调用'apktool'反编译\n" +
	"点击'dex'文件时调用'baksmali'反编译\n" +
	"点击文件夹下包含'apktool.yml'时调用'apktool'回编译\n" +
	"点击文件夹后缀名为'_smali'是调用'smali'回编译\n" +
	"安卓4.4及以下初次使用请加载框架\n" +
	"其他请自行体验\n" +
	"联系我：百度贴吧 @4455jkjh1";
	TextView tv;
	@Override
	
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		tv = (TextView)findViewById(R.id.aboutTextView);
		((TextView)(findViewById(R.id.aboutTextView1))).setText(help);
		setTitle("关于");
		version();
		set(String.format("apktool版本：%s", Androlib.getVersion()));
		set(String.format("baksmali版本：%s", ApktoolProperties.get("baksmaliVersion")));
		set(String.format("smali版本:%s", ApktoolProperties.get("smaliVersion")));
		BouncyCastleProvider bc=new BouncyCastleProvider();
		set(String.format("%s：%s", bc.getName(), bc.getInfo()));
		set("SignApk： 1.0");
	} 
	private void set(String s)
	{
		tv.append("\n" + s);
	}
	private void version()
	{
		PackageManager pm=getPackageManager();
		try
		{
			PackageInfo pi=pm.getPackageInfo("com.mycompany.apktool", PackageManager.GET_INSTRUMENTATION);
			tv.setText(String.format("版本：%s (%d)", pi.versionName, pi.versionCode));
		}
		catch (PackageManager.NameNotFoundException e)
		{}
	}
}
