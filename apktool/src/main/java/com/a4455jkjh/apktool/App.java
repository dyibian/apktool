package com.a4455jkjh.apktool;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import brut.util.Log;
import com.a4455jkjh.apktool.utils.Settings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import org.apache.commons.io.IOUtils;
import sun1.security.provider.JavaProvider;

public class App extends Application {
	public static String pk8,x509;

	public void copyFramework (String p2) {
		try {
			copyFramework(getAssets(), new File(p2));
		} catch (IOException e) {}
	}

	@Override
	public void onCreate () {
		super.onCreate();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
			StrictMode.setVmPolicy(builder.build());
		}
		Log.reset();
		JavaProvider provider = new JavaProvider();
		Security.addProvider(provider);
		try {
			copyFiles();
		} catch (IOException e) {}
	}

	private void copyFiles () throws IOException {
		AssetManager assets = getAssets();
		File target = getFilesDir();
		copy_aapt(assets, target);
		copyKey(assets, target);
		SharedPreferences sp = PreferenceManager.
			getDefaultSharedPreferences(this);
		String frame = sp.getString("framework_dir", "");
		File  f;
		if (frame.equals(""))
			f = new File(target, "framework");
		else
			f = new File(frame);
		copyFramework(assets, f);
	}
	private void copy_aapt (AssetManager assets, File dir) throws IOException {
		File aapt = new File(dir, "aapt");
		Settings.aapt = aapt.getAbsolutePath();
		if (aapt.exists())
			return;
		String name;
		if (Build.VERSION.SDK_INT >= 21)
			name = Build.SUPPORTED_ABIS[0];
		else
			name = Build.CPU_ABI;
		InputStream is = assets.open("aapt/" + name + "/aapt");
		OutputStream os = new FileOutputStream(aapt);
		IOUtils.copy(is, os);
		is.close();
		os.close();
		aapt.setExecutable(true, false);
	}
	private void copyKey (AssetManager assets, File dir) throws IOException {
		File pk8 = new File(dir, "testkey.pk8");
		File x509 = new File(dir, "testkey.x509.pem");
		Settings.pk8 = pk8.getAbsolutePath();
		Settings.x509 = x509.getAbsolutePath();
		if (!pk8.exists()) {
			InputStream i_pk8 = assets.open("testkey.pk8");
			OutputStream o = new FileOutputStream(pk8);
			IOUtils.copy(i_pk8, o);
			i_pk8.close();
			o.close();
		}
		if (!x509.exists()) {
			InputStream i_x509 = assets.open("testkey.x509");
			OutputStream o = new FileOutputStream(x509);
			IOUtils.copy(i_x509, o);
			i_x509.close();
			o.close();
		}
		App.pk8 = pk8.getAbsolutePath();
		App.x509 = x509.getAbsolutePath();
	}
	public void copyFramework (AssetManager assets, File dir) throws IOException {
		Settings.framework = dir.getAbsolutePath();
		File f1 = new File(dir, "1.apk");
		if (!dir.exists())
			dir.mkdirs();
		if (f1.exists())
			return;
		InputStream is = assets.open("android-framework.jar");
		OutputStream os = new FileOutputStream(f1);
		IOUtils.copy(is, os);
		is.close();
		os.close();
	}
}
