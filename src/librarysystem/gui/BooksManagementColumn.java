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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import librarysystem.models.*;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;

import static librarysystem.gui.Dashboard.*;

public class BooksManagementColumn {
    public static void draw(ObservableList<Node> parentLayout) {
        VBox borrowBookColumn = new VBox();
        borrowBookColumn.setSpacing(SPACING);
        borrowBookColumn.setStyle("-fx-border-color: black");
        borrowBookColumn.setPadding(LABELS_BUTTONS);
        Label borrowBookLabel = new Label(Resources.getString("books"));
        borrowBookLabel.setPadding(LABELS_BUTTONS);
        Button borrowBookButton = new Button(Resources.getString("borrow_book"));
        borrowBookButton.setPadding(LABELS_BUTTONS);
        borrowBookButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
        borrowBookButton.setOnAction(event -> {
            borrowBook();
        });

        Button returnButton = new Button(Resources.getString("return_book"));
        returnButton.setPadding(LABELS_BUTTONS);
        returnButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
        returnButton.setOnAction(event -> {
            returnBook();
        });

        Button addBookButton = new Button(Resources.getString("add_book"));
        addBookButton.setPadding(LABELS_BUTTONS);
        addBookButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
        addBookButton.setOnAction(event -> {
            addBook();
        });

        Button listBooksButton = new Button(Resources.getString("list_books"));
        listBooksButton.setPadding(LABELS_BUTTONS);
        listBooksButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
        listBooksButton.setOnAction(event -> {
            listBooks();
        });

        ObservableList<Node> borrowBookColumnChildren = borrowBookColumn.getChildren();
        borrowBookColumnChildren.add(borrowBookLabel);
        borrowBookColumnChildren.add(borrowBookButton);
        borrowBookColumnChildren.add(returnButton);
        borrowBookColumnChildren.add(addBookButton);
        borrowBookColumnChildren.add(listBooksButton);

        // Autoresize children according to parent
        for (Node n : borrowBookColumnChildren) {
            VBox.setVgrow(n, Priority.ALWAYS);
        }
        borrowBookColumn.setAlignment(Pos.CENTER);
        parentLayout.add(borrowBookColumn);
    }

    private static void addBook() {
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

    private static void returnBook() {
        if (!displayUserEmailInputForBookReturn()) {
            return;
        }

        GridPane booksListGrid = new GridPane();

        // VBoxes
        VBox bookIDAndLabel = new VBox();
        VBox bookTitleAndLabel = new VBox();
        VBox bookCategoryAndLabel = new VBox();

        // Labels
        Label bookIDLabel = new Label(Resources.getString("book_id"));
        Label bookTitleLabel = new Label(Resources.getString("book_title"));
        Label bookCategoryLabel = new Label(Resources.getString("book_category"));

        // ListViews
        ListView<String> bookIDsListView = new ListView<>();
        ListView<String> bookTitlesListView = new ListView<>();
        ListView<String> bookCategoriesListView = new ListView<>();

        // ArrayLists of data:
        ArrayList<String> bookIDsArrayList = new ArrayList<>();
        ArrayList<String> bookTitlesArrayList = new ArrayList<>();
        ArrayList<String> bookCategoriesArrayList = new ArrayList<>();

        // Populate ArrayLists:
        // Get user borrowed books:
        BorrowedBooks borrowedBooks = new BorrowedBooks();
        ArrayList<Model> userBooks = borrowedBooks.where(borrowedBooks.USER_ID_FIELD, "=", userID);
        if (userBooks.size() <= 0) {
            alertUser(Resources.getString("user_has_no_borrowed_books"), Alert.AlertType.WARNING,
                    Resources.getString("warning"));
            return;
        }
        Books books = new Books();
        Categories categories = new Categories();
        for (Model userBook : userBooks) {
            BorrowedBooks aBook = (BorrowedBooks) userBook;
            ArrayList<Model> allBooks = books.where(books.ID_FIELD, "=", aBook.BookID);
            for (Model book : allBooks) {
                Books theBook = (Books) book;
                bookIDsArrayList.add(theBook.ID);
                bookTitlesArrayList.add(theBook.bookTitle);
                bookCategoriesArrayList.add(((Categories) categories
                        .where(categories.ID_FIELD, "=", theBook.bookCategory).get(0)).categoryName);
            }
        }

        // Observable lists of ListViews:
        ObservableList<String> bookIDsObservableList = FXCollections.observableArrayList(bookIDsArrayList);
        ObservableList<String> bookTitlesObservableList = FXCollections.observableArrayList(bookTitlesArrayList);
        ObservableList<String> bookCategoriesObservableList = FXCollections
                .observableArrayList(bookCategoriesArrayList);

        bookIDsListView.setItems(bookIDsObservableList);
        bookTitlesListView.setItems(bookTitlesObservableList);
        bookCategoriesListView.setItems(bookCategoriesObservableList);

        bookIDAndLabel.getChildren().addAll(bookIDLabel, bookIDsListView);
        bookTitleAndLabel.getChildren().addAll(bookTitleLabel, bookTitlesListView);
        bookCategoryAndLabel.getChildren().addAll(bookCategoryLabel, bookCategoriesListView);

        booksListGrid.addColumn(0, bookIDAndLabel);
        booksListGrid.addColumn(1, bookTitleAndLabel);
        booksListGrid.addColumn(2, bookCategoryAndLabel);

        // This sets the initial height of the ListView:
        bookIDsListView.setPrefHeight(bookIDsObservableList.size() * ROW_HEIGHT + 2);
        bookTitlesListView.setPrefHeight(bookTitlesObservableList.size() * ROW_HEIGHT + 2);
        bookCategoriesListView.setPrefHeight(bookCategoriesObservableList.size() * ROW_HEIGHT + 2);

        // Listeners for the listviews clicks:
        // Store corresponding IDs to an ID variable
        bookTitlesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            bookID = bookIDsListView.getItems().get(newIndex.intValue());
        });
        bookCategoriesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            bookID = bookIDsListView.getItems().get(newIndex.intValue());
        });
        bookIDsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            bookID = bookIDsListView.getItems().get(newIndex.intValue());
        });

        // Dialog buttons:
        ButtonType borrowButton = new ButtonType(Resources.getString("return"), ButtonBar.ButtonData.OK_DONE);

        // The Dialog in which we display our list
        Dialog<ListView<String>> dialog = new Dialog<>();
        dialog.setTitle(Resources.getString("return_book"));
        dialog.setHeaderText(Resources.getString("return_book"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/borrow_book.jpg").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dialog.setGraphic(new ImageView(url.toString()));
        dialog.getDialogPane().getButtonTypes().add(borrowButton);
        dialog.getDialogPane().setContent(booksListGrid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == borrowButton) {
                return bookIDsListView;
            }
            return null;
        });

        // Prevent the borrow button from executing if a user has no fields clicked:
        Button button = (Button) dialog.getDialogPane().lookupButton(borrowButton);
        button.addEventFilter(ActionEvent.ACTION, event -> {
            if (bookID.equals("") && bookIDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
                // Tell the user to select at least a single cell.
                Dashboard.alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            }
        });

        Optional<ListView<String>> result = dialog.showAndWait();

        result.ifPresent(clickedList -> {
            BorrowedBooks borrowedBookss = new BorrowedBooks();
            boolean errorOccured = false;

            ArrayList<Model> theBooks = borrowedBookss.whereAnd(borrowedBookss.BOOK_ID_FIELD, "=", bookID,
                    borrowedBookss.USER_ID_FIELD, "=", userID);

            // Check if borrowed book has surpassed deadline:
            Date currentDate = new Date(System.currentTimeMillis());
            BorrowedBooks theBorrowedBook = (BorrowedBooks) theBooks.get(0);
            if (theBorrowedBook.bookDeadline.compareTo(currentDate) > 0
                    || theBorrowedBook.bookDeadline.compareTo(currentDate) == 0) {
                // Deadline is on or after the current date, so no fine

            } else {
                // User incurs fine
                Dashboard.alertUser(Resources.getString("user_should_pay_fine"), Alert.AlertType.INFORMATION,
                        Resources.getString("warning"));
            }
            if (!theBorrowedBook.delete()) {
                errorOccured = true;
            }
            Alert alert;

            if (errorOccured) {
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("book_could_not_be_returned"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("book_returned_successfully"),
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

    private static void borrowBook() {
        if (!displayUserEmailInput()) {
            return;
        }
        ScrollPane scrollPane = new ScrollPane();
        VBox parentLayout = new VBox();
        GridPane booksListGrid = new GridPane();

        // Search
        HBox search = new HBox();
        search.prefWidthProperty().bind(parentLayout.prefWidthProperty());
        search.setAlignment(Pos.CENTER);
        search.setPadding(Dashboard.COLUMNS);
        Button searchButton = new Button(Resources.getString("search"));
        TextField searchInput = new TextField();
        search.getChildren().addAll(searchInput, searchButton);

        // VBoxes
        VBox bookIDAndLabel = new VBox();
        VBox bookTitleAndLabel = new VBox();
        VBox bookCategoryAndLabel = new VBox();
        VBox bookTotalAndLabel = new VBox();

        // Labels
        Label bookIDLabel = new Label(Resources.getString("book_id"));
        Label bookTitleLabel = new Label(Resources.getString("book_title"));
        Label bookCategoryLabel = new Label(Resources.getString("book_category"));
        Label bookTotalLabel = new Label(Resources.getString("book_total"));

        // ListViews
        ListView<String> bookIDsListView = new ListView<>();
        ListView<String> bookTitlesListView = new ListView<>();
        ListView<String> bookCategoriesListView = new ListView<>();
        ListView<String> bookTotalsListView = new ListView<>();

        // ArrayLists of data:
        ArrayList<String> bookIDsArrayList = new ArrayList<>();
        ArrayList<String> bookTitlesArrayList = new ArrayList<>();
        ArrayList<String> bookCategoriesArrayList = new ArrayList<>();
        ArrayList<String> bookTotalsArrayList = new ArrayList<>();

        // Populate ArrayLists:
        Books books = new Books();
        ArrayList<Model> allBooks = books.all();
        Categories categories = new Categories();
        for (Model book : allBooks) {
            Books theBook = (Books) book;
            bookIDsArrayList.add(theBook.ID);
            bookTitlesArrayList.add(theBook.bookTitle);
            bookCategoriesArrayList.add(((Categories) categories.where(categories.ID_FIELD, "=", theBook.bookCategory)
                    .get(0)).categoryName);
            bookTotalsArrayList.add("" + theBook.bookTotal);
        }
        // Observable lists of ListViews:
        ObservableList<String> bookIDsObservableList = FXCollections.observableArrayList(bookIDsArrayList);
        ObservableList<String> bookTitlesObservableList = FXCollections.observableArrayList(bookTitlesArrayList);
        ObservableList<String> bookCategoriesObservableList = FXCollections
                .observableArrayList(bookCategoriesArrayList);
        ObservableList<String> bookTotalsObservableList = FXCollections.observableArrayList(bookTotalsArrayList);

        bookIDsListView.setItems(bookIDsObservableList);
        bookTitlesListView.setItems(bookTitlesObservableList);
        bookCategoriesListView.setItems(bookCategoriesObservableList);
        bookTotalsListView.setItems(bookTotalsObservableList);

        bookIDAndLabel.getChildren().addAll(bookIDLabel, bookIDsListView);
        bookTitleAndLabel.getChildren().addAll(bookTitleLabel, bookTitlesListView);
        bookCategoryAndLabel.getChildren().addAll(bookCategoryLabel, bookCategoriesListView);
        bookTotalAndLabel.getChildren().addAll(bookTotalLabel, bookTotalsListView);

        booksListGrid.addColumn(0, bookIDAndLabel);
        booksListGrid.addColumn(1, bookTitleAndLabel);
        booksListGrid.addColumn(2, bookCategoryAndLabel);
        booksListGrid.addColumn(3, bookTotalAndLabel);

        scrollPane.setPrefHeight(10 * Dashboard.ROW_HEIGHT + 2);
        scrollPane.setContent(booksListGrid);

        parentLayout.getChildren().addAll(search, scrollPane);

        // This sets the initial height of the ListView:
        bookIDsListView.setPrefHeight(bookIDsObservableList.size() * Dashboard.ROW_HEIGHT + 2);
        bookTitlesListView.setPrefHeight(bookTitlesObservableList.size() * Dashboard.ROW_HEIGHT + 2);
        bookCategoriesListView.setPrefHeight(bookCategoriesObservableList.size() * Dashboard.ROW_HEIGHT + 2);
        bookTotalsListView.setPrefHeight(bookTotalsObservableList.size() * Dashboard.ROW_HEIGHT + 2);

        // Listeners for the listviews clicks:
        // Store corresponding IDs to an ID variable
        bookTitlesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            bookID = bookIDsListView.getItems().get(newIndex.intValue());
        });
        bookCategoriesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            bookID = bookIDsListView.getItems().get(newIndex.intValue());
        });
        bookTotalsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            bookID = bookIDsListView.getItems().get(newIndex.intValue());
        });
        bookIDsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            bookID = bookIDsListView.getItems().get(newIndex.intValue());
        });

        // Dialog buttons:
        ButtonType borrowButton = new ButtonType(Resources.getString("borrow"), ButtonBar.ButtonData.OK_DONE);

        // The Dialog in which we display our list
        Dialog<ListView<String>> dialog = new Dialog<>();
        dialog.setTitle(Resources.getString("borrow_book"));
        dialog.setHeaderText(Resources.getString("borrow_book"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/borrow_book.jpg").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dialog.setGraphic(new ImageView(url.toString()));
        dialog.getDialogPane().setContent(parentLayout);
        dialog.getDialogPane().getButtonTypes().add(borrowButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == borrowButton) {
                return bookIDsListView;
            }
            return null;
        });

        // Prevent the borrow button from executing if a user has no fields clicked:
        Button button = (Button) dialog.getDialogPane().lookupButton(borrowButton);
        button.addEventFilter(ActionEvent.ACTION, event -> {
            if (bookID.equals("") && bookIDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
                // Tell the user to select at least a single cell.
                Dashboard.alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            }
        });

        Optional<ListView<String>> result = dialog.showAndWait();

        result.ifPresent(clickedList -> {
            BorrowedBooks borrowedBooks = new BorrowedBooks();

            // Check if the user already has this book:
            if (borrowedBooks
                    .whereAnd(borrowedBooks.BOOK_ID_FIELD, "=", bookID, borrowedBooks.USER_ID_FIELD, "=", Dashboard.userID)
                    .size() >= 1) {
                Dashboard.alertUser(Resources.getString("user_has_this_book"), Alert.AlertType.ERROR, Resources.getString("error"));
                return;
            }
            boolean errorOccured = false;
            borrowedBooks.BookID = bookID;
            borrowedBooks.UserID = Dashboard.userID;
            // 2 weeks in milliseconds: 1209600000
            borrowedBooks.bookDeadline = new Date(System.currentTimeMillis() + 1209600000);
            if (!borrowedBooks.save()) {
                errorOccured = true;
            }
            Alert alert;

            if (errorOccured) {
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("book_could_not_be_borrowed"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("book_borrowed_successfully") +
                        "\r\n" + Resources.getString("deadline") + ": " + borrowedBooks.bookDeadline,
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

    private static boolean displayUserEmailInput() {
        TextInputDialog userEmailInput = new TextInputDialog();
        userEmailInput.setTitle(Resources.getString("enter_email"));
        userEmailInput.setHeaderText(Resources.getString("enter_email"));
        Optional<String> userInput = userEmailInput.showAndWait();

        if (userInput.isPresent()) {
            Members members = new Members();
            ArrayList<Model> member = members.where(members.EMAIL_FIELD, "=", userInput.get());
            if (member.size() <= 0) {
                Dashboard.alertUser(
                        Resources.getString("email_does_not_exist") + ". "
                                + Resources.getString("try_registering_user"),
                        Alert.AlertType.ERROR, Resources.getString("error"));
                return false;
            }
            String userIDTemp = ((Members) member.get(0)).ID;
            // Check if the user meets minimum requirements (Can only borrow up to 3 books)
            BorrowedBooks books = new BorrowedBooks();
            ArrayList<Model> totalBooks = books.where(books.USER_ID_FIELD, "=", userIDTemp);
            if (totalBooks.size() >= 3) {
                Dashboard.alertUser(Resources.getString("user_has_exceeded_book_borrowing_limit"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                return false;
            }
            Dashboard.userID = ((Members) member.get(0)).ID;
        } else {
            return false;
        }
        return true;
    }

    private static boolean displayUserEmailInputForBookReturn() {
        TextInputDialog userEmailInput = new TextInputDialog();
        userEmailInput.setTitle(Resources.getString("enter_email"));
        userEmailInput.setHeaderText(Resources.getString("enter_email"));
        Optional<String> userInput = userEmailInput.showAndWait();

        if (userInput.isPresent()) {
            Members members = new Members();
            ArrayList<Model> member = members.where(members.EMAIL_FIELD, "=", userInput.get());
            if (member.size() <= 0) {
                Dashboard.alertUser(
                        Resources.getString("email_does_not_exist") + ". "
                                + Resources.getString("try_registering_user"),
                        Alert.AlertType.ERROR, Resources.getString("error"));
                return false;
            }
            Dashboard.userID = ((Members) member.get(0)).ID;
        } else {
            return false;
        }
        return true;
    }

    private static void listBooks() {
        ArrayList<String> ID = new ArrayList<>();
        ScrollPane scrollbar = new ScrollPane();
        GridPane usersGridLayout = new GridPane();

        VBox IDsListViewAndLabel = new VBox();
        VBox bookTitlesListViewAndLabel = new VBox();
        VBox authorNamesListViewAndLabel = new VBox();
        VBox categoriesListViewAndLabel = new VBox();

        IDsListViewAndLabel.setSpacing(SPACING);
        bookTitlesListViewAndLabel.setSpacing(SPACING);
        authorNamesListViewAndLabel.setSpacing(SPACING);
        categoriesListViewAndLabel.setSpacing(SPACING);

        ListView<String> IDsListView = new ListView<>();
        ListView<String> bookTitlesListView = new ListView<>();
        ListView<String> authorNamesListView = new ListView<>();
        ListView<String> categoriesListView = new ListView<>();

        Label IDsListViewLabel = new Label(Resources.getString("book_id"));
        Label bookTitlesListViewLabel = new Label(Resources.getString("book_title"));
        Label authorNamesListViewLabel = new Label(Resources.getString("author_names"));
        Label categoriesListViewLabel = new Label(Resources.getString("book_category"));

        IDsListView.setOrientation(Orientation.VERTICAL);
        bookTitlesListView.setOrientation(Orientation.VERTICAL);
        authorNamesListView.setOrientation(Orientation.VERTICAL);
        categoriesListView.setOrientation(Orientation.VERTICAL);

        Books books = new Books();
        ArrayList<Model> allBooks = books.all();

        ArrayList<String> IDsList = new ArrayList<>();
        ArrayList<String> bookTitlesList = new ArrayList<>();
        ArrayList<String> authorNamesList = new ArrayList<>();
        ArrayList<String> bookCategoriesList = new ArrayList<>();

        for (Model user : allBooks) {
            Books booksAgain = (Books) user;
            IDsList.add(booksAgain.ID);
            bookTitlesList.add(booksAgain.bookTitle);
            authorNamesList.add(booksAgain.bookAuthors);
            bookCategoriesList.add(booksAgain.bookCategory);
        }

        ObservableList<String> IDsListViewItems = FXCollections.observableArrayList(IDsList);
        ObservableList<String> firstNamesListViewItems = FXCollections.observableArrayList(bookTitlesList);
        ObservableList<String> lastNamesListViewItems = FXCollections.observableArrayList(authorNamesList);
        ObservableList<String> emailsListViewItems = FXCollections.observableArrayList(bookCategoriesList);

        IDsListView.setItems(IDsListViewItems);
        bookTitlesListView.setItems(firstNamesListViewItems);
        authorNamesListView.setItems(lastNamesListViewItems);
        categoriesListView.setItems(emailsListViewItems);

        // This sets the initial height of the ListView:
        IDsListView.setPrefHeight(IDsListViewItems.size() * Dashboard.ROW_HEIGHT + 2);
        bookTitlesListView.setPrefHeight(firstNamesListViewItems.size() * Dashboard.ROW_HEIGHT + 2);
        authorNamesListView.setPrefHeight(lastNamesListViewItems.size() * Dashboard.ROW_HEIGHT + 2);
        categoriesListView.setPrefHeight(emailsListViewItems.size() * Dashboard.ROW_HEIGHT + 2);

        IDsListViewAndLabel.getChildren().addAll(IDsListViewLabel, IDsListView);
        bookTitlesListViewAndLabel.getChildren().addAll(bookTitlesListViewLabel, bookTitlesListView);
        authorNamesListViewAndLabel.getChildren().addAll(authorNamesListViewLabel, authorNamesListView);
        categoriesListViewAndLabel.getChildren().addAll(categoriesListViewLabel, categoriesListView);

        VBox parentLayout = new VBox();
        usersGridLayout.addColumn(0, IDsListViewAndLabel);
        usersGridLayout.addColumn(1, bookTitlesListViewAndLabel);
        usersGridLayout.addColumn(2, authorNamesListViewAndLabel);
        usersGridLayout.addColumn(3, categoriesListViewAndLabel);
        scrollbar.setPrefHeight(10 * Dashboard.ROW_HEIGHT + 2);
        scrollbar.setContent(usersGridLayout);
        parentLayout.getChildren().addAll(scrollbar);

        // Display the created GUI inside a dialog box
        Dialog<ListView<String>> listBooksDialog = new Dialog<>();
        listBooksDialog.setTitle(Resources.getString("list_books"));
        listBooksDialog.setHeaderText(Resources.getString("list_books"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/list_books.png").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Node graphic = new ImageView(url.toString());
        graphic.prefHeight(50);
        graphic.prefWidth(50);
        graphic.maxHeight(50);
        graphic.maxWidth(50);
        listBooksDialog.setGraphic(graphic);

        // Multiple select in a list view:
        IDsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ButtonType deleteBookButton = new ButtonType(Resources.getString("delete_book"), ButtonBar.ButtonData.OK_DONE);

        // Dialog functionality
        listBooksDialog.getDialogPane().getButtonTypes().addAll(deleteBookButton, ButtonType.CANCEL);

        // Store corresponding IDs to an ID variable
        bookTitlesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            ID.add(IDsListView.getItems().get(newIndex.intValue()));
        });
        authorNamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            ID.add(IDsListView.getItems().get(newIndex.intValue()));
        });
        categoriesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            ID.add(IDsListView.getItems().get(newIndex.intValue()));
        });

        listBooksDialog.getDialogPane().setContent(parentLayout);

        listBooksDialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteBookButton) {
                return IDsListView;
            }
            return null;
        });

        // Prevent the delete button from executing if a book has no fields clicked:
        Button button = (Button) listBooksDialog.getDialogPane().lookupButton(deleteBookButton);
        button.addEventFilter(ActionEvent.ACTION, event -> {
            if (ID.size() == 0 && IDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
                // Tell the user to select at least a single cell.
                alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
                event.consume();
            }
        });

        Optional<ListView<String>> result = listBooksDialog.showAndWait();

        result.ifPresent(clickedList -> {
            Books booksAgain = new Books();
            booksAgain.ID = "";
            ObservableList<String> listOfIDs = clickedList.getSelectionModel().getSelectedItems();
            boolean errorOccured = false;
            System.out.println(ID.size());
            if (listOfIDs.size() != 0) {

                for (String theID : listOfIDs) {
                    booksAgain.ID = theID;
                    if (booksAgain.delete()) {
                        Books theBook = (Books) booksAgain.where(booksAgain.ID, "=", theID).get(0);
                        Log.log("Deleted Book " + theBook.ID + ": " + theBook.bookTitle);
                    } else {
                        errorOccured = true;
                    }
                }
            } else if (ID.size() != 0) {
                for (String theID : ID) {
                    booksAgain.ID = theID;
                    ArrayList<Model> membersTemp = booksAgain.where(booksAgain.ID_FIELD, "=", theID);
                    if (booksAgain.delete()) {
                        Books theBook = ((Books) membersTemp.get(0));
                        Log.log("Deleted Book " + theBook.ID + ": " + theBook.bookTitle);
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
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("book_could_not_be_deleted"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("book_deleted_successfully"),
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