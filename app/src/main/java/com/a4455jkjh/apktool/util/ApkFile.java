package com.a4455jkjh.apktool.util;

import brut.util.Log;
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
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
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
import sun1.misc.BASE64Encoder;
import sun1.security.pkcs.ContentInfo;
import sun1.security.pkcs.PKCS7;
import sun1.security.pkcs.SignerInfo;
import sun1.security.x509.AlgorithmId;
import sun1.security.x509.X500Name;

public class ApkFile {
	private static MessageDigest sha;
	private static PrivateKey prik;
	private static List<X509Certificate> certs;
	private static final BASE64Encoder encoder;
	private static final String nnnn = "META-INF/[A-Za-z0-9]*\\.(MF|SF|RSA|DSA)";
	private static String Digest,Digest_Manifest,sig,alg;

	static{
		encoder = new BASE64Encoder();
		try {
			certs = new ArrayList<X509Certificate>();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private static void reset () {
		prik = null;
		certs.clear();
	}
	public static void init_kety_from_keystore (String keystore_file, String alias, String storepass, String keypass) throws IOException {
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
	public static void init_kety_from_keyfile (String pk8, String x509) throws IOException {
		init_kety_from_keyfile(new File(pk8), new File(x509));
	}
	public static void init_kety_from_keyfile (File pk8, File x509) throws IOException {
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
	public static void build (String apk, String ap_, int api) throws IOException {
		Log.info("正在签名 api:" + api);
		if (api < 19) {
			alg = "SHA-1";
			Digest = "SHA1-Digest";
			Digest_Manifest = "SHA1-Digest-Manifest";
			sig = "SHA1withRSA";
		} else {
			alg = "SHA-256";
			Digest = "SHA-256-Digest";
			Digest_Manifest = "SHA-256-Digest-Manifest";
			sig = "SHA256withRSA";
		}
		try {
			sha = MessageDigest.getInstance(alg);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
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
		//m.setMethod(ZipEntry.DEFLATED);
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

	private static Manifest writeSf (Manifest manifest, JarOutputStream out)
	throws UnsupportedEncodingException, IOException {
        Manifest sf = new Manifest();
        Attributes main = sf.getMainAttributes();
        main.putValue("Signature-Version", "1.0");
        main.putValue("Created-By", "1.0 (Android SignApk)");
        PrintStream print = new PrintStream(
            new DigestOutputStream(new ByteArrayOutputStream(), sha),
            true, "UTF-8");

        // Digest of the entire manifest
        manifest.write(print);
        print.flush();
        main.putValue(Digest_Manifest,
                      encoder.encode(sha.digest()));

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
            sfAttr.putValue(Digest,
                            encoder.encode(sha.digest()));
            sf.getEntries().put(entry.getKey(), sfAttr);
        }
		ZipEntry entry = new ZipEntry("META-INF/CERT.SF");
		entry.setCompressedSize(-1);
		out.putNextEntry(entry);
		return sf;
    }
	private static void writeKey (JarOutputStream out, Manifest sf) throws IOException {
		try {
			Signature signature = Signature.getInstance(sig);
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
				AlgorithmId.get(alg),
				AlgorithmId.get("RSA"),
				b);
			pkcs7 = new PKCS7(
				new AlgorithmId[] { AlgorithmId.get(alg) },
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
	private static void copyFile (JarOutputStream jos, ZipFile zip, Map<String,Attributes> map) throws IOException {
		Enumeration<? extends ZipEntry> entries = zip.entries();
		byte[] data = new byte[1024];
		int num;
		while (entries.hasMoreElements()) {
			ZipEntry in = entries.nextElement();
			if (in.isDirectory() || in.getName().matches(nnnn))
				continue;
			boolean meta = in.getName().startsWith("META-INF/");
			ZipEntry out = new ZipEntry(in);
			InputStream is = zip.getInputStream(in);
			//out.setTime(time);
			
			//out.setMethod(ZipEntry.DEFLATED);
			//out.setCompressedSize(-1);
			jos.putNextEntry(out);
			while ((num = is.read(data)) != -1) {
				jos.write(data, 0, num);
				if (!meta)
					sha.update(data, 0, num);
			}
			jos.flush();
			if (!meta) {
				Attributes attr = new Attributes();
				attr.putValue(Digest, encoder.encode(sha.digest()));
				map.put(in.getName(), attr);
			}
		}
	}
	private static byte[] readFully (File file) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (FileInputStream in = new FileInputStream(file)) {
            drain(in, result);
        }
        return result.toByteArray();
    }
	public static void drain (InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[65536];
        int chunkSize;
        while ((chunkSize = in.read(buf)) != -1) {
            out.write(buf, 0, chunkSize);
        }
    }
}
