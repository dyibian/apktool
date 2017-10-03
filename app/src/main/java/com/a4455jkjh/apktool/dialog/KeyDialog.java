package com.a4455jkjh.apktool.dialog;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.util.CertAndKeyGen;
import java.io.File;
import com.a4455jkjh.apktool.MainActivity;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Adapter;

public class KeyDialog extends DialogCommon
implements View.OnClickListener,TextWatcher,AdapterView.OnItemSelectedListener {
	EditText path;
	EditText alias;
	EditText storePass;
	EditText keyPass;
	EditText commonName;
	EditText organizationUnit;
	EditText organizationName;
	EditText localityName;
	EditText stateName;
	EditText country;
	EditText date;
	Spinner format;
	BaseAdapter adapter;
	int type;
	public KeyDialog(MainActivity activity,int theme, BaseAdapter adapter) {
		super(activity,theme);
		this.adapter = adapter;
		type = 0;
	}
	public void onCreate(Bundle b) {
		onCreate1(b);
		setContentView(R.layout.keystore);
		init();
	}

	@Override
	public void show() {
		setTitle("创建密钥：");
		show1();
	}


	private void init() {
		format = (Spinner)findViewById(R.id.format);
		path = getEditText(R.id.path);
		alias = getEditText(R.id.alias);
		storePass = getEditText(R.id.storePass);
		keyPass = getEditText(R.id.keyPass);
		commonName = getEditText(R.id.name);
		organizationUnit = getEditText(R.id.organizationUnit);
		organizationName = getEditText(R.id.organizationName);
		localityName = getEditText(R.id.localityName);
		stateName = getEditText(R.id.stateName);
		country = getEditText(R.id.country);
		date = getEditText(R.id.date);
		path.addTextChangedListener(this);
		alias.addTextChangedListener(this);
		storePass.addTextChangedListener(this);
		keyPass.addTextChangedListener(this);
		date.addTextChangedListener(this);
		findViewById(R.id.cancel).
			setOnClickListener(this);
		findViewById(R.id.confirm).
			setOnClickListener(this);
		format.setAdapter(adapter);
		format.setOnItemSelectedListener(this);
	}
	private EditText getEditText(int id) {
		return (EditText)findViewById(id);
	}
	public void start() {
		//Empty
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.confirm:
				String path=this.path.getText().toString();
				if (path.equals(""))
					if (type == 0)
						path = "/sdcard/apktool/apktool.keystore";
					else if (type == 1)
						path = "/sdcard/apktool/apktool.pk12";
					else if (type == 2)
						path = "/sdcard/apktool/apktool.pk8";

				if(!path.endsWith(".keystore")&&type == 0){
					path=path+".keystore";
				}
				if(!path.endsWith(".pk12")&&type ==1){
					path=path+".pk12";
				}
				if(!path.endsWith(".pk8")&&type==2){
					path=path+".pk8";
				}
				String alias=this.alias.getText().toString();
				if (alias.equals("") && type < 2) {
					this.alias.setError("别名不能为空");
					return;
				}
				String storePass = this.storePass.getText().toString();
				if (storePass.length() < 6 && type < 2) {
					this.storePass.setError("必须填写6位以上密码");
					return;
				}
				String keyPass = this.keyPass.getText().toString();
				if (keyPass.equals(""))
					keyPass = storePass;
				if (storePass.length() < 6 && type < 2) {
					this.storePass.setError("密码不能少于6位或不填写本项。\n不填写时使用store密码作为key密码。");
					return;
				}
				String date=this.date.getText().toString();
				if (date.length() == 0) {
					this.date.setError("有效期不能为空！");
					return;
				}
				long days = Long.parseLong(date);
				String commonName = this.commonName.getText().toString();
				String organizationUnit = this.organizationUnit.getText().toString();
				String organizationName = this.organizationName.getText().toString();
				String localityName = this.localityName.getText().toString();
				String stateName = this.stateName.getText().toString();
				String country = this.country.getText().toString();
				try {
					CertAndKeyGen.generate(commonName, organizationUnit,
										   organizationName, localityName,
										   stateName, country,
										   path, alias,
										   days * 365l, storePass.toCharArray(),
										   keyPass.toCharArray(), type);
					dismiss();
					main.generateDone(true, path);
				} catch (Exception e) {
					main.generateDone(false, e.getMessage());
				}
				break;
			case R.id.cancel:
				dismiss();
				break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
		empty();
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
		empty();
	}

	@Override
	public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
		type = p3;
		switch (type) {
			case 0:
				path.setHint("/sdcard/apktool/apktool.keystore");
				break;
			case 1:
				path.setHint("/sdcard/apktool/apktool.pk12");
				break;
			case 2:
				path.setHint("/sdcard/apktool/apktool.pk8");
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> p1) {
		empty();
	}

	private void empty() {
		//Empty
	}

	@Override
	public void afterTextChanged(Editable p1) {
		String text=p1.toString().toLowerCase();
		if (path.isFocused()) {
			File f = new File(text);
			if (f.exists())
				if (f.isDirectory())
					path.setError("这是一个文件夹");
				else
					path.setError("文件已存在，store密码必须为当前文件的密码");
			else{
				boolean err=false;
				if(!text.endsWith(".keystore")&&type == 0){
					text=text+".keystore";
					err=true;
				}
				if(!text.endsWith(".pk12")&&type ==1){
					text=text+".pk12";
					err=true;
				}
				if(!text.endsWith(".pk8")&&type==2){
					text=text+".pk8";
					err=true;
				}
				if(err)
					path.setError("格式不支持，已自动替换为："+text);
				else
					path.setError(null);
			}
			return;
		}
		if (alias.isFocused()) {
			if (text.equals(""))
				alias.setError("别名不能为空");
			else
				alias.setError(null);
			return;
		}
		int l = text.length();
		if (storePass.isFocused()) {
			if (l < 6)
				storePass.setError("必须填写6位以上密码");
			else
				storePass.setError(null);
			return;
		}
		if (keyPass.isFocused()) {
			if (l < 6 && l > 0)
				keyPass.setError("密码不能少于6位或不填写本项。\n不填写时使用store密码作为key密码。");
			else
				keyPass.setError(null);
			return;
		}
	}

}
