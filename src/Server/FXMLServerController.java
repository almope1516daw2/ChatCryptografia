package Server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;










public class FXMLServerController
  implements Initializable
{
  @FXML
  private TextField tfMsg;
  @FXML
  private TextArea taLog;
  
  public FXMLServerController() {}
  
  @FXML
  private void sendButtonAction(ActionEvent event)
  {
    taLog.setText(tfMsg.getText());
  }
  
  public void initialize(URL url, ResourceBundle rb) {}
}
