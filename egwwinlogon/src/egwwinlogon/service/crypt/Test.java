/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service.crypt;

import de.flexiprovider.api.Registry;
import javax.crypto.Cipher;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.SecureRandom;
import java.rmi.registry.LocateRegistry;

import de.flexiprovider.pqc.FlexiPQCProvider;
import de.flexiprovider.core.FlexiCoreProvider;
import de.flexiprovider.ec.FlexiECProvider;
import de.flexiprovider.nf.FlexiNFProvider;
import de.flexiprovider.pqc.ecc.mceliece.McElieceKeyFactory;
import de.flexiprovider.pqc.ecc.mceliece.McEliecePrivateKey;
import de.flexiprovider.pqc.ecc.mceliece.McEliecePrivateKeySpec;
import de.flexiprovider.pqc.ecc.mceliece.McEliecePublicKeySpec;
import de.flexiprovider.pqc.ecc.mceliece.McEliecePublicKey;
import de.flexiprovider.pqc.PQCRegistry;
import java.security.spec.AlgorithmParameterSpec;

/**
 *
 * @author swe
 */
public class Test {
    public Test() throws Exception {

        Security.addProvider(new FlexiCoreProvider());
        Security.addProvider(new FlexiPQCProvider());
        Security.addProvider(new FlexiECProvider());
        Security.addProvider(new FlexiNFProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("McEliece","FlexiPQC"); // 1. get instance of McEliecePKC key pair generator:
        kpg.initialize(1024); // 2. initialize the KPG with key size n:, n =1024 // or with parameters m and t via a McElieceParameterSpec:

        /*McElieceParameterSpec paramSpec = new McElieceParameterSpec(m, t);
        * kpg.initialize(paramSpec, Registry.getSecureRandom()); */
        KeyPair keyPair = kpg.generateKeyPair(); // 3. create McEliecePKC key pair:
        byte[] encodedPublicKey = keyPair.getPublic().getEncoded(); // 4. get the encoded public keys from the key pair:
        byte[] encodedPrivateKey = keyPair.getPrivate().getEncoded(); // 4. get the encoded private keys from the key pair:

        /*
        * File to be encrypted // Will try and get user to inout file to be encrypted
        */ // Might also do for pics aswell
        String message = "secret message"; // The message which should be encrypted
        byte[] messageBytes = message.getBytes();

        /*
        * Encryption Process
        */
        KeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey); // Generate KeySpec from encoded McEliece publickey:
        KeyFactory keyFactory = KeyFactory.getInstance("McEliece", "FlexiPQC"); // Initialize the McEliece key factory:
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec); // Decode McEliece public key:
        //SecureRandom secureRand = Registry.getSecureRandom(); // The source of randomness
        Cipher cipher = Cipher.getInstance("McEliecePKCS"); // Obtain a McEliecePKC Cipher Object
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, (AlgorithmParameterSpec) Registry.getSecureRandom()); // Initialize the cipher
        byte[] ciphertextBytes = cipher.doFinal(messageBytes); // Finally encrypt the message

        /*
        * Decryption Process
        */
        KeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey); // Generate KeySpec from encoded McEliece private key:
        KeyFactory keyFactory1 = KeyFactory.getInstance("McEliece", "FlexiPQC"); // Initialize the McEliece key factory:
        PrivateKey privateKey = keyFactory1.generatePrivate(privateKeySpec); // Decode McEliece private key:
        Cipher cipher2 = Cipher.getInstance("McEliecePKCS"); // Obtain a McEliecePKC Cipher Object
        cipher2.init(Cipher.DECRYPT_MODE, privateKey); // Initialize the cipher

        /*
        * Finally decrypt the message
        */
        byte[] decodMessageBytes = cipher2.doFinal(ciphertextBytes);
        String decodMessage = new String(decodMessageBytes);
    }
}
