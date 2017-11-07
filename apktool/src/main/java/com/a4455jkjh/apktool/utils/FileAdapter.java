package com.a4455jkjh.apktool.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import java.io.File;
import java.io.FilenameFilter;

public class FileAdapter extends ApktoolAdapter<File>
implements FilenameFilter {
	private static File home;
	
	private static File cur_file = null;
	private FileUtils fileUtils;
	public static void init (Context c) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		String path = sp.getString("HOME", "");
		if (path.equals(""))
			path = "/sdcard";
		home = new File(path);
	}

	public FileAdapter (MainActivity main) {
		super(main);
		fileUtils = new FileUtils(main, this);
	}

	@Override
	public void init (TextView path, Spinner location) {
		path.setVisibility(View.VISIBLE);
		location.setVisibility(View.GONE);
	}

	public void savePath (SharedPreferences prefs) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("HOME", cur_file.getAbsolutePath());
		editor.commit();
	}

	@Override
	public void refresh1 () {
		if (cur_file == null)
			cur_file = home;
		for (File f:cur_file.listFiles(this))
			list.add(f);
		main.updatePath(cur_file.getAbsolutePath());
	}

	@Override
	public boolean accept (File p1, String p2) {
		return ! p2.startsWith(".");
	}


	@Override
	public int compare (File f1, File f2) {
		if (f1.isDirectory() && f2.isFile())
			return -1;
		if (f2.isDirectory() && f1.isFile())
			return 1;
		return f1.getName().toLowerCase().
			compareTo(f2.getName().toLowerCase());
	}

	@Override
	protected void setup (File file, TextView title, ImageView icon) {
		if (file == null)
			return;
		String n = file.getName();
		if (!cur_file.getAbsolutePath().equals("/"))
			if (file.getAbsolutePath().equals(cur_file.getParentFile().getAbsolutePath()))
				n = "..";
		title.setText(n);
		if (file.isDirectory())
			icon.setImageResource(R.drawable.folder);
		else if (n.endsWith(".apk")) {
			Drawable draw = loadIcon(file);
			if (draw != null)
				icon.setImageDrawable(draw);
		} else if (isKey(n))
			icon.setImageResource(R.drawable.key);
		else
			icon.setImageResource(R.drawable.file);
	}

	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
		File file = getItem(position);
		if (file.isDirectory()) {
			if (!file.canRead())
				return;
			cur_file = file;
			refresh();
		} else
			fileUtils.process(file);
	}

	@Override
	public boolean goBack () {
		if (cur_file == null)
			return false;
		if (cur_file.getAbsolutePath().equals(
				"/"))
			return false;
		if (!(cur_file.getParentFile().canRead()))
			return false;
		File f = cur_file.getParentFile();
		if (!f.canRead())
			return false;
		cur_file = f;
		refresh();
		return true;
	}
	public Drawable loadIcon (File apk) {
		String path = apk.getAbsolutePath();
		PackageInfo info = pm.getPackageArchiveInfo(path, 0);
		if (info == null)
			return null;
		ApplicationInfo app = info.applicationInfo;
		app.sourceDir = path;
		app.publicSourceDir = path;
		return app.loadIcon(pm);
	}

	private static final String key = ".*\\.(pk8|pk12|p12|jks|keystore|x509|x509\\.pem)";
	private boolean isKey (String name) {
		return name.matches(key);
	}

	@Override
	public void reset () {
		cur_file = null;
	}

	@Override
	public int getCount () {
		int c= super.getCount();
		File f =cur_file.getParentFile();
		if (f != null)
			if (f.canRead())
				c++;
		return c;
	}

	@Override
	public File getItem (int p1) {
		File f = cur_file.getParentFile();
		if (f != null) {
			if (f.canRead()) {
				if (p1 == 0)
					return f;
				p1--;
			}
		}
		return super.getItem(p1);
	}


}
