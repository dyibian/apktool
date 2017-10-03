package com.a4455jkjh.apktool.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun1.misc.IOUtils;
import sun1.security.pkcs.ContentInfo;
import sun1.security.pkcs.PKCS7;
import sun1.security.pkcs.SignerInfo;
import sun1.security.x509.AlgorithmId;
import sun1.security.x509.X500Name;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.cert.CertificateFactory;
import java.util.logging.Logger;
import sun1.misc.BASE64Encoder;

public class ApkFile {
	private static MessageDigest sha1;
	private static PrivateKey prik;
	private static List<X509Certificate> certs;
	private static Logger LOGGER = Logger.getLogger(ApkFile.class.getName());
	private static final BASE64Encoder encoder;
	private static final String nnnn = "META-INF/[A-Z]*\\.(MF|SF|RSA|DSA)";

	static{
		encoder = new BASE64Encoder();
		try {
			certs = new ArrayList<X509Certificate>();
			sha1 = MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private static void reset() {
		prik = null;
		certs.clear();
	}
	public static void init_kety_from_keystore(String keystore_file, String alias, String storepass, String keypass) throws IOException {
		try {
			KeyStore keyStore;
			if (keystore_file.endsWith(".pk12") || keystore_file.endsWith("p12"))
				keyStore = KeyStore.getInstance("PKCS12");
			else
				keyStore = KeyStore.getInstance("jks");
			keyStore.load(new FileInputStream(keystore_file),
						  storepass.toCharArray());
			if (alias.equals("") || !keyStore.isKeyEntry(alias)) {
				Enumeration<String> aliases = keyStore.aliases();
				while (aliases.hasMoreElements()) {
					alias = aliases.nextElement();
					if (keyStore.isKeyEntry(alias))
						break;
				}
			}
			prik = (PrivateKey)keyStore.getKey(alias, keypass.toCharArray());
			Certificate[] cert = keyStore.getCertificateChain(alias);
			for (Certificate c:cert)
				certs.add((X509Certificate)c);
		} catch (Exception e) {
			throw new IOException("密钥加载错误，请检查密码是否输入正确！");
		}
	}
	public static void init_kety_from_keyfile(String pk8, String x509) throws IOException {
		init_kety_from_keyfile(new File(pk8), new File(x509));
	}
	public static void init_kety_from_keyfile(File pk8, File x509) throws IOException {
		try {
			byte[] key_buf=readFully(pk8);
			PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(key_buf);
			prik = KeyFactory.getInstance("RSA").generatePrivate(pkcs8);
			Certificate c=CertificateFactory.getInstance("X.509").
				generateCertificate(new FileInputStream(x509));
			certs.add((X509Certificate)c);
		} catch (Exception e) {
			throw new IOException("密钥加载错误，请检查密钥文件是否存在！" + pk8);
		}
	}
	public static void build(String apk, String ap_) throws IOException {
		LOGGER.info("正在签名");
		OutputStream os = new FileOutputStream(apk);
		JarOutputStream jos = new JarOutputStream(os);
		ZipFile zip = new ZipFile(ap_);
		Manifest manifest = new Manifest();
		Attributes attr= manifest.getMainAttributes();
		attr.putValue("Manifest-Version", "1.0");
		attr.putValue("Created-By", "1.0 (Android SignApk)");
		copyFile(jos, zip, manifest.getEntries());
		ZipEntry m= new ZipEntry(JarFile.MANIFEST_NAME);
		m.setCompressedSize(-1);
		m.setMethod(ZipEntry.DEFLATED);
		jos.putNextEntry(m);
		manifest.write(jos);
		jos.flush();
		Manifest sf = writeSf(manifest, jos);
		sf.write(jos);
		writeKey(jos, sf);
		jos.close();
		zip.close();
		reset();
	}

	private static Manifest writeSf(Manifest manifest, JarOutputStream out)
	throws UnsupportedEncodingException, IOException {
        Manifest sf = new Manifest();
        Attributes main = sf.getMainAttributes();
        main.putValue("Signature-Version", "1.0");
        main.putValue("Created-By", "1.0 (Android SignApk)");
        PrintStream print = new PrintStream(
            new DigestOutputStream(new ByteArrayOutputStream(), sha1),
            true, "UTF-8");

        // Digest of the entire manifest
        manifest.write(print);
        print.flush();
        main.putValue("SHA1-Digest-Manifest",
                      encoder.encode(sha1.digest()));

        Map<String, Attributes> entries = manifest.getEntries();
        for (Map.Entry<String, Attributes> entry : entries.entrySet()) {
            // Digest of the manifest stanza for this entry.
            print.print("Name: " + entry.getKey() + "\r\n");
            for (Map.Entry<Object, Object> att : entry.getValue().entrySet()) {
                print.print(att.getKey() + ": " + att.getValue() + "\r\n");
            }
            print.print("\r\n");
            print.flush();

            Attributes sfAttr = new Attributes();
            sfAttr.putValue("SHA1-Digest",
                            encoder.encode(sha1.digest()));
            sf.getEntries().put(entry.getKey(), sfAttr);
        }
		ZipEntry entry = new ZipEntry("META-INF/CERT.SF");
		entry.setCompressedSize(-1);
		out.putNextEntry(entry);
		return sf;
    }
	private static void writeKey(JarOutputStream out, Manifest sf) throws IOException {
		try {
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(prik);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			sf.write(baos);
			signature.update(baos.toByteArray());
			ZipEntry entry = new ZipEntry("META-INF/CERT." + prik.getAlgorithm());
			entry.setCompressedSize(-1);
			out.putNextEntry(entry);
			PKCS7 pkcs7;
			byte[] b = signature.sign();
			SignerInfo signerInfo = new SignerInfo(
				new X500Name(certs.get(0).getIssuerX500Principal().getName()),
				certs.get(0).getSerialNumber(),
				AlgorithmId.get("SHA1"),
				AlgorithmId.get("RSA"),
				b);
			pkcs7 = new PKCS7(
				new AlgorithmId[] { AlgorithmId.get("SHA-1") },
				new ContentInfo(ContentInfo.DATA_OID, null),
				certs.toArray(new X509Certificate[certs.size()]),
				new SignerInfo[] { signerInfo });
			pkcs7.encodeSignedData(out);
			baos.close();
		} catch (InvalidKeyException e) {
			throw new IOException("密钥文件错误！");
		} catch (NoSuchAlgorithmException|SignatureException e) {
			throw new IOException("签名失败");
		}
	}
	private static void copyFile(JarOutputStream jos, ZipFile zip, Map<String,Attributes> map) throws IOException {
		Enumeration<? extends ZipEntry> entries = zip.entries();
		byte[] data = new byte[1024];
		int num;
		while (entries.hasMoreElements()) {
			ZipEntry in = entries.nextElement();
			if (in.isDirectory() || in.getName().matches(nnnn))
				continue;
			ZipEntry out = new ZipEntry(in.getName());
			InputStream is = zip.getInputStream(in);
			//out.setTime(time);
			//;out.setMethod(ZipEntry.DEFLATED);
			out.setCompressedSize(-1);
			jos.putNextEntry(out);
			while ((num = is.read(data)) != -1) {
				jos.write(data, 0, num);
				sha1.update(data, 0, num);
			}
			jos.flush();
			Attributes attr = new Attributes();
			attr.putValue("SHA1-Digest", encoder.encode(sha1.digest()));
			map.put(in.getName(), attr);
		}
	}
	private static byte[] readFully(File file) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (FileInputStream in = new FileInputStream(file)) {
            drain(in, result);
        }
        return result.toByteArray();
    }
	public static void drain(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[65536];
        int chunkSize;
        while ((chunkSize = in.read(buf)) != -1) {
            out.write(buf, 0, chunkSize);
        }
    }
}
