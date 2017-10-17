package com.a4455jkjh.apktool.dialog;

import android.content.Context;
import brut.util.Log;
import com.android.apksig.ApkVerifier;
import com.android.apksig.apk.ApkFormatException;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.List;
import com.a4455jkjh.apktool.ApktoolActivity;

public class VerifyDialog extends ProcessDialog<File> {
	public VerifyDialog (ApktoolActivity a, CharSequence t) {
		super(a, t);
	}

	@Override
	protected boolean appendInfo () {
		return false;
	}

	@Override
	protected void start () throws Exception {
		ApkVerifier verifier = new ApkVerifier.Builder(data).
			setMinCheckedPlatformVersion(18).
			build();
		try {
			ApkVerifier.Result result = verifier.verify();
			boolean verified = result.isVerified();
			if (verified) {
				List<X509Certificate> signerCerts = result.getSignerCertificates();
				Log.info("验证签名……");
				if (result.isVerifiedUsingV1Scheme())
					Log.info("V1签名验证成功");
				else
					Log.warning("V1签名验证失败");
				if (result.isVerifiedUsingV2Scheme())
					Log.info("V2签名验证成功");
				else
					Log.warning("V2签名验证失败");
				Log.info("签名签名数量：" + signerCerts.size());
				int signerNumber = 0;
				for (X509Certificate signerCert : signerCerts) {
					signerNumber++;
					Log.info(
						"签名" + signerNumber + " 唯一判别名："
						+ signerCert.getSubjectDN());
					byte[] encodedCert = signerCert.getEncoded();
					log("签名" + signerNumber + " 证书SHA-256: ",
						sha256.digest(encodedCert));
					log("签名" + signerNumber + " 证书SHA-1: ",
						sha1.digest(encodedCert));
					log("签名" + signerNumber + " 证书MD5: ",
						md5.digest(encodedCert));
					PublicKey publicKey = signerCert.getPublicKey();
					Log.info(
						"签名" + signerNumber + "密钥算法: "
						+ publicKey.getAlgorithm());
					int keySize = -1;
					if (publicKey instanceof RSAKey) {
						keySize = ((RSAKey) publicKey).getModulus().bitLength();
					} else if (publicKey instanceof ECKey) {
						keySize = ((ECKey) publicKey).getParams()
							.getOrder().bitLength();
					} else if (publicKey instanceof DSAKey) {
						// DSA parameters may be inherited from the certificate. We
						// don't handle this case at the moment.
						DSAParams dsaParams = ((DSAKey) publicKey).getParams();
						if (dsaParams != null) {
							keySize = dsaParams.getP().bitLength();
						}
					}
					Log.info(
						"签名" + signerNumber + "密钥大小(位): "
						+ ((keySize != -1)
						? String.valueOf(keySize) : "未知"));
					byte[] encodedKey = publicKey.getEncoded();
					log("签名" + signerNumber + "公钥SHA-256: ",
						sha256.digest(encodedKey));
					log("签名" + signerNumber + "公钥SHA-1: ",
						sha1.digest(encodedKey));
					log("签名" + signerNumber + "公钥MD5: ",
						md5.digest(encodedKey));
				}
			}
			for (ApkVerifier.IssueWithParams error : result.getErrors()) {
				Log.error(error.toString());
			}


			for (ApkVerifier.IssueWithParams warning : result.getWarnings()) {
				Log.warning(warning.toString());
			}
			for (ApkVerifier.Result.V1SchemeSignerInfo signer : result.getV1SchemeSigners()) {
				String signerName = signer.getName();
				for (ApkVerifier.IssueWithParams error : signer.getErrors()) {
					Log.error("JAR signer " + signerName + ": " + error);
				}
				for (ApkVerifier.IssueWithParams warning : signer.getWarnings()) {
					Log.warning(" JAR signer " + signerName + ": " + warning);
				}
			}
			for (ApkVerifier.Result.V2SchemeSignerInfo signer : result.getV2SchemeSigners()) {
				String signerName = "签名" + (signer.getIndex() + 1);
				for (ApkVerifier.IssueWithParams error : signer.getErrors()) {
					Log.error("APK Signature Scheme v2 " + signerName + ": " + error);
				}
				for (ApkVerifier.IssueWithParams warning : signer.getWarnings()) {
					Log.warning("APK Signature Scheme v2 " + signerName + ": " + warning);
				}
			}
			if (verified)
				Log.info("验证成功");
			else
				Log.error("验证失败");
		} catch (NoSuchAlgorithmException | IllegalStateException 
		| ApkFormatException  e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	private static void log (String n, byte[] data) {
		Log.info(n);
		Log.warning(encode(data));
	}
	private static String encode (byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b:data)
			sb.append(String.format("%02X", b));
		return sb.toString();
	}
	static{
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
			sha1 = MessageDigest.getInstance("SHA-1");
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			//won't happen
		}
	}
	private static MessageDigest sha256,sha1,md5;
}
