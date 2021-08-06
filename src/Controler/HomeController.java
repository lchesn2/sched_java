package Controler;

import Model.Appointment;
import Model.DataBaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController extends DataBaseManager implements Initializable {
    public Button customersButton;
    public Button reportsButton;
    public Button newAppmntButton;
    public Button updateAppmntButton;
    public Button logoutButton;
    public Button deleteAppmntButton;
    public TableView<Appointment> appointmentTable;
    public TableColumn apIdColumn;
    public TableColumn customerColumn;
    public TableColumn userColumn;
    public TableColumn typeColumn;
    public TableColumn dateColumn;
    public TableColumn endCol;
    public RadioButton allRB;
    public RadioButton weekRB;
    public RadioButton monthRB;

    public void goToCustomers(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/customerView.fxml"));
        loader.load();
        customerView cvController = loader.getController();
        cvController.setTables();

        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void goToReports(ActionEvent actionEvent) throws IOException {
        setStage(actionEvent, "/view/ReportsScreen.fxml");
    }

    public void updateAppmnt(ActionEvent actionEvent) throws IOException {
        Appointment a= appointmentTable.getSelectionModel().getSelectedItem();
        if(a==null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/ModifyAppointment.fxml"));
        loader.load();
        ModifyAppointmentController apptControl = loader.getController();
        apptControl.sendData(appointmentTable.getSelectionModel().getSelectedItem());


        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void createNewAppmnt(ActionEvent actionEvent) throws IOException {
        setStage(actionEvent, "/view/AddAppointment.fxml");
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        { Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to log out of the scheduling system?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK)
               {   LoginController.isFirstHomeVisit = true;
                   ResourceBundle rb = ResourceBundle.getBundle("Model/Login", Locale.getDefault());
                   setStage(actionEvent, "/View/Login.fxml", rb);
               }
        }
    }

    public void deleteAppmnt(ActionEvent actionEvent) throws SQLException {
        Appointment a= appointmentTable.getSelectionModel().getSelectedItem();
        if(a==null)
            return;
        { Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to delete this appointment?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                int id =a.getAppointmentId();
                if(Appointment.deleteAppointment(id))
                {   System.out.println("appointment with id "+ id + "has been ");
                    DataBaseManager.deleteAppointmentDB(a);
                    System.out.println("deleted");}}
        }
    }
    public static void checkAppointmentsInFifteen() {

        try {
            DataBaseManager.users();


        for(int appId : DataBaseManager.currUserAppts) {

            Appointment a =Appointment.findAppointmentObject(appId);
         if(a.getStartDate().equals(LocalDate.now())) {
                LocalTime startTime = a.getStartTime();
                LocalTime currentTime = LocalTime.now();
                Long timeDifference = ChronoUnit.MINUTES.between(startTime, currentTime);
                Long interval = timeDifference * (-1);
                if (interval > 0 && interval <= 15) {
                    System.out.println("interval is between");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Appointment Alert");
                    alert.setContentText("You have an appointment in approximately fifteen minutes");
                    alert.show();
                    LoginController.isFirstHomeVisit = false;
                return;}
          }


        } }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(LoginController.isFirstHomeVisit)
            {

                    checkAppointmentsInFifteen();

            }
        else
            {LoginController.isFirstHomeVisit = false;
            System.out.println(LoginController.isFirstHomeVisit);}

        appointmentTable.setItems(Appointment.getAppointments());
        apIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));





    }

    public void allSelected(ActionEvent actionEvent) {
        appointmentTable.setItems(Appointment.getAppointments());
    }

    public void weekSelected(ActionEvent actionEvent) {

        LocalDate thisWeekStart = LocalDate.now();
        LocalDate thisWeekEnd = LocalDate.now().plusDays(7);
        Appointment.appointmentsThisWeek.clear();
        for(Appointment a: Appointment.appointments){
            if((a.getStartDate().isAfter(thisWeekStart)|| a.getStartDate().equals(thisWeekStart))&& (a.getStartDate().isBefore(thisWeekEnd)|| a.getEndDate().equals(thisWeekEnd) )){
                Appointment.addAppointmentThisWeek(a);
            }

        }
        appointmentTable.setItems(Appointment.getAppointmentsThisWeek());
    }

    public void monthSelected(ActionEvent actionEvent) {
        Month thisMonthStart = LocalDate.now().getMonth();
        Appointment.appointmentsThisMonth.clear();
        for(Appointment a: Appointment.appointments){
            Month start =a.getStartDate().getMonth();

            if(start.equals(thisMonthStart))
                Appointment.addAppointmentsThisMonth(a);
        }
        appointmentTable.setItems(Appointment.getAppointmentsThisMonth());

    }


}
