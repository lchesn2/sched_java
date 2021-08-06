package Controler;

import Model.Appointment;
import Model.City;
import Model.Customer;
import Model.DataBaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class ModifyAppointmentController extends DataBaseManager implements Initializable {
    public TextField idTextField;
    public Button saveButton;
    public Button cancelButton;
    public ComboBox typeComboBox;
    public ComboBox startComboBox;
    public ComboBox<Customer> customerComboBox;
    public ComboBox dateComboBox;
    public boolean overlapsTime = false;
    public boolean equalsTime = false;
    boolean sameDate =false;

    public void sendData(Appointment a) {
        idTextField.setText(String.valueOf(a.getAppointmentId()));
        customerComboBox.setValue(Customer.findCustomerObject(a.getCustomerId()));
        typeComboBox.setValue(a.getType());
        dateComboBox.setValue(a.getStartDate());
        startComboBox.setValue(a.getStartTime());

    }


    public void checkTimes(LocalDate sD, LocalTime st, LocalTime e) throws overLap {
        for(Appointment a: Appointment.getAppointments()){
            LocalDate bookedApptDate = a.getStartDate();
            LocalTime bookedApptStartTime = a.getStartTime();
            LocalTime bookedApptEndTime = a.getEndTime();
            if(sD.equals(bookedApptDate)){
                System.out.println("I am running checkTimes method and the date matches so I will check the times");
                if (st.isAfter(bookedApptStartTime) && e.isBefore(bookedApptEndTime)) {
                    overlapsTime = true;
                } else if (st.equals(bookedApptStartTime) || e.equals(bookedApptEndTime)) {
                    equalsTime = true;
                } else {
                    overlapsTime = false;
                    equalsTime = false;
                }
                if(overlapsTime){
                    System.out.println("time overlaps");
                    throw new overLap();
                }
                if(equalsTime){
                    System.out.println("time matches another");
                    throw new overLap();
                }
            }
        }
    }

    public void modifyData(Appointment a){

        a.setCustomerId(customerComboBox.getValue().getCustomerId());
        a.setType(typeComboBox.getSelectionModel().getSelectedItem().toString());
        a.setStartDate((LocalDate) dateComboBox.getSelectionModel().getSelectedItem());
        a.setStartTime((LocalTime) startComboBox.getSelectionModel().getSelectedItem());
        LocalDate endDate = (LocalDate) dateComboBox.getSelectionModel().getSelectedItem();
        LocalTime endTime = ((LocalTime) startComboBox.getSelectionModel().getSelectedItem()).plusMinutes(15);
        a.setEndTime(endTime);
        a.setEndDate(endDate);

    }
    public static class overLap extends Exception{
        public overLap(){super();}
    }

    public void saveAppointmentUpdate(ActionEvent actionEvent) {
        try{
            LocalDate startDate = (LocalDate) dateComboBox.getSelectionModel().getSelectedItem();
            LocalDate endDate = startDate;
            LocalTime startTime= (LocalTime) startComboBox.getSelectionModel().getSelectedItem();
            LocalTime endTime = startTime.plusMinutes(15);

            Appointment a = Appointment.findAppointmentObject(parseInt(idTextField.getText()));
            assert a != null;
            checkTimes(startDate, startTime, endTime);
            System.out.println("i am checking to see if there is an overlap or match with values "+ startDate + " "+ startTime + " " + endTime);

            modifyData(a);
            modifyApptDB(a);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/Home.fxml"));
            loader.load();

            Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (overLap e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("The updated appointment either matches or overlaps an existing appointment");
            alert.showAndWait();

            System.out.println("Exception " + e.getCause());
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(SQLException s){
            s.printStackTrace();
        }
    }



    public void cancelAppointmentUpdate(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all values, do you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
           setStage(actionEvent, "/view/Home.fxml");
        }
    }

    public void typeComboBoxDrop(ActionEvent actionEvent) {
    }

    public void durationComboBoxDrop(ActionEvent actionEvent) {
    }

    public void startComboBoxDrop(ActionEvent actionEvent) {
    }

    public void customerComboBoxDrop(ActionEvent actionEvent) {
    }

    public void dateComboBoxDrop(ActionEvent actionEvent) {
    }
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(Appointment.getTypes());
        customerComboBox.setItems(Customer.getCustomers());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.of(2020, 12,31);
        LocalTime start = LocalTime.of(11, 00);
        LocalTime end = LocalTime.of(16, 45);

        while(start.isBefore(end.plusSeconds(1))){
            startComboBox.getItems().add(start);
            start = start.plusMinutes(15);
        }
        while(startDate.isBefore(endDate)){
            dateComboBox.getItems().add(startDate);
            startDate = startDate.plusDays(1);
        }

    }
}
