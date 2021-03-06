package com.a4455jkjh.apktool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.io.File;

public class ApktoolActivity extends Activity {
	protected String theme_key,theme;
	protected SharedPreferences prefs;
	private NotificationManager nm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nm = null;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		theme_key = getString(R.string.theme_key);
		theme = prefs.getString(theme_key, "light");
		Window window=getWindow();
		if (theme.equals("light")) {
			setTheme(R.style.AppTheme);
			if (Build.VERSION.SDK_INT >= 23) {
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		} else if (theme.equals("dark")) {
			setTheme(R.style.AppTheme_Dark);
			if (Build.VERSION.SDK_INT >= 23) {
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				View v =window.getDecorView();
				int flag = v.getSystemUiVisibility();
				flag = flag & (~ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				v.setSystemUiVisibility(flag);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		String t = prefs.getString(theme_key, "light");
		if (!t.equals(theme))
			recreate();
	}

	public AlertDialog.Builder getDialog() {
		return new AlertDialog.Builder(this);
	}
	public final void install(File apk) {
		Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
		Uri data = Uri.fromFile(apk);
		install.setData(data);
		startActivity(install);
	}
	public void refresh() {}

	public <T extends View> T findViewById1(int id) {
		// TODO: Implement this method
		return (T)findViewById(id);
	}
	public void notify(CharSequence title, CharSequence message) {
		Notification n=new Notification.Builder(this).
			setContentTitle(title).
			setContentText(message).
			setSmallIcon(R.mipmap.apktool).
			build();
		if(nm == null)
			nm =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.notify(0,n);
	}
	public void cancel(){
		nm.cancel(0);
	}
}
