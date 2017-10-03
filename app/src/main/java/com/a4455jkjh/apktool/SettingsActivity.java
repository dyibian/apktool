package com.a4455jkjh.apktool;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;

public class SettingsActivity extends PreferenceActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String theme_key = getString(R.string.theme_key);
		String theme = sp.getString(theme_key, "light");
		if (theme.equals("light")){
			setTheme(R.style.AppTheme);
			if (Build.VERSION.SDK_INT >= 23)
			{
				Window window=getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		}
		else if (theme.equals("dark"))
			setTheme(R.style.AppTheme_Dark);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
}
