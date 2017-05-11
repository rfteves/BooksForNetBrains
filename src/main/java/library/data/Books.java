/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rfteves
 */
@XmlRootElement
public class Books {
    private List<Book>book = new ArrayList<>();

    /**
     * @return the book
     */
    @XmlElement
    public List<Book> getBook() {
        return book;
    }

    /**
     * @param book the book to set
     */
    public void setBook(List<Book> book) {
        this.book = book;
    }
    
    public static boolean filter(Book b, String key) {
        return b.getAuthor().toLowerCase().contains(key.toLowerCase())
                                    || b.getTitle().toLowerCase().contains(key.toLowerCase())
                                    || b.getDescription().toLowerCase().contains(key.toLowerCase());
    }
}
