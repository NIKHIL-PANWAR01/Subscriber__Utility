package com.protean.ondc.onboarding.service;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.protean.ondc.onboarding.model.EncKeyModel;
import com.protean.ondc.onboarding.model.KeyModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoService {
	/*
	 * public static String serverKey =
	 * "MFECAQEwBQYDK2VuBCIEIJDIsJi4nLGZ7BKaJkkIzJxubIndEOvT5hx0MKgoGYFvgSEA13ZQjiRLAA5YG6prELnmQwboQlpj0MzI94XF/kG4UmY=";
	 * public static String secretKey = "TlsPremasterSecret";
	 */
	public static void setup() {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
			System.out.println(Security.addProvider(new BouncyCastleProvider()));
		}
	}
	public KeyModel generateKey() {

		// generate ed25519 keys
		SecureRandom random = new SecureRandom();
		Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
		keyPairGenerator.init(new Ed25519KeyGenerationParameters(random));
		AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
		Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters) asymmetricCipherKeyPair.getPrivate();
		Ed25519PublicKeyParameters publicKey = (Ed25519PublicKeyParameters) asymmetricCipherKeyPair.getPublic();

		KeyModel key = new KeyModel();
		key.setPrivateKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
		key.setPublicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()));

		return key;
	}
	/*
	 * public PublicKey getPublicKey(String algo, byte[] jceBytes) throws Exception {
	 * X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(jceBytes);
	 * PublicKey key = KeyFactory.getInstance(algo, BouncyCastleProvider.PROVIDER_NAME)
	 * .generatePublic(x509EncodedKeySpec);
	 * return key;
	 * }
	 * public PrivateKey getPrivateKey(String algo, byte[] jceBytes) throws Exception {
	 * PrivateKey key = KeyFactory.getInstance(algo, BouncyCastleProvider.PROVIDER_NAME)
	 * .generatePrivate(new PKCS8EncodedKeySpec(jceBytes));
	 * return key;
	 * }
	 */

	public EncKeyModel generateEncKey() {
		KeyPair agreementKeyPair;
		try {
			agreementKeyPair = KeyPairGenerator.getInstance("X25519",
					BouncyCastleProvider.PROVIDER_NAME).generateKeyPair();
			String encPublicKey = Base64.getEncoder().encodeToString(agreementKeyPair.getPublic().getEncoded());
			String encPrivateKey = Base64.getEncoder().encodeToString(agreementKeyPair.getPrivate().getEncoded());
			EncKeyModel key1 = new EncKeyModel();
			key1.setEncPublicKey(encPublicKey);
			key1.setEncPrivatekey(encPrivateKey);
			return key1;
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String generateSignature(String req, String pk) {
		String signature = null;
		try {
			Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(Base64.getDecoder().decode(pk.getBytes()), 0);
			Signer sig = new Ed25519Signer();
			sig.init(true, privateKey);
			sig.update(req.getBytes(), 0, req.length());
			byte[] s1 = sig.generateSignature();
			signature = Base64.getEncoder().encodeToString(s1);
		} catch (DataLengthException | CryptoException e) {
			e.printStackTrace();
		}
		return signature;
	}
}