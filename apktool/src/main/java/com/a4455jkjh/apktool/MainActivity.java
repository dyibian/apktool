package com.a4455jkjh.apktool;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.dialog.HelpDialog;
import com.a4455jkjh.apktool.dialog.InstallFrameDialog;
import com.a4455jkjh.apktool.utils.ApktoolAdapter;
import com.a4455jkjh.apktool.utils.AppAdapter;
import com.a4455jkjh.apktool.utils.FileAdapter;
import java.io.File;
import android.net.Uri;
import android.os.Build;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MainActivity extends ApktoolActivity
implements OnClickListener {
	private ApktoolAdapter adapter;
	private TextView path;
	private Spinner location;
	private ListView list;
	private int action;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action = prefs.getInt("action", 0);
		setContentView(R.layout.main);
		path = findViewById1(R.id.path);
		path.setOnClickListener(this);
		list = findViewById1(R.id.list);
		location = findViewById1(R.id.location);
		FileAdapter.init(this);
		if (Build.VERSION.SDK_INT >= 23)
			if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0)
				setupAdapter();
			else
				requestPermissions(new String[]{
									   android.Manifest.permission.WRITE_EXTERNAL_STORAGE
								   }, 0);
		else
			setupAdapter();
	}

	@Override
	public void refresh () {
		//list.setse
		adapter.refresh();
	}

	@Override
	public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
		if (grantResults[0] == 0)
			setupAdapter();
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void setupAdapter () {
		if (adapter != null)
			adapter.reset();
		if (action == 0)
			adapter = new FileAdapter(this);
		else
			adapter = new AppAdapter(this);
		adapter.init(path, location);
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
	}
	public void updatePath (CharSequence text) {
		path.setText(text + "(点击设置为主目录)");
	}
	private long last_press = 0;
	@Override
	public void onBackPressed () {
		if (adapter.goBack())
			return;
		long time = System.currentTimeMillis();
		if ((time - last_press) > 2000) {
			last_press = time;
			Toast.makeText(this, "再按一次返回键退出", 0).show();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().
			inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		MenuItem item = menu.findItem(R.id.action);
		switch (action) {
			case 0:
				item.setTitle("从已安装应用读取");
				break;
			case 1:
				item.setTitle("从文件读取");
		}
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action:
				action = 1 - action;
				setupAdapter();
				break;
			case R.id.refresh:
				adapter.refresh();
				break;
			case R.id.settings:
				setting();
				break;
			case R.id.exit:
				finish();
				break;
			case R.id.help:
				HelpDialog hdg =new HelpDialog(this, "说明");
				hdg.setData(this);
				hdg.show();
		}
		return true;
	}

	private void setting () {
		Intent setting = new Intent(this, SettingsActivity.class);
		startActivity(setting);
	}

	public void installFramework (ApkOptions opt) {
		InstallFrameDialog ifdg = new InstallFrameDialog(this, "安装框架");
		ifdg.setData(opt);
		ifdg.show();
	}
	@Override
	public void finish () {
		adapter.reset();
		super.finish();
	}
	public static final String PATH = "FILE_PATH";
	public void edit (File file) {
		Intent intent = new Intent(this, SmaliActivity.class);
		intent.setData(Uri.fromFile(file));
		startActivity(intent);
	}

	@Override
	public void onClick (View p1) {
		((FileAdapter)adapter).savePath(prefs);
		Toast.makeText(this, "设置成功", 0).show();
	}


}
