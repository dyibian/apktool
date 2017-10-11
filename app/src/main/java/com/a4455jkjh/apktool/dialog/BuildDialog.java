package com.a4455jkjh.apktool.dialog;
import android.view.View;
import android.widget.TextView;
import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import brut.common.BrutException;
import brut.util.Log;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.util.ApkFile;
import java.io.File;
import java.io.IOException;

public class BuildDialog extends DialogCommon {
	public BuildDialog(MainActivity c,int theme) {
		super(c,theme);
	}

	@Override
	public void show() {
		setTitle("正在构建APK...");
		super.show();
		TextView install = (TextView)findViewById(R.id.select);
		install.setVisibility(View.VISIBLE);
		install.setText("安装");
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
			ApkFile.build(option.out.getAbsolutePath(), option.tmp.getAbsolutePath(),option.api);
			option.tmp.delete();
			if (option.in == null) {
				option.out.renameTo(option.tmp);
				option.out = option.tmp;
			}
			Log.info("签名成功");
			Log.info("输出文件为：" + option.out.getAbsolutePath());
		} catch (IOException|BrutException e) {
			Log.warning(e.getMessage());
			for (StackTraceElement s:e.getStackTrace()) {
				Log.warning(s.toString());
			}
			throw new BuildException(e);
		}
	}

	@Override
	protected void select () {
		ApkOptions o = (ApkOptions)input;
		main.install(o.out);
	}
}
