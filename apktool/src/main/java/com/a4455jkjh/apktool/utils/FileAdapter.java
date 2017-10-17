package com.a4455jkjh.apktool.utils;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import java.io.File;
import java.io.FilenameFilter;
import android.widget.Spinner;

public class FileAdapter extends ApktoolAdapter<File>
implements FilenameFilter {
	private static final File root = new File("/sdcard");
	private static File cur_file = null;
	private FileUtils fileUtils;

	public FileAdapter (MainActivity main) {
		super(main);
		fileUtils = new FileUtils(main,this);
	}

	@Override
	public void init (TextView path, Spinner location) {
		path.setVisibility(View.VISIBLE);
		location.setVisibility(View.GONE);
	}

	

	@Override
	public void refresh1 () {
		if (cur_file == null)
			cur_file = root;
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
		String n = file.getName();
		title.setText(n);
		if (file.isDirectory())
			icon.setImageResource(R.drawable.folder);
		else if (n.endsWith(".apk")) 
			icon.setImageDrawable(loadIcon(file));
		else if (isKey(n))
			icon.setImageResource(R.drawable.key);
		else
			icon.setImageResource(R.drawable.file);
	}

	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
		File file = list.get(position);
		if (file.isDirectory()) {
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
				root.getAbsolutePath()))
			return false;
		cur_file = cur_file.getParentFile();
		refresh();
		return true;
	}
	public Drawable loadIcon (File apk) {
		String path = apk.getAbsolutePath();
		PackageInfo info = pm.getPackageArchiveInfo(path, 0);
		ApplicationInfo app = info.applicationInfo;
		app.sourceDir=path;
		app.publicSourceDir=path;
		return app.loadIcon(pm);
	}

	private static final String key = ".*\\.(pk8|pk12|p12|jks|keystore|x509|x509\\.pem)";
	private boolean isKey (String name) {
		return name.matches(key);
	}

	@Override
	public void reset () {
		cur_file=null;
	}
	
}
