package com.a4455jkjh.apktool.utils;
import android.content.Context;
import brut.androlib.ApkDecoder;
import brut.androlib.ApkOptions;
import java.io.File;
import com.a4455jkjh.apktool.preference.KeystorePreference;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
	public static String framework;
	public static String aapt;
	public static String pk8,x509;
	public ApkDecoder getDecoder (File in, File out, int method, Context ctx) {
		ApkDecoder decoder = new ApkDecoder();
		if (out == null) {
			String n = in.getName();
			int end = n.lastIndexOf('.');
			String target = n.substring(0, end);
			out = new File(in.getParentFile(), target);
		}
		SharedPreferences prefs = PreferenceManager.
			getDefaultSharedPreferences(ctx);
		method = 3 - method;
		if ((method & 1) > 0)
			decoder.setDecodeSources(ApkDecoder.DECODE_SOURCES_SMALI);
		else
			decoder.setDecodeSources(ApkDecoder.DECODE_SOURCES_NONE);
		if ((method & 2) > 0)
			decoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_FULL);
		else
			decoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_NONE);
		decoder.setDecodeAssets(ApkDecoder.DECODE_ASSETS_FULL);
		String api = prefs.getString("api", "15");
		decoder.setApi(Integer.parseInt(api));
		decoder.setApkFile(in);
		decoder.setOutDir(out);
		decoder.setForceDelete(true);
		decoder.setKeepBrokenResources(prefs.
									   getBoolean("keep_broken_res", false));
		decoder.setBaksmaliDebugMode(prefs.
									 getBoolean("no_debug_info", false));
		decoder.setFrameworkDir(prefs.
								getString("framework_dir", ""), framework);

		return decoder;
	}
	public ApkOptions getOptions (File yml, Context context) {
		ApkOptions options = new ApkOptions();
		File dir = yml.getParentFile();
		String name = yml.getName();
		SharedPreferences prefs = PreferenceManager.
			getDefaultSharedPreferences(context);
		if (name.equals("apktool.yml")) {
			name = dir.getName();
			options.in = dir;
			String api = prefs.getString("api", "15");
			options.api = Integer.parseInt(api);
			options.forceBuildAll = false;
			options.copyOriginalFiles = false;
			options.debugMode = prefs.getBoolean("debug", false);
			options.verbose = prefs.getBoolean("verbose", false);

			if (options.frameworkFolderLocation.equals(""))
				options.frameworkFolderLocation = framework;
			options.aaptPath = aapt;
		} else {
			options.in = yml;
			name = name.substring(0, name.length() - 4);
		}
		options.frameworkFolderLocation = prefs.
			getString("framework_dir", "");
		if (options.frameworkFolderLocation.equals(""))
			options.frameworkFolderLocation = framework;
		File out = new File(dir, name + "_out.apk");
		options.out = out;
		ApkSigner signer = KeystorePreference.getKeyParam(prefs);
		options.signTool = signer;
		return options;
	}
	public static String getFrameworkDir (Context context) {
		SharedPreferences prefs = PreferenceManager.
			getDefaultSharedPreferences(context);
		String frameworkFolderLocation = prefs.
			getString("framework_dir", "");
		if (frameworkFolderLocation.equals(""))
			frameworkFolderLocation = framework;
		return frameworkFolderLocation;
	}
}
