import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.util.*;

public class TCPClient {
    public static void main(String[] args) {
        System.out.println("Client started");
        while (true) {
            try  {

            	BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            	System.out.println("\nPlease Input Command in either of the following forms:\nGET <key> \nPUT <key> <val> \nDELETE <key> \nSEARCH <key> \nUPDATE <key> <new val> \nKEYS \nCLEAR \nQUIT \nEnter Command:");
                String str = userInput.readLine();
                String[] words = inputHandling(str);

                Socket soc = new Socket("localhost", 1111);
                soc.setSoTimeout(1000);
                ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(soc.getInputStream());

                
                

                out.writeObject(words);
                out.flush();

                if (words[0].equals("KEYS")) {
                    String[] keys = (String[]) in.readObject();
                    for (String key : keys) {
                        if (key != null) System.out.println(key);
                    } 
                }
                else if(words[0].equals("SEARCH"))
                {
                	List<String> s = (List<String>)in.readObject();
                	for(String o : s)
                	{
                		System.out.println(o);
                	}

                }
                else {
                    String response = (String) in.readObject();
                    System.out.println(response);
                }
            } catch (IOException e ) {
                System.out.println("Exception: [Unknown IO Error. Command Not Successful]");
            } catch (WrongFormatException  | LongKeyException |  ClassNotFoundException  e) {
                System.out.println("Exception: [" + e.getMessage() + "]");
            }
            
        }
    }

    public static String[] inputHandling(String s) throws WrongFormatException, LongKeyException {
        String[] words = s.split("\\s+");
        switch (words[0]) {
            case "GET":
                if (words.length != 2) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "PUT":
                if (words.length != 3) {
                    throw new WrongFormatException("Invalid command format.");
                }
                if (words[1].length() > 10) {
                    throw new LongKeyException("Key length should not exceed 10 characters.");
                }
                break;
            case "KEYS":
                if (words.length != 1) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "QUIT":
                if (words.length != 1) {
                    throw new WrongFormatException("Invalid command format.");
                } else {
                    System.exit(0);
                }
                break;
            case "DELETE":
                if (words.length != 2) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "UPDATE": 
            	if (words.length != 3) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "CLEAR": 
            	if (words.length != 1) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "SEARCH": 
            	if (words.length != 2) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            default:
                throw new WrongFormatException("Invalid command format.");
        }
        return words;
    }
}

class WrongFormatException extends Exception {
    public WrongFormatException(String message) {
        super(message);
    }
}

class LongKeyException extends Exception {
    public LongKeyException(String message) {
        super(message);
    }
}
