package com.a4455jkjh.apktool;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.dialog.BuildDialog;
import com.a4455jkjh.apktool.dialog.DecodeDialog;
import com.a4455jkjh.apktool.dialog.DialogCommon;
import com.a4455jkjh.apktool.dialog.KeyDialog;
import com.a4455jkjh.apktool.dialog.KeystoreDialog;
import com.a4455jkjh.apktool.util.PrintHandler;
import com.a4455jkjh.apktool.util.Settings;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.text.SpannableStringBuilder;
import brut.androlib.res.AndrolibResources;
import brut.androlib.Androlib;
import android.text.style.URLSpan;
import android.text.Spanned;
import android.net.Uri;
import com.a4455jkjh.apktool.dialog.HelpDialog;

public class MainActivity extends ListActivity
implements Comparator<File>,FilenameFilter {
	private List<File> FileList;
	private static File cur_dir = null;
	private Adapter adapter;
	private Settings settings;
	private TextView path;
	private String[] formats;
	private String theme,theme_key;
	private int theme_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		settings = new Settings(this);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		settings.update(sp);
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
		setContentView(R.layout.main);
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
			processFile(f);
	}
	private static final String key_file=".*\\.(keystore|jks|p12|pk12)";
	private void processFile(File file) {
		String name = file.getName().toLowerCase();
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle(file.getName());
		if (name.endsWith(".apk"))
			processApk(file, builder);
		else if (name.equals("apktool.yml"))
			build(file, null);
		else if (name.matches(key_file))
			showInfo(file, builder);

	}
	private void processApk(final File apk, AlertDialog.Builder builder) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2) {
				if (p2 == 3) {
					ApkOptions option = settings.buildApkOptions();
					option.tmp = apk;
					option.out = new File(apk.getParentFile(), "tmp-" + System.currentTimeMillis());
					if (option.keystore != null)
						getPassword(option);
					else
						build(null, option);
					return;
				}
				try {
					ApkDecoder decoder = new ApkDecoder();
					switch (p2) {
						case 0:
							settings.setDecoder(decoder, apk);
							break;
						case 1:
							settings.setDecoderResources(decoder, apk);
							break;
						case 2:
							settings.setDecoderSource(decoder, apk);
							break;
					}
					decode(decoder);
				} catch (AndrolibException e) {
					Toast.makeText(MainActivity.this, "错误", 0).show();
				}
			}
		};
		builder.setItems(R.array.apk, listener).
			create().show();
	}
	private void showInfo(final File keyStore, final AlertDialog.Builder builder) {
		View v = getLayoutInflater().
			inflate(R.layout.password, null);
		EditText alias=(EditText)v.findViewById(R.id.alias);
		alias.setVisibility(View.GONE);
		final EditText store=(EditText)v.findViewById(R.id.store);
		final EditText key = (EditText)v.findViewById(R.id.key);
		DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2) {
				String spass = store.getText().toString();
				if (spass.equals("")) {
					showInfo(keyStore, builder);
					return;
				}
				String kpass = key.getText().toString();
				if (kpass.equals(""))
					kpass = spass;
				KeystoreDialog.KeyStoreParam param = new KeystoreDialog.KeyStoreParam();
				param.path = keyStore.getAbsolutePath();
				param.storePass = spass.toCharArray();
				param.keyPass = kpass.toCharArray();
				KeystoreDialog kdg = new KeystoreDialog(MainActivity.this, theme_id);
				kdg.setInput(param);
				kdg.show();
			}
		};
		builder.
			setTitle("输入密码").
			setView(v).
			setPositiveButton("确定", confirm).
			setNegativeButton("取消", null).
			create().show();
	}
	private void decode(ApkDecoder decoder) {
		DialogCommon dialog = new DecodeDialog(this, theme_id);
		dialog.setInput(decoder);
		dialog.show();
	}
	private void build(File yml, ApkOptions option) {
		if (option == null) {
			File dir = yml.getParentFile();
			File out = new File(dir, dir.getName() + "_out.apk");
			option = settings.buildApkOptions();
			option.in = dir;
			option.out = out;
			if (option.keystore != null) {
				getPassword(option);
				return;
			}
		}
		DialogCommon builder = new BuildDialog(this, theme_id);
		builder.setInput(option);
		builder.show();
	}

	private void getPassword(final ApkOptions option) {
		View v = getLayoutInflater().
			inflate(R.layout.password, null);
		final EditText alias=(EditText)v.findViewById(R.id.alias);
		final EditText store=(EditText)v.findViewById(R.id.store);
		final EditText key = (EditText)v.findViewById(R.id.key);
		DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2) {
				String spass = store.getText().toString();
				if (spass.equals("")) {
					getPassword(option);
					return;
				}
				String kpass = key.getText().toString();
				if (kpass.equals(""))
					kpass = spass;
				option.storepass = spass;
				option.keypass = kpass;
				option.alias = alias.getText().toString();
				build(null, option);
			}
		};
		new AlertDialog.Builder(this).
			setTitle("输入密码").
			setView(v).
			setPositiveButton("确定", confirm).
			setNegativeButton("取消", null).
			create().show();
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
				KeyDialog kd = new KeyDialog(this, theme_id, new FormatAdapter());
				kd.show();
				break;
			case R.id.help:
				help();
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
		settings.update(sp);
		String t = sp.getString(theme_key, "light");
		if (!t.equals(theme))
			recreate();
	}
	private void setKeystore(String keyStore) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String key=getText(R.string.keystore_key).toString();
		SharedPreferences.Editor spe=sp.edit();
		spe.putString(key, keyStore);
		spe.commit();
		settings.update(sp);
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
						setKeystore(msg);
					}
				});
		} else {
			builder.setTitle("创建失败").
				setMessage(msg).
				setPositiveButton("确定", null);
		}
		builder.create().show();
	}
	@Override
	protected void onStop() {
		super.onStop();
		PrintHandler.reset();
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

	private void help() {
		HelpDialog help=new HelpDialog(this,theme_id);
		help.show();
	}
}