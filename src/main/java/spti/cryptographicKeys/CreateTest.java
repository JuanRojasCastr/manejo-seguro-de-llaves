package spti.cryptographicKeys;


import java.io.*;
import java.util.*;

/**
 *	Creates credit cards and puts them in the
 *	database, encrypted.
 */
public class CreateTest {

    private static final String PROPERTIES_FILE = "config.properties";

    public static void main(String[] args) throws Exception {
        args = new String[2];
        args[0] = "35415886";
        args[1] = "null";
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
