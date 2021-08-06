package Controler;

import Model.City;
import Model.Customer;
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
import java.sql.SQLException;
import java.util.Optional;

public class customerView extends DataBaseManager {
    public TableColumn idColumn;
    public TableColumn nameColumn;
    public TableColumn addressColumn;
    public TableColumn cityColumn;
    public TableColumn zipColumn;
    public TableColumn countryColumn;
    public TableColumn phoneColumn;
    public Button addButton;
    public Button modifyButton;
    public Button deleteButton;
    public Button cancelButton;
    public TableView<Customer> customerTableView;

    public void setTables(){
        customerTableView.setItems(Customer.getCustomers());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        zipColumn.setCellValueFactory(new PropertyValueFactory<>("zip"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

    }
    public void addNewCustomer(ActionEvent actionEvent) throws IOException {
        setStage(actionEvent, "/View/AddCustomer.fxml");

    }

    public void modifyCustomer(ActionEvent actionEvent) throws IOException {
        if(customerTableView.getSelectionModel().getSelectedItem()== null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/ModifyCustomer.fxml"));
        loader.load();
        ModifyCustomerController mc = loader.getController();
        mc.sendData((Customer) customerTableView.getSelectionModel().getSelectedItem());


        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();

    }

    public void customerDeleted(ActionEvent actionEvent) throws SQLException {
        Customer c= customerTableView.getSelectionModel().getSelectedItem();
        if(c==null)
            return;
        { Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to delete this customer?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                int id =c.getCustomerId();
                if(Customer.deleteCustomer(id))
                   {deleteCustomerDB(c);
                    System.out.println("deleted");}}
        }
    }

    public void cancelCustomer(ActionEvent actionEvent) throws IOException {
        setStage(actionEvent, "/View/Home.fxml");


    }
}
