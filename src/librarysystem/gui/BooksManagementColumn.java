package librarysystem.gui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import librarysystem.gui.books.*;
import librarysystem.gui.users.UserManagementUIListUsers;
import librarysystem.models.ListBooksButtonTypes;
import librarysystem.models.ListBooksReturnType;
import librarysystem.models.ListUsersReturnType;
import librarysystem.resources.Resources;

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
            BooksManagementUIAddBook.addBook();
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

    private static void borrowBook() {
        Optional<ListUsersReturnType> resultUsers = UserManagementUIListUsers.listUsers().showAndWait();

        resultUsers.ifPresent(clickedList -> {
            if (clickedList.userIds.size() > 0) {
                String selectedUserId = (String) clickedList.userIds.toArray()[0];
                Optional<ListBooksReturnType> result = BooksManagementUIListBooks.listBooks(null).showAndWait();

                result.ifPresent(listBooksReturnType -> {
                    if (listBooksReturnType.buttonClicked == ListBooksButtonTypes.BORROW_BUTTON) {
                        BooksManagementUIBorrowBook.borrowBook(selectedUserId, listBooksReturnType);
                    }
                });
            } else {
                alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
            }
        });
    }

    private static void returnBook() {
        Optional<ListUsersReturnType> resultUsers = UserManagementUIListUsers.listUsers().showAndWait();

        resultUsers.ifPresent(clickedList -> {
            if (clickedList.userIds.size() > 0) {
                String selectedUserId = (String) clickedList.userIds.toArray()[0];
                Optional<ListBooksReturnType> result = BooksManagementUIListBooks.listBooks(selectedUserId).showAndWait();

                result.ifPresent(listBooksReturnType -> {
                    if (listBooksReturnType.buttonClicked == ListBooksButtonTypes.RETURN_BUTTON) {
                        BooksManagementUIReturnBook.returnBook(selectedUserId, listBooksReturnType);
                    }
                });
            } else {
                alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                        Resources.getString("error"));
            }
        });
    }

    private static void listBooks() {
        Optional<ListBooksReturnType> result = BooksManagementUIListBooks.listBooks(null).showAndWait();

        result.ifPresent(listBooksReturnType -> {
            if (listBooksReturnType.buttonClicked == ListBooksButtonTypes.DELETE_BUTTON) {
                BooksManagementUIDeleteBook.deleteBook(listBooksReturnType);
            }
        });
    }
}