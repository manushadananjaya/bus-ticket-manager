package manusha.busticket.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import manusha.busticket.model.Bus;
import manusha.busticket.util.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane main_form, availableB_form, bookingTicket_form, customer_form, dashboard_form;

    @FXML
    private Button availableB_addBtn, availableB_btn, availableB_delBtn, availableB_resetBtn, availableB_updateBtn;
    @FXML
    private Button bookingTicket_btn, customers_btn, close_btn, logout_btn, min_btn, dashboard_btn;

    @FXML
    private TableView<Bus> availableB_tableView;

    @FXML
    private TableColumn<Bus, String> availableB_col_busId, availableB_col_date, availableB_col_location, availableB_col_price, availableB_col_status;

    @FXML
    private TableColumn<?, ?> availableB_col_seats;

    @FXML
    private TextField availableB_busId, availableB_location, availableB_price, availableB_search, cus_search;

    @FXML
    private TextField availableB_totalSeats;

    @FXML
    private DatePicker availableB_date;

    @FXML
    private ComboBox<String> availableB_status;


    @FXML
    private ComboBox<String> bookingTicket_busId;

    @FXML
    private ComboBox<String> bookingTicket_location;

    @FXML
    private ComboBox<String> bookingTicket_ticketNum;

    @FXML
    private AreaChart<String, Number> dashboard_chart;

    @FXML
    private TextField bookingTicket_firstName;

    @FXML
    private TextField bookingTicket_lastName;

    @FXML
    private DatePicker bookingTicket_date;


    @FXML
    private TextField bookingTicket_phoneNum;

    @FXML
    private Button bookingTicket_resetBtn;

    @FXML
    private Label bookingTicket_sci_busId;

    @FXML
    private Label bookingTicket_sci_date;

    @FXML
    private Label bookingTicket_sci_firstName;

    @FXML
    private Label bookingTicket_sci_gender;

    @FXML
    private Label bookingTicket_sci_lastName;

    @FXML
    private Label bookingTicket_sci_location;

    @FXML
    private Button bookingTicket_sci_payBtn;

    @FXML
    private Label bookingTicket_sci_phoneNum;

    @FXML
    private Button bookingTicket_sci_recBtn;

    @FXML
    private Label bookingTicket_sci_ticketNum;

    @FXML
    private Label bookingTicket_availableSeats;



    @FXML
    private Label bookingTicket_sci_total;


    @FXML
    private Button bookingTicket_selectBtn;

    @FXML
    private ComboBox<String> bookingTicket_gender;


    @FXML
    private Label dashboard_incomeTodal, dashboard_todayIncome;

    // Database Connection
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Statement statement;

    // ObservableList to hold the bus data
    private ObservableList<Bus> availableBusListData;

    // Array for status options
    private final String[] statusList = {"Available", "Not Available"};

    // Fetch available buses from the database
    public ObservableList<Bus> getAvailableBuses() {
        ObservableList<Bus> buses = FXCollections.observableArrayList();
        String query = "SELECT * FROM buses";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                buses.add(new Bus(
                        rs.getInt("busId"),
                        rs.getString("busStatus"),
                        rs.getString("busLocation"),
                        rs.getDate("busDate"),
                        rs.getDouble("busPrice"),
                        rs.getInt("busSeats")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    // Show bus data in the TableView
    public void availableBusShowBusData() {
        availableBusListData = getAvailableBuses();

        availableB_col_busId.setCellValueFactory(new PropertyValueFactory<>("busId"));
        availableB_col_status.setCellValueFactory(new PropertyValueFactory<>("busStatus"));
        availableB_col_location.setCellValueFactory(new PropertyValueFactory<>("busLocation"));
        availableB_col_date.setCellValueFactory(new PropertyValueFactory<>("busDate"));
        availableB_col_price.setCellValueFactory(new PropertyValueFactory<>("busPrice"));
        availableB_col_seats.setCellValueFactory(new PropertyValueFactory<>("busSeats"));


        availableB_tableView.setItems(availableBusListData);
    }

    // Add a bus to the database
    public void availableBusAdd() {
        String busId = availableB_busId.getText();
        String location = availableB_location.getText();
        String price = availableB_price.getText();
        String date = availableB_date.getValue() != null ? availableB_date.getValue().toString() : "";
        String status = availableB_status.getSelectionModel().getSelectedItem();
        int totalSeats = Integer.parseInt(availableB_totalSeats.getText());

        if (busId.isEmpty() || location.isEmpty() || price.isEmpty() || date.isEmpty() || status == null || totalSeats == 0) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            // Check if bus ID is already in the database
            String checkQuery = "SELECT * FROM buses WHERE busId = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
                pstmt.setString(1, busId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Bus ID already exists.");
                    return;
                }
            }

            // Parse price and insert into database
            double busPrice = Double.parseDouble(price);
            String query = "INSERT INTO buses(busId, busLocation, busPrice, busDate, busStatus , busSeats) VALUES(?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, busId);
                pstmt.setString(2, location);
                pstmt.setDouble(3, busPrice);
                pstmt.setString(4, date);
                pstmt.setString(5, status);
                pstmt.setInt(6, totalSeats);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Bus added successfully.");
                    availableBusShowBusData();
                    availableBusReset();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add bus.");
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid price format.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reset the form fields
    public void availableBusReset() {
        availableB_busId.setText("");
        availableB_location.setText("");
        availableB_price.setText("");
        availableB_date.setValue(null);
        availableB_status.getSelectionModel().clearSelection();
        availableB_totalSeats.setText("");
    }

    public void setAvailableBusDelete() {
        Bus bus = availableB_tableView.getSelectionModel().getSelectedItem();
        if (bus == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a bus to delete.");
            return;
        }

        // Show a confirmation alert before deleting the bus
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the bus with ID " + bus.getBusId() + "?");

        // Wait for user's confirmation
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user confirmed, proceed with deletion
            try {
                String query = "DELETE FROM buses WHERE busId = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, bus.getBusId());
                    int dbResult = pstmt.executeUpdate();
                    if (dbResult > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Bus deleted successfully.");
                        availableBusShowBusData();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete bus.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // If the user canceled, show a cancellation message
            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Bus deletion cancelled.");
        }
    }

    // Update bus data in the database
public void availableBusUpdate() {
    String busId = availableB_busId.getText();
    String location = availableB_location.getText();
    String price = availableB_price.getText();
    String date = availableB_date.getValue() != null ? availableB_date.getValue().toString() : "";
    String status = availableB_status.getSelectionModel().getSelectedItem();
    int totalSeats = Integer.parseInt(availableB_totalSeats.getText());

    if(busId.isEmpty() && location.isEmpty() && price.isEmpty() && date.isEmpty() && status == null && totalSeats == 0) {
        showAlert(Alert.AlertType.WARNING, "Validation Error", "Please Select a Bus.");
        return;

    }
    else if (busId.isEmpty() || location.isEmpty() || price.isEmpty() || date.isEmpty() || status == null) {
        showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
        return;
    }

    try {
        // Parse price and update the database
        double busPrice = Double.parseDouble(price);
        String query = "UPDATE buses SET busLocation = ?, busPrice = ?, busDate = ?, busStatus = ?, busSeats = ? WHERE busId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, location);
            pstmt.setDouble(2, busPrice);
            pstmt.setString(3, date);
            pstmt.setString(4, status);

            pstmt.setInt(5, totalSeats);
            pstmt.setString(6, busId);


            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Bus updated successfully.");
                availableBusShowBusData();
                availableBusReset();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update bus.");
            }
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid price format.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void availableBusSearch() {
        // search by any field in the table
        String search = availableB_search.getText();


        String query = "SELECT * FROM buses WHERE busId LIKE ? OR busLocation LIKE ? OR busPrice LIKE ? OR busDate LIKE ? OR busStatus LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + search + "%");
            pstmt.setString(2, "%" + search + "%");
            pstmt.setString(3, "%" + search + "%");
            pstmt.setString(4, "%" + search + "%");
            pstmt.setString(5, "%" + search + "%");

            ResultSet rs = pstmt.executeQuery();
            availableBusListData.clear();
            while (rs.next()) {
                availableBusListData.add(new Bus(
                        rs.getInt("busId"),
                        rs.getString("busStatus"),
                        rs.getString("busLocation"),
                        rs.getDate("busDate"),
                        rs.getDouble("busPrice"),
                        rs.getInt("busSeats")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Booking ticket System
    public void BusIdList() {
        String busD = "SELECT * FROM buses WHERE busStatus = 'Available'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(busD)) {
            List<String> busIdList = new ArrayList<>();
            while (rs.next()) {
                busIdList.add(rs.getString("busId"));
            }
            ObservableList<String> busId = FXCollections.observableArrayList(busIdList);
            bookingTicket_busId.setItems(busId);

            // Get the total number of seats available for the selected bus
            bookingTicket_busId.setOnAction(event -> {
                String selectedBusId = bookingTicket_busId.getSelectionModel().getSelectedItem();
                String query = "SELECT busSeats FROM buses WHERE busId = ?";
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, selectedBusId);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        bookingTicket_availableSeats.setText(String.valueOf(resultSet.getInt("busSeats")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void busLocationList(){
        String locationL = "SELECT * FROM buses WHERE busStatus = 'Available'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(locationL)) {
            List<String> locationList = new ArrayList<>();
            while (rs.next()) {
                locationList.add(rs.getString("busLocation"));
            }
            ObservableList<String> location = FXCollections.observableArrayList(locationList);
            bookingTicket_location.setItems(location);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ticketNumList(){
        List<String> listTicket = new ArrayList<>();
        for (int q = 1; q <= 40; q++){
            listTicket.add(String.valueOf(q));
        }
        ObservableList<String> listT = FXCollections.observableArrayList(listTicket);
        bookingTicket_ticketNum.setItems(listT);
    }


    public void bookingTicketSelect() {
        String firstName = bookingTicket_firstName.getText();
        String lastName = bookingTicket_lastName.getText();
        String phoneNo = bookingTicket_phoneNum.getText();
        String gender = (String) bookingTicket_gender.getSelectionModel().getSelectedItem();
        String date = bookingTicket_date.getValue() != null ? bookingTicket_date.getValue().toString() : "";

        String busId = bookingTicket_busId.getSelectionModel().getSelectedItem();
        String location = bookingTicket_location.getSelectionModel().getSelectedItem();
        String ticketNum = bookingTicket_ticketNum.getSelectionModel().getSelectedItem();


        if (firstName.isEmpty() || lastName.isEmpty() || phoneNo.isEmpty() || gender == null || date.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }
        if (busId == null || location == null || ticketNum == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a bus.");
            return;
        }

        String totalPrice = "SELECT busPrice FROM buses WHERE busLocation = ?";

        double total = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(totalPrice);
            pstmt.setString(1, location);
            ResultSet rs = pstmt.executeQuery();
            total = 0;
            while (rs.next()) {
                total = rs.getDouble("busPrice") * Integer.parseInt(ticketNum);
            }


        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid price format.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        bookingTicket_sci_busId.setText(busId);
        bookingTicket_sci_date.setText(date);
        bookingTicket_sci_firstName.setText(firstName);
        bookingTicket_sci_gender.setText(gender);
        bookingTicket_sci_lastName.setText(lastName);
        bookingTicket_sci_location.setText(location);
        bookingTicket_sci_phoneNum.setText(phoneNo);
        bookingTicket_sci_ticketNum.setText(ticketNum);
        bookingTicket_sci_total.setText(String.valueOf(total));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully Selected");


    }

    private String[] genderList = {"Male" , "Female", "Other"};

    public void genderList(){
        List<String> ListGender = new ArrayList<>();
        for(String data : genderList){
            ListGender.add(data);
        }
        ObservableList listGender = FXCollections.observableArrayList(ListGender);
        bookingTicket_gender.setItems(listGender);
    }

    public void bookingTicketReset(){
        bookingTicket_firstName.setText("");
        bookingTicket_lastName.setText("");
        bookingTicket_phoneNum.setText("");
        bookingTicket_date.setValue(null);
        bookingTicket_gender.getSelectionModel().clearSelection();
        bookingTicket_busId.getSelectionModel().clearSelection();
        bookingTicket_location.getSelectionModel().clearSelection();
        bookingTicket_ticketNum.getSelectionModel().clearSelection();

    }

    public void bookingTicketSelectReset(){
        bookingTicket_sci_busId.setText("");
        bookingTicket_sci_date.setText("");
        bookingTicket_sci_firstName.setText("");
        bookingTicket_sci_gender.setText("");
        bookingTicket_sci_lastName.setText("");
        bookingTicket_sci_location.setText("");
        bookingTicket_sci_phoneNum.setText("");
        bookingTicket_sci_ticketNum.setText("");
        bookingTicket_sci_total.setText("");
    }


    private int countRow;
    public void bookingTicketPay() {

        String payData = "INSERT INTO customer(customerID, firstName, lastName , gender , phoneNo, customerDate, location, busId, ticketNo, total) VALUES (?,?,?,?,?,?,?,?,?,?)";


        try {
            Connection conn = DatabaseConnection.getConnection();
            String CountNum = "SELECT COUNT(customerID) FROM customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(CountNum);

            while (rs.next()) {
                countRow = rs.getInt("COUNT(customerID)");
            }

            if (bookingTicket_sci_firstName.getText().isEmpty() || bookingTicket_sci_lastName.getText().isEmpty() || bookingTicket_sci_phoneNum.getText().isEmpty() || bookingTicket_sci_gender.getText().isEmpty() || bookingTicket_sci_date.getText().isEmpty() || bookingTicket_sci_location.getText().isEmpty() || bookingTicket_sci_busId.getText().isEmpty() || bookingTicket_sci_ticketNum.getText().isEmpty() || bookingTicket_sci_total.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
                return;
            }

            PreparedStatement pstmt = conn.prepareStatement(payData);
            pstmt.setString(1, String.valueOf(countRow));
            pstmt.setString(2, bookingTicket_sci_firstName.getText());
            pstmt.setString(3, bookingTicket_sci_lastName.getText());
            pstmt.setString(4, bookingTicket_sci_gender.getText());
            pstmt.setString(5, bookingTicket_sci_phoneNum.getText());
            pstmt.setString(6, bookingTicket_sci_date.getText());
            pstmt.setString(7, bookingTicket_sci_location.getText());
            pstmt.setString(8, bookingTicket_sci_busId.getText());
            pstmt.setString(9, bookingTicket_sci_ticketNum.getText());
            pstmt.setString(10, bookingTicket_sci_total.getText());


            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment Successful.");
                bookingTicketReset();
                bookingTicketSelectReset();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to pay.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

        // Initialize ComboBox with status options
    public void comboBoxStatus() {
        ObservableList<String> ListStatus = FXCollections.observableArrayList(statusList);
        availableB_status.setItems(ListStatus);
    }

    // Select bus data from the TableView for updating
    public void availableBSelectBusData() {
        Bus bus = availableB_tableView.getSelectionModel().getSelectedItem();
        if (bus == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a bus to update.");
            return;
        }

        availableB_busId.setText(String.valueOf(bus.getBusId()));
        availableB_location.setText(bus.getBusLocation());
        availableB_price.setText(String.valueOf(bus.getBusPrice()));
        availableB_date.setValue(bus.getBusDate().toLocalDate());  // Fix the date conversion here
        availableB_status.setValue(bus.getBusStatus());
        availableB_totalSeats.setText(String.valueOf(bus.getBusSeats()));
    }

    // Alert method
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Close the application
    public void close() {
        System.exit(0);
    }

    // Minimize the window
    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    // Switch between forms
    public void switchForm(ActionEvent event) {
        dashboard_form.setVisible(false);
        availableB_form.setVisible(false);
        bookingTicket_form.setVisible(false);
        customer_form.setVisible(false);

        if (event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true);
        } else if (event.getSource() == availableB_btn) {
            availableBusShowBusData();
            availableB_form.setVisible(true);
        } else if (event.getSource() == bookingTicket_btn) {

            bookingTicket_form.setVisible(true);
            BusIdList();
            busLocationList();
            ticketNumList();
            genderList();

        } else if (event.getSource() == customers_btn) {
            customer_form.setVisible(true);
        }
    }

    // Logout functionality
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/MainForm.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Bus Ticket Reservation System");
                stage.setScene(new Scene(root));
                stage.show();
                main_form.getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        availableBusShowBusData();
        comboBoxStatus();
        BusIdList();
        busLocationList();
        ticketNumList();
        genderList();
    }
}