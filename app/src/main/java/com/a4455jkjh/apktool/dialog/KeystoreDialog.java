package com.a4455jkjh.apktool.dialog;
import android.view.View;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import java.io.ByteArrayOutputStream;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import com.a4455jkjh.apktool.util.FileUtils;

public class KeystoreDialog extends DialogCommon {
	private String key;
	public KeystoreDialog (MainActivity main, int theme) {
		super(main, theme);
	}

	@Override
	protected void start () throws BuildException {
		throw new BuildException("Empty");
	}
	public void show () {
		super.show1();
		setCancelable(true);
		findViewById(R.id.progress).
			setVisibility(View.GONE);
		findViewById(R.id.close).
			setVisibility(View.VISIBLE);
		findViewById(R.id.select).
			setVisibility(View.VISIBLE);
		setTitle("密钥信息");
		out.setText("");
		KeyStoreParam param = (KeyStoreParam)input;
		try {
			if (param.path.matches(FileUtils.no_pass)) {
				readPk8(param.path);
				return;
			}
			key = param.path;
			out.append(key);
			out.append(":\n");
			String type;
			if (param.path.endsWith("12"))
				type = "PKCS12";
			else 
				type = "JKS";
			KeyStore keyStore = KeyStore.getInstance(type);
			InputStream i = new FileInputStream(param.path);
			keyStore.load(i, param.storePass);
			Enumeration<String> aliases = keyStore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				try {
					Key key = keyStore.getKey(alias, param.keyPass);
					printKey(key);
				} catch (NoSuchAlgorithmException|UnrecoverableKeyException|KeyStoreException e) {
					out.append("别名为：" + alias + " 的密码不正确\n");
				}
				Certificate[] certs = keyStore.getCertificateChain(alias);
				for (Certificate cert:certs)
					out.append(cert.toString());
				out.append("\n");
			}
		} catch (KeyStoreException e) {
			out.append(e.getMessage());
		} catch (IOException e) {
			out.append("不能打开文件：" + param.path);
		} catch (NoSuchAlgorithmException|CertificateException e) {
			out.append("打开文件失败：" + param.path);
		} catch (InvalidKeySpecException e) {
			out.append("不支持的密钥格式");
		}
	}
	private void readPk8 (String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
		String pk8,x509;
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
		key = pk8;
		InputStream pk8_in = new FileInputStream(pk8);
		InputStream x509_in = new FileInputStream(x509);
		KeyFactory kfact = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(read(pk8_in));
		PrivateKey key = kfact.generatePrivate(spec);
		out.append(pk8);
		out.append(":\n");
		printKey(key);
		Certificate c=CertificateFactory.getInstance("X.509").
			generateCertificate(x509_in);
		out.append(x509);
		out.append(":\n");
		out.append(c.toString());
	}

	@Override
	protected void select () {
		main.setKey(key);
	}

	public static class KeyStoreParam {
		public String path;
		public char[] storePass;
		public char[] keyPass;
	}
	private byte[] read (InputStream in) throws IOException {
		byte[] tmp = new byte[1024];
		int num;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((num = in.read(tmp)) != -1)
			baos.write(tmp, 0, num);
		return baos.toByteArray();
	}
	private void printKey (Key key) {
		out.append(key.toString());
		out.append("\n\n");
	}
}
