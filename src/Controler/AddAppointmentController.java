package Controler;

import Model.Appointment;
import Model.Customer;
import Model.DataBaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddAppointmentController extends DataBaseManager implements Initializable {
    public TextField idTextField;
    public Button saveButton;
    public Button cancelButton;
    public ComboBox<Customer> customerComboBox;
    public ComboBox startComboBox;
    public ComboBox typeComboBox;
    public int appointmentId;
    public ComboBox dateComboBox;
    public boolean overlapsTime = false;
    public boolean equalsTime = false;

    public class emptyDataFields extends Exception{
        public emptyDataFields(){super();}
    }

    public void checkTimes(LocalDate sD, LocalTime st, LocalTime e) throws overLap {
        for(Appointment a: Appointment.getAppointments()){
            LocalDate bookedApptDate = a.getStartDate();
            LocalTime bookedApptStartTime = a.getStartTime();
            LocalTime bookedApptEndTime = a.getEndTime();
            if(sD.equals(bookedApptDate)){
            if (st.isAfter(bookedApptStartTime) && e.isBefore(bookedApptEndTime)) {
                overlapsTime = true;
            } else if (st.equals(bookedApptStartTime) || e.equals(bookedApptEndTime)) {
                equalsTime = true;
            } else {
                overlapsTime = false;
                equalsTime = false;
            }
                if(overlapsTime){
                    throw new overLap();
                }
                if(equalsTime){
                    throw new overLap();
                }
            }
        }
    }


    public static class overLap extends Exception{
        public overLap(){super();}
    }


    public void saveAppointment(ActionEvent actionEvent) {

        try{
            if(customerComboBox.getSelectionModel().isEmpty() || typeComboBox.getSelectionModel().isEmpty() || dateComboBox.getSelectionModel().isEmpty() || startComboBox.getSelectionModel().isEmpty())
                throw new emptyDataFields();
            LocalDate startDate = (LocalDate) dateComboBox.getSelectionModel().getSelectedItem();
            LocalDate endDate = startDate;
            LocalTime startTime= (LocalTime) startComboBox.getSelectionModel().getSelectedItem();
            LocalTime endTime = startTime.plusMinutes(15);
            System.out.println("about to run checkTimes");
            checkTimes(startDate, startTime, endTime);

             int appointmentId= Integer.parseInt(idTextField.getText());
             int customerId= customerComboBox.getValue().getCustomerId();
             String type= typeComboBox.getSelectionModel().getSelectedItem().toString();
             String createdBy= DataBaseManager.getCurrentUser();
             int userId =DataBaseManager.getUserId(createdBy);

            Appointment.addAppointment(new Appointment(appointmentId, customerId, userId, type, startDate, endDate, startTime, endTime, createdBy));

            Appointment a = Appointment.findAppointmentObject(appointmentId);
            assert a != null;
            addNewAppointmentDB(a);

           setStage(actionEvent, "/view/Home.fxml");
        }catch(overLap e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("This appt either overlaps or matches an existing appt");
            alert.showAndWait();

            System.out.println("Exception" + e.getMessage());

        } catch (NumberFormatException | IOException | emptyDataFields e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("Please enter a valid value for each text field, any empty value is considered invalid");
            alert.showAndWait();

            System.out.println("Exception" + e.getMessage());
        }

    }

    public void cancelAddingAppointment(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all values, do you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            setStage(actionEvent, "/view/Home.fxml");
        }
    }

    public void what(ActionEvent actionEvent) {
    }

    public void customerComboBoxDrop(ActionEvent actionEvent) {
    }

    public void startComboBoxDrop(ActionEvent actionEvent) {

    }


    public void typeComboBoxDrop(ActionEvent actionEvent) {


    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            appointmentId = DataBaseManager.getNewAppointmentId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        idTextField.setText(String.valueOf(appointmentId));

        typeComboBox.setItems(Appointment.getTypes());
        typeComboBox.setPromptText("Choose appt Type");
        customerComboBox.setItems(Customer.getCustomers());
        customerComboBox.setPromptText("Choose a Customer");
        startComboBox.setPromptText("Choose a start Time");
        dateComboBox.setPromptText("Choose a start Date");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.of(2021, 12,31);
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

    public void dateComboBoxDrop(ActionEvent actionEvent) {
    }
}
