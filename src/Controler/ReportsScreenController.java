package Controler;

import Model.DataBaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReportsScreenController extends DataBaseManager implements Initializable {
    public Button miscButton;
    public Button homeButton;
    public Button consultantButton;
    public Label monthLabel;
    public Label consultantLabel;
    public Label miscLabel;
    public Button monthButton;
    StringBuilder misc = new StringBuilder();
    public void generateMisc(ActionEvent actionEvent) {
        DataBaseManager.miscReport();
        int index = misc.length();
        misc.delete(0, index);
        for(String s: DataBaseManager.misc){
            misc.append(s + ". ");
            miscLabel.setText(misc.toString());
        }
    }

    public void goHome(ActionEvent actionEvent) throws IOException {
        setStage(actionEvent, "/view/Home.fxml");

    }
    public static StringBuilder schedule = new StringBuilder();

    public void generateConsultant(ActionEvent actionEvent) {

        DataBaseManager.reportsUserAppts();
        int index = schedule.length();
        schedule.delete(0, index);

        for(String s: DataBaseManager.userAppointments)
            schedule.append(s + " ");
        consultantLabel.setText(schedule.toString());
    }

    StringBuilder wholeReport = new StringBuilder();
        public void generateMonth (ActionEvent actionEvent) {
            DataBaseManager.reportsByMonth();
            int lastIndexStringBuild = wholeReport.length();
            wholeReport.delete(0, lastIndexStringBuild);

            for(String s : DataBaseManager.numbByMonth){
                wholeReport.append(s + " ");
            }
            monthLabel.setText(wholeReport.toString());

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}