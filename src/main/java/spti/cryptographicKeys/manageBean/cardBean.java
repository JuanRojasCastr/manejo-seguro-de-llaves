package spti.cryptographicKeys.manageBean;

import lombok.Getter;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class cardBean {
    @Getter @Setter public String username;
    @Getter @Setter public String numbercard;
    @Getter @Setter public String expiration;
    @Getter @Setter public String cvv;
    @Getter @Setter public String type;
    @Getter @Setter public String numeroDocumento;


    public cardBean(){}


}
