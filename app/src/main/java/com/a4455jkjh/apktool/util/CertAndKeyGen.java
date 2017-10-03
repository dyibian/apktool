package com.a4455jkjh.apktool.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import sun1.security.x509.AlgorithmId;
import sun1.security.x509.CertificateAlgorithmId;
import sun1.security.x509.CertificateExtensions;
import sun1.security.x509.CertificateSerialNumber;
import sun1.security.x509.CertificateValidity;
import sun1.security.x509.CertificateVersion;
import sun1.security.x509.CertificateX509Key;
import sun1.security.x509.KeyIdentifier;
import sun1.security.x509.PrivateKeyUsageExtension;
import sun1.security.x509.SubjectKeyIdentifierExtension;
import sun1.security.x509.X500Name;
import sun1.security.x509.X509CertImpl;
import sun1.security.x509.X509CertInfo;
import sun1.security.x509.X509Key;
import java.io.FileInputStream;
import sun1.security.x509.CertificateSubjectName;
import sun1.security.x509.CertificateIssuerName;
import sun1.misc.BASE64Encoder;

public final class CertAndKeyGen {
	/**
	 * Creates a CertAndKeyGen object for a particular key type
	 * and signature algorithm.
	 *
	 * @param keyType type of key, e.g. "RSA", "DSA"
	 * @param sigAlg name of the signature algorithm, e.g. "MD5WithRSA",
	 *          "MD2WithRSA", "SHAwithDSA".
	 * @exception NoSuchAlgorithmException on unrecognized algorithms.
	 */
	public CertAndKeyGen(String keyType, String sigAlg)
	throws NoSuchAlgorithmException {
		keyGen = KeyPairGenerator.getInstance(keyType);
		this.sigAlg = sigAlg;
	}

	/**
	 * Creates a CertAndKeyGen object for a particular key type,
	 * signature algorithm, and provider.
	 *
	 * @param keyType type of key, e.g. "RSA", "DSA"
	 * @param sigAlg name of the signature algorithm, e.g. "MD5WithRSA",
	 *          "MD2WithRSA", "SHAwithDSA".
	 * @param providerName name of the provider
	 * @exception NoSuchAlgorithmException on unrecognized algorithms.
	 * @exception NoSuchProviderException on unrecognized providers.
	 */
	public CertAndKeyGen(String keyType, String sigAlg, String providerName)
	throws NoSuchAlgorithmException, NoSuchProviderException {
		if (providerName == null) {
			keyGen = KeyPairGenerator.getInstance(keyType);
		} else {
			try {
				keyGen = KeyPairGenerator.getInstance(keyType, providerName);
			} catch (Exception e) {
				// try first available provider instead
				keyGen = KeyPairGenerator.getInstance(keyType);
			}
		}
		this.sigAlg = sigAlg;
	}

	/**
	 * Sets the source of random numbers used when generating keys.
	 * If you do not provide one, a system default facility is used.
	 * You may wish to provide your own source of random numbers
	 * to get a reproducible sequence of keys and signatures, or
	 * because you may be able to take advantage of strong sources
	 * of randomness/entropy in your environment.
	 */
	public void         setRandom(SecureRandom generator) {
		prng = generator;
	}

	// want "public void generate (X509Certificate)" ... inherit DSA/D-H param

	/**
	 * Generates a random public/private key pair, with a given key
	 * size.  Different algorithms provide different degrees of security
	 * for the same key size, because of the "work factor" involved in
	 * brute force attacks.  As computers become faster, it becomes
	 * easier to perform such attacks.  Small keys are to be avoided.
	 *
	 * <P>Note that not all values of "keyBits" are valid for all
	 * algorithms, and not all public key algorithms are currently
	 * supported for use in X.509 certificates.  If the algorithm
	 * you specified does not produce X.509 compatible keys, an
	 * invalid key exception is thrown.
	 *
	 * @param keyBits the number of bits in the keys.
	 * @exception InvalidKeyException if the environment does not
	 *  provide X.509 public keys for this signature algorithm.
	 */
	public void generate(int keyBits)
	throws InvalidKeyException {
		KeyPair pair;

		try {
			if (prng == null) {
				prng = new SecureRandom();
			}
			keyGen.initialize(keyBits, prng);
			pair = keyGen.generateKeyPair();

		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		publicKey = pair.getPublic();
		privateKey = pair.getPrivate();

		// publicKey's format must be X.509 otherwise
		// the whole CertGen part of this class is broken.
		if (!"X.509".equalsIgnoreCase(publicKey.getFormat())) {
			throw new IllegalArgumentException("publicKey's is not X.509, but "
											   + publicKey.getFormat());
		}
	}


	/**
	 * Returns the public key of the generated key pair if it is of type
	 * <code>X509Key</code>, or null if the public key is of a different type.
	 *
	 * XXX Note: This behaviour is needed for backwards compatibility.
	 * What this method really should return is the public key of the
	 * generated key pair, regardless of whether or not it is an instance of
	 * <code>X509Key</code>. Accordingly, the return type of this method
	 * should be <code>PublicKey</code>.
	 */
	public X509Key getPublicKey() {
		if (!(publicKey instanceof X509Key)) {
			return null;
		}
		return (X509Key)publicKey;
	}

	/**
	 * Always returns the public key of the generated key pair. Used
	 * by KeyTool only.
	 *
	 * The publicKey is not necessarily to be an instance of
	 * X509Key in some JCA/JCE providers, for example SunPKCS11.
	 */
	public PublicKey getPublicKeyAnyway() {
		return publicKey;
	}

	/**
	 * Returns the private key of the generated key pair.
	 *
	 * <P><STRONG><em>Be extremely careful when handling private keys.
	 * When private keys are not kept secret, they lose their ability
	 * to securely authenticate specific entities ... that is a huge
	 * security risk!</em></STRONG>
	 */
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * Returns a self-signed X.509v3 certificate for the public key.
	 * The certificate is immediately valid. No extensions.
	 *
	 * <P>Such certificates normally are used to identify a "Certificate
	 * Authority" (CA).  Accordingly, they will not always be accepted by
	 * other parties.  However, such certificates are also useful when
	 * you are bootstrapping your security infrastructure, or deploying
	 * system prototypes.
	 *
	 * @param myname X.500 name of the subject (who is also the issuer)
	 * @param firstDate the issue time of the certificate
	 * @param validity how long the certificate should be valid, in seconds
	 * @exception CertificateException on certificate handling errors.
	 * @exception InvalidKeyException on key handling errors.
	 * @exception SignatureException on signature handling errors.
	 * @exception NoSuchAlgorithmException on unrecognized algorithms.
	 * @exception NoSuchProviderException on unrecognized providers.
	 */
	public X509Certificate getSelfCertificate(
		X500Name myname, Date firstDate, long validity)
	throws CertificateException, InvalidKeyException, SignatureException,
	NoSuchAlgorithmException, NoSuchProviderException {
		Date endDate = new Date();
		endDate.setTime(firstDate.getTime() + validity * 1000);
		return getSelfCertificate(myname, firstDate, endDate, null);
	}
	public static void generate(String commonName, String organizationUnit,
								String organizationName, String localityName,
								String stateName, String country,
								String path, String alias,
								long days, 
								char[] storePass, char[] keyPass, int type) throws NoSuchAlgorithmException, InvalidKeyException, Exception {
		CertAndKeyGen keygen = new CertAndKeyGen("RSA", "SHA256withRSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keygen.setRandom(random);
		keygen.generate(2048);
		PrivateKey key = keygen.getPrivateKey();
		CertificateExtensions ext = new CertificateExtensions();
		ext.set(SubjectKeyIdentifierExtension.NAME,
				new SubjectKeyIdentifierExtension(
					new KeyIdentifier(keygen.getPublicKeyAnyway()).getIdentifier()));
		X500Name name = new X500Name(commonName, organizationUnit,
									 organizationName, localityName,
									 stateName, country);
		Date start = new Date();
		long len = days * 24 * 3600000l;
		Date end = new Date();
		end.setTime(start.getTime() + len);
		PrivateKeyUsageExtension keyUsage = new PrivateKeyUsageExtension(start, end);
		ext.set(PrivateKeyUsageExtension.NAME, keyUsage);
		X509Certificate cert = keygen.getSelfCertificate(name,
														 start,
														 end,
														 ext);
		if (type < 2) {
			KeyStore ks;
			if (type == 0)
				ks = KeyStore.getInstance("JKS");
			else
				ks = KeyStore.getInstance("PKCS12");
			File out = new File(path);
			if (out.exists())
				ks.load(new FileInputStream(out), storePass);
			else
				ks.load(null);
			if (!out.getParentFile().exists())
				out.getParentFile().mkdirs();
			ks.setKeyEntry(alias, key, keyPass,
						   new X509Certificate[]{cert});
			System.out.println(ks);
			OutputStream os = new FileOutputStream(out);
			ks.store(os, storePass);
		} else if (type == 2) {
			OutputStream pk8 = new FileOutputStream(path);
			pk8.write(key.getEncoded());
			pk8.close();
			BASE64Encoder encoder = new BASE64Encoder();
			String x509 = encoder.encode(cert.getEncoded());
			int e = path.lastIndexOf('.');
			String p = path.substring(0, e) + ".x509.pem";
			OutputStream pem = new FileOutputStream(p);
			pem.write("-----BEGIN CERTIFICATE-----\n".getBytes());
			pem.write(x509.getBytes());
			pem.write("\n-----END CERTIFICATE-----\n".getBytes());
			//pem.write(cert.getEncoded());
			pem.close();
		}
	}
	// Like above, plus a CertificateEtensions argument, which can be null.
	public X509Certificate getSelfCertificate(X500Name myname, Date startDate,
											  Date endDate, CertificateExtensions ext)
	throws CertificateException, InvalidKeyException, SignatureException,
	NoSuchAlgorithmException, NoSuchProviderException {
		X509CertImpl    cert;

		try {
			CertificateValidity interval =
				new CertificateValidity(startDate, endDate);

			X509CertInfo info = new X509CertInfo();
			// Add all mandatory attributes
			info.set(X509CertInfo.VERSION,
					 new CertificateVersion(CertificateVersion.V3));
			info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(
						 new java.util.Random().nextInt() & 0x7fffffff));
			AlgorithmId algID = AlgorithmId.get(sigAlg);
			info.set(X509CertInfo.ALGORITHM_ID,
					 new CertificateAlgorithmId(algID));
			info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(myname));
			info.set(X509CertInfo.KEY, new CertificateX509Key(publicKey));
			info.set(X509CertInfo.VALIDITY, interval);
			info.set(X509CertInfo.ISSUER, new CertificateIssuerName(myname));
			if (ext != null) info.set(X509CertInfo.EXTENSIONS, ext);

			cert = new X509CertImpl(info);
			cert.sign(privateKey, this.sigAlg);

			return (X509Certificate)cert;

		} catch (IOException e) {
			throw new CertificateEncodingException("getSelfCert: " +
												   e.getMessage());
		}
	}

	// Keep the old method
	public X509Certificate getSelfCertificate(X500Name myname, long validity)
	throws CertificateException, InvalidKeyException, SignatureException,
	NoSuchAlgorithmException, NoSuchProviderException {
		return getSelfCertificate(myname, new Date(), validity);
	}

	private SecureRandom        prng;
	private String              sigAlg;
	private KeyPairGenerator    keyGen;
	private PublicKey           publicKey;
	private PrivateKey          privateKey;
}
