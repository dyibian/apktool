package com.a4455jkjh.apktool;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.Preference;

public class SettingsActivity extends ApktoolActivity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().
			beginTransaction().
			add(android.R.id.content, new SettingFrag()).
			commit();
	}
	private class SettingFrag extends PreferenceFragment
	implements Preference.OnPreferenceChangeListener {
		private String custom_key;
		@Override
		public void onCreate (Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			custom_key = getString(R.string.custom_key);
			findPreference(custom_key).
				setOnPreferenceChangeListener(this);
			findPreference("framework_dir").
				setOnPreferenceChangeListener(this);
			boolean enable = prefs.getBoolean(custom_key, false);
			setKeystore(enable);
		}

		@Override
		public boolean onPreferenceChange (Preference p1, Object p2) {
			String key = p1.getKey();
			if(key.equals(custom_key))
			setKeystore((boolean)p2);
			else if(key.equals("framework_dir"))
				((App)getApplication()).copyFramework((String)p2);
			return true;
		}
		private void setKeystore (boolean enable) {
			findPreference("keystore").setEnabled(enable);
			findPreference("gen_key").setEnabled(enable);
		}
	}
}
