package peer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Peer {
    static ArrayList<Peer> peers = new ArrayList<>();

    long ip;
    long port;
    String file;
    long ip1;
    long port1;
    long ip2;
    long port2;

    public Peer(long ip, long port, String file, long ip1, long port1, long ip2, long port2){
        this.ip = ip;
        this.port = port;
        this.file = file;
        this.ip1 = ip1;
        this.port1 = port1;
        this.ip2 = ip2;
        this.port2 = port2;
    }

    public void add(Peer newPeer){
        peers.add(newPeer);
        System.out.println(peers);
    }

    public void menu(){

    }

    public static void main(String args[]) throws IOException {
    Scanner entrada = new Scanner(System.in);

    int opcao = -1;

    while (opcao != 0) {

        System.out.println("Selecione uma das opções abaixo:");
        System.out.println("1-) Criar novo Peer");
        System.out.println("2-) Buscar arquivo");
        System.out.println("0-) Sair do programa");

        opcao = parseInt(entrada.nextLine());

        switch (opcao) {
            case 1: {
                System.out.println("\n");
                System.out.println("Digite o IP:");
                int ip = parseInt(entrada.nextLine());

                System.out.println("Digite a porta:");
                int port = parseInt(entrada.nextLine());

                System.out.println("Digite os arquivos a serem monitorados:");
                String file = entrada.nextLine();

                System.out.println("Digite o IP de um segundo Peer a ser conectado:");
                int ip1 = parseInt(entrada.nextLine());

                System.out.println("Digite a porta de um segundo Peer a ser conectado:");
                int port1 = parseInt(entrada.nextLine());

                System.out.println("Digite o IP de um terceiro Peer a ser conectado:");
                int ip2 = parseInt(entrada.nextLine());

                System.out.println("Digite a porta de um terceiro Peer a ser conectado:");
                int port2 = parseInt(entrada.nextLine());

                Peer newPeer = new Peer(ip, port, file, ip1, port1, ip2, port2);
                newPeer.add(newPeer);

                break;
            }
            case 0: {
                System.out.println("Obrigado.");
                System.exit(opcao);
            }
            default: {
                System.out.println("Opção " + opcao + " inválida.");
                break;
            }
        }


}
    entrada.close();
        }

}
