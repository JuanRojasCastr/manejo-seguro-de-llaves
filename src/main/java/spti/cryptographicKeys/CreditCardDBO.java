package spti.cryptographicKeys;

/**
 * Credit Card DataBase Object
 *
 * Read-only credit card object for holding information pulled directly from
 * the database.
 *
 */
public class CreditCardDBO {

    private long mAccountID;
    private byte[] mEncryptedSessionKey;
    private byte[] mEncryptedCCNumber;

    public CreditCardDBO(long accountID, byte[] encryptedSessionKey,
                         byte[] encryptedCCNumber) {
        mAccountID = accountID;
        mEncryptedSessionKey = encryptedSessionKey;
        mEncryptedCCNumber = encryptedCCNumber;
    }

    public long getAccountID() {
        return mAccountID;
    }

    public byte[] getEncryptedSessionKey() {
        return mEncryptedSessionKey;
    }

    public byte[] getEncryptedCCNumber() {
        return mEncryptedCCNumber;
    }
}