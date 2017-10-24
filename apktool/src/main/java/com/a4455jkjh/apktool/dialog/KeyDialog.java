package com.a4455jkjh.apktool.dialog;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import brut.util.Log;
import com.a4455jkjh.apktool.ApktoolActivity;
import com.a4455jkjh.apktool.preference.KeystorePreference;
import com.a4455jkjh.apktool.utils.KeyParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import org.apache.commons.io.IOUtils;

public class KeyDialog extends ProcessDialog<File> 
implements PasswordDialog.Callback {
	private boolean start;
	private KeyParam param;
	private int type;
	private String storePass;
	private String keyPass;

	public KeyDialog (ApktoolActivity a, CharSequence t) {
		super(a, t);
		param = new KeyParam();
		start = false;
	}
	@Override
	protected boolean appendInfo () {
		return false;
	}

	@Override
	protected void start () throws Exception {
		start = true;
		String name = data.getName();
		if (name.matches(".*\\.(pk8|x509|x509\\.pem)"))
			readKey();
		else
			readKs();
	}

	@Override
	public void show () {
		if (data.getName().matches(".*\\.(pk8|x509|x509\\.pem)"))
			super.show();
		else {
			PasswordDialog pdg = new PasswordDialog(((ApktoolActivity)context), data.getAbsolutePath(), this);
			pdg.show();
		}
	}

	@Override
	protected void finish () {
		super.finish();
		setNeutralButton("使用当前密钥");
	}

	@Override
	public void cancel () {
		dismiss();
	}
	@Override
	public void done (int type, String storePass, String keyPass) {
		this.type = type;
		this.storePass = storePass;
		this.keyPass = keyPass;
		super.show();
	}
	private void readKs () {
		String keyType;
		if (type == 0)
			keyType = "JKS";
		else if (type == 1)
			keyType = "PKCS12";
		else if (type == 2)
			keyType = "BKS"; 
		else
			return;
		param.keyPath = data.getAbsolutePath();
		param.certOrAlias = "";
		param.type = type;
		param.storePass = storePass;
		param.keyPass = keyPass;
		try {
			KeyStore keyStore = KeyStore.getInstance(keyType);
			InputStream i = new FileInputStream(data);
			keyStore.load(i, storePass.toCharArray());
			Enumeration<String> aliases = keyStore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Log.info("别名：" + alias);
				if (keyStore.isKeyEntry(alias))
					try {
						Key key = keyStore.getKey(alias, keyPass.toCharArray());
						VerifyDialog.logKey(key, "");
					} catch (NoSuchAlgorithmException|UnrecoverableKeyException|KeyStoreException e) {
						Log.warning("别名为：" + alias + " 的密码不正确\n");
					}
				Certificate[] certs = keyStore.getCertificateChain(alias);
				int num=1;
				for (Certificate cert:certs) {
					Log.info("证书：" + (num++));
					VerifyDialog.logCert((X509Certificate)cert, "  ");
				}
			}
		} catch (KeyStoreException e) {
			Log.error(e.getMessage());
		} catch (IOException e) {
			Log.error("不能打开文件：" + data);
		} catch (NoSuchAlgorithmException|CertificateException e) {
			Log.error("打开文件失败：" + data);
		} 
	}
	private void readKey () throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
		String pk8,x509;
		String path=data.getAbsolutePath();
		int end=path.length();
		if (path.endsWith(".pk8")) {
			pk8 = path;
			x509 = path.substring(0, end - 3) + "x509.pem";
		} else if (path.endsWith(".x509")) {
			x509 = path;
			pk8 = path.substring(0, end - 4) + "pk8";
		} else if (path.endsWith(".x509.pem")) {
			x509 = path;
			pk8 = path.substring(0, end - 8) + "pk8";
		} else
			throw new IOException(path + ":文件格式不支持！");
		InputStream pk8_in = new FileInputStream(pk8);
		InputStream x509_in = new FileInputStream(x509);
		KeyFactory kfact = KeyFactory.getInstance("RSA");
		byte[] pk8_data = IOUtils.toByteArray(pk8_in);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pk8_data);
		PrivateKey key = kfact.generatePrivate(spec);
		Log.info(pk8 + ":");
		VerifyDialog.logKey(key, "  ");
		X509Certificate c=(X509Certificate) CertificateFactory.getInstance("X.509").
			generateCertificate(x509_in);
		Log.info("\n" + x509 + ":");
		VerifyDialog.logCert(c, "  ");
		param.keyPath = pk8;
		param.type = 3;
		param.certOrAlias = x509;
		param.keyPass = "";
		param.storePass = "";
	}
	@Override
	protected void onNeutralButtonClicked () {
		SharedPreferences sp = PreferenceManager.
			getDefaultSharedPreferences(context);
		KeystorePreference.saveKeyParam(sp, param);
	}

	@Override
	protected CharSequence getTitle (boolean success) {
		return "密钥信息";
	}


}
