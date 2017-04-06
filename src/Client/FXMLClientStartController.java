package Client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;









public class FXMLClientStartController
  implements Initializable
{
  @FXML
  private TextField tfMsg;
  @FXML
  private TextArea taLog;
  
  public FXMLClientStartController() {}
  
  @FXML
  private void loginButtonAction(ActionEvent event)
    throws IOException
  {
    Parent home_page_parent = (Parent)FXMLLoader.load(getClass().getResource("FXMLClient.fxml"));
    Scene home_page_scene = new Scene(home_page_parent);
    Stage app_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    app_stage.setScene(home_page_scene);
    app_stage.show();
  }
  
  public void initialize(URL url, ResourceBundle rb) {}
}
