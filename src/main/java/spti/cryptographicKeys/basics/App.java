package spti.cryptographicKeys.basics;

import javax.crypto.KeyGenerator;

public class App {


    public static void main( String[] args ) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
        System.out.println( kg );
    }

}