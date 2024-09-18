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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private TextField availableB_busId, availableB_location, availableB_price, availableB_search, cus_search;

    @FXML
    private DatePicker availableB_date;

    @FXML
    private ComboBox<String> availableB_status;

    @FXML
    private AreaChart<String, Number> dashboard_chart;

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
                        rs.getDouble("busPrice")
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

        availableB_tableView.setItems(availableBusListData);
    }

    // Add a bus to the database
    public void availableBusAdd() {
        String busId = availableB_busId.getText();
        String location = availableB_location.getText();
        String price = availableB_price.getText();
        String date = availableB_date.getValue() != null ? availableB_date.getValue().toString() : "";
        String status = availableB_status.getSelectionModel().getSelectedItem();

        if (busId.isEmpty() || location.isEmpty() || price.isEmpty() || date.isEmpty() || status == null) {
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
            String query = "INSERT INTO buses(busId, busLocation, busPrice, busDate, busStatus) VALUES(?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, busId);
                pstmt.setString(2, location);
                pstmt.setDouble(3, busPrice);
                pstmt.setString(4, date);
                pstmt.setString(5, status);

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
    }
}