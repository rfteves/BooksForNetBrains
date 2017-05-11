/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.entry;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import library.data.Book;
import library.data.Books;
import library.io.ConsoleHelper;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author rfteves
 */
public class Main {

    public final String[] MENU_OPTIONS = {"View all books", "Add a book", "Edit a book", "Search for a book", "Save and exit"};
    public final static File DATABASE_NAME = new File("Books.json");
    private final static Gson g = new Gson();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main().displayMenu();
    }

    private void displayMenu() {
        boolean keepWorking = true;
        int choice = 0;
        String json = null;
        Books books = null;
        List<Book> selected = new ArrayList<>();
        while (keepWorking) {
            switch (choice) {
                case 4:
                    while (!books.getBook().isEmpty()) {
                        ConsoleHelper.printMessage("");
                        ConsoleHelper.printMessage("==== Search ====");
                        ConsoleHelper.printMessage("");
                        ConsoleHelper.printMessage("Type in one or more keywords to search for");
                        ConsoleHelper.printMessage("");
                        ConsoleHelper.printPrompt("Search");
                        String keys = ConsoleHelper.getString();
                        if (keys == null || keys.length() == 0) {
                            break;
                        } else {
                            selected.clear();
                            books.getBook().stream().filter(b -> b.getAuthor().toLowerCase().contains(keys.toLowerCase())
                                    || b.getTitle().toLowerCase().contains(keys.toLowerCase())
                                    || b.getDescription().toLowerCase().contains(keys.toLowerCase())).forEach(selected::add);
                            ConsoleHelper.printMessage("");
                            if (selected.isEmpty()) {
                                ConsoleHelper.printMessage("The search did not return a match.");
                            } else {
                                ConsoleHelper.printMessage("The following books matched your query. Enter the book ID to see more details, or <Enter> to return.");
                                ConsoleHelper.printMessage("");
                                Main.viewbooks(selected);
                            }
                        }
                    }
                    if (books.getBook().isEmpty()) {
                        ConsoleHelper.printMessage("");
                        ConsoleHelper.printMessage("==== No books available ====");
                    }
                    choice = 0;
                    break;
                case 5:
                    Main.save(books);
                    ConsoleHelper.printMessage("Exiting... thank you for using the book manager.");
                    keepWorking = false;
                    break;
                case 3:
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printMessage("==== Edit a Book ====");
                    selected.clear();
                    while (selected.isEmpty()) {
                        ConsoleHelper.printMessage("");
                        books.getBook().stream().forEach(mo -> ConsoleHelper.printMessage(String.format("[%s] %s", "" + mo.getId(), mo.getTitle()), 1));
                        ConsoleHelper.printMessage("");
                        ConsoleHelper.printMessage("Enter the book ID of the book you want to edit; to return press <Enter>.");
                        ConsoleHelper.printMessage("");
                        ConsoleHelper.printPrompt("Enter a valid Book ID");
                        Integer selectedId = ConsoleHelper.getInt(true);
                        if (selectedId == null) {
                            break;
                        } else {
                            selected.clear();
                            books.getBook().stream().filter(b -> b.getId() == selectedId).forEach(selected::add);
                            if (!selected.isEmpty()) {
                                ConsoleHelper.printMessage("");
                                ConsoleHelper.printMessage("Book ID: " + selected.get(0).getId());
                                ConsoleHelper.printMessage("");
                                ConsoleHelper.printMessage("Input the following information. To leave a field unchanged, hit <Enter>");
                                ConsoleHelper.printMessage("");
                                ConsoleHelper.printPrompt(String.format("Title [%s]", selected.get(0).getTitle()));
                                String line = ConsoleHelper.getString();
                                if (!line.isEmpty()) {
                                    selected.get(0).setTitle(line);
                                }
                                ConsoleHelper.printPrompt(String.format("Author [%s]", selected.get(0).getAuthor()));
                                line = ConsoleHelper.getString();
                                if (!line.isEmpty()) {
                                    selected.get(0).setAuthor(line);
                                }
                                ConsoleHelper.printPrompt(String.format("Description [%s]", selected.get(0).getDescription()));
                                line = ConsoleHelper.getString();
                                if (!line.isEmpty()) {
                                    selected.get(0).setDescription(line);
                                }
                                selected.clear();
                                Main.save(books);
                            }
                        }
                    }
                    choice = 0;
                    break;
                case 0:
                    if (DATABASE_NAME.exists()) {
                        try {
                            json = FileUtils.readFileToString(DATABASE_NAME, "UTF-8");
                            books = (Books) g.fromJson(json, Books.class);
                            if (books == null) {
                                books = new Books();
                            }
                        } catch (Exception ex) {
                            books = new Books();
                        }
                    } else {
                        books = new Books();
                    }
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printMessage(String.format("Loaded %s book%s into the library", "" + books.getBook().size(), books.getBook().size() <= 1 ? "" : "s"));
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printMessage("==== Book Manager ====");
                    ConsoleHelper.printMessage("");
                    //List<String> menuOptions = Arrays.asList(MENU_OPTIONS);
                    Main.createMenuOption(MENU_OPTIONS).forEach(ConsoleHelper::printOption);
                    while (true) {
                        ConsoleHelper.printMessage("");
                        ConsoleHelper.printPrompt("Choose [1-5]");
                        Integer selectedInt = ConsoleHelper.getInt(false);
                        if (selectedInt != null && selectedInt >= 1 && selectedInt <= 5) {
                            choice = selectedInt;
                            break;
                        }
                    }
                    break;
                case 1:
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printMessage("==== View Books ====");
                    Main.viewbooks(books.getBook());
                    choice = 0;
                    break;
                case 2:
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printMessage("==== Add a Book ====");
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printMessage("Please enter the following information:");
                    ConsoleHelper.printMessage("");
                    Book book = new Book();
                    book.setId(books.getBook().size() + 1);
                    books.getBook().add(book);
                    ConsoleHelper.printPrompt("Title");
                    book.setTitle(ConsoleHelper.getString(false, "title"));
                    ConsoleHelper.printPrompt("Author");
                    book.setAuthor(ConsoleHelper.getString(false, "author"));
                    ConsoleHelper.printPrompt("Description");
                    book.setDescription(ConsoleHelper.getString(false, "description"));
                    Main.save(books);
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printMessage(String.format("Book [%s] Saved", "" + book.getId()));
                    choice = 0;
                    break;
                default:
                    ConsoleHelper.printMessage("");
                    ConsoleHelper.printError("Sorry, invalid choice. Please enter [1-5].");
                    ConsoleHelper.printMessage("");
            }
        }
    }

    private static void save(Books books) {
        String json = g.toJson(books);
        try {
            FileUtils.write(DATABASE_NAME, json, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void viewbooks(List<Book> books) {
        if (books.isEmpty()) {
            ConsoleHelper.printMessage("");
            ConsoleHelper.printMessage("==== No books available ====");
            return;
        }
        while (true) {
            ConsoleHelper.printMessage("");
            ConsoleHelper.printMessage("To view details enter the book ID, to return press <Enter>");
            ConsoleHelper.printMessage("");
            books.stream().forEach(mo -> ConsoleHelper.printMessage(String.format("[%s] %s", "" + mo.getId(), mo.getTitle()), 1));
            ConsoleHelper.printMessage("");
            ConsoleHelper.printPrompt("Book ID");
            Integer selectedId = ConsoleHelper.getInt(true);
            if (selectedId == null) {
                break;
            } else {
                books.stream().filter(b -> b.getId() == selectedId).forEach(b -> {
                    ConsoleHelper.printMessage(String.format("ID: %s", "" + b.getId()), 1);
                    ConsoleHelper.printMessage(String.format("Title: %s", b.getTitle()), 1);
                    ConsoleHelper.printMessage(String.format("Author: %s", b.getAuthor()), 1);
                    ConsoleHelper.printMessage(String.format("Description: %s", b.getDescription()), 1);
                });
            }
        }
    }

    static Map<Integer, String> createMenuOption(String[] options) {
        Map<Integer, String> values = new LinkedHashMap<>();
        int ordinal = 0;
        for (String option : options) {
            values.put(++ordinal, option);
        }
        return values;
    }
}
