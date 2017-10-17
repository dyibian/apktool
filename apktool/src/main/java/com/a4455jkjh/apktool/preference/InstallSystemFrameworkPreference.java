package com.a4455jkjh.apktool.preference;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import com.a4455jkjh.apktool.dialog.InstallSystemFrameworkDialog;
import android.content.pm.PackageManager;

public class InstallSystemFrameworkPreference extends Preference
{
	public InstallSystemFrameworkPreference (Context c, AttributeSet a) {
		super(c, a);
	}

	@Override
	protected void onClick () {
		InstallSystemFrameworkDialog isfdg = new InstallSystemFrameworkDialog(getContext());
		PackageManager pm = getContext().getPackageManager();
		isfdg.setData(pm);
		isfdg.show();
	}
	
}
