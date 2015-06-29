package com.mycompany.apktool;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import android.content.*;
import brut.androlib.*;
import android.view.ContextMenu.*;
import android.widget.AdapterView.*;
import android.content.res.*;
import android.preference.*;
import org.yaml.snakeyaml.*;

public class la extends ListActivity implements ListView.OnItemClickListener
{
	Context c;
	File x509,pk8;
	String aapt="aapt%s.mp3";
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		// TODO: Implement this method
		File f=(File)p1.getItemAtPosition(p3);
		if (f.isDirectory())
		{
			if (f.listFiles() == null)
			{
				Toast.makeText(this, "无权限", 2000).show();
				return;
			}
			if (f.getName().endsWith("_smali"))
			{
				smali(f.getAbsolutePath());
				return;
			}
			for (File ff:f.listFiles())
			{
				if (ff.getName().equals("apktool.yml") & ff.isFile())
				{
					encode(f);
					return;
				}
			}
			file(f);
			return;
		}
		if (f.getName().endsWith(".apk"))decode(f);

		if (f.getName().endsWith(".dex"))
		{
			baksmali(f.getAbsolutePath());
			return;
		}
	}

	File curFile;
	List<File> lf;
	adapter ad;
	AssetManager am;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//ApkOptions.verbose=true;

		am = getAssets();
		aapt=String.format(aapt,options.abi());
		copy(aapt);
		copy("pk8.mp3");
		copy("x509.mp3");
		lf = new ArrayList<File>();
		ad = new adapter();
		i = new Intent(this, MainActivity.class);
		c = this;
		setListAdapter(ad);
		registerForContextMenu(getListView());
		curFile = new File("/sdcard");
		file(curFile);
		getListView().setOnItemClickListener(this);
		//getListView().setOnCreateContextMenuListener(this);
	}
	private void copy(String n)
	{
		try
		{
			File f = File.createTempFile("aaaaaaa", null);
			f.deleteOnExit();
			InputStream is = am.open(n);
			OutputStream os = new FileOutputStream(f);
			byte[] b = new byte[is.available()];
			is.read(b);
			os.write(b);
			os.flush();
			is.close();
			os.close();
			if (n.equals(aapt))
			{
				f.setExecutable(true);
				options.aapt = f.getAbsolutePath();
			}
			else if (n.equals("x509.mp3"))
			{
				x509 = f;
				options.x509 = f.getAbsolutePath();
			}
			else
			{
				pk8 = f;
				options.pk8 = f.getAbsolutePath();
			}
		}
		catch (IOException e)
		{setTitle(e.getMessage());}
	}
	int size;
	private void file(File f)
	{
		curFile = f;
		setTitle(f.getName());
		if (!lf.isEmpty())
			lf.removeAll(lf);
		List<File> lff=new ArrayList<File>();
		List<File> ld=new ArrayList<File>();
		for (File ff:f.listFiles())
		{
			if (ff.isDirectory())
			{
				if (ff.isHidden())continue;
				ld.add(ff);
				continue;
			}
			lff.add(ff);
		}
		size = ld.size();
		Collections.sort(ld);
		Collections.sort(lff);
		lf.addAll(ld);
		lf.addAll(lff);
		ad.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO: Implement this method
		switch (keyCode)
		{
			case 4:
				//if(curFile.getParent()==null)finish();
				curFile = curFile.getParentFile();
				if (curFile == null)
				{
					System.exit(0);
					break;
				}
				file(curFile);
				break;
			case 82:
				openOptionsMenu();
				break;
		}
		return true;
	}
	Intent i;
	private void decode(File f)
	{
		/*for (File file:curFile.listFiles())
		{
			if (file.getName().equals(f.getName().replace(".apk", "")))
			{
				Toast.makeText(c, "这个文件已经反编译了", 2000).show();
				return;
			}
		}*/
		options.reset();
		options.action = "d";
		options.path = f.getAbsolutePath();
		options.target=f.getAbsolutePath().replace(".apk", "");
		options.add("-o");
		options.add(f.getAbsolutePath().replace(".apk", ""));
		startActivity(i);
	}
	private void encode(File f)
	{
		options.reset();
		options.action = "b";
		options.target = f.getAbsolutePath() + "_1.apk";
		options.path = f.getAbsolutePath();
		options.add("-a");
		options.add(options.aapt);
		if(options.verbose)options.add("-v");
		options.add("-o");
		options.add(f.getAbsolutePath() + "_1.apk");
		startActivity(i);

	}
	private void baksmali(String str)
	{
		options.action = "baksmali";
		options.path = str;
		options.target = str.replace(".dex", "_smali");
		startActivity(i);
	}
	private void smali(String str)
	{
		options.action = "smali";
		options.path = str;
		options.target = str.replace("_smali", "_1.dex");
		startActivity(i);
	}
	private void sign(String from)
	{
		//com.android.signapk.SignApk.main(new String[]{options.x509,options.pk8,from,from.replace(".apk","_sign.apk")});
		options.path = from.replace(".apk", "");
		options.action = "s";
		options.target = from;//.replace(".apk","_sign.apk");
		startActivity(i);
	}
	private void framework(String from)
	{
		options.path = from;
		options. action = "if";
		startActivity(i);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		// TODO: Implement this method
		if (v instanceof ListView)
		{
			getMenuInflater().inflate(R.menu.la_context, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO: Implement this method

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		// TODO: Implement this method
		if (item.getMenuInfo() instanceof AdapterContextMenuInfo)
		{ 
			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item 
				.getMenuInfo(); 
			// 处理菜单的点击事件  
			File sele=(File)getListView().getItemAtPosition(menuInfo.position);
			switch (item.getItemId())
			{
				case R.id.open:
					if(sele.isDirectory()){
						file(sele);
					}
					break;
				case R.id.delete:
					if (sele.delete())
					{
						Toast.makeText(c, "删除成功", 2000).show();
						file(curFile);
					}
					break;
				case R.id.rename:
					dorename(sele);
					break;
				case R.id.sign:
					sign(sele.getAbsolutePath());
					break;
				case R.id.framework:
					framework(sele.getAbsolutePath());
			}
		} 
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void dorename(File f)
	{
		AlertDialog.Builder ab=new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		EditText et=new EditText(this);
		et.setText(f.getName());
		ab.setTitle("重命名" + f.getName())
			.setView(et).setPositiveButton("确定", new dclick(et, f))
			.setNegativeButton("取消", null);
		ab.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		getMenuInflater().inflate(R.menu.main1, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		switch (item.getItemId())
		{
			case R.id.setting:
				Intent i=new Intent(this, prefercence.class);
				startActivity(i);
				break;
			case R.id.exit:
				finish();
				System.exit(0);
				break;
			case R.id.about:
				Intent ii=new Intent(this, about.class);
				startActivity(ii);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
		options.verbose = sp.getBoolean("verbose", false);
		options.usekey = sp.getBoolean("usekey", false);
		options.qiangzhi = sp.getBoolean("qiangzhi", false);
		String str=sp.getString("keypath", "");
		options.loglevel=Integer.parseInt(sp.getString("loglevel","0"));
		if (new File(str + ".x509").exists() & new File(str + ".pk8").exists() & options.usekey)
		{
			options.x509 = str + ".x509";
			options.pk8 = str + ".pk8";
		}
		else
		{
			options.x509 = x509.getAbsolutePath();
			options.pk8 = pk8.getAbsolutePath();
		}
		file(curFile);
	}

	private class dclick implements DialogInterface.OnClickListener
	{
		File ff;
		EditText et;
		public dclick(EditText e, File f)
		{
			et = e;
			ff = f;
		}
		@Override
		public void onClick(DialogInterface p1, int p2)
		{
			// TODO: Implement this method
			String s=et.getText().toString();
			if (!s.equals(""))
			{
				File filee=new File(ff.getParent() + "/" + s);
				if (ff.renameTo(filee))
				{
					s = "重命名成功";
					file(curFile);
				}
				else
				{
					s = "重名失败";
				}
			}
			Toast.makeText(c, s, 1000).show();
		}
	}
	private class adapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return lf != null ?lf.size(): 0;
		}

		@Override
		public Object getItem(int p1)
		{
			// TODO: Implement this method
			return lf.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			// TODO: Implement this method
			return p1;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			// TODO: Implement this method
			p2 = getLayoutInflater().inflate(R.layout.adapter, p3, false);
			File file=lf.get(p1);
			String s="";
			ImageView iv=(ImageView)p2.findViewById(R.id.image);
			((TextView)p2.findViewById(R.id.title)).setText(file.getName());
			try
			{
				if (file.isDirectory())
				{
					s = file.listFiles().length + "项";
					iv.setBackgroundResource(R.drawable.dic);
				}
				else
				{
					long l=file.length();
					s = open.FormetFileSize(l);
					iv.setBackgroundResource(R.drawable.txt);
					String str=file.getName();
					if (str.endsWith(".zip") | str.endsWith(".jar"))
					{
						iv.setBackgroundResource(R.drawable.zip);
					}
				}

			}
			catch (Exception r)
			{
				s = " ";
				if (p1 < size)iv.setBackgroundResource(R.drawable.dic);
				else iv.setBackgroundResource(R.drawable.txt);
				if (file.isDirectory())s = "文件夹";
			}
			((TextView)p2.findViewById(R.id.detail)).setText(s);
			return p2;
		}
		

	}
}
