package com.a4455jkjh.apktool;
import android.app.Application;
import com.a4455jkjh.apktool.util.PrintHandler;
import java.io.File;
import java.io.InputStream;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.commons.io.IOUtils;
import android.os.Build;
import android.widget.Toast;
import brut.androlib.res.AndrolibResources;
import sun1.security.provider.JavaProvider;
import java.security.Security;
import com.a4455jkjh.apktool.util.Settings;

public class App extends Application
{

	public static String pk8,x509;
	@Override
	public void onCreate()
	{
		super.onCreate();
		JavaProvider provider = new JavaProvider();
		Security.addProvider(provider);
		PrintHandler.init();
		try
		{
			copyData();
		}
		catch (IOException e)
		{
			Toast.makeText(this, e.getMessage(), 0).show();
		}
	}
	private void copyData() throws IOException
	{
		AssetManager assets = getAssets();
		File dir = getFilesDir();
		Settings.mFrameworkDirectory = dir.getAbsolutePath();
		copyFramework(assets, dir);
		copy_aapt(assets, dir);
		copyKey(assets,dir);
	}

	private void copy_aapt(AssetManager assets, File dir) throws IOException
	{
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

	private void copyKey(AssetManager assets,File dir) throws IOException{
		File pk8 = new File(dir,"testkey.pk8");
		File x509 = new File(dir,"testkey.x509.pem");
		if(!pk8.exists()){
			InputStream i_pk8 = assets.open("testkey.pk8");
			OutputStream o = new FileOutputStream(pk8);
			IOUtils.copy(i_pk8,o);
			i_pk8.close();
			o.close();
		}
		if(!x509.exists()){
			InputStream i_x509 = assets.open("testkey.x509");
			OutputStream o = new FileOutputStream(x509);
			IOUtils.copy(i_x509,o);
			i_x509.close();
			o.close();
		}
		App.pk8=pk8.getAbsolutePath();
		App.x509=x509.getAbsolutePath();
	}
	private void copyFramework(AssetManager assets, File dir) throws IOException
	{
		File f1 = new File(dir, "1.apk");
		if (f1.exists())
			return;
		InputStream is = assets.open("android-framework.jar");
		OutputStream os = new FileOutputStream(f1);
		IOUtils.copy(is, os);
		is.close();
		os.close();
	}

}
