package com.mycompany.apktool;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import brut.androlib.*;
import brut.androlib.res.*;
import java.io.*;
import java.nio.channels.*;
import java.text.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
	public TextView tv;
	public ScrollView sv;
	public Button b;
	handler h;
	String aapt,action,path,target,x509,pk8;
	InputStream is;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		tv = (TextView)findViewById(R.id.mainTextView);
		sv = (ScrollView)findViewById(R.id.mainScrollView1);
		b = (Button)findViewById(R.id.mainButton1);
		h = new handler(this);
		action = options.action;
		path = options.path;
		target = options.target;
		x509 = options.x509;
		pk8 = options.pk8;
		print p=new print(h);
		brut.androlib.out1.putout(p);
		AndrolibResources.setr9s(new r9s());
		tv.append("\n目的:" + action + "\n源文件地址:" + path + "\n目标文件地址:" + target +"啰嗦模式:"+options.verbose
				  + "\nx509证书地址:" + options.x509 + "\npk8私钥地址:" + options.pk8 + "\n强制模式：" + options.qiangzhi);
		new r().start();
	}
	public void on(View v)
	{
		finish();
	}
	class r extends Thread
	{

		@Override
		public void run()
		{
			// TODO: Implement this method
			super.run();
			long start=System.currentTimeMillis();
			String str1="";
			try
			{
				if ("s".equals(action))
				{
					str1 = "签名时间";
					sign();
				}
				else if ("d".equals(action))
				{
					str1 = "反编译apk时间：";
					if (new File(target).exists() & "d".equals(action))
					{
						if (!options.qiangzhi)
						{
							out1.getout().out("此文件已反编译");
						}
						else
						{
							options.add("-f");
							brut.apktool.Main.main(options.get());
							out1.getout().out("输出文件为:" + options.target);
						}
					}
					else
					{
						brut.apktool.Main.main(options.get());
						out1.getout().out("输出文件为:" + options.target);
					}
				}
				if ("b".equals(action))
				{
					str1 = "回编译apk时间：";
					brut.apktool.Main.main(options.get());
					sign();
					new File(target).delete();
				}
				else if ("baksmali".equals(action))
				{
					str1 = "反编译dex时间：";
					baksmali();
				}
				else if ("smali".equals(action))
				{
					str1 = "回编译dex时间：";
					smali();
				}
				else if ("if".equals(action))
				{
					brut.apktool.Main.main(new String[]{action,path});
					str1 = "安装框架时间：";
					File f1=new File(path),
						f2=new File(options.framework);
					if (!f2.getParentFile().exists())f2.getParentFile().mkdir();
					if (f2.exists())f2.delete();
					open.fileChannelCopy(f1, f2);
					out1.getout().out("framework复制到:" + options.framework);
				}
			}
			catch (Exception e)
			{
				out1.getout().out(e.getMessage() + "");
			}
			long end=System.currentTimeMillis();
			long aa=end - start;
			SimpleDateFormat sd=new SimpleDateFormat("mm分ss秒");
			String time=sd.format(aa);
			Message msg=new Message();
			msg.obj = str1 + time;
			h.sendMessage(msg);
			//out1.getout().out("已完成");
			h.sendEmptyMessage(100);
		}
    }
	
	private void sign()
	{
		out1.getout().out("正在签名");
		com.android.signapk.SignApk.main(new String[]{x509,pk8,target,path + "_sign.apk"});
		//;new File(target).delete();
		out1.getout().out("输出文件为:" + path + "_sign.apk");
	}
	private void baksmali() throws IOException
	{
		out1.getout().out(options.action + " " + options.path);
		org.jf.baksmali.main.main(new String[]{options.path,"-o",options.target});
		out1.getout().out("输出文件为:" + options.target);
	}
	private void smali()
	{
		out1.getout().out(options.action + " " + options.path);
		org.jf.smali.main.main(new String[]{options.path,"-o",options.target});
		out1.getout().out("输出文件为:" + options.target);
	}
	/*
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu)
	 {
	 // TODO: Implement this method ""
	 getMenuInflater().inflate(R.menu.main, menu);
	 return super.onCreateOptionsMenu(menu);
	 }

	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
	 {
	 // TODO: Implement this method
	 MenuItem mi=menu.findItem(R.id.item);
	 if (action.equals("b"))
	 {
	 if (ApkOptions.verbose)mi.setTitle("啰嗦模式关");
	 else mi.setTitle("啰嗦模式开");
	 }
	 else
	 {
	 mi.setVisible(false);
	 }
	 return super.onPrepareOptionsMenu(menu);
	 }

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
	 {
	 // TODO: Implement this method
	 switch (item.getItemId())
	 {
	 case R.id.item:
	 ApkOptions.verbose = !ApkOptions.verbose;
	 }
	 return super.onOptionsItemSelected(item);
	 }*/


}
