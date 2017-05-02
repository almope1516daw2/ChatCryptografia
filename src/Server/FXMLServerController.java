package Server;
import static org.apache.commons.codec.binary.Hex.*;
import static org.apache.commons.io.FileUtils.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FXMLServerController implements Initializable {

    @FXML
    public TextArea taLog;
    @FXML
    public Button btnStart;
    @FXML
    public Button btnStop;
    @FXML
    public Button btnUsers;
    @FXML
    public Button btnClear;

    ArrayList clientOutputStreams;
    ArrayList<String> users;
    public  String StringSecretKey;
    public class ClientHandler implements Runnable {

        BufferedReader reader;
        Socket sock;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user) {
            client = user;
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                System.out.println("Unexpected error... ");
            }

        }

        @Override
        public void run() {
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat", key = "Key";
            String[] data;

            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message + "\n");
                    data = message.split(":");

                    for (String token : data) {
                        System.out.println(token + "\n");
                    }

                    if (data[2].equals(connect)) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        tellEveryone((StringSecretKey + ":" + data[0] + ":" + key));
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone((data[0] + ":has disconnected." + ":" + chat));
                        userRemove(data[0]);
                    } else if (data[2].equals(chat)) {
                        
                        tellEveryone(message);
                        
                    } else {
                        System.out.println("No Conditions were met. \n");
                    }
                }
            } catch (Exception ex) {
                System.out.println("Lost a connection. \n");
                ex.printStackTrace();
                clientOutputStreams.remove(client);
            }
        }
    }

    public class ServerStart implements Runnable {

        @Override
        public void run() {
            clientOutputStreams = new ArrayList();
            users = new ArrayList();

            try {
                ServerSocket serverSock = new ServerSocket(2222);

                while (true) {
                    Socket clientSock = serverSock.accept();
                    PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                    clientOutputStreams.add(writer);

                    Thread listener = new Thread(new ClientHandler(clientSock, writer));
                    listener.start();
                    System.out.println("Got a connection. \n");
                }
            } catch (Exception ex) {
                System.out.println("Error making a connection. \n");
            }
        }
    }

    public void userAdd(String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        
        if(users.size() <= 2){
            users.add(name);
            String[] tempList = new String[(users.size())];
            users.toArray(tempList);

            for (String token : tempList) {
                message = (token + add);
                tellEveryone(message);
            }
            tellEveryone(done);
        } else {
        tellEveryone("Cannot connect user");
        }
    }

    public void userRemove(String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        users.remove(name);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                taLog.setText(taLog.getText() + message + "\n");
                writer.flush();
                System.out.println("SERVER: " + message + "\n");
                //taLog.setCaretPosition(taLog.getDocument().getLength());

            } catch (Exception ex) {
                taLog.setText(taLog.getText() + "Error telling everyone. \n");
                System.out.println("Error telling everyone. \n");
            }
        }
    }

    public FXMLServerController() {
    }

    @FXML
    private void Start(ActionEvent event) throws NoSuchAlgorithmException, IOException {
        btnStop.setDisable(false);
        btnUsers.setDisable(false);
        btnStart.setDisable(true);
        Thread starter = new Thread(new ServerStart());
        starter.start();

        SecretKey secretKey = generateKey();
        StringSecretKey = saveKey(secretKey);
        
        taLog.setText(taLog.getText() + "Server started...\n");
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // 128 default; 192 and 256 also possible
        return keyGenerator.generateKey();
    }

    public static String saveKey(SecretKey key) throws IOException
    {
        char[] hex = encodeHex(key.getEncoded());
        return String.valueOf(hex);
    }


    @FXML
    private void Stop(ActionEvent event) {
        btnStop.setDisable(true);
        btnUsers.setDisable(true);
        btnStart.setDisable(false);
        tellEveryone("Server:is stopping and all users will be disconnected.\n:Chat");
        System.out.println("HOLA SEÑOR");
        try {
            Thread.sleep(2000);                 //5000 milliseconds is five second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        //tellEveryone("Server:is stopping and all users will be disconnected.\n:Chat");
        taLog.setText(taLog.getText() + "Server stopping... \n");

        taLog.setText(taLog.getText() + "");
    }

    @FXML
    private void Users(ActionEvent event) {
        taLog.setText(taLog.getText() + "\n Online users : \n");
        for (String current_user : users) {
            System.out.println(current_user);
            taLog.setText(taLog.getText() + current_user);
            taLog.setText(taLog.getText() + "\n");
        }

    }

    @FXML
    private void Clear(ActionEvent event) {
        taLog.setText("");
    }

    public void initialize(URL url, ResourceBundle rb) {
        btnStop.setDisable(true);
        btnUsers.setDisable(true);
    }
}