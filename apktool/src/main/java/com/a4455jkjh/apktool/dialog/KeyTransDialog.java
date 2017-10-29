package com.a4455jkjh.apktool.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.a4455jkjh.apktool.ApktoolActivity;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.utils.KeyParam;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.io.IOUtils;
import sun1.security.pkcs.PKCS8Key;
import sun1.security.util.DerValue;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Adapter;

public class KeyTransDialog extends ApktoolDialog<Void>
implements AdapterView.OnItemSelectedListener {
	private int src_type;
	private List<String> aliases;
	private List<String> formats;
	private KeyStore keystore;
	private PrivateKey key;
	private X509Certificate cert;
	private String kpass;
	boolean init=false;

	private Spinner aliases_spinner;
	private Spinner format_spinner;
	private LinearLayout password;
	private EditText store_pass,key_pass;
	public KeyTransDialog (ApktoolActivity a, CharSequence t) {
		super(a, t);
	}

	@Override
	protected void setup () {
		if (!init)
			return;
		aliases_spinner = findViewById(R.id.alias);
		format_spinner = findViewById(R.id.out_format);
		password = findViewById(R.id.password);
		store_pass = findViewById(R.id.storePass);
		key_pass = findViewById(R.id.keyPass);
		if (src_type == 3) {
			LinearLayout a=findViewById(R.id.aliases);
			a.setVisibility(View.GONE);
		} else
			setSpinnerAdapter(context, aliases_spinner, aliases);
		setSpinnerAdapter(context, format_spinner, formats);
		format_spinner.setOnItemSelectedListener(this);
	}

	@Override
	protected int getViewId () {
		return R.layout.trans;
	}

	@Override
	public void show () {
		super.show();
		setPositiveButton("确定");
		setNegativeButton("取消");
	}
	public void init (KeyParam param) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
		src_type = param.type;
		kpass = param.keyPass;
		if (src_type == 3)
			read_keyfile(param.keyPath, param.certOrAlias);
		else
			read_ks(param.keyPath, param.storePass);
		formats = new ArrayList<String>();
		String[] fmts = context.getResources().	getStringArray(R.array.format);
		for (int i=0;i < fmts.length;i++) {
			if (src_type == i)
				continue;
			formats.add(fmts[i]);
		}
		init = true;
		setup();
	}

	private void read_ks (String keyPath, String storePass) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
		String type="JKS";
		if (src_type == 1)
			type = "PKCS12";
		if (src_type == 2)
			type = "BKS";
		key = null;
		cert = null;
		keystore = KeyStore.getInstance(type);
		InputStream ks = new FileInputStream(keyPath);
		keystore.load(ks, storePass.toCharArray());
		aliases = new ArrayList<String>();
		aliases.add("全部(ALL)");
		Enumeration<String> as = keystore.aliases();
		while (as.hasMoreElements())
			aliases.add(as.nextElement());
	}

	private void read_keyfile (String keyPath, String certOrAlias) throws IOException, CertificateException {
		InputStream pk8 = new FileInputStream(keyPath);
		byte[] data = IOUtils.toByteArray(pk8);
		pk8.close();
		DerValue value = new DerValue(data);
		key = PKCS8Key.parseKey(value);
		InputStream x509 = new FileInputStream(certOrAlias);
		cert = (X509Certificate) CertificateFactory.getInstance("X.509").
			generateCertificate(x509);
		x509.close();
		keystore = null;
	}
	private static <T> void setSpinnerAdapter (Context context, Spinner spinner, List<T> contents) {
		ArrayAdapter adapter=new ArrayAdapter<T>(context, android.R.layout.simple_list_item_1, contents);
		spinner.setAdapter(adapter);
	}

	@Override
	public void onItemSelected (AdapterView<?> p1, View p2, int p3, long p4) {
		if (src_type == 3)
			return;
		if (p3 == 2)
			password.setVisibility(View.GONE);
		else
			password.setVisibility(View.VISIBLE);
	}

	@Override
	public void onNothingSelected (AdapterView<?> p1) {
		// Empty
	}

}
