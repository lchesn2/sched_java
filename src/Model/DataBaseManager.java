package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;

public class DataBaseManager {

    private static final String mysqlDriver ="com.mysql.cj.jdbc.Driver"; //"com.mysql.jdbc.Driver";
    private static final String dbase = "U06ap3";
    private static final String url = "jdbc:mysql://3.227.166.251/U06ap3";
    private static final String user = "U06ap3";
    private static final String dataBasePassword = "53688711747";
    private static Connection connection = null;

    public void setStage(ActionEvent actionEvent, String location) throws IOException {
        Stage stage;
        Parent scene;
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(location));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    public void setStage(ActionEvent actionEvent, String location, ResourceBundle rb) throws IOException {
        Stage stage;
        Parent scene;
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(location), rb);
        stage.setScene(new Scene(scene));
        stage.show();
    }
    public static ObservableList<Customer> customers= Customer.getCustomers();

    public boolean checkCustomerExists(int id) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery("select customerId from customer where active = 1");
        while(r.next())
            {if(r.getInt(1)==id)
                return true;}
        r.close();
        return false;
    }
    public static ObservableList<City> getAllCities(){
        ObservableList<City> cList = FXCollections.observableArrayList();
        try{
            String sql = "select * from city";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int cityId = rs.getInt("cityId");
                String cityName = rs.getString("city");
                City c = new City(cityId, cityName);
                cList.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    return cList;}

    public static ArrayList<Integer> appointmentIds = new ArrayList<>();
    public static LocalDateTime changeFromUTC(Timestamp t){
       // LocalDateTime ldt = t.toLocalDateTime();
        //ZonedDateTime ZUTC = ldt.atZone(ZoneId.of("UTC"));
        //ZonedDateTime ZSD = ZUTC.withZoneSameInstant(ZoneId.systemDefault());


    return t.toLocalDateTime();}

    public static void getAppointmentDataFromDB(){
        try{
            Statement s = connection.createStatement();
            Appointment.appointments.clear();

            ResultSet appointmentIdSet = s.executeQuery("select appointmentId from appointment");

            appointmentIds.clear();
            while(appointmentIdSet.next())
                appointmentIds.add(appointmentIdSet.getInt(1));
            for(int appointmentId: appointmentIds){
                ResultSet appointmentSet = s.executeQuery("select customerId, userId, type, start, end, createdBy from appointment where appointmentId =" +appointmentId);
                appointmentSet.next();
                int customerId = appointmentSet.getInt("customerId");
                int userId = appointmentSet.getInt("userId");
                String type = appointmentSet.getString("type");
                Timestamp startTimestamp = appointmentSet.getTimestamp("start");
                Timestamp endTimestamp = appointmentSet.getTimestamp("end");
                String createdBy = appointmentSet.getString("createdBy");

                LocalDateTime startDate1 = changeFromUTC(startTimestamp);
                LocalDateTime endDate1 = changeFromUTC(endTimestamp);

                LocalDate localStartDate = startDate1.toLocalDate();
                LocalTime localStartTime = startDate1.toLocalTime();
                LocalDate localEndDate = endDate1.toLocalDate();
                LocalTime localEndTime = endDate1.toLocalTime();


                String user = getCurrentUser();
                Appointment appointment = new Appointment(appointmentId, customerId, userId, type, localStartDate, localEndDate, localStartTime, localEndTime, user);
                appointment.setAppointmentId(appointmentId);
                appointment.setCustomerId(customerId);
                appointment.setUserId(userId);
                appointment.setType(type);
                appointment.setStartDate(localStartDate);
                appointment.setEndDate(localEndDate);
                appointment.setStartTime(localStartTime);
                appointment.setEndTime(localEndTime);
                appointment.setCreatedBy(createdBy);
                Appointment.appointments.add(appointment);

            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCustomerDataFromDB(){
        try{
            Statement s = connection.createStatement();
            customers.clear();
            ResultSet customerIdResults = s.executeQuery("select customerId from customer where active = 1");
           ArrayList<Integer> customerIds = new ArrayList<>();
            while(customerIdResults.next())
                customerIds.add(customerIdResults.getInt(1));
            for(int customerId : customerIds){
                ResultSet customerResults = s.executeQuery("select customerName, active, addressId from customer where customerId = " + customerId );
                customerResults.next();
                String name = customerResults.getString("customerName");
                int active = customerResults.getInt("active");
                int addressId = customerResults.getInt("addressId");
                ResultSet addressResultSet = s.executeQuery("select address, cityId, postalCode, phone from address where addressId = " + addressId);
                addressResultSet.next();
                String address1 = addressResultSet.getString("address");
                int cityId = addressResultSet.getInt("cityId");
                String zip = addressResultSet.getString("postalCode");
                String phone = addressResultSet.getString("phone");
                Customer customer = new Customer(customerId, name, active, addressId, address1, zip, phone, cityId);
                customer.setCustomerId(customerId);
                customer.setCustomerName(name);
                customer.setActive(active);
                customer.setAddressId(addressId);
                customer.setAddress(address1);
                customer.setZip(zip);
                customer.setPhone(phone);
                customers.add(customer);
            }
            customerIdResults.close();

    } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void closeConnection(){
        try{connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Connection startConnection(){
        try {
            Class.forName(mysqlDriver);
            connection = DriverManager.getConnection(url, user, dataBasePassword);
            if(connection == null) throw new SQLException();

        }catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private static String currentUser;

    private static void setCurrentUser(String userName){
        currentUser = userName;
    }
    public static String getCurrentUser(){return currentUser;}

    public static boolean checkLogInCredentials(String userName, String password, ResourceBundle rb) {

        int userId = getUserId(userName);
        boolean passwordIsCorrect = checkPassword(userId, password);
        if(passwordIsCorrect){
            setCurrentUser(userName);
            try{
                Path path;
                path = Paths.get("timeTracker.txt");
                Files.write(path, Arrays.asList("Log in on "+ Date.from(Instant.now()).toString() + ". User: '" + currentUser + "'."),
                        StandardCharsets.UTF_8, Files.exists(path)? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            }catch(IOException e){
                e.printStackTrace();
            }return true;
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("error_dialogue"));
            alert.setContentText(rb.getString("error_message"));
            alert.showAndWait();

            return false;
        }

    }
    public static int getUserId(String userName)  {

        int userId = -1;
        try(
            Statement statement = connection.createStatement()){
            ResultSet userIdResult = statement.executeQuery("select userId from user where userName = '" + userName + "'");
            if(userIdResult.next())
                userId = userIdResult.getInt("userId");
            userIdResult.close();

        }catch(SQLException e){
        }
        return userId;

    }
    private static boolean checkPassword(int userId, String password){
        try(
            Statement statement = connection.createStatement()){
            ResultSet passwordResults = statement.executeQuery("select password from user where userId = '"+ userId + "'");
            String passwordForUser = null;
            if(passwordResults.next())
                passwordForUser = passwordResults.getString("password");
            else
                return false;
            passwordResults.close();
            if(passwordForUser.equals(password))
                return true;
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public static void deleteAppointmentDB(Appointment a) throws SQLException {
        int id = a.getAppointmentId();
        PreparedStatement ps = connection.prepareStatement("delete from appointment where appointmentId = ?");
        ps.setInt(1, id);
        ps.execute();
    }



    public static void deleteCustomerDB(Customer c) throws SQLException {
        int id = c.getCustomerId();
        int addressId=c.getAddressId();
        PreparedStatement ps3 = connection.prepareStatement("delete from appointment where customerId = ?");
        PreparedStatement ps1 = connection.prepareStatement("delete from customer where customerId = ?");
        PreparedStatement ps2 = connection.prepareStatement("delete from address where addressId = ?");
        ps1.setInt(1, id);
        ps2.setInt(1, addressId);
        ps3.setInt(1, id);
        ps3.execute();
        ps1.execute();
        ps2.execute();

    }
    public static int getNewAppointmentId() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet allAppointmentIds = stmt.executeQuery("SELECT appointmentId FROM appointment ORDER BY appointmentId");

        int appointmentId = 0;
        if (allAppointmentIds.last()) {
            appointmentId = allAppointmentIds.getInt(1) + 1;
            allAppointmentIds.close();
        } else {
            appointmentId = 1;
        }
    return appointmentId;}

    public int getNewCustomerId() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet allCustomerId = stmt.executeQuery("SELECT customerId FROM customer ORDER BY customerId");

        int customerId=0;
        if (allCustomerId.last()) {
            customerId = allCustomerId.getInt(1) + 1;
            allCustomerId.close();
        }
        else {customerId = 1;
        }

    return customerId;}

    public int getNewAddressId() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet allAddressId = stmt.executeQuery("SELECT addressId FROM address ORDER BY addressId");

        int addressId = 0;
        if (allAddressId.last()) {
            addressId = allAddressId.getInt(1) + 1;
            allAddressId.close();
        }
        else{
        }

    return addressId;}
    public void addNewAppointmentDB(Appointment a){
        try

        { Statement stmt = connection.createStatement();
            int appointmentId = a.getAppointmentId();
            int customerId = a.getCustomerId();
            int userId= getUserId(currentUser);
            String type = a.getType();
            LocalDate start = a.getStartDate();
            LocalDate end = a.getEndDate();
            LocalTime startTime = a.getStartTime();
            LocalTime endTime = a.getEndTime();

            Timestamp startTimestamp = Timestamp.valueOf(start.atTime(startTime));
            Timestamp endTimestamp = Timestamp.valueOf(end.atTime(endTime));
            PreparedStatement ps = connection.prepareStatement("INSERT INTO appointment VALUES (?, ?, ?, 'null', 'null', 'null', 'null', ?, 'null', ?, ?, '"+Timestamp.from(Instant.now())+"', '"+currentUser+"', '"+Timestamp.from(Instant.now())+"', '"+currentUser+"')");
            ps.setInt(1, appointmentId);
            ps.setInt(2, customerId);
            ps.setInt(3, userId);
            ps.setString(4, type);
            ps.setTimestamp(5, startTimestamp);
            ps.setTimestamp(6, endTimestamp);
            ps.execute();
        }catch(SQLException e)
        {e.printStackTrace();}
    }
    public void addNewCustomer(Customer c) {
            try
                { Statement stmt = connection.createStatement();
                    int id = c.getCustomerId();
                    String name = c.getCustomerName();
                    int addressId = c.getAddressId();
                PreparedStatement ps = connection.prepareStatement("INSERT INTO customer VALUES (?, ?, ?, '1', '"+Timestamp.from(Instant.now())+"', '"+currentUser+"', '"+Timestamp.from(Instant.now())+"', '"+currentUser+"')");
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setInt(3, addressId);
                ps.execute();

                }catch(SQLException e)
            {e.printStackTrace();}
        }
        public void finishAddingNewCustomer(Customer c, int cityId) throws SQLException {
            Statement s = connection.createStatement();
            String address = c.getAddress();
            int addressId = c.getAddressId();
            String phone = c.getPhone();
            String zip = c.getZip();
            PreparedStatement ps = connection.prepareStatement("insert into address values(?, ?, 'null', ?, ?, ?, '"+Timestamp.from(Instant.now())+"', '"+currentUser+"', '"+Timestamp.from(Instant.now())+"','"+currentUser+"')");
            ps.setInt(1, addressId);
            ps.setString(2, address);
            ps.setInt(3, cityId);
            ps.setString(4, zip);
            ps.setString(5, phone);
            ps.execute();
        }
    public void modifyApptDB(Appointment a) throws SQLException {

        try{
            Statement s = connection.createStatement();
            int apptId = a.getAppointmentId();
            int cId = a.getCustomerId();
            String type = a.getType();
            LocalDate start = a.getStartDate();
            LocalDate end = a.getEndDate();
            LocalTime startTime = a.getStartTime();
            LocalTime endTime = a.getEndTime();
            int userId = getUserId(currentUser);
            Timestamp startTimestamp = Timestamp.valueOf(start.atTime(startTime));
            Timestamp endTimestamp = Timestamp.valueOf(end.atTime(endTime));
            PreparedStatement ps = connection.prepareStatement("update appointment set customerId = ?, userId = ?, type = ?, start= ?, end= ?, lastUpdate = '"+Timestamp.from(Instant.now())+"', lastUpdateBy = '"+currentUser+"' where appointmentId = '"+apptId+"'");
            ps.setInt(1, cId);
            ps.setInt(2, userId);
            ps.setString(3, type);
            ps.setTimestamp(4, startTimestamp);
            ps.setTimestamp(5, endTimestamp);
            ps.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void modifyCustomerDB(Customer c) throws  SQLException{

            try {
                Statement s = connection.createStatement();
                int id = c.getCustomerId();
                String name = c.getCustomerName();
                int addressId = c.getAddressId();
                String address = c.getAddress();
                String phone = c.getPhone();
                String zip = c.getZip();

                    PreparedStatement ps1 = connection.prepareStatement("update customer set customerName =?, lastUpdate = '"+Timestamp.from(Instant.now())+"', lastUpdateBy = '"+currentUser+"' where customerId = ?");
                    PreparedStatement ps2 = connection.prepareStatement("update address set address =?, postalCode = ?, phone =?, lastUpdate = '"+Timestamp.from(Instant.now())+"', lastUpdateBy = '"+currentUser+"' where addressId = ?");
                    ps1.setString(1, name);
                    ps1.setInt(2, id);
                    ps2.setString(1, address);
                    ps2.setString(2, zip);
                    ps2.setString(3, phone);
                    ps2.setInt(4, addressId);
                    ps1.execute();
                    ps2.execute();

            }catch(SQLException e){
                e.printStackTrace();
            }
    }
    public static ObservableList<String> numbByMonth= FXCollections.observableArrayList();


    public static ArrayList<Integer> currUserAppts = new ArrayList<>();

    public static void users() throws SQLException {
        currUserAppts.clear();
        Statement s = connection.createStatement();
        int userId = getUserId(currentUser);
        //ResultSet rs = s.executeQuery("select appointmentId from appointments where userId = ?");
        PreparedStatement ps = connection.prepareStatement("select appointmentId from appointment where userId = ?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {int appId = rs.getInt("appointmentId");
        currUserAppts.add(appId);
        }


    }
    interface sqlQueryType {
        boolean sqlQueryType(String sq) throws SQLException;
    }

    public static void reportsByMonth() {
        try {
            numbByMonth.clear();
            sqlQueryType getAllTypes = sq ->//This lambda runs a sql query to search for three different appointment types. It is useful to be able to specify many 'types' of appointments with one function
            {
                Statement s = connection.createStatement();
                int numberInitial = 0;
                for (int i = 1; i < 13; i++) {
                    ResultSet getInitial = s.executeQuery("select count(type) as type from appointment where (type = '" + sq + "' && extract(month from start)= " + i + ")");
                    getInitial.next();
                    numberInitial = getInitial.getInt("type");
                    if (!(numberInitial == 0)) {
                        numbByMonth.add(numberInitial + " '" + sq + "' appt(s) " + " in mo. " + i + ".");
                    }
                }
                return true;
            };
            getAllTypes.sqlQueryType("Initial");
            getAllTypes.sqlQueryType("Follow-Up");
            getAllTypes.sqlQueryType("Other");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ObservableList<String> misc = FXCollections.observableArrayList();
    public static void miscReport(){
        try{misc.clear();
            sqlQueryType getAllTypes = sq -> {
                PreparedStatement ps = connection.prepareStatement("select count(type) as type from appointment where type = ?");
                ps.setString(1, sq);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int count = rs.getInt("type");
                misc.add("'"+sq + "': "+ count);

            return true;};
            getAllTypes.sqlQueryType("Initial");
            getAllTypes.sqlQueryType("Follow-Up");
            getAllTypes.sqlQueryType("Other");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static ObservableList<String> userAppointments= FXCollections.observableArrayList();
    public static void reportsUserAppts(){
        try{
            ArrayList<Integer> userIds = new ArrayList<>();
            ArrayList<Integer> appIds = new ArrayList<>();
            userIds.clear();
            appIds.clear();
            Statement s = connection.createStatement();
            ResultSet userIdSet = s.executeQuery("select userId from user");
            while(userIdSet.next())
                userIds.add(userIdSet.getInt(1));
            for(int id : userIds){
                userAppointments.add("User "+ id+ ": ");
                ResultSet apptsIds = s.executeQuery("select appointmentId from appointment");
                while(apptsIds.next())
                    appIds.add(apptsIds.getInt("appointmentId"));
                        for(int appId: appIds){
                            ResultSet getAppts = s.executeQuery("select type, start from appointment where appointmentId = " +appId+ " && userId = "+ id+" order by start");
                            while(getAppts.next())
                            {String type = getAppts.getString("type");
                            Timestamp start = getAppts.getTimestamp("start");
                                LocalDateTime startDate1 = changeFromUTC(start);
                                LocalDate localStartDate = startDate1.toLocalDate();
                                LocalTime localStartTime = startDate1.toLocalTime();
                            userAppointments.add("'"+ type + "' on "+ localStartDate+ " at "+ localStartTime);}
}
                        }



        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}
