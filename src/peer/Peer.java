package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Peer {
    public static void main(String args[]) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Digite o IP e a porta deste Peer");
        String[] setUpValues = bufferedReader.readLine().split(" ");
    }
}
