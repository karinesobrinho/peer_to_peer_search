package peer;

import org.json.simple.JSONObject;
import com.google.gson.Gson;

public class Message {
    String arc;
    String ip;
    int port;

    public Message(String arc, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.arc = arc;


    }

    public String serializedMessage() {
        JSONObject my_obj = new JSONObject();

        //preenche o objeto com os campos: titulo, ano e genero
        my_obj.put("arc", arc);
        my_obj.put("ip", ip);
        my_obj.put("port", port);

        //serializa para uma string e imprime
        String json_string = my_obj.toString();
        System.out.println("objeto original -> " + json_string);
        System.out.println();

        return my_obj.toString();
    }
}
