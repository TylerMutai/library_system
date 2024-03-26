package librarysystem.models;

import java.util.HashSet;

public class ListBooksReturnType {
    public final ListBooksButtonTypes buttonClicked;
    public final HashSet<String> bookIds;

    public ListBooksReturnType(ListBooksButtonTypes buttonClicked, HashSet<String> bookIds) {
        this.buttonClicked = buttonClicked;
        this.bookIds = bookIds;
    }
}