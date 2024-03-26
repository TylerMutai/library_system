package librarysystem.gui.books;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import librarysystem.models.Books;
import librarysystem.models.Categories;
import librarysystem.models.Model;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

import static librarysystem.gui.Dashboard.*;

public class BooksManagementUIAddBook {
    public static void addBook() {
        Dialog<Books> addBookDialog = new Dialog<>();
        addBookDialog.setTitle(Resources.getString("add_book"));
        addBookDialog.setHeaderText(Resources.getString("multiple_authors"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/borrow_book.png").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert url != null;
        addBookDialog.setGraphic(new ImageView(url.toString()));
        ButtonType saveBookButton = new ButtonType(Resources.getString("save"), ButtonBar.ButtonData.OK_DONE);

        TextField bookTitleTextField = new TextField();
        TextField bookAuthorsTextField = new TextField();
        ListView<String> bookCategoryListView = new ListView<>();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(5 * ROW_HEIGHT + 2);
        scrollPane.setContent(bookCategoryListView);

        Categories categories = new Categories();
        ArrayList<Model> tempCategories = categories.all();
        ArrayList<String> parsedCategories = new ArrayList<>();
        for (Model category : tempCategories) {
            Categories tempCategory = (Categories) category;
            parsedCategories.add(tempCategory.ID + ":" + tempCategory.categoryName);
        }
        ObservableList<String> bookCategories = FXCollections.observableArrayList(parsedCategories);
        bookCategoryListView.setItems(bookCategories);

        Label bookTitleLabel = new Label(Resources.getString("book_title"));
        Label bookCategoryLabel = new Label(Resources.getString("book_category"));
        Label bookAuthorsLabel = new Label(Resources.getString("book_author"));

        GridPane parentGridLayout = new GridPane();
        parentGridLayout.setHgap(SPACING);
        parentGridLayout.setVgap(SPACING);
        parentGridLayout.setPadding(LABELS_BUTTONS);

        parentGridLayout.add(bookTitleLabel, 0, 0);
        parentGridLayout.add(bookTitleTextField, 1, 0);

        parentGridLayout.add(bookAuthorsLabel, 0, 1);
        parentGridLayout.add(bookAuthorsTextField, 1, 1);

        parentGridLayout.add(bookCategoryLabel, 0, 2);
        parentGridLayout.add(scrollPane, 1, 2);

        addBookDialog.getDialogPane().getButtonTypes().addAll(saveBookButton, ButtonType.CANCEL);

        // Disable Login Button until the user fills in all necessary fields
        Node saveButton = addBookDialog.getDialogPane().lookupButton(saveBookButton);
        saveButton.setDisable(true);

        bookTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
        bookAuthorsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        // Display dialog box as long as category isn't selected
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (bookCategoryListView.getSelectionModel().getSelectedItem() == null) {
                alertUser(Resources.getString("book_category_not_selected"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            } else if (bookTitleTextField.getText().trim().isEmpty() || bookAuthorsTextField.getText().trim().isEmpty()) {
                alertUser(Resources.getString("did_not_fill_in_fields"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            }

        });

        addBookDialog.getDialogPane().setContent(parentGridLayout);

        addBookDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBookButton) {

                Books books = new Books();
                String category = bookCategoryListView.getSelectionModel().getSelectedItem();
                books.bookCategory = category.substring(0, category.indexOf(":"));
                books.bookTitle = bookTitleTextField.getText();
                books.bookAuthors = bookAuthorsTextField.getText();
                return books;
            }
            return null;
        });

        Optional<Books> result = addBookDialog.showAndWait();

        result.ifPresent(books -> {

            if (books.save()) {
                Log.log("Saved new Book: " + books.ID + ": " + books.bookTitle);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("book_saved_successfully"),
                        ButtonType.OK);
                alert.setHeaderText(null);
                alert.setTitle(Resources.getString("success"));
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK) {
                    alert.hide();
                }

            } else {
                alertUser(Resources.getString("book_could_not_be_saved"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
            }
        });
    }
}