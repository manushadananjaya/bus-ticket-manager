package manusha.busticket.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;  // Correct import

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController {
    @FXML
    private AnchorPane main_form;

    @FXML
    private Button availableB_addBtn;

    @FXML
    private Button availableB_btn;

    @FXML
    private TableColumn<String, String> availableB_col_busId;

    @FXML
    private TableColumn<String, String> availableB_col_date;

    @FXML
    private TableColumn<String, String> availableB_col_location;

    @FXML
    private TableColumn<String, String> availableB_col_price;

    @FXML
    private TableColumn<String, String> availableB_col_type;

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

    // Method to close the application
    public void close() {
        System.exit(0);  // Correctly calling System.exit(0) directly
    }

    // Method to minimize the window
    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();  // Correct syntax for getting the Stage
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    // Method to switch between forms
    public void switchForm(ActionEvent event) {
        if(event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true);
            availableB_form.setVisible(false);
            bookingTicket_form.setVisible(false);
            customer_form.setVisible(false);
        } else if(event.getSource() == availableB_btn) {
            dashboard_form.setVisible(false);
            availableB_form.setVisible(true);
            bookingTicket_form.setVisible(false);
            customer_form.setVisible(false);
        } else if(event.getSource() == bookingTicket_btn) {
            dashboard_form.setVisible(false);
            availableB_form.setVisible(false);
            bookingTicket_form.setVisible(true);
            customer_form.setVisible(false);
        } else if(event.getSource() == customers_btn) {
            dashboard_form.setVisible(false);
            availableB_form.setVisible(false);
            bookingTicket_form.setVisible(false);
            customer_form.setVisible(true);
        }
    }

    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        // Show the alert and wait for the user response
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);  // Defaulting to CANCEL if no button is pressed

        // If the user presses OK, proceed with logout
        if (result == ButtonType.OK) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/MainForm.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Bus Ticket Reservation System");
                stage.setScene(new Scene(root));
                stage.show();
                main_form.getScene().getWindow().hide();  // Hide the current window
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization logic for the controller
//        availableB_status.getItems().addAll("Active", "Inactive");
//        bookingTicket_gender.getItems().addAll("Male", "Female", "Other");
//        bookingTicket_location.getItems().addAll("Location1", "Location2", "Location3");
//        bookingTicket_busId.getItems().addAll("Bus1", "Bus2", "Bus3");
    }
}