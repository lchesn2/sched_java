package Controler;
import Model.DataBaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle rb = ResourceBundle.getBundle("Model/Login", Locale.getDefault());
        Parent root = FXMLLoader.load(getClass().getResource("../View/Login.fxml"), rb);
        primaryStage.setTitle("Europa Scheduling");
        primaryStage.setScene(new Scene(root, 423, 478));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {

        DataBaseManager.startConnection();
        DataBaseManager.getCustomerDataFromDB();
        DataBaseManager.getAllCities();

        Locale french = new Locale("fr", "CA");
        //Locale.setDefault(french);


        launch(args);
    }
}
