package com.a4455jkjh.apktool.utils;
import brut.androlib.SignTool;
import java.io.File;
import java.util.List;
import com.android.apksig.ApkSigner.SignerConfig;
import java.util.ArrayList;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.security.KeyStore;
import java.util.Enumeration;

public class ApkSigner implements SignTool {
	public boolean v2Sign;
	public String keystore;
	public int type;
	public String storepass;
	public String keypass;
	public String alias;

	private List<SignerConfig> signerConfigs;
	public ApkSigner () {
		signerConfigs = new ArrayList<SignerConfig>();
	}
	@Override
	public void loadKey () throws Exception {
		switch (type) {
			case 0:
			case 1:
			case 2:
				loadKeyFromKs();
				break;
			case 3:
				loadKeyFromPk8();
				break;
		}
	}
	private void loadKeyFromKs () throws Exception {
		String keyType;
		switch (type) {
			case 0:
				keyType = "JKS";
				break;
			case 1:
				keyType = "PKCS12";
				break;
			case 2:
				keyType = "BKS";
				break;
			default:
				return;
		}
		KeyStore ks = KeyStore.getInstance(keyType);
		InputStream in = new FileInputStream(keystore);
		ks.load(in, storepass.toCharArray());
		if (alias.equals("")) {
			Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements()) {
				alias = aliases.nextElement();
				if (ks.isKeyEntry(alias))
					break;
			}
		}
		if (!ks.isKeyEntry(alias))
			throw new Exception("不是有效的别名");
		if (keypass.equals(""))
			keypass = storepass;
		PrivateKey key = (PrivateKey) ks.getKey(alias, keypass.toCharArray());
		X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
		SignerConfig config = new SignerConfig.Builder(alias.toUpperCase(), key, cert).
			build();
		signerConfigs.add(config);
	}
	private void loadKeyFromPk8 () throws Exception {
		File pk8 = new File(keystore);
		InputStream k = new FileInputStream(pk8);
		byte[] data = IOUtils.toByteArray(k);
		k.close();
		PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(data);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		PrivateKey key = fact.generatePrivate(kspec);
		InputStream x509 = new FileInputStream(alias);
		X509Certificate cert = (X509Certificate) CertificateFactory.
			getInstance("X.509").
			generateCertificate(x509);
		x509.close();
		SignerConfig config = new SignerConfig.Builder("ANDROID", key, cert).
			build();
		signerConfigs.add(config);
	}
	@Override
	public void sign (File in, File out) throws Exception {
		new com.android.apksig.ApkSigner.Builder(signerConfigs).
			setInputApk(in).
			setOutputApk(out).
			setOtherSignersSignaturesPreserved(false).
			setMinSdkVersion(15).
			setV1SigningEnabled(true).
			setV2SigningEnabled(v2Sign).
			build().
			sign();
	}
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder(keystore);
		sb.append(',').
			append(type).
			append(',').
			append(alias).
			append(',').
			append(storepass).
			append(',').
			append(keypass);
		return sb.toString();
	}
	@Override
	public char charAt (int p1) {
		return toString().charAt(p1);
	}
	@Override
	public CharSequence subSequence (int p1, int p2) {
		return toString().subSequence(p1, p2);
	}
	@Override
	public int length () {
		return toString().length();
	}
}

