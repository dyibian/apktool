package com.a4455jkjh.apktool.dialog;
import android.app.Activity;
import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import brut.util.Log;
import com.a4455jkjh.apktool.ApktoolActivity;
import java.io.File;
import android.content.Context;

public class BuildDialog extends ProcessDialog<ApkOptions>
implements PasswordDialog.Callback{
	public BuildDialog(ApktoolActivity a,CharSequence t){
		super(a,t);
	}

	@Override
	public void show () {
		if(data.signTool.storepass.equals("")){
			PasswordDialog pdg = new PasswordDialog((ApktoolActivity)context,data.signTool.keystore,this);
			pdg.show();
			return;
		}
		super.show();
	}

	
	@Override
	protected boolean appendInfo () {
		return true;
	}

	@Override
	protected void start () throws Exception {
		data.signTool.loadKey();
		data.tmp = File.createTempFile("apktool-","-"+System.currentTimeMillis(),data.in.getParentFile());
		Androlib lib = new Androlib(data);
		lib.build();
		data.sign();
		data.tmp.delete();
		Log.info("输出文件为："+data.out);
	}

	@Override
	protected void finish () {
		setNeutralButton("安装");
	}

	@Override
	protected void onNeutralButtonClicked () {
		((ApktoolActivity)context).install(data.out);
	}

	@Override
	public void done (int type, String sp, String kp) {
		data.signTool.type=type;
		data.signTool.storepass=sp;
		data.signTool.keypass=kp;
		show();
	}

	@Override
	public void cancel () {
		// TODO: Implement this method
	}
	
}
