package com.a4455jkjh.apktool;
import com.a4455jkjh.apktool.utils.ApktoolAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.os.Bundle;
import com.a4455jkjh.apktool.utils.FileAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import com.a4455jkjh.apktool.dialog.HelpDialog;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.dialog.InstallFrameDialog;
import com.a4455jkjh.apktool.utils.AppAdapter;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.widget.Spinner;

public class MainActivity extends ApktoolActivity {
	private ApktoolAdapter adapter;
	private TextView path;
	private Spinner location;
	private ListView list;
	private int action;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action = prefs.getInt("action",0);
		setContentView(R.layout.main);
		path = (TextView) findViewById(R.id.path);
		list = (ListView) findViewById(R.id.list);
		location=(Spinner) findViewById(R.id.location);
		setupAdapter();
	}

	@Override
	public void refresh () {
		adapter.refresh();
	}

	private void setupAdapter () {
		if(adapter!=null)
			adapter.reset();
		if (action == 0)
			adapter = new FileAdapter(this);
		else
			adapter = new AppAdapter(this);
		adapter.init(path,location);
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
	}
	public void updatePath (CharSequence text) {
		path.setText(text);
	}

	@Override
	public void onBackPressed () {
		if (adapter.goBack())
			return;
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
		switch(action){
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
		switch(item.getItemId()){
			case R.id.action:
				action=1-action;
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
				HelpDialog hdg =new HelpDialog(this,"说明");
				hdg.setData(this);
				hdg.show();
		}
		return true;
	}

	private void setting () {
		Intent setting = new Intent(this,SettingsActivity.class);
		startActivity(setting);
	}

	public void installFramework(ApkOptions opt){
		InstallFrameDialog ifdg = new InstallFrameDialog(this,"安装框架");
		ifdg.setData(opt);
		ifdg.show();
	}
	@Override
	public void finish () {
		adapter.reset();
		super.finish();
	}
}
