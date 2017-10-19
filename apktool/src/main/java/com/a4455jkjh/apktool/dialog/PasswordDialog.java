package com.a4455jkjh.apktool.dialog;
import android.content.DialogInterface;
import com.a4455jkjh.apktool.ApktoolActivity;
import com.a4455jkjh.apktool.R;
import android.widget.Spinner;
import android.widget.EditText;
import android.content.Context;

public class PasswordDialog extends ApktoolDialog {
	private final Callback callback;

	private Spinner format;
	private EditText storePass;
	private EditText keyPass;
	public interface Callback {
		public void done (int type, String sp, String kp);
		public void cancel ();
	}
	public PasswordDialog (ApktoolActivity a, CharSequence t, Callback callback) {
		super(a, "请输入" + t + "的密码");
		this.callback = callback;
	}
	@Override
	protected void setup () {
		format = (Spinner) findViewById(R.id.format);
		storePass = (EditText) findViewById(R.id.storePass);
		keyPass = (EditText) findViewById(R.id.keyPass);
	}

	@Override
	public void show () {
		super.show();
		setPositiveButton("确定");
		setNegativeButton("取消");
	}


	@Override
	protected int getViewId () {
		return R.layout.password;
	}

	@Override
	protected void onPotisitiveButtonClicked () {
		int type = format.getSelectedItemPosition();
		String storePass = this.storePass.getText().toString();
		String keyPass = this.keyPass.getText().toString();
		if (keyPass.equals(""))
			keyPass = storePass;
		callback.done(type, storePass, keyPass);
	}

	@Override
	protected void onNegativeButtonClicked () {
		callback.cancel();
	}

}
