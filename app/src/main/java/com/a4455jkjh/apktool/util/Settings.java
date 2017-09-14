package com.a4455jkjh.apktool.util;
import android.content.SharedPreferences;
import brut.androlib.ApkDecoder;
import brut.androlib.ApkOptions;
import java.io.File;
import java.util.Collection;
import brut.androlib.AndrolibException;
import android.content.Context;
import com.a4455jkjh.apktool.R;

public class Settings
{
	//build option
	public boolean Build_forceBuildAll = false;
    public boolean Build_debugMode = false;
    public boolean Build_verbose = false;
    public boolean Build_copyOriginalFiles = false;
	
	//decode option
    private boolean mKeepBrokenResources;
    private boolean mBakDeb;
    private int mApi = 15;
	
	private final String no_debug_key;
	private final String keep_broken_key;
	public Settings(Context c){
		no_debug_key=c.getText(R.string.no_debug_key).toString();
		keep_broken_key=c.getText(R.string.keep_broken_key).toString();
	}
	public ApkOptions buildApkOptions(){
		ApkOptions option = new ApkOptions();
		option.forceBuildAll = Build_forceBuildAll;
		option.copyOriginalFiles = Build_copyOriginalFiles;
		option.debugMode = Build_debugMode;
		option.verbose = Build_verbose;
		return option;
	}
	public void setDecoder(ApkDecoder decoder,File apk) throws AndrolibException{
		decoder.setApkFile(apk);
		String name = apk.getName().replace(".apk","");
		File out = new File(apk.getParentFile(),name);
		decoder.setOutDir(out);
		decoder.setDecodeSources(ApkDecoder.DECODE_SOURCES_SMALI);
		decoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_FULL);
		decoder.setForceDelete(true);
		decoder.setApi(mApi);
		decoder.setKeepBrokenResources(mKeepBrokenResources);
		decoder.setBaksmaliDebugMode(mBakDeb);
	}
	public void update(SharedPreferences preference){
		mBakDeb = preference.getBoolean(no_debug_key,false);
		mKeepBrokenResources = preference.getBoolean(keep_broken_key,false);
	}
}
