package librarysystem.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import librarysystem.models.Members;
import librarysystem.models.Model;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
            listUsers();
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
        Node saveButton = (Node) addUserDialog.getDialogPane().lookupButton(saveUserDataButton);
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
        ((Button) saveButton).addEventFilter(ActionEvent.ACTION, event -> {
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
                    return;
                }

            } else {
                alertUser(Resources.getString("user_could_not_be_added"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
            }
        });
    }

    private static void listUsers() {
        ID = new ArrayList<>();
        ScrollPane scrollbar = new ScrollPane();
        GridPane usersGridLayout = new GridPane();

        VBox IDsListViewAndLabel = new VBox();
        VBox firstNamesListViewAndLabel = new VBox();
        VBox lastNamesListViewAndLabel = new VBox();
        VBox emailsListViewAndLabel = new VBox();
        VBox paymentMethodsListViewAndLabel = new VBox();

        IDsListViewAndLabel.setSpacing(SPACING);
        firstNamesListViewAndLabel.setSpacing(SPACING);
        lastNamesListViewAndLabel.setSpacing(SPACING);
        emailsListViewAndLabel.setSpacing(SPACING);
        paymentMethodsListViewAndLabel.setSpacing(SPACING);

        ListView<String> IDsListView = new ListView<>();
        ListView<String> firstNamesListView = new ListView<>();
        ListView<String> lastNamesListView = new ListView<>();
        ListView<String> emailsListView = new ListView<>();
        ListView<String> paymentMethodsListView = new ListView<>();

        Label IDsListViewLabel = new Label(Resources.getString("user_id"));
        Label firstNamesListViewLabel = new Label(Resources.getString("first_name"));
        Label lastNamesListViewLabel = new Label(Resources.getString("last_name"));
        Label emailsListViewLabel = new Label(Resources.getString("email"));
        Label paymentMethodsListViewLabel = new Label(Resources.getString("payment_method"));

        IDsListView.setOrientation(Orientation.VERTICAL);
        firstNamesListView.setOrientation(Orientation.VERTICAL);
        lastNamesListView.setOrientation(Orientation.VERTICAL);
        emailsListView.setOrientation(Orientation.VERTICAL);
        paymentMethodsListView.setOrientation(Orientation.VERTICAL);

        Members members = new Members();
        ArrayList<Model> allUsers = members.all();

        ArrayList<String> IDsList = new ArrayList<>();
        ArrayList<String> firstNamesList = new ArrayList<>();
        ArrayList<String> lastNamesList = new ArrayList<>();
        ArrayList<String> emailsList = new ArrayList<>();
        ArrayList<String> paymentMethodsList = new ArrayList<>();

        for (Model user : allUsers) {
            Members member = (Members) user;
            IDsList.add(member.ID);
            firstNamesList.add(member.firstName);
            lastNamesList.add(member.lastName);
            emailsList.add(member.email);
            paymentMethodsList.add(member.paymentMethod);
        }

        ObservableList<String> IDsListViewItems = FXCollections.observableArrayList(IDsList);
        ObservableList<String> firstNamesListViewItems = FXCollections.observableArrayList(firstNamesList);
        ObservableList<String> lastNamesListViewItems = FXCollections.observableArrayList(lastNamesList);
        ObservableList<String> emailsListViewItems = FXCollections.observableArrayList(emailsList);
        ObservableList<String> paymentMethodsListViewItems = FXCollections.observableArrayList(paymentMethodsList);

        IDsListView.setItems(IDsListViewItems);
        firstNamesListView.setItems(firstNamesListViewItems);
        lastNamesListView.setItems(lastNamesListViewItems);
        emailsListView.setItems(emailsListViewItems);
        paymentMethodsListView.setItems(paymentMethodsListViewItems);

        // This sets the initial height of the ListView:
        IDsListView.setPrefHeight(IDsListViewItems.size() * ROW_HEIGHT + 2);
        firstNamesListView.setPrefHeight(firstNamesListViewItems.size() * ROW_HEIGHT + 2);
        lastNamesListView.setPrefHeight(lastNamesListViewItems.size() * ROW_HEIGHT + 2);
        emailsListView.setPrefHeight(emailsListViewItems.size() * ROW_HEIGHT + 2);
        paymentMethodsListView.setPrefHeight(paymentMethodsListViewItems.size() * ROW_HEIGHT + 2);

        IDsListViewAndLabel.getChildren().addAll(IDsListViewLabel, IDsListView);
        firstNamesListViewAndLabel.getChildren().addAll(firstNamesListViewLabel, firstNamesListView);
        lastNamesListViewAndLabel.getChildren().addAll(lastNamesListViewLabel, lastNamesListView);
        emailsListViewAndLabel.getChildren().addAll(emailsListViewLabel, emailsListView);
        paymentMethodsListViewAndLabel.getChildren().addAll(paymentMethodsListViewLabel, paymentMethodsListView);

        VBox parentLayout = new VBox();
        usersGridLayout.addColumn(0, IDsListViewAndLabel);
        usersGridLayout.addColumn(1, firstNamesListViewAndLabel);
        usersGridLayout.addColumn(2, lastNamesListViewAndLabel);
        usersGridLayout.addColumn(3, emailsListViewAndLabel);
        usersGridLayout.addColumn(4, paymentMethodsListViewAndLabel);
        scrollbar.setPrefHeight(10 * ROW_HEIGHT + 2);
        scrollbar.setContent(usersGridLayout);
        parentLayout.getChildren().addAll(scrollbar);

        // Display the created GUI inside a dialog box
        Dialog<ListView<String>> listUserDialog = new Dialog<>();
        listUserDialog.setTitle(Resources.getString("list_users"));
        listUserDialog.setHeaderText(Resources.getString("list_users"));
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
        paymentMethodsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
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
            Members membersAgain = new Members();
            membersAgain.ID = "";
            ObservableList<String> listOfIDs = clickedList.getSelectionModel().getSelectedItems();
            boolean errorOccured = false;
            if (listOfIDs.size() != 0) {

                for (String theID : listOfIDs) {
                    membersAgain.ID = theID;
                    if (membersAgain.delete()) {
                        Members theMember = (Members) membersAgain.where(membersAgain.ID, "=", theID).get(0);
                        Log.log("Deleted Member " + theMember.email + ": " + theMember.firstName);
                    } else {
                        errorOccured = true;
                    }
                }
            } else if (ID.size() != 0) {
                for (String theID : ID) {
                    membersAgain.ID = theID;
                    ArrayList<Model> membersTemp = membersAgain.where(membersAgain.ID_FIELD, "=", theID);
                    if (membersAgain.delete()) {
                        Members theMember = ((Members) membersTemp.get(0));
                        Log.log("Deleted Member " + theMember.email + ": " + theMember.firstName);
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
