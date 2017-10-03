package com.a4455jkjh.apktool.dialog;
import com.a4455jkjh.apktool.MainActivity;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.security.UnrecoverableKeyException;
import com.a4455jkjh.apktool.R;
import android.view.View;

public class KeystoreDialog extends DialogCommon {
	public KeystoreDialog(MainActivity main, int theme) {
		super(main, theme);
	}

	@Override
	protected void start() throws BuildException {}
	public void show() {
		super.show1();
		setCancelable(true);
		findViewById(R.id.progress).
			setVisibility(View.GONE);
		setTitle("密钥信息");
		out.setText("");
		KeyStoreParam param = (KeyStoreParam)input;
		try {
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
					out.append(key.toString());
					out.append("\n");
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
		}
	}
	public static class KeyStoreParam {
		public String path;
		public char[] storePass;
		public char[] keyPass;
	}
}
