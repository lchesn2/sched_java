package Controler;

import Model.Appointment;
import Model.DataBaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController extends DataBaseManager implements Initializable {
    public Label UserName;
    public TextField UserNameTextF;
    public Label Password;
    public TextField PasswordTextF;
    public Button LoginButton;
    public RadioButton EnglishRB;
    public Button exit;
    private ResourceBundle rb;
    public static boolean isFirstHomeVisit = false;

    public void UserName_Entered(ActionEvent actionEvent) {
    }

    public void Password_Entered(ActionEvent actionEvent) {
    }


    public void Login_Clicked(ActionEvent actionEvent) throws IOException {

        String userName = UserNameTextF.getText();
        String password = PasswordTextF.getText();

        if(DataBaseManager.checkLogInCredentials(userName, password, rb)){

            try {
                DataBaseManager.getAppointmentDataFromDB();
                isFirstHomeVisit = true;
                setStage(actionEvent, "/view/Home.fxml");

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void EnglishRB_Selected(ActionEvent actionEvent) {

        boolean EnglishIsSelected = EnglishRB.isSelected();


    }

    public void SloveneRB_Selected(ActionEvent actionEvent) {

    }

    public void exited(ActionEvent actionEvent) {
        System.exit(-1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        System.out.println(rb.getString("username"));



    }
}
