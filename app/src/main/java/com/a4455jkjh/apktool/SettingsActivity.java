package com.a4455jkjh.apktool;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String theme_key = getString(R.string.theme_key);
		String theme = sp.getString(theme_key, "light");
		if (theme.equals("light")) {
			setTheme(R.style.AppTheme);
			if (Build.VERSION.SDK_INT >= 23) {
				Window window=getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		} else if (theme.equals("dark"))
			setTheme(R.style.AppTheme_Dark);
		getFragmentManager().
			beginTransaction().
			add(android.R.id.content, new SettingsFragment()).
			commit();
		//addPreferencesFromResource(R.xml.settings);
	}


	private static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
        }
	}
}
