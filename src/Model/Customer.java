package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class Customer {
    private int customerId;
    private String customerName;
    private int active;
    private int addressId;
    private String address;
    private String zip;
    private String phone;
    private int cityId;

    public static ObservableList<Customer> customers = FXCollections.observableArrayList();

    public static void addCustomer(Customer c) {
        customers.add(c);
    }

    public static ObservableList<Customer> getCustomers() {
        return customers;
    }

    public static boolean deleteCustomer(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) {
                Customer.getCustomers().remove(c);
                return true;
            }
        }
        return false;
    }

    public Customer(int customerId, String customerName, int active, int addressId,
                    String address, String zip, String phone, int cityId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.active = active;
        this.addressId = addressId;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.cityId = cityId;
    }

    public static Customer findCustomerObject(int id){
        for(Customer c: customers){
            if(c.getCustomerId()== id)
                return c;
        }
        return null;
    }

    public int getCityId(){return cityId;}
    public void setCityId(int id){this.cityId = id;}
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return customerName;
    }

}