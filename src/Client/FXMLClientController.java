package Client;

import Server.FXMLServerController;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.misc.BASE64Encoder;

public class FXMLClientController implements Initializable {

    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfMsg;
    @FXML
    private TextArea taLog;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Button btnSend;

    String username, address,psw;
    ArrayList<String> users = new ArrayList();
    int port;
    Boolean isConnected = false;

    String serverPubKey;
    
    Socket sock;
    BufferedReader reader;
    PrintWriter writer;
    KeyPair parellaClaus;
    PublicKey pubKey;
    PrivateKey privKey;
    //Crea un thread que maneja los eventos que envia servidor
    public void ListenThread() {
        Thread IncomingReader = new Thread(new IncomingReader());
        IncomingReader.start();
    }

    //--------------------------//
    public void userAdd(String data) {
        users.add(data);
    }

    public void userRemove(String data) {
        taLog.setText(taLog.getText() + data + " is now offline.\n");
        //System.out.println(data + " is now offline.\n");
    }

    //--------------------------//
    public void sendDisconnect() {
        String bye = (username + ": :Disconnect");
        try {
            writer.println(bye);
            writer.flush();
        } catch (Exception e) {
            taLog.setText(taLog.getText() + "Could not send Disconnect message.\n");
        }
    }

    public void DisconnectUser() {
        try {
            taLog.setText(taLog.getText() + "Disconnected.\n");
            sock.close();
        } catch (Exception ex) {
            taLog.setText(taLog.getText() + "Failed to disconnect. \n");
        }
        isConnected = false;
        //tf_username.setEditable(true);

    }

    public void writeUsers() {
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
        for (String token : tempList) {
            //users.append(token + "\n");
        }
    }

    /**
     * Maneja lo que le llega del servidor. Ejemplo: Manolito se ha conectado.
     * Eso le llega del servidor y lo muestra en pantalla
     */
    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat", publicKey = "Public";

            try {
                while ((stream = reader.readLine()) != null) {
                    data = stream.split(":");

                    if (data[2].equals(chat)) {
                        taLog.setText(taLog.getText() + data[0] + ": " + data[1] + "\n");

                    } else if (data[2].equals(connect)) {
                        //taLog.removeAll();
                        userAdd(data[0]);
                        serverPubKey = data[3];
                        System.out.println("CLAU PUBLICA: " + serverPubKey);
                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);

                    } else if (data[2].equals(publicKey)) {
                        System.out.println("PUBLIC ON CLIENT");

                    } else if (data[2].equals(done)) {
                        //users.setText("");
                        writeUsers();
                        users.clear();
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    public FXMLClientController() {
    }

    @FXML
    private void Send(ActionEvent event) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String nothing = "";
        if ((tfMsg.getText()).equals(nothing)) {
            tfMsg.setText("");
            tfMsg.requestFocus();
        } else {
            ////////////// Encriptació
            
            String msg = tfMsg.getText();
            byte[] missatge = msg.getBytes();
            
			
            Cipher xifrarRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            System.out.println("\n" + xifrarRSA.getProvider().getInfo());
            
            
            // Inicialització del xifrador: xifrem amb la clau pública
            xifrarRSA.init(Cipher.ENCRYPT_MODE, parellaClaus.getPublic());
            
            // Xifrat del missatge
            byte[] missatgeXifrat = xifrarRSA.doFinal(missatge);
            System.out.println(new String(missatgeXifrat));
            
            
            try {
                writer.println(username + ":" + missatgeXifrat + ":" + "Chat");
                writer.flush(); // flushes the buffer
            } catch (Exception ex) {
                taLog.setText(taLog.getText() + "Message was not sent. \n");
            }
            tfMsg.setText("");
            tfMsg.requestFocus();
        }

        tfMsg.setText("");
        tfMsg.requestFocus();
    }

    @FXML
    private void Connect(ActionEvent event) throws NoSuchAlgorithmException {
        btnDisconnect.setDisable(false);
        btnSend.setDisable(false);
        btnConnect.setDisable(true);

        
        
        address = tfAddress.getText();
        port = Integer.parseInt(tfPort.getText());
        System.out.println(address);
        System.out.println(port);
        
        
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        parellaClaus = keyGen.generateKeyPair();
        pubKey = parellaClaus.getPublic();
        privKey = parellaClaus.getPrivate();
            
        /*FXMLServerController servCont = new FXMLServerController();
        
        serverPubKey = servCont.getPublicServer();
        System.out.println("PUBLIC KEY SERVER" + serverPubKey);*/
        
        String pubKeyToSend = Base64.getEncoder().encodeToString(pubKey.getEncoded());
        System.out.println(pubKeyToSend);
        if (isConnected == false) {

            try {
                sock = new Socket(address, port);
                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":has connected.:Connect");
                writer.println(pubKeyToSend + ":sent public key:Public");
                //writer.println(pubKey);
                writer.flush();
                isConnected = true;
            } catch (Exception ex) {
                taLog.setText(taLog.getText() + "Cannot Connect! Try Again. \n");
                //tf_username.setEditable(true);
            }

            ListenThread();

        } else if (isConnected == true) {
            taLog.setText(taLog.getText() + "You are already connected. \n");
        }
    }

    @FXML
    private void Disconnect(ActionEvent event) {
        btnDisconnect.setDisable(true);
        btnSend.setDisable(true);
        btnConnect.setDisable(false);
        taLog.setText("");
        sendDisconnect();
        DisconnectUser();
    }

    public void setUser(String user) {
        username = user;
        System.out.println("HELLO " + username);
    }
    
    public void setPsw(String psw){
        this.psw = psw;
    }
    
    public String getPsw(){
        return psw;
    }
    
    public String getPswChat() throws UnsupportedEncodingException{
        String pToMatch = "fjeclot";
        String pswResult;
        pswResult = FXMLClientStartController.encryptPsw(pToMatch);
        
        return pswResult;
    }

    public void initialize(URL url, ResourceBundle rb) {
        btnDisconnect.setDisable(true);
        btnSend.setDisable(true);
    }
    

    
}
