package peer;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Peer {
    static ArrayList<Peer> peers;

    static ArrayList<String> pastSearches;

    String ip;
    int port;
    String file;
    String ip1;
    int port1;
    String ip2;
    int port2;

    Random gerador = new Random();

    public Peer(String address, String file, String address1, String address2) {
        String[] ipPort = address.split(":");
        this.ip = ipPort[0];
        this.port = Integer.parseInt(ipPort[1]);

        this.file = file;

        String[] ipPort1 = address1.split(":");
        this.ip1 = ipPort1[0];
        this.port1 = Integer.parseInt(ipPort1[1]);

        String[] ipPort2 = address2.split(":");
        this.ip2 = ipPort2[0];
        this.port2 = Integer.parseInt(ipPort2[1]);

        tempReminder();
        peers = new ArrayList<>();
        pastSearches = new ArrayList<>();

        try {
            startPeer(port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(Peer newPeer){
        peers.add(newPeer);

        for(int i = 0; i < peers.size(); i++){
            System.out.println("added ip:" + peers.get(0).ip);
            System.out.println("added port:" + peers.get(0).port);
        }
    }

    public void getArc(String arc){
        for(int i = 0; i < peers.size(); i++){
            if(peers.get(i).file.equals(arc)){
                System.out.println("requisição já processada para "+ arc);
                return;
            }
        }

        pastSearches.add(arc);

        int position = gerador.nextInt(peers.size()); //gets a random peer to init search with
        Peer initialPeer = peers.get(position);

        System.out.println("chegamos " + arc);
    }

    public void tempReminder(){
        new Thread(() -> {
        long interval = 30000;  // intervalo de 30 seg.
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(
                new TimerTask() {
                public void run () {
                    Map<String, List<String>> newFiles =  fileReader();
                    System.out.println("Sou peer " + ip + ":" + port + " com arquivos " + newFiles);
                }
            },interval,interval);
        }).start();
    }

    public Map<String, List<String>> fileReader(){
        try {
            Path projectPath = Paths.get(this.file);
            Set<Path> directoriesToList = Files.list(projectPath).map(Path::getFileName).collect(Collectors.toSet());
            Map<String, List<String>> fileList = Files.walk(projectPath).filter(p -> {
                        try {
                            return directoriesToList.contains(p.getParent().getFileName());
                        } catch (Exception e) {
                            return false;
                        }
                    }).collect(Collectors.groupingBy(p -> p.getParent().getFileName().toString()))
                    .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, file -> file.getValue().stream().map(Path::getFileName).map(Path::toString).collect(Collectors.toList())));

            return fileList;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startSender(int port1) throws Exception {

        BufferedReader inFromUser = new BufferedReader(
                new InputStreamReader(System.in));

        DatagramSocket clientSocket = new DatagramSocket();

        String servidor = "localhost";
        //int porta = 9876;

        InetAddress IPAddress = InetAddress.getByName(servidor);

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        System.out.println("Digite o texto a ser enviado ao servidor: ");
        String sentence = inFromUser.readLine();
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, IPAddress, port1);

        System.out
                .println("Enviando pacote UDP para " + servidor + ":" + port1);
        clientSocket.send(sendPacket);

        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);

        clientSocket.receive(receivePacket);
        System.out.println("Pacote UDP recebido...");

        String modifiedSentence = new String(receivePacket.getData());

        System.out.println("Texto recebido do servidor:" + modifiedSentence);
        clientSocket.close();
        System.out.println("Socket cliente fechado!");
    }


    public static void startPeer(int porta) throws Exception {

        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(porta);

        } catch (SocketException e) {
            System.out.println("erro");
            throw new RuntimeException(e);
        }

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);
            System.out.println("Esperando por datagrama UDP na porta " + porta);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.print("Datagrama UDP [" + "nun" + "] recebido...");

            String sentence = new String(receivePacket.getData());
            System.out.println(sentence);

            InetAddress IPAddress = receivePacket.getAddress();

            int port = receivePacket.getPort();

            String capitalizedSentence = sentence.toUpperCase();

            sendData = capitalizedSentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, IPAddress, port);

            System.out.print("Enviando " + capitalizedSentence + "...");

            try {
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("OK\n");

        }
    }

    public static void main(String args[]) throws Exception {
        (new Thread() {
            public void run() {
                Scanner entrada = new Scanner(System.in);
                Peer newPeer = null;

                String opcao = "";

                while (!opcao.equals("LEAVE")) {
                    System.out.println("Selecione uma das opções abaixo:");
                    System.out.println("INICIALIZA");
                    System.out.println("SEARCH");

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
                /*if(newPeer != null){
                    System.out.println("Digite o arquivo a ser buscado:");
                    String searchArc = entrada.nextLine();
                    newPeer.getArc(searchArc);
                } else System.out.println("Nenhum Peer inicializado até o momento");*/

                            try {
                                System.out.println("Digite o IP:porta do peer que deseja enviar msg:");
                                String portSender = entrada.nextLine();
                                startSender(Integer.parseInt(portSender));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
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
        }).start();
    }
}
