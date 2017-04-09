package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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


public class FXMLClientController implements Initializable
{
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
  
  
  
   String username, address;
     ArrayList<String> users = new ArrayList();
     int port;
     Boolean isConnected = false;
    
     Socket sock;
     BufferedReader reader;
     PrintWriter writer;
    
     
     //Crea un thread que maneja los eventos que envia servidor
     public void ListenThread() 
    {
         Thread IncomingReader = new Thread(new IncomingReader());
         IncomingReader.start();
    }
    
    //--------------------------//
    
     public void userAdd(String data) 
    {
         users.add(data);
    }
    
     public  void userRemove(String data) 
    {
         taLog.setText(taLog.getText() + data + " is now offline.\n");
         //System.out.println(data + " is now offline.\n");
    }
    
    //--------------------------//
     
     public  void sendDisconnect() 
    {
        String bye = (username + ": :Disconnect");
        try
        {
            writer.println(bye); 
            writer.flush(); 
        } catch (Exception e) 
        {
            taLog.setText(taLog.getText() + "Could not send Disconnect message.\n");
        }
    }
     
     
     public  void DisconnectUser() 
    {
        try 
        {
            taLog.setText(taLog.getText() + "Disconnected.\n");
            sock.close();
        } catch(Exception ex) {
            taLog.setText(taLog.getText() + "Failed to disconnect. \n");
        }
        isConnected = false;
        //tf_username.setEditable(true);

    }
     
     public void writeUsers() 
    {
         String[] tempList = new String[(users.size())];
         users.toArray(tempList);
         for (String token:tempList) 
         {
             //users.append(token + "\n");
         }
    }
    
     /**
      * Maneja lo que le llega del servidor. 
      * Ejemplo: Manolito se ha conectado. 
      *          Eso le llega del servidor y lo muestra en pantalla
      */
    public  class IncomingReader implements Runnable
    {
        @Override
        public void run() 
        {
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";
            
            try 
            {
                while ((stream = reader.readLine()) != null) 
                {
                     data = stream.split(":");

                     if (data[2].equals(chat)) 
                     {
                        taLog.setText(taLog.getText() + data[0] + ": " + data[1] + "\n");
                        
                     } 
                     else if (data[2].equals(connect))
                     {
                        //taLog.removeAll();
                        userAdd(data[0]);
                     } 
                     else if (data[2].equals(disconnect)) 
                     {
                         userRemove(data[0]);
                         
                     } 
                     else if (data[2].equals(done)) 
                     {
                        //users.setText("");
                        writeUsers();
                        users.clear();
                     }
                }
           }catch(Exception ex) { }
        }
    }

  public FXMLClientController() {}
  
  @FXML
  private void Send(ActionEvent event)
  {
    String nothing = "";
        if ((tfMsg.getText()).equals(nothing)) {
            tfMsg.setText("");
            tfMsg.requestFocus();
        } else {
            try {
               writer.println(username + ":" + tfMsg.getText() + ":" + "Chat");
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
  private void Connect(ActionEvent event)
  {
      btnDisconnect.setDisable(false);
      btnSend.setDisable(false);
      btnConnect.setDisable(true);
      
      address = tfAddress.getText();
      port = Integer.parseInt(tfPort.getText());
      System.out.println(address);
      System.out.println(port);
    if (isConnected == false) 
        {

            try 
            {
                sock = new Socket(address, port);
                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":has connected.:Connect");
                writer.flush(); 
                isConnected = true; 
            } 
            catch (Exception ex) 
            {
                taLog.setText(taLog.getText() + "Cannot Connect! Try Again. \n");
                //tf_username.setEditable(true);
            }
            
            ListenThread();
            
        } else if (isConnected == true) 
        {
            taLog.setText(taLog.getText() + "You are already connected. \n");
        }
  }
  
  @FXML
  private void Disconnect(ActionEvent event)
  {
      btnDisconnect.setDisable(true);
      btnSend.setDisable(true);
      btnConnect.setDisable(false);
        taLog.setText("");
        sendDisconnect();
        DisconnectUser();
  }
  public void setUser(String user){
      username = user;
      System.out.println("HELLO " + username);
  }
  
  
  
  public void initialize(URL url, ResourceBundle rb) {
    btnDisconnect.setDisable(true);
    btnSend.setDisable(true);
  }
}
