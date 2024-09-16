package manusha.busticket.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController {
    @FXML
    private AnchorPane main_form;

    @FXML
    private Button availableB_addBtn;

    @FXML
    private TableView<Bus> availableB_tableView;

    @FXML
    private Button availableB_btn;

    @FXML
    private TextField availableB_busId;

    @FXML
    private TableColumn<Bus, String> availableB_col_busId;

    @FXML
    private TableColumn<Bus, String> availableB_col_date;

    @FXML
    private TableColumn<Bus, String> availableB_col_location;

    @FXML
    private TableColumn<Bus, String> availableB_col_price;

    @FXML
    private TableColumn<Bus, String> availableB_col_type;

    @FXML
    private DatePicker availableB_date;

    @FXML
    private Button availableB_delBtn;

    @FXML
    private AnchorPane availableB_form;

    @FXML
    private TextField availableB_location;

    @FXML
    private TextField availableB_price;

    @FXML
    private Button availableB_resetBtn;

    @FXML
    private TextField availableB_search;

    @FXML
    private ComboBox<String> availableB_status;

    @FXML
    private Button availableB_updateBtn;

    @FXML
    private Button bookingTicket_btn;

    @FXML
    private ComboBox<String> bookingTicket_busId;

    @FXML
    private DatePicker bookingTicket_date;

    @FXML
    private TextField bookingTicket_firstName;

    @FXML
    private AnchorPane bookingTicket_form;

    @FXML
    private ComboBox<String> bookingTicket_gender;

    @FXML
    private TextField bookingTicket_lastName;

    @FXML
    private ComboBox<String> bookingTicket_location;

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
    private Label bookingTicket_sci_type;

    @FXML
    private Button bookingTicket_selectBtn;

    @FXML
    private Button close_btn;

    @FXML
    private TextField cus_search;

    @FXML
    private TableView<String> cus_table;

    @FXML
    private TableColumn<String, String> customer_busId;

    @FXML
    private TableColumn<String, String> customer_customerNum;

    @FXML
    private TableColumn<String, String> customer_date;

    @FXML
    private TableColumn<String, String> customer_firstName;

    @FXML
    private AnchorPane customer_form;

    @FXML
    private TableColumn<String, String> customer_gender;

    @FXML
    private TableColumn<String, String> customer_lastName;

    @FXML
    private TableColumn<String, String> customer_location;

    @FXML
    private TableColumn<String, String> customer_phone;

    @FXML
    private TableColumn<String, String> customer_ticketNum;

    @FXML
    private TableColumn<String, String> customer_type;

    @FXML
    private Button customers_btn;

    @FXML
    private Label dashboard_availableB;

    @FXML
    private Button dashboard_btn;

    @FXML
    private AreaChart<String, Number> dashboard_chart;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private Label dashboard_incomeTodal;

    @FXML
    private Label dashboard_todayIncome;

    @FXML
    private Button logout_btn;

    @FXML
    private Button min_btn;

    // Database Connection
    private DatabaseConnection databaseConnection = new DatabaseConnection();
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Statement statement;

    // Fetch available buses
    public ObservableList<Bus> getAvailableBuses() {
        ObservableList<Bus> buses = FXCollections.observableArrayList();
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM buses");

            while (resultSet.next()) {
                buses.add(new Bus(
                        resultSet.getInt("busId"),
                        resultSet.getString("busType"),
                        resultSet.getString("busLocation"),
                        resultSet.getDate("busDate"),
                        resultSet.getDouble("busPrice")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buses;
    }

    // Show bus data in TableView
    private ObservableList<Bus> availableBusListData;
    public void availableBusShowBusData() {
        availableBusListData = getAvailableBuses();

        availableB_col_busId.setCellValueFactory(new PropertyValueFactory<>("busId"));
        availableB_col_type.setCellValueFactory(new PropertyValueFactory<>("busType"));
        availableB_col_location.setCellValueFactory(new PropertyValueFactory<>("busLocation"));
        availableB_col_date.setCellValueFactory(new PropertyValueFactory<>("busDate"));
        availableB_col_price.setCellValueFactory(new PropertyValueFactory<>("busPrice"));

        availableB_tableView.setItems(availableBusListData);
    }

    // Add a bus
    public void availableBusAdd() {
        String busId = availableB_busId.getText();
        String location = availableB_location.getText();
        String price = availableB_price.getText();
        String date = availableB_date.getValue().toString();

        if (busId.isEmpty() || location.isEmpty() || price.isEmpty() || date.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO buses(busId, busLocation, busPrice, busDate) VALUES(?, ?, ?, ?)");
            preparedStatement.setString(1, busId);
            preparedStatement.setString(2, location);
            preparedStatement.setString(3, price);
            preparedStatement.setString(4, date);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Bus added successfully.");
                availableBusShowBusData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add bus.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void availableBusReset() {
        availableB_busId.setText("");
        availableB_location.setText("");
        availableB_price.setText("");
        availableB_date.setValue(null);
        availableB_status.getSelectionModel().clearSelection();
    }


    // Select a bus for updating
    public void availableBSelectBusData() {
        Bus bus = availableB_tableView.getSelectionModel().getSelectedItem();
        int num = availableB_tableView.getSelectionModel().getSelectedIndex();
        if (num < 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a bus to update.");
            return;
        }

        availableB_busId.setText(String.valueOf(bus.getBusId()));
        availableB_location.setText(bus.getBusLocation());
        availableB_price.setText(String.valueOf(bus.getBusPrice()));
        availableB_date.setValue(LocalDate.parse(bus.getBusDate().toString()));
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
        if (event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true);
            availableB_form.setVisible(false);
            bookingTicket_form.setVisible(false);
            customer_form.setVisible(false);
        } else if (event.getSource() == availableB_btn) {
            dashboard_form.setVisible(false);
            availableB_form.setVisible(true);
            bookingTicket_form.setVisible(false);
            customer_form.setVisible(false);
        } else if (event.getSource() == bookingTicket_btn) {
            dashboard_form.setVisible(false);
            availableB_form.setVisible(false);
            bookingTicket_form.setVisible(true);
            customer_form.setVisible(false);
        } else if (event.getSource() == customers_btn) {
            dashboard_form.setVisible(false);
            availableB_form.setVisible(false);
            bookingTicket_form.setVisible(false);
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
    }
}