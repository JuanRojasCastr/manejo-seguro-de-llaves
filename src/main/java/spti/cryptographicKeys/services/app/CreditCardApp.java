package spti.cryptographicKeys.services.app;


import spti.cryptographicKeys.entities.CreditCard;
import spti.cryptographicKeys.services.CreditCardFactory;

import java.io.*;
import java.util.*;
//ANTIGUA CLASE CREATE-TEST
/**
 *	Creates credit cards and puts them in the
 *	database, encrypted.
 */
public class CreditCardApp {

    private static final String PROPERTIES_FILE = "config.properties";

    public void ingresarCard(String numeroDocumento, String numbercard) throws Exception{

        long id = Long.parseLong(numeroDocumento);

        // Load the database properties.
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(PROPERTIES_FILE);
        properties.load(fis);
        fis.close();

        // Create the credit card
        CreditCardFactory factory = new CreditCardFactory(properties);
        CreditCard creditCard = factory.createCreditCard(id,numbercard);
    }

    public static void main(String[] args) throws Exception {
        args = new String[2];
        args[0] = "3012651";
        args[1] = "5316917325555196";

        if (args.length != 2) {
            System.out.println("Usage: java CreateTest ID CreditCardNumber");
            //System.exit(1);
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
        CreditCard creditCard = factory.createCreditCard(id,ccNumber);
    }
}
