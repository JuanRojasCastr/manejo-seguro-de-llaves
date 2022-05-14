package spti.cryptographicKeys.services.app;

import spti.cryptographicKeys.entities.CreditCard;
import spti.cryptographicKeys.services.CreditCardFactory;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.*;

//ANTIGUA CLASE CREATE-TEST
/**
 * Creates credit cards and puts them in the
 * database, encrypted.
 */
public class CreditCardApp {
    // Properties file for the database and public key information
    private static final String PROPERTIES_FILE = "config.properties";

    // Keystore that holds the private key
    private static final String KEYSTORE = "tarjetas.ks";

    // Password for the keystore
    private static final char[] PASSWORD = { 'd', 'c', 'c', 'i', 'a', 'd', 'c', 'c', 'i', 'a' };

    public void ingresarCard(String numeroDocumento, String numbercard) throws Exception {

        long id = Long.parseLong(numeroDocumento);

        // Load the database properties.
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(PROPERTIES_FILE);
        properties.load(fis);
        fis.close();

        // Create the credit card
        CreditCardFactory factory = new CreditCardFactory(properties);
        CreditCard creditCard = factory.createCreditCard(id, numbercard);
    }

    public List<CreditCard> getAllCards() throws Exception {

        List<CreditCard> cardList = new ArrayList<>();

        // Load the keystore to retrieve the private key.
        String ksType = KeyStore.getDefaultType();
        KeyStore ks = KeyStore.getInstance(ksType);
        FileInputStream fis = new FileInputStream(KEYSTORE);
        ks.load(fis, PASSWORD);
        fis.close();
        PrivateKey privateKey = (PrivateKey) ks.getKey("mykey", PASSWORD);

        // Load the database properties file.
        Properties properties = new Properties();
        fis = new FileInputStream(PROPERTIES_FILE);
        properties.load(fis);
        fis.close();

        // Create a credit card factory with the given properties.
        CreditCardFactory factory = new CreditCardFactory(properties);

        // Get all the credit cards.
        Iterator iterator = factory.findAllCreditCards(privateKey);

        // Display all credit cards.
        while (iterator.hasNext()) {
            CreditCard creditCard = (CreditCard) iterator.next();
            cardList.add(creditCard);
        }

        return cardList;
    }

    public static void main(String[] args) throws Exception {

        // Load the keystore to retrieve the private key.
        String ksType = KeyStore.getDefaultType();
        KeyStore ks = KeyStore.getInstance(ksType);
        FileInputStream fis = new FileInputStream(KEYSTORE);
        ks.load(fis, PASSWORD);
        fis.close();
        PrivateKey privateKey = (PrivateKey) ks.getKey("mykey", PASSWORD);

        // Load the database properties file.
        Properties properties = new Properties();
        fis = new FileInputStream(PROPERTIES_FILE);
        properties.load(fis);
        fis.close();

        // Create a credit card factory with the given properties.
        CreditCardFactory factory = new CreditCardFactory(properties);

        // Get all the credit cards.
        Iterator iterator = factory.findAllCreditCards(privateKey);

        // Display all credit cards.
        while (iterator.hasNext()) {
            CreditCard creditCard = (CreditCard) iterator.next();
            System.out.println("\nAccount ID: " + creditCard.getAccountID());
            System.out.println("CC Number: " + creditCard.getCreditCardNumber());
        }
    }

    public static void main2(String[] args) throws Exception {
        args = new String[2];
        args[0] = "3012651";
        args[1] = "5316917325555196";

        if (args.length != 2) {
            System.out.println("Usage: java CreateTest ID CreditCardNumber");
            // System.exit(1);
        }

        long id = Long.parseLong(args[0]);
        String ccNumber = args[1];

        // Load the database properties.
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(PROPERTIES_FILE);
        properties.load(fis);
        fis.close();

        // Create the credit card
        CreditCardFactory factory = new CreditCardFactory(properties);
        CreditCard creditCard = factory.createCreditCard(id, ccNumber);
    }
}
