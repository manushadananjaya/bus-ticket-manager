package manusha.busticket.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import manusha.busticket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent; // Import ActionEvent

public class MainFormController {

    @FXML
    private Button btnsignin;

    @FXML
    private TextField txtpword;

    @FXML
    private TextField txtuname;

    @FXML
    private void initialize() {
        // Link the button action to the correct handler method
        btnsignin.setOnAction(this::handleSignIn); // Correctly link the ActionEvent handler
    }

    // Method to handle button click event
    @FXML
    void handleSignIn(ActionEvent event) {
        // Call the private sign-in logic method
        handleSignIn();
    }

    // Method to handle sign-in action logic
    private void handleSignIn() {
        String username = txtuname.getText();
        String password = txtpword.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQL query to check username and password
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // User found, handle successful login
                System.out.println("Login successful!");
            } else {
                // User not found, handle login failure
                System.out.println("Invalid username or password.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions (e.g., show error message to user)
        }
    }
}