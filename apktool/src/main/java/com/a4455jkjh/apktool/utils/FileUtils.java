package com.a4455jkjh.apktool.utils;
import com.a4455jkjh.apktool.MainActivity;
import java.io.File;
import com.a4455jkjh.apktool.R;
import android.content.DialogInterface;
import com.a4455jkjh.apktool.dialog.DecodeDialog;
import com.a4455jkjh.apktool.dialog.BuildDialog;
import com.a4455jkjh.apktool.dialog.SignDialog;
import com.a4455jkjh.apktool.dialog.VerifyDialog;
import brut.androlib.ApkOptions;
import com.a4455jkjh.apktool.dialog.KeyDialog;

public class FileUtils {
	private final MainActivity main;
	private final FileAdapter adapter;
	private final Settings settings;

	public FileUtils (MainActivity main, FileAdapter adapter) {
		this.main = main;
		this.adapter = adapter;
		settings = new Settings();
	}

	public void process (File file) {
		String name = file.getName();
		if (name.endsWith(".apk"))
			processApk(file);
		else if (name.equals("apktool.yml"))
			build(file);
		else if(name.matches(key_file))
			read_key(file);
	}

	private void read_key (File key) {
		KeyDialog kdg = new KeyDialog(main,"密钥信息");
		kdg.setData(key);
		kdg.show();
	}

	private void build (File file) {
		BuildDialog bdg = new BuildDialog(main, "正在编译");
		bdg.setData(settings.getOptions(file, main));
		bdg.show();
	}

	private void processApk (File apk) {
		main.getDialog().
			setTitle(apk.getName()).
			setItems(R.array.apk, new Apk(apk)).
			setIcon(adapter.loadIcon(apk)).
			create().show();
	}
	private void verify (File apk) {
		VerifyDialog vdg = new VerifyDialog(main, "验证中……");
		vdg.setData(apk);
		vdg.show();
	}
	private void sign (File apk) {
		SignDialog sdg = new SignDialog(main, "正在签名……");
		sdg.setData(settings.getOptions(apk, main));
		sdg.show();
	}
	private void decode (File apk, int methods) {
		DecodeDialog ddg = new DecodeDialog(main, "正在反编译……");
		ddg.setData(settings.getDecoder(apk, null, methods,main));
		ddg.show();
	}
	private void installFramework(File apk){
		ApkOptions opt = settings.getOptions(apk,main);
		main.installFramework(opt);
	}
	private class Apk implements DialogInterface.OnClickListener {
		private File apk;
		public Apk (File apk) {
			this.apk = apk;
		}
		@Override
		public void onClick (DialogInterface p1, int p2) {
			switch (p2) {
				case 0:
				case 1:
				case 2:
					decode(apk, p2);
					break;
				case 3:
					sign(apk);
					break;
				case 4:
					verify(apk);
					break;
				case 5:
					main.install(apk);
					break;
				case 6:
					installFramework(apk);
					break;
			}
		}
	}
	private static final String key_file=".*\\.(jks|keystore|p12|pk12|bks|pk8|x509|x509\\.pem)";
}
