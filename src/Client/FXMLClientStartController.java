package Client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.security.*;

public class FXMLClientStartController
        implements Initializable {

    @FXML
    private TextField tfUser;
    @FXML
    private PasswordField tfPass;
    @FXML
    private TextField tfAlias;

    public FXMLClientStartController() {
    }

    /**
     * Aqui hay que poner la verificacion y algun tipo de encriptacion o algo
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void loginButtonAction(ActionEvent event)
            throws IOException {

        String user = tfAlias.getText();
        String psw = tfPass.getText();
        
        String pswEncrypted = encryptPsw(psw);
        
        System.out.println("PSW: " + pswEncrypted + "\n");

        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("FXMLClient.fxml"));
        try {
            Loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        //Aqui se envia el alias al cliente
        FXMLClientController cli = Loader.getController();
        cli.setUser(user);
        cli.getPsw(pswEncrypted);

        Parent p = Loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(p));
        stage.show();
        ((Node) (event.getSource())).getScene().getWindow().hide();
        /*Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLClient.fxml"));
         Scene home_page_scene = new Scene(home_page_parent);
         Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
         app_stage.setScene(home_page_scene);
         app_stage.show();*/

    }
    
    public static String encryptPsw(String psw) throws UnsupportedEncodingException{
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytesOfMessage = md.digest(psw.getBytes("UTF-8"));
            BigInteger number = new BigInteger(1, bytesOfMessage);
            String hashtext = number.toString(16);
            
            while(hashtext.length() < 32){
                hashtext = "0" + hashtext;
            }
            return hashtext;
            
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
            //System.out.println("Error: " + e.getMessage());
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
    }
}
