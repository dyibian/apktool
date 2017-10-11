package com.a4455jkjh.apktool;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.a4455jkjh.apktool.util.FileUtils;
import com.a4455jkjh.apktool.util.PrintHandler;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import brut.util.Log;

public class MainActivity extends ListActivity
implements Comparator<File>,FilenameFilter,AdapterView.OnItemLongClickListener {
	private List<File> FileList;
	private static File cur_dir = null;
	private Adapter adapter;
	private FileUtils fileUtils;
	private TextView path;
	private String[] formats;
	private String theme,theme_key;
	private int theme_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		theme_key = getString(R.string.theme_key);
		theme = sp.getString(theme_key, "light");
		Window window=getWindow();
		if (theme.equals("light")) {
			setTheme(R.style.AppTheme);
			theme_id = R.style.AppTheme_Dialog;
			if (Build.VERSION.SDK_INT >= 23) {
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		} else if (theme.equals("dark")) {
			setTheme(R.style.AppTheme_Dark);
			theme_id = R.style.AppTheme_Dialog_Dark;
		}
		fileUtils=new FileUtils(this,theme_id);
		fileUtils.updateSettings(sp);
		setContentView(R.layout.main);
		getListView().setOnItemLongClickListener(this);
		formats = getResources().
			getStringArray(R.array.format);
		path = (TextView)findViewById(R.id.path);
		FileList = new ArrayList<File>();
		adapter = new Adapter();
		setListAdapter(adapter);
		if (cur_dir == null)
			cur_dir = new File("/sdcard");
		refresh(cur_dir);
    }
	private void refresh(File file) {
		if (file.isFile())
			return;
		cur_dir = file;
		path.setText(file.getAbsolutePath());
		FileList.clear();
		for (File f:file.listFiles(this))
			FileList.add(f);
		Collections.sort(FileList, this);
		adapter.notifyDataSetChanged();
	}

	public void refresh() {
		refresh(cur_dir);
	}
	@Override
	public int compare(File f1, File f2) {
		if (f1.isDirectory() && f2.isFile())
			return -1;
		if (f2.isDirectory() && f1.isFile())
			return 1;
		return f1.getName().toLowerCase().
			compareTo(f2.getName().toLowerCase());
	}

	@Override
	public boolean accept(File p1, String p2) {
		return ! p2.startsWith(".");
	}

	@Override
	public void onBackPressed() {
		if (cur_dir.getAbsolutePath().equals("/sdcard"))
			super.onBackPressed();
		else
			refresh(cur_dir.getParentFile());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File f = FileList.get(position);
		if (f.isDirectory())
			refresh(f);
		else
			fileUtils.processFile(f);
	}

	@Override
	public boolean onItemLongClick (AdapterView<?> p1, View p2, int p3, long p4) {
		File f = FileList.get(p3);
		fileUtils.processFileLong(f);
		return true;
	}
	
	public void install (File apk) {
		Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
		Uri data = Uri.fromFile(apk);
		install.setData(data);
		startActivity(install);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				break;
			case R.id.generateKeystore:
				fileUtils.generateKey(new FormatAdapter());
				break;
			case R.id.help:
				fileUtils.help();
				break;
			case R.id.refresh:
				refresh();
				break;
			case R.id.exit:
				finish();
				cur_dir = null;
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		fileUtils.updateSettings(sp);
		String t = sp.getString(theme_key, "light");
		if (!t.equals(theme))
			recreate();
	}
	public void setKey(String keyStore) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String key=getText(R.string.keystore_key).toString();
		SharedPreferences.Editor spe=sp.edit();
		spe.putString(key, keyStore);
		spe.commit();
		fileUtils.updateSettings(sp);
		Toast.makeText(this,"设置成功！",0).show();
	}
	public void generateDone(boolean success, final String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (success) {
			builder.setTitle("创建成功").
				setMessage("是否使用" + msg + "来签名？").
				setNegativeButton("不使用", null).
				setPositiveButton("使用", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface a, int b) {
						setKey(msg);
					}
				});
		} else {
			builder.setTitle("创建失败").
				setMessage(msg).
				setPositiveButton("确定", null);
		}
		builder.create().show();
	}

	class FormatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return formats.length;
		}

		@Override
		public String getItem(int p1) {
			return formats[p1];
		}

		@Override
		public long getItemId(int p1) {
			return getItem(p1).hashCode();
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3) {
			if (p2 == null)
				p2 = getLayoutInflater().
					inflate(R.layout.file_entry, p3, false);
			TextView name=(TextView) p2.findViewById(R.id.filename);
			ImageView icon=(ImageView) p2.findViewById(R.id.icon);
			name.setText(formats[p1]);
			icon.setVisibility(View.GONE);
			return p2;
		}
	}
	class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return FileList.size();
		}

		@Override
		public File getItem(int p1) {
			return FileList.get(p1);
		}

		@Override
		public long getItemId(int p1) {
			return getItem(p1).hashCode();
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3) {
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
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO: Implement this method
		super.onConfigurationChanged(newConfig);
	}

}
