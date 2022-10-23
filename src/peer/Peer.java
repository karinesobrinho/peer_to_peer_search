package peer;

//import org.json.simple.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
//import peer.Message;
//import com.google.gson.Gson;

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

    Map<String, List<String>> archives;

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


        new Thread(() -> {
            try {
                startPeer(port);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void add(Peer newPeer) {
        peers.add(newPeer);
    }

    public boolean pastArchives(String arc) {
        if (pastSearches.contains(arc)) {
            System.out.println("requisição já processada para " + arc);
            return true;
        }

        pastSearches.add(arc);
        return false;
    }

    public void getArc(String arc) {
        if (pastArchives(arc)) return;

        if (searchCompare(arc)) {
            System.out.println("Tenho o arquivo " + this.ip + this.port);
            return;
        }
        try {
            //Message  myMessage = new Message(arc, ip, port);

            startSender(this.port1, //myMessage.serializedMessage()
                    this.ip +"!"+ this.port +"!"+ arc
                    //serializedMessage(arc, ip, port)
            );
            // startSender(this.port2, arc);
        } catch (Exception e) {
            System.out.println("falha ao chamar sender");
        }
    }

    /*public String serializedMessage(String arc, String ip, int port) {
        JSONObject my_obj = new JSONObject();

        //preenche o objeto com os campos: titulo, ano e genero
        my_obj.put("arc", arc);
        my_obj.put("ip", ip);
        my_obj.put("port", port);

        //serializa para uma string e imprime
        String json_string = my_obj.toString();
        System.out.println("objeto json -> " + json_string);

        return my_obj.toString();
    }*/

    public boolean searchCompare(String searchFor) {
        Map<String, List<String>> map = this.archives;
        System.out.println("\n"+ searchFor+ "\n");
        if (map == null) return false;

        for (Map.Entry<String, List<String>> pair : map.entrySet()) {
            if (searchFor.equals(pair.getKey())) {
                System.out.println("ACHEI " + pair.getKey() + searchFor);
                return true;
            } else {System.out.println(pair.getKey() + searchFor);System.out.println(pair.getKey().compareTo(searchFor));}
            for (int i = 0; i < pair.getValue().size(); i++) {
                if (searchFor.equals(pair.getValue().get(i))) {
                    System.out.println("ACHEI " + pair.getValue().get(i) + searchFor);
                    return true;
                }
            }
        }
        if (map.containsValue(searchFor) || map.containsKey(searchFor)) {
            return true;
        }

        return false;
    }

    public void tempReminder() {
        new Thread(() -> {
            long interval = 30000;  // intervalo de 30 seg.
            Timer timer = new Timer();

            timer.scheduleAtFixedRate(
                    new TimerTask() {
                        public void run() {
                            Map<String, List<String>> newFiles = fileReader();
                            System.out.println("Sou peer " + ip + ":" + port + " com arquivos " + newFiles);
                        }
                    }, interval, interval);
        }).start();
    }

    public Map<String, List<String>> fileReader() {
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

            this.archives = fileList;
            return fileList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startSender(int portToSend, String sentence) throws Exception {
        if (pastArchives(sentence)) return;
        new Thread(() -> {
            try {
                DatagramSocket clientSocket = new DatagramSocket();

                String servidor = "localhost";
                //int porta = 9876;

                InetAddress IPAddress = InetAddress.getByName(servidor);

                byte[] sendData = new byte[1024];
                byte[] receiveData = new byte[1024];

                sendData = (sentence).getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, IPAddress, portToSend);

                System.out
                        .println("Enviando pacote UDP para " + servidor + ":" + portToSend);
                clientSocket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveData,
                        receiveData.length);

                clientSocket.receive(receivePacket);
                System.out.println("Pacote UDP recebido...");

                String modifiedSentence = new String(receivePacket.getData());

        /*String modifiedSentence = "";

        try {
            //
            modifiedSentence = CompletableFuture.supplyAsync(() ->  new String(receivePacket.getData()))
                    .get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException  | TimeoutException e) {
            System.out.println("Time out has occurred");
        }*/

                System.out.println("Texto recebido do servidor:" + modifiedSentence);
                clientSocket.close();
                System.out.println("Socket cliente fechado!");
            } catch (Exception e) {

            }
        }).start();
    }

    public void startPeer(int porta) throws Exception {
        new Thread(() -> {
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

                String[] s = sentence.split("!");

                DatagramPacket sendPacket;

                if (searchCompare(s[2].trim())) {
                    sendData = ("Arquivo encontrado " + this.ip).getBytes();
                    sendPacket = new DatagramPacket(sendData,
                            sendData.length, IPAddress, Integer.parseInt(s[0]));
                } else {
                    try {
                        startSender(port1, sentence);
                        startSender(port2, sentence);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    sendData = ("Arquivo nao encontrado em " + this.ip + ":" + this.port).getBytes();
                    sendPacket = new DatagramPacket(sendData,
                            sendData.length, IPAddress, port);
                }

                System.out.print("Enviando " + sentence + "...");

                try {
                    serverSocket.send(sendPacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("OK\n");

            }
        }).start();
    }

    public static void main(String args[]) throws Exception {
        (new Thread() {
            public void run() {
                Scanner entrada = new Scanner(System.in);
                Peer newPeer = null;

                String opcao = "";

                while (!opcao.equals("QUIT")) {
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
                            //newPeer.searchCompare("peer_to_peer_search");
                            //newPeer.keyContainingValue("peer_to_peer_search");

                            break;
                        }
                        case "SEARCH": {
                            if (newPeer != null) {
                                System.out.println("Digite o arquivo a ser buscado:");
                                String searchArc = entrada.nextLine();
                                newPeer.getArc(searchArc.trim());


                                /*try {
                                    //System.out.println("Digite o IP:porta do peer que deseja enviar msg:");
                                    //String portSender = entrada.nextLine();
                                    startSender(newPeer.port1);
                                    //startSender(newPeer.port2);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }*/
                            } else System.out.println("Nenhum Peer inicializado até o momento");
                            break;
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