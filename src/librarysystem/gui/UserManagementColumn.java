package librarysystem.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import librarysystem.gui.users.UserManagementUIDeleteUsers;
import librarysystem.gui.users.UserManagementUIListUsers;
import librarysystem.models.ListUsersButtonTypes;
import librarysystem.models.ListUsersReturnType;
import librarysystem.models.Members;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static librarysystem.gui.Dashboard.*;

public class UserManagementColumn {
    public static void draw(ObservableList<Node> parentLayout) {
        VBox registerUserColumn = new VBox();
        registerUserColumn.setSpacing(SPACING);
        registerUserColumn.setPadding(COLUMNS);
        registerUserColumn.setStyle("-fx-border-color: black");
        registerUserColumn.setPadding(LABELS_BUTTONS);
        Label registerUserLabel = new Label(Resources.getString("register_users"));
        registerUserLabel.setPadding(LABELS_BUTTONS);

        Button addUserButton = new Button(Resources.getString("add_user"));
        addUserButton.setPadding(LABELS_BUTTONS);
        addUserButton.prefWidthProperty().bind(registerUserColumn.widthProperty());
        // Set OnClick listeners:
        addUserButton.setOnAction(event -> {
            addUserDialog();
        });

        Button listUsersButton = new Button(Resources.getString("list_users"));
        listUsersButton.setPadding(LABELS_BUTTONS);
        listUsersButton.prefWidthProperty().bind(registerUserColumn.widthProperty());
        listUsersButton.setOnAction(event -> {
            Optional<ListUsersReturnType> result = UserManagementUIListUsers.listUsers().showAndWait();

            result.ifPresent(listUsersReturnType -> {
                if(listUsersReturnType.buttonClicked == ListUsersButtonTypes.DELETE_BUTTON) {
                    UserManagementUIDeleteUsers.deleteUsers(listUsersReturnType);
                }
            });
        });

        ObservableList<Node> registerUserColumnChildren = registerUserColumn.getChildren();
        registerUserColumnChildren.add(registerUserLabel);
        registerUserColumnChildren.add(addUserButton);
        registerUserColumnChildren.add(listUsersButton);
        // Autoresize children according to parent
        for (Node n : registerUserColumnChildren) {
            VBox.setVgrow(n, Priority.ALWAYS);
        }
        registerUserColumn.setAlignment(Pos.CENTER);
        parentLayout.add(registerUserColumn);
    }

    private static void addUserDialog() {
        Dialog<Members> addUserDialog = new Dialog<>();
        addUserDialog.setTitle(Resources.getString("add_user"));
        addUserDialog.setHeaderText(Resources.getString("add_user"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/add_user.png").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert url != null;
        addUserDialog.setGraphic(new ImageView(url.toString()));
        ButtonType saveUserDataButton = new ButtonType(Resources.getString("save"), ButtonBar.ButtonData.OK_DONE);

        TextField firstNameTextField = new TextField();
        TextField lastNameTextField = new TextField();
        TextField emailTextField = new TextField();

        ObservableList<String> paymentMethods = FXCollections.observableArrayList("MPESA", "CASH");
        ListView<String> paymentMethodListView = new ListView<String>(paymentMethods);

        // This sets the initial height of the ListView:
        paymentMethodListView.setPrefHeight(paymentMethods.size() * ROW_HEIGHT + 2);

        Label firstNameLabel = new Label(Resources.getString("first_name"));
        Label lastNameLabel = new Label(Resources.getString("last_name"));
        Label emailLabel = new Label(Resources.getString("email"));
        Label paymentMethodLabel = new Label(Resources.getString("payment_method"));

        GridPane parentGridLayout = new GridPane();
        parentGridLayout.setHgap(SPACING);
        parentGridLayout.setVgap(SPACING);
        parentGridLayout.setPadding(LABELS_BUTTONS);

        parentGridLayout.add(firstNameLabel, 0, 0);
        parentGridLayout.add(firstNameTextField, 1, 0);

        parentGridLayout.add(lastNameLabel, 0, 1);
        parentGridLayout.add(lastNameTextField, 1, 1);

        parentGridLayout.add(emailLabel, 0, 2);
        parentGridLayout.add(emailTextField, 1, 2);

        parentGridLayout.add(paymentMethodLabel, 0, 3);
        parentGridLayout.add(paymentMethodListView, 1, 3);

        addUserDialog.getDialogPane().getButtonTypes().addAll(saveUserDataButton, ButtonType.CANCEL);

        // Disable Login Button until the user fills in all necessary fields
        Node saveButton = addUserDialog.getDialogPane().lookupButton(saveUserDataButton);
        saveButton.setDisable(true);

        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        // Display dialog box as long as email is invalid
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!isEmailValid(emailTextField.getText())) {
                alertUser(Resources.getString("email") + " " + Resources.getString("is_invalid"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            } else if (firstNameTextField.getText().trim().isEmpty() ||
                    lastNameTextField.getText().trim().isEmpty() || emailTextField.getText().trim().isEmpty()) {
                alertUser(Resources.getString("did_not_fill_in_fields"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            }

        });

        addUserDialog.getDialogPane().setContent(parentGridLayout);

        addUserDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveUserDataButton) {

                Members members = new Members();
                members.email = emailTextField.getText().trim();
                members.firstName = firstNameTextField.getText().trim();
                members.lastName = lastNameTextField.getText().trim();
                members.paymentMethod = paymentMethodListView.getSelectionModel().getSelectedItem();
                return members;
            }
            return null;
        });

        Optional<Members> result = addUserDialog.showAndWait();

        result.ifPresent(members -> {

            if (members.save()) {
                Log.log("Saved new member: " + members.email + ": " + members.firstName);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("user_saved_successfully"),
                        ButtonType.OK);
                alert.setHeaderText(null);
                alert.setTitle(Resources.getString("success"));
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK) {
                    alert.hide();
                }

            } else {
                alertUser(Resources.getString("user_could_not_be_added"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
            }
        });
    }

}