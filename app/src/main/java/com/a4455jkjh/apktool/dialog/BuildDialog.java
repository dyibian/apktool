package com.a4455jkjh.apktool.dialog;
import android.content.Context;
import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import brut.common.BrutException;
import com.a4455jkjh.apktool.util.ApkFile;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import com.a4455jkjh.apktool.MainActivity;

public class BuildDialog extends DialogCommon {
	public BuildDialog(MainActivity c,int theme) {
		super(c,theme);
	}

	@Override
	public void show() {
		setTitle("正在构建APK...");
		super.show();
	}

	@Override
	protected void start() throws BuildException {
		if (input == null)
			throw new BuildException("没有设置输入文件");
		if (!(input instanceof ApkOptions))
			throw new BuildException("错误");
		ApkOptions option = (ApkOptions)input;
		try {
			if (option.keystore != null) {
				ApkFile.init_kety_from_keystore(option.keystore, option.alias, option.storepass, option.keypass);
			} else if (option.pk8 != null) {
				ApkFile.init_kety_from_keyfile(option.pk8, option.x509);
			}
			if (option.in != null) {
				option.tmp = File.createTempFile("apktool", "-tmp.apk");
				Androlib builder = new Androlib(option);
				builder.build(); 
			}
			ApkFile.build(option.out.getAbsolutePath(), option.tmp.getAbsolutePath());
			option.tmp.delete();
			if (option.in == null) {
				option.out.renameTo(option.tmp);
				option.out = option.tmp;
			}
			LOGGER.info("签名成功");
			LOGGER.info("输出文件为：" + option.out.getAbsolutePath());
		} catch (IOException|BrutException e) {
			LOGGER.warning(e.getMessage());
			for (StackTraceElement s:e.getStackTrace()) {
				LOGGER.warning(s.toString());
			}
			throw new BuildException(e);
		}
	}
	static Logger LOGGER = Logger.getLogger(BuildDialog.class.getName());
}
