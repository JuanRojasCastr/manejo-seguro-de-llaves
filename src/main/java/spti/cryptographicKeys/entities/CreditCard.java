package spti.cryptographicKeys.entities;
/**
 *  Read-only Credit Card object.
 */
public class CreditCard {

    private long mAccountID;
    private String mCreditCardNumber;

    /**
     *  Constructor is protected, as CreditCards should
     *  only be created from the CreditCardFactory.
     */
    public CreditCard(long accountID, String creditCardNumber) {
        mAccountID = accountID;
        mCreditCardNumber = creditCardNumber;
    }

    public long getAccountID() {
        return mAccountID;
    }

    public String getCreditCardNumber() {
        return mCreditCardNumber;
    }
}