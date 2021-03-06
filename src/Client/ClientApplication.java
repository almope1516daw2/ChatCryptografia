package Client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    public ClientApplication() {
    }

    public void start(Stage stage) throws Exception {
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("FXMLClient.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        //stage.show();
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
