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
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
public class AddCustomerController extends DataBaseManager implements Initializable {
    public Button saveButton;
    public Button cancelButton;
    public TextField idTextField;
    public TextField nameTextField;
    public TextField addressTextField;
    public TextField zipTextField;
    public TextField phoneTextField;
    public ComboBox<City>cityComboBox;
    public int customerId;
    public TextField countryTextField;


    public void countryTexted(ActionEvent actionEvent) {
    }
    public class emptyDataFields extends Exception{
        public emptyDataFields(){super();}
    }

    public void saveNewCustomer(ActionEvent actionEvent) {
        try{
            if(nameTextField.getText().length()==0 || addressTextField.getText().length()==0 || phoneTextField.getText().length()==0 || cityComboBox.getSelectionModel().isEmpty())
                throw new emptyDataFields();

            String name = nameTextField.getText();
            String address = addressTextField.getText();
            String zip =zipTextField.getText();
            String phone = phoneTextField.getText();
            int addressId = getNewAddressId();
            customerId = getNewCustomerId();
            idTextField.setText(String.valueOf(customerId));
            int cityId = cityComboBox.getValue().getCityId();

            Customer.addCustomer(new Customer(customerId, name, 1, addressId, address, zip, phone, cityId));

            Customer c = Customer.findCustomerObject(customerId);
            assert c != null;

            finishAddingNewCustomer(c,cityId);
            addNewCustomer(c);

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
        catch (NumberFormatException | IOException | SQLException |emptyDataFields e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("Please enter a valid value for each text field, any empty value is considered invalid");
            alert.showAndWait();

            System.out.println("Exception" + e.getMessage());
        }

    }

    public void cancelAddingCustomer(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all values, do you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
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
    }
    interface city {
        boolean specific(String s);
    }


    public void cityComboBoxDrop(ActionEvent actionEvent) {//I wanted to display the zip and country that would be tied to the new customer on the add customer and mod customer screens so i created a lambda to deduce country and zip based on the city
        City c = cityComboBox.getValue();
        System.out.println(c.getCityId() + " "+ c.getCity() );

        city usa = s -> {if(c.getCity().equals(s))
            {countryTextField.setText("USA");
            zipTextField.setText("11111");}
            return true;};

        city canada = s -> {if(c.getCity().equals(s))
            {countryTextField.setText("Canada");
            zipTextField.setText("11112");}
            return true;};

        city norway = s -> {if(c.getCity().equals(s))
           { countryTextField.setText("Norway");
            zipTextField.setText("11113");}
            return true;};

        usa.specific("Los Angeles");
        usa.specific("New York");
        usa.specific("Washington DC");

        canada.specific("Toronto");
        canada.specific("Montreal");
        canada.specific("Vancouver");

        norway.specific("Oslo");
        norway.specific("Bergen");
        System.out.println("finished");
    }

    @Override

    public void initialize(URL location, ResourceBundle resources) {

        try {
            customerId = getNewCustomerId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        idTextField.setText(String.valueOf(customerId));
        cityComboBox.setItems(DataBaseManager.getAllCities());
    }
}
