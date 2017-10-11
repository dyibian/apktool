package com.a4455jkjh.apktool.util;
import android.content.Context;
import android.content.SharedPreferences;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.App;
import com.a4455jkjh.apktool.R;
import java.io.File;

public class Settings {

	//build option
	private boolean Build_forceBuildAll = false;
    private boolean Build_debugMode = false;
    private boolean Build_verbose = false;
    private boolean Build_copyOriginalFiles = false;
	private String keystore;
	private String pk8,x509;
	public static String mFrameworkDirectory;
	public static String aapt;

	//decode option
    private boolean mKeepBrokenResources;
    private boolean mBakDeb;
    private int mApi = 15;

	private final String no_debug_key;
	private final String keep_broken_key;
	private final String keystore_key;
	private final String api_key;
	public Settings(Context c) {
		no_debug_key = c.getText(R.string.no_debug_key).toString();
		keep_broken_key = c.getText(R.string.keep_broken_key).toString();
		keystore_key = c.getText(R.string.keystore_key).toString();
		api_key=c.getText(R.string.api_key).toString();
	}
	public ApkOptions buildApkOptions() {
		ApkOptions option = new ApkOptions();
		option.forceBuildAll = Build_forceBuildAll;
		option.copyOriginalFiles = Build_copyOriginalFiles;
		option.debugMode = Build_debugMode;
		option.verbose = Build_verbose;
		option.pk8 = pk8;
		option.x509 = x509;
		option.keystore = keystore;
		option.aaptPath=aapt;
		option.frameworkFolderLocation=mFrameworkDirectory;
		option.api = mApi;
		return option;
	}
	public void setDecoder(ApkDecoder decoder,File apk) throws AndrolibException{
		setDecoder(decoder,apk,true,true,true);
	}
	public void setDecoderSource(ApkDecoder decoder,File apk) throws AndrolibException{
		setDecoder(decoder,apk,true,false,true);
	}
	public void setDecoderResources(ApkDecoder decoder,File apk) throws AndrolibException{
		setDecoder(decoder,apk,false,true,true);
	}
	private void setDecoder(ApkDecoder decoder, File apk, boolean decodeSource, boolean decodeResources,boolean assets) throws AndrolibException {
		decoder.setApkFile(apk);
		String name = apk.getName().replace(".apk", "");
		File out = new File(apk.getParentFile(), name);
		decoder.setOutDir(out);
		if (decodeSource)
			decoder.setDecodeSources(ApkDecoder.DECODE_SOURCES_SMALI);
		else
			decoder.setDecodeSources(ApkDecoder.DECODE_SOURCES_NONE);
		if (decodeResources)
			decoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_FULL);
		else
			decoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_NONE);
		decoder.setForceDelete(true);
		decoder.setApi(mApi);
		decoder.setKeepBrokenResources(mKeepBrokenResources);
		decoder.setBaksmaliDebugMode(mBakDeb);
		decoder.setFrameworkDir(mFrameworkDirectory);
		if(assets)
			decoder.setDecodeAssets(ApkDecoder.DECODE_ASSETS_FULL);
		else
			decoder.setDecodeAssets(ApkDecoder.DECODE_ASSETS_NONE);
	}
	public void update(SharedPreferences preference) {
		mBakDeb = preference.getBoolean(no_debug_key, false);
		mKeepBrokenResources = preference.getBoolean(keep_broken_key, false);
		mApi=Integer.parseInt( preference.getString(api_key,"15"));
		String kpath= preference.getString(keystore_key, App.pk8);
		if (kpath.equals(""))
			kpath = App.pk8;
		if (kpath.endsWith(".pk8")) {
			pk8 = kpath;
			int l=pk8.lastIndexOf('.');
			x509 = kpath.substring(0, l) + ".x509.pem";
			keystore = null;
		} else {
			keystore = kpath;
			pk8 = null;
			x509 = null;
		}
	}
}
