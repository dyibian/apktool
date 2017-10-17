package com.a4455jkjh.apktool.preference;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.utils.ApkSigner;
import com.a4455jkjh.apktool.utils.Settings;
import com.a4455jkjh.apktool.utils.KeyParam;

public class KeystorePreference extends ApktoolPreference {
	private Spinner format;
	private TextView cert;
	private EditText key_path;
	private EditText alias;
	private EditText store_pass;
	private EditText key_pass;
	private View password;

	public KeystorePreference (Context c, AttributeSet a) {
		super(c, a);
	}

	@Override
	protected int getViewId () {
		return R.layout.keystore;
	}

	@Override
	protected void setup () {
		format = findViewById(R.id.format);
		cert = findViewById(R.id.cert);
		key_path = findViewById(R.id.key_path);
		alias = findViewById(R.id.alias);
		store_pass = findViewById(R.id.storePass);
		key_pass = findViewById(R.id.keyPass);
		password = findViewById(R.id.password);
		format.setOnItemSelectedListener(this);
		ApkSigner signer = getKeyParam(sp);
		format.setSelection(signer.type);
		key_path.setText(signer.keystore);
		alias.setText(signer.alias);
		store_pass.setText(signer.storepass);
		key_pass.setText(signer.keypass);
	}

	@Override
	protected void setButton (AlertDialog.Builder builder) {
		super.setButton(builder);
		builder.setNegativeButton("取消", this).
			setNeutralButton("清除密码", this);
	}

	@Override
	public void onItemSelected (AdapterView<?> p1, View p2, int p3, long p4) {
		switch (p3) {
			case 0:
			case 1:
			case 2:
				cert.setText("别名：");
				password.setVisibility(View.VISIBLE);
				break;
			case 3:
				cert.setText("证书路径：");
				password.setVisibility(View.GONE);
				store_pass.setText("");
				key_pass.setText("");
				break;
		}
	}

	@Override
	public void onNothingSelected (AdapterView<?> p1) {
		// Empty
	}

	@Override
	protected void onPotisitiveButtonClicked () {
		KeyParam p = new KeyParam();
		p. type = format.getSelectedItemPosition();
		p.keyPath = this.key_path.getText().toString();
		p.certOrAlias = this.alias.getText().toString();
		p.storePass = this.store_pass.getText().toString();
		p.keyPass = this.key_pass.getText().toString();
		saveKeyParam(sp, p);
	}

	@Override
	protected void onNeutralButtonClicked () {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key + "_store_pass", "");
		editor.putString(key + "_key_pass", "");
		editor.commit();
	}
	private static final String key = "keystore";
	public static void saveKeyParam (SharedPreferences sp, KeyParam param) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key + "_type", param.type);
		editor.putString(key + "_key_path", param.keyPath);
		editor.putString(key + "_alias", param.certOrAlias);
		editor.putString(key + "_store_pass", param.storePass);
		editor.putString(key + "_key_pass", param.keyPass);
		editor.commit();
	}
	public static ApkSigner getKeyParam (SharedPreferences sp) {
		ApkSigner param = new ApkSigner();
		boolean enable = sp.getBoolean("custom_keystore", false);
		param.v2Sign = sp.getBoolean("v2sign", true);
		if (enable) {
			param.type = sp.getInt(key + "_type", 3);
			param.keystore = sp.getString(key + "_key_path", Settings.pk8);
			param.alias = sp.getString(key + "_alias", Settings.x509);
			param.storepass = sp.getString(key + "_store_pass", "");
			param.keypass = sp.getString(key + "_key_pass", "");
		} else {
			param.type = 3;
			param.keystore = Settings.pk8;
			param.alias = Settings.x509;
		}
		return param;
	}

}
