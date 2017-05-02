package Client;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.codec.binary.Hex.decodeHex;

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

    Socket sock;
    BufferedReader reader;
    PrintWriter writer;

    public String StringSecretKey;
    SecretKey secretKey;
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
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat", key = "Key";

            try {
                while ((stream = reader.readLine()) != null) {
                    data = stream.split(":");
                    System.out.println("################" + data[0] + ":::"+ data[1] + ":::"+ data[2]);

                    if (data[2].equals(chat)) {
                        if(data[1].equals("has connected.")){
                            taLog.setText(taLog.getText() + data[0] + ": " + data[1] + "\n");
                        } else {
                            String message = decryptData(secretKey, data[1]);
                        System.out.println(message);
                        taLog.setText(taLog.getText() + data[0] + ": " + String.valueOf(message) + "\n");
                        }
                        

                    } else if (data[2].equals(connect)) {
                        //taLog.removeAll();
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);

                    } else if (data[2].equals(done)) {
                        //users.setText("");
                        writeUsers();
                        users.clear();
                    } 
                    if (data[2].equals(key)) {
                        
                        StringSecretKey = data[0];
                        
                        byte[] decodedKey = Base64.getDecoder().decode(StringSecretKey);
                        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                        
                        //secretKey = loadKey(StringSecretKey);
                        //System.out.println("Secret key on client: " + secretKey + " string secretkey: " + StringSecretKey);
                        
                        // decode the base64 encoded string
                        //byte[] decodedKey = Base64.decodeBase64(StringSecretKey);
                        // rebuild key using SecretKeySpec
                        /*SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");*/ 
                        //System.out.println("Secret key on client: " + secretKey + " string secretkey: " + StringSecretKey);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    public static SecretKey loadKey(String key) throws IOException
    {
        String data = key;
        byte[] encoded;
        try {
            encoded = decodeHex(data.toCharArray());
        } catch (DecoderException e) {
            System.out.println("ERROR SECRET KEY");
            e.printStackTrace();
            return null;
        }
        return new SecretKeySpec(encoded, "AES");
    }
    /*
    public byte[] encryptData(SecretKey sKey, String stringData) {
        byte[] data = stringData.getBytes();
        byte[] encryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            
            encryptedData = cipher.doFinal(data);
        } catch (Exception ex) {
            System.err.println("Error xifrant les dades: " + ex.getMessage());
        }
    return encryptedData;
    }
    */
    
    public String encryptData(SecretKey sKey, String stringData) {
        byte[] raw;
        System.out.println("String key: " + sKey + " String data: " + stringData);
        String encryptedString;
        byte[] encryptedData;
        SecretKeySpec skeySpec;
        byte[] encryptText = stringData.getBytes();
        Cipher cipher;
        try {
            //raw = Base64.decodeBase64(sKey);
            //skeySpec = new SecretKeySpec(raw, "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            //encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
            encryptedData = cipher.doFinal(encryptText);
            encryptedString = new String(encryptedData);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "Error" + e.getMessage();
        }
        
        return encryptedString;
    }
    
    
    /*public byte[] decryptData(SecretKey sKey, String stringData) {
        byte[] data = stringData.getBytes();
        byte[] encryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            
            encryptedData = cipher.doFinal(data);
        } catch (Exception ex) {
            System.err.println("Error xifrant les dades: " + ex);
        }
    return encryptedData;
    }*/
    
    public String decryptData(SecretKey sKey, String stringData) {
        byte[] raw;
        System.out.println("String key: " + sKey + " String data: " + stringData);
        String decryptedString;
        byte[] encryptedData;
        SecretKeySpec skeySpec;
        byte[] encryptText = stringData.getBytes();
        Cipher cipher;
        try {
            //raw = Base64.decodeBase64(sKey);
            //skeySpec = new SecretKeySpec(raw, "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            //encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
            encryptedData = cipher.doFinal(encryptText);
            decryptedString = new String(encryptedData);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "Error" + e.getMessage();
        }
        
        return decryptedString;
    }
    
    public FXMLClientController() {
    }

    @FXML
    private void Send(ActionEvent event) {
        String nothing = "";
        if ((tfMsg.getText()).equals(nothing)) {
            tfMsg.setText("");
            tfMsg.requestFocus();
        } else {
            try {
                
                String message = encryptData(secretKey, tfMsg.getText());
                //byte[] message = encryptData(secretKey, tfMsg.getText());
                System.out.println(message);
                writer.println(username + ":" + String.valueOf(message) + ":" + "Chat");
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
    private void Connect(ActionEvent event) {
        btnDisconnect.setDisable(false);
        btnSend.setDisable(false);
        btnConnect.setDisable(true);

        address = tfAddress.getText();
        port = Integer.parseInt(tfPort.getText());
        System.out.println(address);
        System.out.println(port);
        if (isConnected == false) {

            try {
                sock = new Socket(address, port);
                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":has connected.:Connect");
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