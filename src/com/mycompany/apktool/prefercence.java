package com.mycompany.apktool;
import android.preference.*;
import android.os.*;

public class prefercence extends PreferenceActivity implements Preference.OnPreferenceChangeListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefercence);
		et = (EditTextPreference)findPreference("keypath");
		CheckBoxPreference cb=(CheckBoxPreference)findPreference("usekey");
		et.setEnabled(options.usekey);
		cb.setOnPreferenceChangeListener(this);
	}
	EditTextPreference et;

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		// TODO: Implement this method
		// TODO: Implement this method
		if (p1 instanceof CheckBoxPreference)
		{
			CheckBoxPreference cbp=(CheckBoxPreference)p1;
			switch (cbp.getKey())
			{
				case "usekey":
					et.setEnabled((Boolean)p2);
					cbp.setChecked((Boolean)p2);
					break;
			}
		}
		return false;
	}


}
