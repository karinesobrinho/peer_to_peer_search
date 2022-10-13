package peer;
import java.net.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Peer {
    static ArrayList<Peer> peers;

    String ip;
    int port;
    String file;
    String ip1;
    int port1;
    String ip2;
    int port2;

    Random gerador = new Random();

    public Peer(String address, String file, String address1, String address2) throws Exception {
        String[] ipPort = address.split(":");
        this.ip = ipPort[0];
        this.port = Integer.parseInt(ipPort[1]);

        this.file = file;

        String[] ipPort1 = address1.split(":");
        this.ip1 = ipPort1[0];
        this.port1 = Integer.parseInt(ipPort1[1]);

        String[] ipPort2 = address1.split(":");
        this.ip2 = ipPort2[0];
        this.port2 = Integer.parseInt(ipPort2[1]);

        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName(ip);

        //DatagramPacket datagramPacket = new DatagramPacket();

        peers = new ArrayList<>();
        tempReminder();
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

        System.out.println("chegamos " + arc);
    }

    public void menu(){

    }

    public void tempReminder(){
        long interval = 30000;  // intervalo de 30 seg.

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println(
                        "Sou peer " + ip + ":"  + port + " com arquivos " + file);
            }
        },interval, interval);
    }

    public static void main(String args[]) throws Exception {
    Scanner entrada = new Scanner(System.in);
    Peer newPeer = null;

    String opcao = "";

    while (!opcao.equals("LEAVE")) {
        System.out.println("Selecione uma das opções abaixo:");
        System.out.println("INICIALIZA");
        System.out.println("SEARCH");
        System.out.println("LEAVE");

        opcao = entrada.nextLine();

        switch (opcao) {
            case "INICIALIZA": {
                System.out.println("Digite o IP:porta");
                String address = entrada.nextLine();

                System.out.println("Digite os arquivos a serem monitorados:");
                String file = entrada.nextLine();

                System.out.println("Digite o IP:porta de um segundo Peer a ser conectado:");
                String address1 = entrada.nextLine();

                System.out.println("Digite o IP:porta de um terceiro Peer a ser conectado:");
                String address2 = entrada.nextLine();

                newPeer = new Peer(address, file, address1, address2);
                newPeer.add(newPeer);

                break;
            }
            case "SEARCH": {
                if(newPeer != null){
                    System.out.println("Digite o arquivo a ser buscado:");
                    String searchArc = entrada.nextLine();
                    newPeer.getArc(searchArc);
                } else System.out.println("Nenhum Peer inicializado até o momento");
                break;
            }
            case "LEAVE": {
                System.out.println("Obrigado.");
                System.exit(0);
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
