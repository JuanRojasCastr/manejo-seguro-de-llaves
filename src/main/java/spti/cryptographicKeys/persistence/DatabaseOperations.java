package spti.cryptographicKeys.persistence;


//import java.util.Base64.Decoder;
//import java.util.Base64.Encoder;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *	Class for performing database operations.
 */
public class DatabaseOperations {

    private final static String CREDIT_CARD_INSERT_SQL =
            "INSERT INTO credit_card (account_id, session_key, cc_number) "+
                    "VALUES (?,?,?)";

    private final static String CREDIT_CARD_SELECT_SQL =
            "SELECT session_key, cc_number FROM credit_card "+
                    "WHERE account_id = ?";

    private final static String CREDIT_CARD_SELECT_IDS_SQL =
            "SELECT account_id FROM credit_card";

    private Connection mConnection;
    private PreparedStatement mInsertCreditCard;
    private PreparedStatement mSelectCreditCard;
    private PreparedStatement mSelectCreditCardAccountIDs;
    private Base64.Encoder mEncoder;
    private Base64.Decoder mDecoder;

    /**
     *	Construct a DatabaseOperations object,
     *	based on the properties passed in which
     *	will include url, username, database, and
     *	JDBC driver name.
     */
    public DatabaseOperations(Properties properties) {
        // Load our connection and initialize objects.
        mEncoder = Base64.getEncoder();
        mDecoder = Base64.getDecoder();
        String driverName = properties.getProperty("DBDriver");
        String url = properties.getProperty("DBUrl");
        String username = properties.getProperty("DBUsername");
        String password = properties.getProperty("DBPassword");
        try {
            // Load the connection
            Class.forName(driverName);
            mConnection = DriverManager.getConnection
                    (url, username, password);
            // Prepare the PreparedStatements.
            mInsertCreditCard = mConnection.prepareStatement
                    (CREDIT_CARD_INSERT_SQL);
            mSelectCreditCard = mConnection.prepareStatement
                    (CREDIT_CARD_SELECT_SQL);
            mSelectCreditCardAccountIDs = mConnection.prepareStatement(
                    CREDIT_CARD_SELECT_IDS_SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *	Store a CreditCardDBO object in the database.
     */
    public void store(CreditCardDBO creditCardDBO)
            throws IOException {
        try {
            System.out.println(mEncoder.encodeToString(creditCardDBO.getEncryptedSessionKey()));
            // Need to synchronize to prevent race conditions.
            synchronized(mConnection) {
                mInsertCreditCard.setLong
                        (1,creditCardDBO.getAccountID());
                mInsertCreditCard.setString
                        (2, mEncoder.encodeToString(creditCardDBO.getEncryptedSessionKey()));
                mInsertCreditCard.setString
                        (3,mEncoder.encodeToString(creditCardDBO.getEncryptedCCNumber()));
                mInsertCreditCard.executeUpdate();
            }
        } catch (SQLException se) {
            se.printStackTrace();
            throw new IOException(se.getMessage());
        }
    }

    /**
     *	Creates a CreditCardDBO object with
     *	data from the database corresponding
     *	to the account id passed in.
     */
    public CreditCardDBO loadCreditCardDBO(long accountID)
            throws IOException {

        CreditCardDBO creditCardDBO = null;
        try {
            // Need to synchronize to prevent race conditions.
            synchronized(mConnection) {
                mSelectCreditCard.setLong(1,accountID);
                ResultSet result = mSelectCreditCard.executeQuery();
                result.next();
                byte[] encryptedSessionKey = mDecoder.decode
                        (result.getString(1));
                byte[] encryptedCCNumber = mDecoder.decode
                        (result.getString(2));
                result.close();
                creditCardDBO = new
                        CreditCardDBO
                        (accountID, encryptedSessionKey, encryptedCCNumber);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            throw new IOException(se.getMessage());
        }
        return creditCardDBO;
    }

    /**
     *	Returns all the account ids in the database.
     *	Useful for displaying all credit cards.
     */
    public long[] getAllCreditCardAccountIDs()
            throws IOException {

        Vector accountIDs = new Vector();
        try {
            synchronized(mConnection) {
                ResultSet result =
                        mSelectCreditCardAccountIDs.executeQuery();
                while (result.next()) {
                    accountIDs.add(new Long(result.getLong(1)));
                }
                result.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
            throw new IOException(se.getMessage());
        }
        // convert the vector to an array.
        long[] accountIDArray = new long[accountIDs.size()];
        for (int i=0; i<accountIDArray.length; i++) {
            Long accountIDLong = (Long)accountIDs.elementAt(i);
            accountIDArray[i] = accountIDLong.longValue();
        }
        return accountIDArray;
    }

}