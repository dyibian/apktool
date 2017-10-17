package com.a4455jkjh.apktool.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.utils.CertAndKeyGen;
import com.a4455jkjh.apktool.utils.KeyParam;

public class GenKeystorePreference extends ApktoolPreference 
implements TextWatcher {
	private EditText path;
	private EditText alias;
	private EditText storePass;
	private EditText keyPass;
	private EditText commonName;
	private EditText organizationUnit;
	private EditText organizationName;
	private EditText localityName;
	private EditText stateName;
	private EditText country;
	private EditText date;
	private Spinner format;
	private TextView cert;
	private View password;
	public GenKeystorePreference (Context c, AttributeSet a) {
		super(c, a);
	}

	@Override
	protected int getViewId () {
		return R.layout.genks;
	}

	@Override
	protected void setup () {
		format = findViewById(R.id.format);
		path = findViewById(R.id.path);
		alias = findViewById(R.id.alias);
		storePass = findViewById(R.id.storePass);
		keyPass = findViewById(R.id.keyPass);
		commonName = findViewById(R.id.name);
		organizationUnit = findViewById(R.id.organizationUnit);
		organizationName = findViewById(R.id.organizationName);
		localityName = findViewById(R.id.localityName);
		stateName = findViewById(R.id.stateName);
		country = findViewById(R.id.country);
		date = findViewById(R.id.date);
		cert = findViewById(R.id.cert);
		password = findViewById(R.id.password);
		format.setOnItemSelectedListener(this);
		path.addTextChangedListener(this);
	}

	@Override
	protected void setButton (AlertDialog.Builder builder) {
		super.setButton(builder);
		builder.setNegativeButton("取消", this).
			setNeutralButton("创建并使用", this);
	}
	@Override
	public void onItemSelected (AdapterView<?> p1, View p2, int p3, long p4) {
		switch (p3) {
			case 0:
			case 1:
			case 2:
				cert.setText("别名：");
				password.setVisibility(View.VISIBLE);
				if (alias.getText().toString().startsWith("/"))
					alias.setText("");
				break;
			case 3:
				setAlias();
				cert.setText("证书路径：");
				password.setVisibility(View.GONE);
				keyPass.setText("");
				storePass.setText("");
				break;
		}
	}

	@Override
	public void beforeTextChanged (CharSequence p1, int p2, int p3, int p4) {
		// Empty
	}

	@Override
	public void onTextChanged (CharSequence p1, int p2, int p3, int p4) {
		// Empty
	}

	@Override
	public void afterTextChanged (Editable p1) {
		if (format.getSelectedItemPosition() != 3)
			return;
		setAlias();
	}

	private void setAlias () {
		String text = path.getText().toString();
		String target;
		if (text.isEmpty())
			target = "";
		else if (text.endsWith(".pk8")) {
			int e = text.length() - 3;
			target = text.substring(0, e) + "x509.pem";
		} else
			target = text + ".x509.pem";
		alias.setText(target);
	}

	@Override
	protected void onPotisitiveButtonClicked () {
		save();
	}

	@Override
	protected void onNeutralButtonClicked () {
		KeyParam param = save();
		KeystorePreference.saveKeyParam(sp,param);
	}
	
	private KeyParam save () {
		KeyParam param = new KeyParam();
		int type = format.getSelectedItemPosition();
		param.type = type;
		param. keyPath = this.path.getText().toString();
		param. certOrAlias = this.alias.getText().toString();
		param. storePass = storePass.getText().toString();
		param.keyPass = keyPass.getText().toString();
		param. commonName = this.commonName.getText().toString();
		param. organizationUnit = this.organizationUnit.getText().toString();
		param. organizationName = this.organizationName.getText().toString();
		param. localityName = this.localityName.getText().toString();
		param. stateName = this.stateName.getText().toString();
		param. country = this.country.getText().toString();
		long date = Long.parseLong(this.date.getText().toString());
		param.days = date * 365;
		try {
			CertAndKeyGen.generate(param);
			Toast.makeText(getContext(), "创建成功", 0).show();
		} catch (Exception e) {
			error("创建密钥失败！" + e.getMessage());
		}
		return param;
	}

}
