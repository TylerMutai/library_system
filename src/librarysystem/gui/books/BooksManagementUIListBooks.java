package librarysystem.gui.books;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import librarysystem.gui.Dashboard;
import librarysystem.models.*;
import librarysystem.resources.Resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import static librarysystem.gui.Dashboard.SPACING;

public class BooksManagementUIListBooks {
    static ObservableList<String> bookIDsListViewItems;
    static ObservableList<String> bookTitlesListViewItems;
    static ObservableList<String> authorNamesListViewItems;
    static ObservableList<String> bookCategoriesListViewItems;


    private static ArrayList<ObservableList<String>> getDataLists(String userID, String searchString) {
        ArrayList<String> IDsList = new ArrayList<>();
        ArrayList<String> bookTitlesList = new ArrayList<>();
        ArrayList<String> authorNamesList = new ArrayList<>();
        ArrayList<String> bookCategoriesList = new ArrayList<>();

        ArrayList<Model> allBooks;
        Books books = new Books();
        if (userID == null) {
            if (!searchString.isEmpty()) {
                allBooks = books.whereOr(books.BOOK_TITLE_FIELD, "LIKE", "%" + searchString + "%",
                        books.BOOK_AUTHORS_FIELD, "LIKE", "%" + searchString + "%");
            } else {
                allBooks = books.all();
            }
        } else {
            BorrowedBooks borrowedBooks = new BorrowedBooks();
            ArrayList<Model> borrowedBooksAll = borrowedBooks.where(borrowedBooks.USER_ID_FIELD, "=", userID);
            StringBuilder ids = new StringBuilder();
            int count = 0;
            for (Model bBook : borrowedBooksAll) {
                BorrowedBooks _bBook = (BorrowedBooks) bBook;
                ids.append(_bBook.BookID);
                if (count < borrowedBooksAll.size() - 1) {
                    ids.append(",");
                }
                count++;
            }
            ids.append(")");
            allBooks = books.where(books.ID_FIELD, "IN (", ids.toString());
        }

        for (Model user : allBooks) {
            Books booksAgain = (Books) user;
            IDsList.add(booksAgain.ID);
            bookTitlesList.add(booksAgain.bookTitle);
            authorNamesList.add(booksAgain.bookAuthors);
            bookCategoriesList.add(booksAgain.bookCategory);
        }
        bookIDsListViewItems = FXCollections.observableArrayList(IDsList);
        bookTitlesListViewItems = FXCollections.observableArrayList(bookTitlesList);
        authorNamesListViewItems = FXCollections.observableArrayList(authorNamesList);
        bookCategoriesListViewItems = FXCollections.observableArrayList(bookCategoriesList);

        ArrayList<ObservableList<String>> data = new ArrayList<>();
        data.add(bookIDsListViewItems);
        data.add(bookTitlesListViewItems);
        data.add(authorNamesListViewItems);
        data.add(bookCategoriesListViewItems);
        return data;
    }

    public static Dialog<ListBooksReturnType> listBooks(String userID) {
        HashSet<String> selectedIds = new HashSet<>();
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

        ListView<String> bookIDsListView = new ListView<>();
        ListView<String> bookTitlesListView = new ListView<>();
        ListView<String> authorNamesListView = new ListView<>();
        ListView<String> categoriesListView = new ListView<>();

        Label IDsListViewLabel = new Label("  " + Resources.getString("book_id"));
        Label bookTitlesListViewLabel = new Label("  " + Resources.getString("book_title"));
        Label authorNamesListViewLabel = new Label("  " + Resources.getString("author_names"));
        Label categoriesListViewLabel = new Label("  " + Resources.getString("book_category"));

        bookIDsListView.setOrientation(Orientation.VERTICAL);
        bookTitlesListView.setOrientation(Orientation.VERTICAL);
        authorNamesListView.setOrientation(Orientation.VERTICAL);
        categoriesListView.setOrientation(Orientation.VERTICAL);

        ArrayList<ObservableList<String>> dataList = getDataLists(userID, "");

        bookIDsListView.setItems(dataList.get(0));
        bookTitlesListView.setItems(dataList.get(1));
        authorNamesListView.setItems(dataList.get(2));
        categoriesListView.setItems(dataList.get(3));

        // This sets the initial height of the ListView:
        bookIDsListView.setPrefHeight(dataList.get(0).size() * Dashboard.ROW_HEIGHT + 2);
        bookTitlesListView.setPrefHeight(dataList.get(1).size() * Dashboard.ROW_HEIGHT + 2);
        authorNamesListView.setPrefHeight(dataList.get(2).size() * Dashboard.ROW_HEIGHT + 2);
        categoriesListView.setPrefHeight(dataList.get(3).size() * Dashboard.ROW_HEIGHT + 2);

        IDsListViewAndLabel.getChildren().addAll(IDsListViewLabel, bookIDsListView);
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
        Dialog<ListBooksReturnType> listBooksDialog = new Dialog<>();
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
        assert url != null;
        Node graphic = new ImageView(url.toString());
        graphic.prefHeight(50);
        graphic.prefWidth(50);
        graphic.maxHeight(50);
        graphic.maxWidth(50);
        listBooksDialog.setGraphic(graphic);

        // Multiple select in a list view:
        bookIDsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ButtonType borrowBooksButton = new ButtonType(Resources.getString("return_book"), ButtonBar.ButtonData.OK_DONE);
        ButtonType returnBookButton = new ButtonType(Resources.getString("borrow_book"), ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteBookButton = new ButtonType(Resources.getString("delete_book"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(Resources.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        // Dialog functionality
        if (userID != null) {
            listBooksDialog.getDialogPane().getButtonTypes().add(borrowBooksButton);
        }
        if (userID != null) {
            listBooksDialog.getDialogPane().getButtonTypes().add(returnBookButton);
        }
        listBooksDialog.getDialogPane().getButtonTypes().addAll(deleteBookButton, cancelButton);

        // Store corresponding IDs to an ID variable
        bookIDsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            selectedIds.add(bookIDsListView.getItems().get(newIndex.intValue()));
        });

        listBooksDialog.getDialogPane().setContent(parentLayout);

        listBooksDialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteBookButton) {
                return new ListBooksReturnType(ListBooksButtonTypes.DELETE_BUTTON, selectedIds);
            }
            if (dialogButton == returnBookButton) {
                return new ListBooksReturnType(ListBooksButtonTypes.RETURN_BUTTON, selectedIds);
            }
            if (dialogButton == borrowBooksButton) {
                return new ListBooksReturnType(ListBooksButtonTypes.BORROW_BUTTON, selectedIds);
            }
            return null;
        });

        return listBooksDialog;
    }
}