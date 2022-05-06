package spti.cryptographicKeys.basicInfoCrypo;

import javax.crypto.KeyGenerator;

public class bireftest {


    public static void main( String[] args ) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
        System.out.println( kg );
    }

}