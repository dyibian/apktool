package com.a4455jkjh.apktool.util;


import brut.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun1.misc.BASE64Encoder;
import sun1.security.pkcs.PKCS7;
import sun1.security.pkcs.SignerInfo;
public class Verify {
	JarFile jar;
	MessageDigest sha1,sha256,md5;
	BASE64Encoder encoder;
	Map<String,Keys> keys;
	JarEntry MF;
	public Verify (File file) throws IOException,NoSuchAlgorithmException {
		this(new JarFile(file));
	}
	public Verify (JarFile jar) throws NoSuchAlgorithmException {
		this.jar = jar;
		sha1 = MessageDigest.getInstance("SHA-1");
		sha256 = MessageDigest.getInstance("SHA-256");
		md5 = MessageDigest.getInstance("MD5");
		encoder = new BASE64Encoder();
		keys = new HashMap<String,Keys>();
	}
	public boolean check () throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateEncodingException {
		Manifest manifest = jar.getManifest();
		if (manifest == null) {
			Log.info("MANIFEST.MF不存在");
			return false;
		}
		if (!checkMF(manifest.getEntries()))
			return false;
		if (!checkSF(manifest))
			return false;
		return true;
	}
	private String F = "META-INF/[A-Za-z0-9_]*\\.(MF|SF|RSA|DSA)";
	private String SF = "META-INF/[A-Za-z0-9_]*\\.SF";
	private String KEY = "META-INF/[A-Za-z0-9_]*\\.(R|D)SA";
	private void setKey (JarEntry entry, String name) {
		if (!name.matches(F))
			return;
		if (name.equals("META-INF/MANIFEST.MF")) {
			MF = entry;
			return;
		}
		int s=name.indexOf('/');
		int e = name.indexOf('.');
		String k = name.substring(s + 1, e);
		Keys ks = keys.get(k);
		if (ks == null) {
			ks = new Keys();
			keys.put(k, ks);
		}
		if (name.matches(SF))
			ks.SF = entry;
		else if (name.matches(KEY))
			ks.CERT = entry;
	}
	private boolean checkMF (Map<String,Attributes> map) throws IOException {
		Enumeration<? extends JarEntry> entries = jar.entries();
		byte[] data = new byte[1024];
		int num;
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (entry.isDirectory())
				continue;
			if (name.startsWith("META-INF")) {
				setKey(entry, name);
				continue;
			}
			Attributes attr=map.get(name);
			if (attr == null) {
				String msg = String.format("MANIFEST.MF中没有%s的信息\n", name);
				Log.warning(msg);
				return false;
			}
			boolean a=false,b=false;
			InputStream is = jar.getInputStream(entry);
			while ((num = is.read(data)) != -1) {
				sha256.update(data, 0, num);
				sha1.update(data, 0, num);
			}
			is.close();
			String s256=encoder.encode(sha256.digest());
			String s1= encoder.encode(sha1.digest());
			String s256_1 = attr.getValue("SHA-256-Digest");
			if (s256_1 != null)
				a = s256.equals(s256_1);
			String s1_1 = attr.getValue("SHA1-Digest");
			if (s1_1 != null)
				b = s1.equals(s1_1);
			if (!a && !b) {
				Log.warning("文件" + name + "在MANIFEST.MF中的信息不对");
				return false;
			}
		}
		return true;
	}
	private boolean checkSF (Manifest m) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateEncodingException {
		if (keys.size() == 0) {
			Log.warning("没有签名文件");
			return false;
		}
		int i = 1;
		for (Keys k:keys.values())
			if (!checkKey(k, m, i++))
				return false;
		return true;
	}
	private boolean checkKey (Keys k, Manifest m, int cert) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateEncodingException {
		if (k.CERT == null || k.SF == null)
			return false;
		InputStream sfi = jar.getInputStream(k.SF);
		Manifest sf = new Manifest(sfi);
		sfi.close();
		InputStream mf = jar.getInputStream(MF);
		byte[] data = read(mf);
		mf.close();
		String m1=encoder.encode(sha1.digest(data));
		String m256 = encoder.encode(sha256.digest(data));
		boolean a=false,b=false;
		Attributes main = sf.getMainAttributes();
		String m1_1 = main.getValue("SHA1-Digest-Manifest");
		String m256_1 = main.getValue("SHA-256-Digest-Manifest");
		if (m256_1 != null)
			a = m256.equals(m256_1);
		if (m1_1 != null)
			b = m1.equals(m1_1);
		if (!a && !b) {
			Log.warning("MANIFEST.MF在" + k.SF.getName() + "中的信息不对");
			return false;
		}
		boolean v2=false;
		String v2s = main.getValue("X-Android-APK-Signed");
		if (v2s != null)
			v2 = v2s.equals("2");
		if (v2) {
			Log.warning("该文件使用了APK Signature scheme v2签名，请使用SDK里面的apksigner进一步验证");
		} else {
			Map<String, Attributes> entries = m.getEntries();
			for (Map.Entry<String, Attributes> entry : entries.entrySet()) {
				// Digest of the manifest stanza for this entry.
				if (entry.getKey().startsWith("META-INF/"))
					continue;
				StringBuilder sb = new StringBuilder("Name: " + entry.getKey());
				int len = sb.length();
				sb.append("\r\n");
				for (Map.Entry<Object, Object> att : entry.getValue().entrySet())
					sb.append(att.getKey() + ": " + att.getValue() + "\r\n");
				sb.append("\r\n");
				byte[] data1 = sb.toString().getBytes();
				sha1.update(data1);
				sha256.update(data1);
				m1 = encoder.encode(sha1.digest());
				m256 = encoder.encode(sha256.digest());
				a = false;
				b = false;
				main = sf.getAttributes(entry.getKey());
				m1_1 = main.getValue("SHA1-Digest");
				m256_1 = main.getValue("SHA-256-Digest");
				if (m256_1 != null)
					a = m256.equals(m256_1);
				if (m1_1 != null)
					b = m1.equals(m1_1);
				if (!a && !b) {
					if (len > 70) {
						int i = len / 70;
						while (i > 0) {
							sb.insert(i * 70, "\r\n ");
							i--;
						}
						data = sb.toString().getBytes();
						m1 = encoder.encode(sha1.digest(data));
						m256 = encoder.encode(sha256.digest(data));
						if (m256_1 != null)
							a = m256.equals(m256_1);
						if (m1_1 != null)
							b = m1.equals(m1_1);
						if (a || b)
							continue;
					}
					Log.warning(entry.getKey() + "在" + k.SF.getName() + "中的信息不对");
					return false;
				}
			}
		}
		return checkSignature(k, cert);
	}
	private boolean checkSignature (Keys k, int index) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateEncodingException {
		InputStream sfi = jar.getInputStream(k.SF);
		byte[] sf = read(sfi);
		sfi.close();
		InputStream keyi = jar.getInputStream(k.CERT);
		PKCS7 pkcs7 = new PKCS7(keyi);
		keyi.close();
		SignerInfo info = pkcs7.getSignerInfos()[0];
		String a = info.getDigestAlgorithmId().toString();
		if (a.equals("SHA"))
			a = "SHA1";
		Signature sig = Signature.getInstance(a + "with" + info.getDigestEncryptionAlgorithmId());
		X509Certificate cert = pkcs7.getCertificates()[0];
		sig.initVerify(cert);
		byte[] encoded = cert.getEncoded();
		Log.info("证书" + index + ":");
		Log.info(cert.getIssuerX500Principal().toString());
		print("sha1", sha1.digest(encoded));
		print("sha256", sha256.digest(encoded));
		print("md5", md5.digest(encoded));
		encoded = cert.getPublicKey().getEncoded();
		Log.info("公钥" + index + ":");
		print("sha1", sha1.digest(encoded));
		print("sha256", sha256.digest(encoded));
		print("md5", md5.digest(encoded));
		sig.update(sf);
		if (!sig.verify(info.getEncryptedDigest())) {
			Log.warning(k.SF.getName() + "签名校验失败");
			return false;
		}
		return true;
	}
	private void print (String n, byte[] data) {
		StringBuilder sb = new StringBuilder(n);
		sb.append(':');
		for (byte b:data) {
			sb.append(String.format("%02x", b));
		}
		Log.info(sb.toString());
	}
	private byte[] read (InputStream is) throws IOException {
		byte[] data=new byte[1024];
		int num;
		ByteArrayOutputStream baos;
		baos = new ByteArrayOutputStream();
		while ((num = is.read(data)) != -1) {
			baos.write(data, 0, num);
		}
		return baos.toByteArray();
	}
	private static class Keys {
		public JarEntry SF;
		public JarEntry CERT;

		@Override
		public String toString () {
			return "[" + SF.getName()
				+ "," + CERT.getName() + "]";
		}
	}
}
