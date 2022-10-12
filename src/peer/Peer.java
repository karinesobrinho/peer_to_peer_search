package peer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Peer {
    static ArrayList<Peer> peers;

    long ip;
    long port;
    String file;
    long ip1;
    long port1;
    long ip2;
    long port2;

    Random gerador = new Random();

    public Peer(long ip, long port, String file, long ip1, long port1, long ip2, long port2){
        this.ip = ip;
        this.port = port;
        this.file = file;
        this.ip1 = ip1;
        this.port1 = port1;
        this.ip2 = ip2;
        this.port2 = port2;

        peers = new ArrayList<>();
    }

    public void add(Peer newPeer){
        peers.add(newPeer);

        for(int i = 0; i < peers.size(); i++){
            System.out.println("added ip:" + peers.get(0).ip);
            System.out.println("added port:" + peers.get(0).port);
        }
    }

    public void getArc(String arc){
        int position = gerador.nextInt(peers.size()); //gets a random peer to init search with
        Peer initialPeer = peers.get(position);

        System.out.println("chegamos" + arc);
    }

    public void menu(){

    }

    public static void main(String args[]) throws IOException {
    Scanner entrada = new Scanner(System.in);
    Peer newPeer = null;

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

                 newPeer = new Peer(ip, port, file, ip1, port1, ip2, port2);
                newPeer.add(newPeer);

                break;
            }
            case 2: {
                if(newPeer != null){
                    System.out.println("Digite o arquivo a ser buscado:");
                    String searchArc = entrada.nextLine();
                    newPeer.getArc(searchArc);
                } else System.out.println("Nenhum Peer inicializado até o momento");
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
