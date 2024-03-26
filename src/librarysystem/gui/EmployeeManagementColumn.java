package librarysystem.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import librarysystem.models.Admins;
import librarysystem.models.Employees;
import librarysystem.models.Model;
import librarysystem.models.User;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

import static librarysystem.gui.Dashboard.*;

public class EmployeeManagementColumn {
    public static void draw(ObservableList<Node> parentLayout, ObservableList<Node> horizontalLayout) {
        VBox editUserDetailsColumn = new VBox();
        editUserDetailsColumn.setSpacing(SPACING);
        editUserDetailsColumn.setStyle("-fx-border-color: black");
        editUserDetailsColumn.setPadding(LABELS_BUTTONS);
        Label editUserDetailsLabel = new Label(Resources.getString("edit_your_details"));
        editUserDetailsLabel.setPadding(new Insets(10, 20, 10, 20));

        Admins theAdmins = new Admins();
        Label userIDLabel = new Label(((User) Dashboard.theModel).ID);
        userIDLabel.setVisible(false);

        Button logoutButton = new Button(Resources.getString("logout"));
        logoutButton.setOnAction(event -> {
            Employees employees = new Employees();

            ArrayList<Model> loggedInEmployee = employees.where(employees.ID_FIELD, "=", userIDLabel.getText());
            if (loggedInEmployee.size() >= 1) {
                // Log the employee out:
                Employees theEmployee = (Employees) loggedInEmployee.get(0);
                theEmployee.rememberToken = "NULL";
                theEmployee.update();
                Log.log("Logged out: " + theEmployee.email + ":" + theEmployee.firstName);
            } else {
                Admins admins = new Admins();
                ArrayList<Model> loggedInAdmin = admins.where(admins.ID_FIELD, "=", userIDLabel.getText());
                if (loggedInAdmin.size() >= 1) {
                    // Log the admin out:
                    Admins theEmployee = (Admins) loggedInAdmin.get(0);
                    theEmployee.rememberToken = "NULL";
                    theEmployee.update();
                    Log.log("Logged out: " + theEmployee.email + ":" + theEmployee.firstName);

                }
            }

            if (myStage != null) {
                Login.instantiate(myStage);
            }

        });

        Button editUsernameButton = new Button(Resources.getString("edit_username"));
        editUsernameButton.setPadding(LABELS_BUTTONS);
        editUsernameButton.prefWidthProperty().bind(editUserDetailsColumn.widthProperty());
        editUsernameButton.setOnAction(event -> {
            Employees employees = new Employees();
            ArrayList<Model> loggedInEmployee = employees.where(employees.ID_FIELD, "=", userIDLabel.getText());
            if (loggedInEmployee.size() >= 1) {
                Employees employee = ((Employees) loggedInEmployee.get(0));
                Optional<String> input = inputDialog(Resources.getString("change_username")
                        , Resources.getString("change_username"),
                        "(" + Resources.getString("previous_username") + ": "
                                + employee.username + ")");
                if (input.isPresent()) {
                    if (input.get().trim().isEmpty()) {
                        alertUser(Resources.getString("did_not_fill_in_fields"),
                                Alert.AlertType.ERROR, Resources.getString("error"));
                        return;
                    }
                    employee.username = input.get();
                    if (employee.update()) {
                        alertUser(Resources.getString("username_saved_successfully"),
                                Alert.AlertType.INFORMATION, Resources.getString("success"));
                    } else {
                        alertUser(Resources.getString("username_could_not_be_saved"),
                                Alert.AlertType.ERROR, Resources.getString("error"));
                    }
                }
            } else {
                Admins admins = new Admins();
                ArrayList<Model> loggedInAdmin = admins.where(admins.ID_FIELD, "=", userIDLabel.getText());
                if (loggedInAdmin.size() >= 1) {
                    Admins admin = ((Admins) loggedInAdmin.get(0));
                    Optional<String> input = inputDialog(Resources.getString("change_username")
                            , Resources.getString("change_username"),
                            "(" + Resources.getString("previous_username") + ": " + admin.username + ")");
                    if (input.isPresent()) {
                        if (input.get().trim().isEmpty()) {
                            alertUser(Resources.getString("did_not_fill_in_fields"),
                                    Alert.AlertType.ERROR, Resources.getString("error"));
                            return;
                        }
                        admin.username = input.get();
                        if (admin.update()) {
                            alertUser(Resources.getString("username_saved_successfully"),
                                    Alert.AlertType.INFORMATION, Resources.getString("success"));
                        } else {
                            alertUser(Resources.getString("username_could_not_be_saved"),
                                    Alert.AlertType.ERROR, Resources.getString("error"));
                        }
                    }
                }
            }
        });

        Button editPasswordButton = new Button(Resources.getString("edit_password"));
        editPasswordButton.setPadding(LABELS_BUTTONS);
        editPasswordButton.prefWidthProperty().bind(editUserDetailsColumn.widthProperty());
        editPasswordButton.setOnAction(event -> {
            Employees employees = new Employees();
            ArrayList<Model> loggedInEmployee = employees.where(employees.ID_FIELD, "=", userIDLabel.getText());
            if (loggedInEmployee.size() >= 1) {
                Employees employee = ((Employees) loggedInEmployee.get(0));
                Optional<String> input = inputDialog(Resources.getString("change_password")
                        , Resources.getString("change_password"),
                        "(" + Resources.getString("previous_password") + ":"
                                + employee.password + ")");
                if (input.isPresent()) {
                    if (input.get().trim().isEmpty()) {
                        alertUser(Resources.getString("did_not_fill_in_fields"),
                                Alert.AlertType.ERROR, Resources.getString("error"));
                        return;
                    }
                    employee.password = input.get();
                    if (employee.update()) {
                        alertUser(Resources.getString("password_saved_successfully"),
                                Alert.AlertType.INFORMATION, Resources.getString("success"));
                    } else {
                        alertUser(Resources.getString("password_could_not_be_saved"),
                                Alert.AlertType.ERROR, Resources.getString("error"));
                    }
                }
            } else {
                Admins admins = new Admins();
                ArrayList<Model> loggedInAdmin = admins.where(admins.ID_FIELD, "=", userIDLabel.getText());
                if (loggedInAdmin.size() >= 1) {
                    Admins admin = ((Admins) loggedInAdmin.get(0));
                    Optional<String> input = inputDialog(Resources.getString("change_password")
                            , Resources.getString("change_password"),
                            "(" + Resources.getString("previous_password") + ":" + admin.password + ")");
                    if (input.isPresent()) {
                        if (input.get().trim().isEmpty()) {
                            alertUser(Resources.getString("did_not_fill_in_fields"),
                                    Alert.AlertType.ERROR, Resources.getString("error"));
                            return;
                        }
                        admin.password = input.get();
                        if (admin.update()) {
                            alertUser(Resources.getString("password_saved_successfully"),
                                    Alert.AlertType.INFORMATION, Resources.getString("success"));
                        } else {
                            alertUser(Resources.getString("password_could_not_be_saved"),
                                    Alert.AlertType.ERROR, Resources.getString("error"));
                        }
                    }
                }
            }
        });

        Button listEmployeesButton = new Button(Resources.getString("list_employees"));
        listEmployeesButton.setPadding(LABELS_BUTTONS);
        listEmployeesButton.prefWidthProperty().bind(editUserDetailsColumn.widthProperty());
        listEmployeesButton.setOnAction(event -> {
            listEmployees();
        });
        Button addEmployeesButton = new Button(Resources.getString("add_employees"));
        addEmployeesButton.setPadding(LABELS_BUTTONS);
        addEmployeesButton.prefWidthProperty().bind(editUserDetailsColumn.widthProperty());
        addEmployeesButton.setOnAction(event -> {
            addEmployee();
        });
        ObservableList<Node> editUserDetilsColumnChildren = editUserDetailsColumn.getChildren();
        editUserDetilsColumnChildren.add(editUserDetailsLabel);
        editUserDetilsColumnChildren.add(editUsernameButton);
        editUserDetilsColumnChildren.add(editPasswordButton);

        // Display the list employees button if the logged-in user is an admin:
        if (theAdmins.where(theAdmins.ID_FIELD, "=", userIDLabel.getText()).size() >= 1) {
            editUserDetilsColumnChildren.add(listEmployeesButton);
            editUserDetilsColumnChildren.add(addEmployeesButton);
        }

        // Autoresize children according to parent
        for (Node n : editUserDetilsColumnChildren) {
            VBox.setVgrow(n, Priority.ALWAYS);
        }
        editUserDetailsColumn.setAlignment(Pos.CENTER);
        parentLayout.add(editUserDetailsColumn);
        horizontalLayout.addAll(userIDLabel);
        horizontalLayout.addAll(logoutButton);
    }

    private static void addEmployee() {
        Dialog<Employees> addUserDialog = new Dialog<>();
        addUserDialog.setTitle(Resources.getString("add_employees"));
        addUserDialog.setHeaderText(Resources.getString("add_employees"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/add_user.png").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        addUserDialog.setGraphic(new ImageView(url.toString()));
        ButtonType saveUserDataButton = new ButtonType(Resources.getString("save"), ButtonBar.ButtonData.OK_DONE);

        TextField firstNameTextField = new TextField();
        TextField lastNameTextField = new TextField();
        TextField emailTextField = new TextField();
        TextField usernameTextField = new TextField();
        PasswordField passwordTextField = new PasswordField();
        TextField plainPasswordTextField = new TextField();
        CheckBox unmaskPasswordCheckBox = new CheckBox(Resources.getString("show") + "/" + Resources.getString("hide")
                + " " + Resources.getString("password"));

        // Bind properties. Toggle textField and passwordField
        // visibility and managability properties mutually when checkbox's state is
        // changed.
        // Because we want to display only one component (textField or passwordField)
        // on the scene at a time.
        plainPasswordTextField.managedProperty().bind(unmaskPasswordCheckBox.selectedProperty());
        plainPasswordTextField.visibleProperty().bind(unmaskPasswordCheckBox.selectedProperty());

        passwordTextField.managedProperty().bind(unmaskPasswordCheckBox.selectedProperty().not());
        passwordTextField.visibleProperty().bind(unmaskPasswordCheckBox.selectedProperty().not());

        // Bind the textField and passwordField text values bidirectionally.
        plainPasswordTextField.textProperty().bindBidirectional(passwordTextField.textProperty());

        Label firstNameLabel = new Label(Resources.getString("first_name"));
        Label lastNameLabel = new Label(Resources.getString("last_name"));
        Label emailLabel = new Label(Resources.getString("email"));
        Label usernameLabel = new Label(Resources.getString("username"));
        Label passwordLabel = new Label(Resources.getString("password"));

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

        parentGridLayout.add(usernameLabel, 0, 3);
        parentGridLayout.add(usernameTextField, 1, 3);

        parentGridLayout.add(passwordLabel, 0, 4);
        parentGridLayout.add(passwordTextField, 1, 4);
        parentGridLayout.add(plainPasswordTextField, 1, 4);

        parentGridLayout.add(unmaskPasswordCheckBox, 1, 5);

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
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        // Display dialog box as long as email is invalid
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!isEmailValid(emailTextField.getText())) {
                alertUser(Resources.getString("email") + " " + Resources.getString("is_invalid"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            } else if (firstNameTextField.getText().trim().isEmpty() ||
                    lastNameTextField.getText().trim().isEmpty()
                    || emailTextField.getText().trim().isEmpty() ||
                    usernameTextField.getText().trim().isEmpty() || passwordTextField.getText().trim().isEmpty()) {
                alertUser(Resources.getString("did_not_fill_in_fields"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            }

        });

        addUserDialog.getDialogPane().setContent(parentGridLayout);

        addUserDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveUserDataButton) {

                Employees members = new Employees();
                members.email = emailTextField.getText().trim();
                members.firstName = firstNameTextField.getText().trim();
                members.lastName = lastNameTextField.getText().trim();
                members.username = usernameTextField.getText().trim();
                members.password = passwordTextField.getText().trim();
                return members;
            }
            return null;
        });

        Optional<Employees> result = addUserDialog.showAndWait();

        result.ifPresent(members -> {

            if (members.save()) {
                Log.log("Saved new Employee: " + members.email + ": " + members.firstName);
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

    private static void listEmployees() {
        ArrayList<String> ID = new ArrayList<>();
        ScrollPane scrollbar = new ScrollPane();
        GridPane usersGridLayout = new GridPane();

        VBox IDsListViewAndLabel = new VBox();
        VBox firstNamesListViewAndLabel = new VBox();
        VBox lastNamesListViewAndLabel = new VBox();
        VBox emailsListViewAndLabel = new VBox();
        VBox usernamesListViewAndLabel = new VBox();

        IDsListViewAndLabel.setSpacing(SPACING);
        firstNamesListViewAndLabel.setSpacing(SPACING);
        lastNamesListViewAndLabel.setSpacing(SPACING);
        emailsListViewAndLabel.setSpacing(SPACING);
        usernamesListViewAndLabel.setSpacing(SPACING);

        ListView<String> IDsListView = new ListView<>();
        ListView<String> firstNamesListView = new ListView<>();
        ListView<String> lastNamesListView = new ListView<>();
        ListView<String> emailsListView = new ListView<>();
        ListView<String> usernamesListView = new ListView<>();

        Label IDsListViewLabel = new Label(Resources.getString("user_id"));
        Label firstNamesListViewLabel = new Label(Resources.getString("first_name"));
        Label lastNamesListViewLabel = new Label(Resources.getString("last_name"));
        Label emailsListViewLabel = new Label(Resources.getString("email"));
        Label usernamesListViewLabel = new Label(Resources.getString("username"));

        IDsListView.setOrientation(Orientation.VERTICAL);
        firstNamesListView.setOrientation(Orientation.VERTICAL);
        lastNamesListView.setOrientation(Orientation.VERTICAL);
        emailsListView.setOrientation(Orientation.VERTICAL);
        usernamesListView.setOrientation(Orientation.VERTICAL);

        Employees members = new Employees();
        ArrayList<Model> allUsers = members.all();

        ArrayList<String> IDsList = new ArrayList<>();
        ArrayList<String> firstNamesList = new ArrayList<>();
        ArrayList<String> lastNamesList = new ArrayList<>();
        ArrayList<String> emailsList = new ArrayList<>();
        ArrayList<String> usernamesList = new ArrayList<>();

        for (Model user : allUsers) {
            Employees member = (Employees) user;
            IDsList.add(member.ID);
            firstNamesList.add(member.firstName);
            lastNamesList.add(member.lastName);
            emailsList.add(member.email);
            usernamesList.add(member.username);
        }

        ObservableList<String> IDsListViewItems = FXCollections.observableArrayList(IDsList);
        ObservableList<String> firstNamesListViewItems = FXCollections.observableArrayList(firstNamesList);
        ObservableList<String> lastNamesListViewItems = FXCollections.observableArrayList(lastNamesList);
        ObservableList<String> emailsListViewItems = FXCollections.observableArrayList(emailsList);
        ObservableList<String> usernamesListViewItems = FXCollections.observableArrayList(usernamesList);

        IDsListView.setItems(IDsListViewItems);
        firstNamesListView.setItems(firstNamesListViewItems);
        lastNamesListView.setItems(lastNamesListViewItems);
        emailsListView.setItems(emailsListViewItems);
        usernamesListView.setItems(usernamesListViewItems);

        // This sets the initial height of the ListView:
        IDsListView.setPrefHeight(IDsListViewItems.size() * Dashboard.ROW_HEIGHT + 2);
        firstNamesListView.setPrefHeight(firstNamesListViewItems.size() * Dashboard.ROW_HEIGHT + 2);
        lastNamesListView.setPrefHeight(lastNamesListViewItems.size() * Dashboard.ROW_HEIGHT + 2);
        emailsListView.setPrefHeight(emailsListViewItems.size() * Dashboard.ROW_HEIGHT + 2);
        usernamesListView.setPrefHeight(usernamesListViewItems.size() * Dashboard.ROW_HEIGHT + 2);

        IDsListViewAndLabel.getChildren().addAll(IDsListViewLabel, IDsListView);
        firstNamesListViewAndLabel.getChildren().addAll(firstNamesListViewLabel, firstNamesListView);
        lastNamesListViewAndLabel.getChildren().addAll(lastNamesListViewLabel, lastNamesListView);
        emailsListViewAndLabel.getChildren().addAll(emailsListViewLabel, emailsListView);
        usernamesListViewAndLabel.getChildren().addAll(usernamesListViewLabel, usernamesListView);

        VBox parentLayout = new VBox();
        usersGridLayout.addColumn(0, IDsListViewAndLabel);
        usersGridLayout.addColumn(1, firstNamesListViewAndLabel);
        usersGridLayout.addColumn(2, lastNamesListViewAndLabel);
        usersGridLayout.addColumn(3, emailsListViewAndLabel);
        usersGridLayout.addColumn(4, usernamesListViewAndLabel);
        scrollbar.setPrefHeight(10 * Dashboard.ROW_HEIGHT + 2);
        scrollbar.setContent(usersGridLayout);
        parentLayout.getChildren().addAll(scrollbar);

        // Display the created GUI inside a dialog box
        Dialog<ListView<String>> listUserDialog = new Dialog<>();
        listUserDialog.setTitle(Resources.getString("list_employees"));
        listUserDialog.setHeaderText(Resources.getString("list_employees"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/list_user.jpg").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        listUserDialog.setGraphic(new ImageView(url.toString()));

        // Multiple select in a list view:
        IDsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ButtonType deleteUserButton = new ButtonType(Resources.getString("delete_user"), ButtonBar.ButtonData.OK_DONE);

        // Dialog functionality
        listUserDialog.getDialogPane().getButtonTypes().addAll(deleteUserButton, ButtonType.CANCEL);

        // Store corresponding IDs to an ID variable
        firstNamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            ID.add(IDsListView.getItems().get(newIndex.intValue()));
        });
        lastNamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            ID.add(IDsListView.getItems().get(newIndex.intValue()));
        });
        emailsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            ID.add(IDsListView.getItems().get(newIndex.intValue()));
        });
        usernamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            ID.add(IDsListView.getItems().get(newIndex.intValue()));
        });

        listUserDialog.getDialogPane().setContent(parentLayout);

        listUserDialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteUserButton) {
                return IDsListView;
            }
            return null;
        });

        // Prevent the delete button from executing if a user has no fields clicked:
        Button button = (Button) listUserDialog.getDialogPane().lookupButton(deleteUserButton);
        button.addEventFilter(ActionEvent.ACTION, event -> {
            if (ID.size() == 0 && IDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
                // Tell the user to select at least a single cell.
                alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            }
        });

        Optional<ListView<String>> result = listUserDialog.showAndWait();

        result.ifPresent(clickedList -> {
            Employees membersAgain = new Employees();
            membersAgain.ID = "";
            ObservableList<String> listOfIDs = clickedList.getSelectionModel().getSelectedItems();
            boolean errorOccured = false;
            System.out.println(ID.size());
            if (listOfIDs.size() != 0) {

                for (String theID : listOfIDs) {
                    membersAgain.ID = theID;
                    if (membersAgain.delete()) {
                        Employees theMember = (Employees) membersAgain.where(membersAgain.ID, "=", theID).get(0);
                        Log.log("Deleted Employee " + theMember.email + ": " + theMember.firstName);
                    } else {
                        errorOccured = true;
                    }
                }
            } else if (ID.size() != 0) {
                for (String theID : ID) {
                    membersAgain.ID = theID;
                    ArrayList<Model> membersTemp = membersAgain.where(membersAgain.ID_FIELD, "=", theID);
                    if (membersAgain.delete()) {
                        Employees theMember = ((Employees) membersTemp.get(0));
                        Log.log("Deleted Employee " + theMember.email + ": " + theMember.firstName);
                    } else {
                        errorOccured = true;
                    }

                }
            } else {
                // Do nothing
                errorOccured = true;
            }

            Alert alert;

            if (errorOccured) {
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("user_could_not_be_deleted"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("user_deleted_successfully"),
                        ButtonType.OK);
                alert.setTitle(Resources.getString("success"));
            }
            alert.setHeaderText(null);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                alert.hide();
            }
        });
    }
}