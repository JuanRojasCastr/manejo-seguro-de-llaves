package spti.cryptographicKeys.manageBean;

import lombok.Getter;
import lombok.Setter;
import spti.cryptographicKeys.entities.CreditCard;
import spti.cryptographicKeys.services.app.CreditCardApp;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class cardBean {
    @Getter
    @Setter
    public String username;
    @Getter
    @Setter
    public String numbercard;
    @Getter
    @Setter
    public String expiration;
    @Getter
    @Setter
    public String cvv;
    @Getter
    @Setter
    public String type;
    @Getter
    @Setter
    public String numeroDocumento;
    public CreditCardApp creditCardApp = new CreditCardApp();

    public List<CreditCard> cardList = new ArrayList<>();

    public cardBean() {
        cardList.add(new CreditCard(1, "0000 7262", "01/27"));
        cardList.add(new CreditCard(1, "0000 0712", "02/23"));
    }

    public Boolean ingresarCard() throws Exception {
        // creditCardApp.ingresarCard(numeroDocumento, numbercard);
        return false;
    }

    public List<CreditCard> getCardList() {
        return cardList;
    }

    public String getSubstring(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }

}
