package com.a4455jkjh.apktool;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import com.a4455jkjh.apktool.util.Settings;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.a4455jkjh.apktool.util.PrintHandler;
import brut.androlib.ApkDecoder;
import brut.androlib.AndrolibException;
import com.a4455jkjh.apktool.dialog.DialogCommon;
import com.a4455jkjh.apktool.dialog.DecodeDialog;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.dialog.BuildDialog;

public class MainActivity extends ListActivity
implements Comparator<File>,FilenameFilter
{
	List<File> FileList;
	File cur_dir;
	Adapter adapter;
	Settings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		settings = new Settings(this);
		FileList = new ArrayList<File>();
		adapter = new Adapter();
		setListAdapter(adapter);
		File start = new File("/sdcard");
		refresh(start);
    }
	private void refresh(File file)
	{
		if (file.isFile())
			return;
		cur_dir = file;
		FileList.clear();
		for (File f:file.listFiles(this))
			FileList.add(f);
		Collections.sort(FileList, this);
		adapter.notifyDataSetChanged();
	}

	@Override
	public int compare(File f1, File f2)
	{
		if (f1.isDirectory() && f2.isFile())
			return -1;
		if (f2.isDirectory() && f1.isFile())
			return 1;
		return f1.getName().toLowerCase().
			compareTo(f2.getName().toLowerCase());
	}

	@Override
	public boolean accept(File p1, String p2)
	{
		return ! p2.startsWith(".");
	}

	@Override
	public void onBackPressed()
	{
		if (cur_dir.getAbsolutePath().equals("/sdcard"))
			super.onBackPressed();
		else
			refresh(cur_dir.getParentFile());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		File f = FileList.get(position);
		if (f.isDirectory())
			refresh(f);
		else
			processFile(f);
	}
	private void processFile(File file)
	{
		String name = file.getName();
		if (name.endsWith(".apk"))
			decode(file);
		else if (name.equals("apktool.yml"))
			build(file);
	}
	private void decode(File apk)
	{
		ApkDecoder decoder = new ApkDecoder();
		try
		{
			settings.setDecoder(decoder, apk);
			DialogCommon dialog = new DecodeDialog(this);
			dialog.setInput(decoder);
			dialog.show();
		}
		catch (AndrolibException e)
		{}
	}
	private void build(File yml)
	{
		File dir = yml.getParentFile();
		File out = new File(dir, dir.getName() + "_out.apk");
		ApkOptions option = settings.buildApkOptions();
		option.in = dir;
		option.out = out;
		DialogCommon builder = new BuildDialog(this);
		builder.setInput(option);
		builder.show();
	}
//http://tieba.baidu.com/p/5281676166?share=9105&fr=share&see_lz=0&sfc=copy&client_type=2&client_version=8.8.8.1&unique=F7D56B766398E1BFFCA4D3CCC01292D7
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.settings:
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		settings.update(sp);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		PrintHandler.reset();
	}

	class Adapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return FileList.size();
		}

		@Override
		public File getItem(int p1)
		{
			return FileList.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			return getItem(p1).hashCode();
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			if (p2 == null)
				p2 = getLayoutInflater().
					inflate(R.layout.file_entry, p3, false);
			File file = FileList.get(p1);
			TextView name=(TextView) p2.findViewById(R.id.filename);
			ImageView icon=(ImageView) p2.findViewById(R.id.icon);
			name.setText(file.getName());
			if (file.isDirectory())
				icon.setImageResource(R.drawable.folder);
			else
				icon.setImageResource(R.drawable.file);
			return p2;
		}


	}
}
