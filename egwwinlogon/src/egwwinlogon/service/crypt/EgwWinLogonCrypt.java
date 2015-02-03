/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service.crypt;

import de.flexiprovider.api.Registry;
import de.flexiprovider.core.FlexiCoreProvider;
import de.flexiprovider.ec.FlexiECProvider;
import de.flexiprovider.nf.FlexiNFProvider;
import de.flexiprovider.pqc.FlexiPQCProvider;
import egwwinlogon.service.EgwWinLogon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 * EgwWinLogonCrypt
 * @author Stefan Werfling
 */
public class EgwWinLogonCrypt {
    
    protected String _privateKeyFile = "private.key";
    protected String _publicKeyFile = "public.key";
    
    protected PublicKey _publicKey = null;
    protected PrivateKey _privateKey = null;
    
    public EgwWinLogonCrypt() {
        if( Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null ) {
			Security.addProvider(new BouncyCastleProvider());
        }
        
        Security.addProvider(new FlexiCoreProvider());
        Security.addProvider(new FlexiPQCProvider());
        Security.addProvider(new FlexiECProvider());
        Security.addProvider(new FlexiNFProvider());
        
        File prfile = new File(this._privateKeyFile);
        File pufile = new File(this._publicKeyFile);
        
        if( !prfile.exists() ) {
            this.generateKeyPair(prfile, pufile);
        }
        else {
            // load
            this._publicKey = this._loadPublicKey(pufile);
            this._privateKey = this._loadPrivateKey(prfile, "test");
        }
        
        System.out.println("Erstellt");
        System.out.println(this.encrypt("Erstellt"));
    }
    
    public Boolean generateKeyPair(File prfile, File pufile) {
        KeyPair kp = null;
        
        try {
            KeyPairGenerator mcElieceKeyPairGenerator = KeyPairGenerator.getInstance("McEliece", "FlexiPQC");
            mcElieceKeyPairGenerator.initialize(1024, new SecureRandom());
            kp = mcElieceKeyPairGenerator.generateKeyPair();

            final byte[] publicKeyData = kp.getPublic().getEncoded(); 
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyData);
            KeyFactory mcElieceKeyFactory = KeyFactory.getInstance("McEliece", "FlexiPQC");
            this._publicKey = mcElieceKeyFactory.generatePublic(publicKeySpec);

            final byte[] privateKeyData = kp.getPrivate().getEncoded();
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyData);
            this._privateKey = mcElieceKeyFactory.generatePrivate(privateKeySpec);
            
            if( prfile.exists() ) {
                prfile.delete();
            }
            
            if( pufile.exists() ) {
                pufile.delete();
            }
            
            // save
            this._savePrivateKey(new FileOutputStream(prfile), this._privateKey, "test");
            this._savePublicKey(new FileOutputStream(pufile), this._publicKey);
        }
        catch( Exception ex ) {
            System.out.println(ex);
            return false;
        }
        
        return true;
    }
    
    protected void _savePrivateKey(OutputStream os, PrivateKey pk, String password) {
		
        JceOpenSSLPKCS8EncryptorBuilder builder =
            new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_3DES);
        
        builder.setIterationCount(10000);
        builder.setPasssword(password.toCharArray());
        
        try {
            OutputEncryptor outputEncryptor = builder.build(); 

            PKCS8Generator pkcs8Generator =
                new JcaPKCS8Generator(pk, outputEncryptor);
            
            PemWriter writer = new PemWriter(new PrintWriter(os));
            writer.writeObject(pkcs8Generator);
            writer.close();
        }
        catch( Exception ex) {
            System.out.println(ex);
        }
	}
    
    protected PrivateKey _loadPrivateKey(File privatekey, String password) {
        try {
            FileInputStream fis = new FileInputStream(privatekey);
            PemReader reader = new PemReader(new InputStreamReader(fis));
            
            EncryptedPrivateKeyInfo ePKInfo = new EncryptedPrivateKeyInfo(
                reader.readPemObject().getContent());
            
            Cipher cipher = Cipher.getInstance(ePKInfo.getAlgName());
            
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            
            SecretKeyFactory skFac = SecretKeyFactory.getInstance(ePKInfo.getAlgName());
            
            Key pbeKey = skFac.generateSecret(pbeKeySpec);
            
            AlgorithmParameters algParams = ePKInfo.getAlgParameters();
            
            cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
            
            KeySpec pkcs8KeySpec = ePKInfo.getKeySpec(cipher);
            
            KeyFactory keyFac = KeyFactory.getInstance("McEliece");
            
            return keyFac.generatePrivate(pkcs8KeySpec);
        }
        catch( Exception ex ) {
            System.out.println(ex);
        }
        
        return null;
    }
    
    protected void _savePublicKey(OutputStream os, PublicKey pk) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				pk.getEncoded());
            
            os.write(x509EncodedKeySpec.getEncoded());
            os.close();
        }
        catch( Exception ex) {
            System.out.println(ex);
        }
    }
    
    protected PublicKey _loadPublicKey(File publickey) {
        try {
            FileInputStream fis = new FileInputStream(publickey);
            byte[] encodedPublicKey = new byte[(int) publickey.length()];
            fis.read(encodedPublicKey);
            fis.close();
            
            KeyFactory keyFactory = KeyFactory.getInstance("McEliece");
            
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            
            return keyFactory.generatePublic(publicKeySpec);
        }
        catch( Exception ex ) {
            System.out.println(ex);
        }
        
        return null;
    }
    
    public String encrypt(String blank) {
        try {
            // Obtain a McEliecePKC Cipher Object
            Cipher cipher = Cipher.getInstance("McEliecePKCS");

            // Initialize the cipher
            cipher.init(Cipher.ENCRYPT_MODE, this._publicKey, new SecureRandom());
            
            byte[] cBytes = cipher.doFinal(blank.getBytes());
            
            return new String(cBytes);
        }
        catch( Exception ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
