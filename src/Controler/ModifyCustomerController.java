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

import static java.lang.Integer.parseInt;

public class ModifyCustomerController extends DataBaseManager implements Initializable {
    public Button saveButton;
    public Button cancelButton;
    public TextField idTextField;
    public TextField nameTextField;
    public TextField addressTextField;
    public TextField zipTextField;
    public ComboBox cityComboBox;
    public TextField phoneTextField;
    public TextField countryTextField;
    private City cName;

    interface city {
        boolean specific(String s);
    }


    public void modifyData(Customer c){

        String name = nameTextField.getText();
        c.setCustomerName(name);
        c.setAddress(addressTextField.getText());
        c.setZip(zipTextField.getText());
        c.setPhone(phoneTextField.getText());
        City cty  = (City) cityComboBox.getValue();
        c.setCityId(cty.getCityId());




    }
    public void setZipCountryValues(){
        City c = (City) cityComboBox.getValue();//used the same Lambda from addCustomerController, to display to the user the country and zip that would be added to database, based on the city selection
        System.out.println(c.getCityId() + " "+ c.getCity() );

        AddCustomerController.city usa = s -> {if(c.getCity().equals(s))
        {countryTextField.setText("USA");
            zipTextField.setText("11111");}
            return true;};

        AddCustomerController.city canada = s -> {if(c.getCity().equals(s))
        {countryTextField.setText("Canada");
            zipTextField.setText("11112");}
            return true;};

        AddCustomerController.city norway = s -> {if(c.getCity().equals(s))
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


    }
    public City getCity(int id){
        for(City c : getAllCities())
            if(id == c.getCityId())
                return c;
    return null;}


    public void sendData(Customer c){
        idTextField.setText(String.valueOf(c.getCustomerId()));
        nameTextField.setText(c.getCustomerName());
        addressTextField.setText(c.getAddress());
        zipTextField.setText(c.getZip());
        phoneTextField.setText(c.getPhone());
        int cId= c.getCityId();
        cName = getCity(cId);
        cityComboBox.setValue(cName);
        setZipCountryValues();



    }
    public void checkFields() throws emptyDataFields {
        if(nameTextField.getText().length()==0 || addressTextField.getText().length()==0 || phoneTextField.getText().length()==0)
            throw new emptyDataFields();
    }
    public class emptyDataFields extends Exception{
        public emptyDataFields(){super();}
    }
    public void saveCustomerUpdate(ActionEvent actionEvent) throws emptyDataFields {

        try{

            Customer c = Customer.findCustomerObject(parseInt(idTextField.getText()));
            assert c != null;
            modifyData(c);
            modifyCustomerDB(c);
            checkFields();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/customerView.fxml"));
            loader.load();
            customerView cvController = loader.getController();
            cvController.setTables();


            Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (emptyDataFields e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("Please enter a valid value for each text field, any empty value is considered invalid");
            alert.showAndWait();

            System.out.println("Exception" + e.getMessage());
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(SQLException s){
            s.printStackTrace();
        }
    }

    public void cancelCustomerUpdate(ActionEvent actionEvent) throws IOException {
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

    public void cityComboBoxDrop(ActionEvent actionEvent) {
        setZipCountryValues();

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cityComboBox.setItems(DataBaseManager.getAllCities());



}}
