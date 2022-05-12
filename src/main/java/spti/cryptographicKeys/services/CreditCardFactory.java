package spti.cryptographicKeys.services;
import spti.cryptographicKeys.persistence.CreditCardDBO;
import spti.cryptographicKeys.persistence.DatabaseOperations;
import spti.cryptographicKeys.entities.CreditCard;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.io.*;
import java.util.*;

/**
 *  Creates or retrieves CreditCard objects
 */
public class CreditCardFactory {

    // Key to use to encrypt all new credit cards
    private PublicKey mPublicKey;

    // default key and init vector for CBC mode encryption
    private static final String initVector = "encryptionInitVector";
    private static final String key = "aesEncryptionKey";

    // Handles all database calls
    private DatabaseOperations mDBOperations;

    /**
     *	Creates a new instance of CreditCardFactory
     *	using a Properties object to establish where the public
     *	key is, as well as what the database properties are.
     */
    public CreditCardFactory (Properties properties) throws IOException {
        String certFilename = properties.getProperty("PublicKeyFilename");
        System.out.println(certFilename);
        try {
            // Get the public key
            FileInputStream fis = new FileInputStream(certFilename);
            java.security.cert.CertificateFactory cf =
                    java.security.cert.CertificateFactory.getInstance
                            ("X.509");
            java.security.cert.Certificate cert =
                    cf.generateCertificate(fis);
            fis.close();
            mPublicKey = cert.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        // Create a new DatabaseOperations instance for
        // database calls.
        mDBOperations = new DatabaseOperations(properties);
    }


    /**
     * Create a credit card with AES encryption and using a public key, then stores it in the database
     * @param accountID id of the account
     * @param creditCardNumber number of the credit card
     * @return the credit card created
     * @throws InvalidKeyException
     * @throws IOException
     */
    public CreditCard createCreditCard2(long accountID, String creditCardNumber) throws InvalidKeyException, IOException {
        CreditCardDBO creditCardDBO = null;
        byte[] encryptedSessionKey, encryptedCCNumber;
        encryptedSessionKey = null;
        encryptedCCNumber = null;
        try {
            // Create a AES key and encrypt the credit card number.
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom sec = new SecureRandom(key.getBytes());
            keygen.init(128, sec);
            Key key = keygen.generateKey();
            SecretKeySpec sessionKey = new SecretKeySpec(key.getEncoded(), "AES");

            Cipher symmetricCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            symmetricCipher.init(Cipher.ENCRYPT_MODE, sessionKey, iv);
            encryptedCCNumber = symmetricCipher.doFinal(creditCardNumber.getBytes("UTF8"));

            // Use the public key to encrypt the session key.
            Cipher asymmetricCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            asymmetricCipher.init(Cipher.ENCRYPT_MODE, mPublicKey);
            encryptedSessionKey = asymmetricCipher.doFinal(sessionKey.getEncoded());

            // Need to catch a large number of possible exceptions:
        } catch (NoSuchAlgorithmException nsae) {
            // We're in trouble. Missing RSA or Blowfish.
            nsae.printStackTrace();
            throw new RuntimeException("Missing Crypto algorithm");
        } catch (NoSuchPaddingException nspe) {
            // again, we're in trouble. Missing padding.
            nspe.printStackTrace();
            throw new RuntimeException("Missing Crypto algorithm");
        } catch (BadPaddingException bpe) {
            // Probably a bad key.
            bpe.printStackTrace();
            throw new InvalidKeyException("Missing Crypto algorithm");
        } catch (IllegalBlockSizeException ibse) {
            // Probably a bad key.
            ibse.printStackTrace();
            throw new InvalidKeyException("Could not encrypt");
        } catch (InvalidAlgorithmParameterException iape) {
            iape.printStackTrace();
        }

        // Create a database object with the encrypted info.
        creditCardDBO = new CreditCardDBO(accountID, encryptedSessionKey, encryptedCCNumber);

        // Store the encrypted credit card in the database
        mDBOperations.store(creditCardDBO);
        CreditCard creditCard = new CreditCard(accountID, creditCardNumber);
        return creditCard;
    }


    /**
     *	Create a credit card from an account id, a credit
     *	card number, and a public key.
     *
     *	Automatically encrypts the card and stores it in
     *	the database.
     */
    public CreditCard createCreditCard
    (long accountID, String creditCardNumber)
            throws InvalidKeyException, IOException {

        CreditCardDBO creditCardDBO = null;
        byte[] encryptedSessionKey, encryptedCCNumber;

        try {
            // Create a blowfish key and encrypt the credit card number.
            KeyGenerator kg = KeyGenerator.getInstance
                    ("Blowfish");
            kg.init(128);
            Key sessionKey = kg.generateKey();

            Cipher symmetricCipher = Cipher.getInstance
                    ("Blowfish/ECB/PKCS5Padding");
            symmetricCipher.init(Cipher.ENCRYPT_MODE, sessionKey);
            encryptedCCNumber = symmetricCipher.doFinal
                    (creditCardNumber.getBytes("UTF8"));

            // Use the public key to encrypt the session key.
            Cipher asymmetricCipher = Cipher.getInstance
                    ("RSA/ECB/PKCS1Padding");
            asymmetricCipher.init(Cipher.ENCRYPT_MODE, mPublicKey);
            encryptedSessionKey = asymmetricCipher.doFinal
                    (sessionKey.getEncoded());

            // Need to catch a large number of possible exceptions:
        } catch (NoSuchAlgorithmException nsae) {
            // We're in trouble. Missing RSA or Blowfish.
            nsae.printStackTrace();
            throw new RuntimeException("Missing Crypto algorithm");
        } catch (NoSuchPaddingException nspe) {
            // again, we're in trouble. Missing padding.
            nspe.printStackTrace();
            throw new RuntimeException("Missing Crypto algorithm");
        } catch (BadPaddingException bpe) {
            // Probably a bad key.
            bpe.printStackTrace();
            throw new InvalidKeyException("Missing Crypto algorithm");
        } catch (IllegalBlockSizeException ibse) {
            // Probably a bad key.
            ibse.printStackTrace();
            throw new InvalidKeyException("Could not encrypt");
        }

        // Create a database object with the encrypted info.
        creditCardDBO = new CreditCardDBO(accountID, encryptedSessionKey, encryptedCCNumber);

        // Store the encrypted credit card in the database
        mDBOperations.store(creditCardDBO);
        CreditCard creditCard = new CreditCard(accountID, creditCardNumber);
        return creditCard;
    }

    /**
     *	Given an account id and a private key,
     *	load a credit card from the database, decrypt it,
     *	and deliver it as a CreditCard object.
     *
     *	Requires the private key.
     */
    public CreditCard findCreditCard
    (long accountID, PrivateKey privateKey)
            throws InvalidKeyException, IOException{

        String creditCardNumber = null;

        // Load the encrypted credit card info.
        CreditCardDBO creditCardDBO =
                mDBOperations.loadCreditCardDBO(accountID);
        try {
            // Decrypt the encrypted session key.
            Cipher asymmetricCipher = Cipher.getInstance
                    ("RSA/ECB/PKCS1Padding");
            asymmetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] sessionKeyBytes = asymmetricCipher.doFinal
                    (creditCardDBO.getEncryptedSessionKey());

            // Decrypt the credit card number with the session key.
            SecretKey symmetricKey = new SecretKeySpec
                    (sessionKeyBytes, "Blowfish");
            Cipher symmetricCipher = Cipher.getInstance
                    ("Blowfish/ECB/PKCS5Padding");
            symmetricCipher.init(Cipher.DECRYPT_MODE, symmetricKey);
            byte[] ccNumberBytes = symmetricCipher.doFinal
                    (creditCardDBO.getEncryptedCCNumber());

            creditCardNumber = new String(ccNumberBytes, "UTF8");

            // Need to catch a large number of possible exceptions:
        } catch (NoSuchAlgorithmException nsae) {
            // Missing an algorithm.
            nsae.printStackTrace();
            throw new RuntimeException("Missing crypto algorithm");
        } catch (NoSuchPaddingException nspe) {
            // again, we're in trouble. Missing padding.
            nspe.printStackTrace();
            throw new RuntimeException("Missing Crypto algorithm");
        } catch (BadPaddingException bpe) {
            // This means the data is probably bad.
            bpe.printStackTrace();
            throw new InvalidKeyException("Could not decrypt");
        } catch (IllegalBlockSizeException ibse) {
            // Probably a bad key.
            ibse.printStackTrace();
            throw new InvalidKeyException("Could not decrypt");
        }

        CreditCard creditCard = new CreditCard
                (accountID, creditCardNumber);
        return creditCard;
    }

    /**
     *	Finds all credit cards and returns them as an Iterator.
     */
    public Iterator findAllCreditCards(PrivateKey privateKey)
            throws InvalidKeyException, IOException {

        long[] accountIDs = mDBOperations.getAllCreditCardAccountIDs();
        Vector creditCards = new Vector();
        for (int i=0; i<accountIDs.length; i++) {
            creditCards.add(findCreditCard(accountIDs[i], privateKey));
        }
        return creditCards.iterator();
    }
}
