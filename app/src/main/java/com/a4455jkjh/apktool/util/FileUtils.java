package com.a4455jkjh.apktool.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.dialog.BuildDialog;
import com.a4455jkjh.apktool.dialog.DecodeDialog;
import com.a4455jkjh.apktool.dialog.DialogCommon;
import com.a4455jkjh.apktool.dialog.HelpDialog;
import com.a4455jkjh.apktool.dialog.KeyDialog;
import com.a4455jkjh.apktool.dialog.KeystoreDialog;
import com.a4455jkjh.apktool.dialog.VerifyDialog;
import java.io.File;
import android.widget.BaseAdapter;
import brut.androlib.Androlib;

public class FileUtils {
	private Settings settings;
	private MainActivity main;
	private int theme_id;

	public FileUtils(MainActivity main, int theme_id) {
		this.settings = new Settings(main);
		this.main = main;
		this.theme_id = theme_id;
	}
	public void updateSettings(SharedPreferences prefs) {
		settings.update(prefs);
	}
	public void processFileLong(final File file) {
		new AlertDialog.Builder(main).
			setTitle(file.getName()).
			setItems(R.array.file, null).
			create().show();
	}


	private static final String key_file= ".*\\.(keystore|jks|p12|pk12)";
	public static final String no_pass = ".*\\.(pk8|x509(\\.pem)?)";
	public void processFile(File file) {
		String name = file.getName().toLowerCase();
		AlertDialog.Builder builder = new AlertDialog.Builder(main)
			.setTitle(file.getName());
		if (name.endsWith(".apk"))
			processApk(file, builder);
		else if (name.equals("apktool.yml"))
			build(file, null);
		else if (name.matches(key_file))
			showInfo(file, builder);
		else if (name.matches(no_pass))
			showInfo(file);
	}

	private void showInfo(final File keyStore, final AlertDialog.Builder builder) {
		View v = main.getLayoutInflater().
			inflate(R.layout.password, null);
		EditText alias=(EditText)v.findViewById(R.id.alias);
		alias.setVisibility(View.GONE);
		final EditText store=(EditText)v.findViewById(R.id.store);
		final EditText key = (EditText)v.findViewById(R.id.key);
		DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2) {
				String spass = store.getText().toString();
				if (spass.equals("")) {
					showInfo(keyStore, builder);
					return;
				}
				String kpass = key.getText().toString();
				if (kpass.equals(""))
					kpass = spass;
				KeystoreDialog.KeyStoreParam param = new KeystoreDialog.KeyStoreParam();
				param.path = keyStore.getAbsolutePath();
				param.storePass = spass.toCharArray();
				param.keyPass = kpass.toCharArray();
				KeystoreDialog kdg = new KeystoreDialog(main, theme_id);
				kdg.setInput(param);
				kdg.show();
			}
		};
		builder.
			setTitle("输入密码").
			setView(v).
			setPositiveButton("确定", confirm).
			setNegativeButton("取消", null).
			create().show();
	}
	private void showInfo(File file) {
		KeystoreDialog.KeyStoreParam param = new KeystoreDialog.KeyStoreParam();
		param.path = file.getAbsolutePath();
		KeystoreDialog kdg = new KeystoreDialog(main, theme_id);
		kdg.setInput(param);
		kdg.show();
	}
	private void processApk(final File apk, AlertDialog.Builder builder) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2) {
				try {
					ApkDecoder decoder = new ApkDecoder();
					switch (p2) {
						case 0:
							settings.setDecoder(decoder, apk);
							break;
						case 1:
							settings.setDecoderResources(decoder, apk);
							break;
						case 2:
							settings.setDecoderSource(decoder, apk);
							break;
						case 3:
							ApkOptions option = settings.buildApkOptions();
							option.tmp = apk;
							option.out = new File(apk.getParentFile(), "tmp-" + System.currentTimeMillis());
							if (option.keystore != null)
								getPassword(option);
							else
								build(null, option);
							return;
						case 4:
							verify(apk);
							return;
						case 5:
							main.install(apk);
							return;
						case 6:
							importFranework(apk);
							return;
					}
					decode(decoder);
				} catch (AndrolibException e) {
					Toast.makeText(main, "错误", 0).show();
				}
			}

		};

		builder.setItems(R.array.apk, listener).
			create().show();
	}
	private void importFranework(File apk) {
		ApkOptions opt = settings.buildApkOptions();
		String name = apk.getName();
		opt.frameworkTag=name.substring(0,name.length()-4);
		Androlib lib = new Androlib(opt);
		String title,msg;
		try {
			msg = lib.installFramework(apk);
			title = "成功";
		} catch (AndrolibException e) {
			title="失败";
			msg=e.getMessage();
		}
		new AlertDialog.Builder(main).
		setTitle(title).
		setMessage(msg).
		setPositiveButton("确定",null).
		create().show();
	}

	private void decode(ApkDecoder decoder) {
		DialogCommon dialog = new DecodeDialog(main, theme_id);
		dialog.setInput(decoder);
		dialog.show();
	}
	private void build(File yml, ApkOptions option) {
		if (option == null) {
			File dir = yml.getParentFile();
			File out = new File(dir, dir.getName() + "_out.apk");
			option = settings.buildApkOptions();
			option.in = dir;
			option.out = out;
			if (option.keystore != null) {
				getPassword(option);
				return;
			}
		}
		DialogCommon builder = new BuildDialog(main, theme_id);
		builder.setInput(option);
		builder.show();
	}
	private void getPassword(final ApkOptions option) {
		View v = main.getLayoutInflater().
			inflate(R.layout.password, null);
		final EditText alias=(EditText)v.findViewById(R.id.alias);
		final EditText store=(EditText)v.findViewById(R.id.store);
		final EditText key = (EditText)v.findViewById(R.id.key);
		DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2) {
				String spass = store.getText().toString();
				if (spass.equals("")) {
					getPassword(option);
					return;
				}
				String kpass = key.getText().toString();
				if (kpass.equals(""))
					kpass = spass;
				option.storepass = spass;
				option.keypass = kpass;
				option.alias = alias.getText().toString();
				build(null, option);
			}
		};
		new AlertDialog.Builder(main).
			setTitle("输入密码").
			setView(v).
			setPositiveButton("确定", confirm).
			setNegativeButton("取消", null).
			create().show();
	}
	private void verify(File apk) {
		VerifyDialog verify = new VerifyDialog(main, theme_id);
		verify.setInput(apk);
		verify.show();
	}
	public void help() {
		HelpDialog help=new HelpDialog(main, theme_id);
		help.show();
	}
	public void generateKey(BaseAdapter adapter) {
		KeyDialog kd = new KeyDialog(main, theme_id, adapter);
		kd.show();
	}
}
