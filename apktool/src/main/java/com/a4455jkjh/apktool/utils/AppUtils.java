package com.a4455jkjh.apktool.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.dialog.DecodeDialog;
import java.io.File;

public class AppUtils {
	private final MainActivity main;
	private final PackageManager pm;
	private final Settings settings;

	public AppUtils (MainActivity main) {
		this.main = main;
		this.pm = main.getPackageManager();
		settings = new Settings();
	}

	public void process (ApplicationInfo app) {
		new AlertDialog.Builder(main).
			setTitle(app.loadLabel(pm)).
			setIcon(app.loadIcon(pm)).
			setItems(R.array.app, new App(app)).
			create().show();
	}
	private void decode (ApplicationInfo app, int methods) {
		File apk=new File(app.sourceDir);
		CharSequence name = app.loadLabel(pm);
		File out = new File("/sdcard/apktool",name.toString());
		DecodeDialog ddg = new DecodeDialog(main, "正在反编译("+name+")……");
		ddg.setData(settings.getDecoder(apk, out, methods,main));
		ddg.show();
	}
	private void installFramework(ApplicationInfo app){
		File apk = new File(app.sourceDir);
		ApkOptions opt = settings.getOptions(apk,main);
		main.installFramework(opt);
	}
	private class App implements DialogInterface.OnClickListener {
		private ApplicationInfo app;
		public App (ApplicationInfo app) {
			this.app = app;
		}
		@Override
		public void onClick (DialogInterface p1, int p2) {
			switch (p2) {
				case 0:
				case 1:
				case 2:
					decode(app, p2);
					break;
				case 4:
					installFramework(app);
					break;
			}
		}
	}
}
