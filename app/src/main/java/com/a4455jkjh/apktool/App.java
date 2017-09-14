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

public class App extends Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();
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
		AndrolibResources.mFrameworkDirectory = dir;
		copyFramework(assets, dir);
		copy_aapt(assets, dir);
	}

	private void copy_aapt(AssetManager assets, File dir) throws IOException
	{
		File aapt = new File(dir, "aapt");
		AndrolibResources.aapt = aapt;
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
