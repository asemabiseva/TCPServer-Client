import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class TCPServer {

    private HashMap<String, String> storage = new HashMap<>();
    private TreeSet<String> set_keys = new TreeSet<>();

    public static void main(String[] args) {
        try {
            System.out.println("Waiting for clients...");
            ServerSocket ss = new ServerSocket(1111);
            TCPServer server = new TCPServer();

            while (true) {
                Socket soc = ss.accept();
                System.out.println("Connected to client: " + soc);

                ClientHandler clientHandler = new ClientHandler(soc, server);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized <T> String get(T key) {
        if (!storage.containsKey(key)) {
            return "The key [" + key + "] does not exist in the store";
        } else {
            return "For key: " + key + " value: " + storage.get(key);
        }
    }

    public synchronized <T> String put(T key, T value) {
        if (storage.containsKey(key)) {
            return "The key [" + key + "] already exists in the store";
        } else {
            storage.put((String)key, (String)value);
            set_keys.add((String)key);
            return "Key-Value pair saved successfully. Key - " + key + ", Value - " + value;
        }
    }

    public synchronized <T> String delete(T key) {
        if (!storage.containsKey(key)) {
            return "The key [" + key + "] does not exist in the store";
        } else {
            storage.remove(key);
            return "The key [" + key + "] deleted successfully";
        }
    }

    public synchronized String[] keys() {
        String[] s = new String[storage.size() + 2];
        if (storage.isEmpty()) {
            s[0] = "The storage is empty";
            return s;
        } else {
            s[0] = "All KEYS:";
            int i = 1;
            for (String k : storage.keySet()) {
                s[i] = "Key(" + i + ") - " + k;
                i++;
            }
            return s;
        }
    }

    public synchronized List<String> search(String key)
    {
        List<String> s = new ArrayList<>();
        if (storage.isEmpty()) {
            s.add("The storage is empty");
            return s;
        } 
        else {

            s.add("SEARCH RESULTS:");
            int i=0;
            for (String k : set_keys) {
                if(k.startsWith((String)key))
                {
                    s.add(k);
                    i++;
                }
            }
            if(i==0)
            {
                s.add("Nothing Found");
            }

            return s;
        }
    }

    public synchronized <T> String update(T key, T new_value) {
        if (!storage.containsKey(key)) {
            return "The key [" + key + "] does not exists in the store";
        } else {
            storage.replace((String)key, (String)new_value);
            return "Key-Value pair updated successfully. Key - " + key + ", New Value - " + new_value;
        }
    }

    public synchronized <T> String clear() {
        storage.clear();
        return "The storage is cleaned successfully";
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private TCPServer server;

    public ClientHandler(Socket socket, TCPServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String[] words = (String[]) in.readObject();
            String response = "";

            switch (words[0]) {
                case "GET":
                    response = server.get(words[1]);
                    break;
                case "PUT":
                    response = server.put(words[1], words[2]);
                    break;
                case "KEYS":
                    String[] keys = server.keys();
                    out.writeObject(keys);
                    break;
                case "QUIT":
                    socket.close();
                    break;
                case "DELETE":
                    response = server.delete(words[1]);
                    break;
                case "UPDATE": 
                    response = server.update(words[1], words[2]);
                    break;
                case "CLEAR": 
                    response = server.clear();
                    break;
                case "SEARCH":
                    List<String> ss = server.search(words[1]);
                    out.writeObject(ss);
                    break;
                default:
                    response = "Invalid command format.";
            }

            if (!words[0].equals("KEYS") && !words[0].equals("SEARCH")) {
                out.writeObject(response);
            }

            socket.close();
            System.out.println("Client disconnected: " + socket);
        } catch (Exception e) {
            System.out.println("Exception: [" + e.getMessage() + "]");
        }
    }
}
