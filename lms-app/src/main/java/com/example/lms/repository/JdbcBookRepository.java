package com.example.lms.repository;

import com.example.lms.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcBookRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcBookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> rowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setTotalQuantity(rs.getInt("total_quantity"));
        book.setAvailableQuantity(rs.getInt("available_quantity"));
        return book;
    };

    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM book", rowMapper);
    }

    public List<Book> search(String query) {
        String likeQuery = "%" + query + "%";
        return jdbcTemplate.query("SELECT * FROM book WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?", 
            rowMapper, likeQuery, likeQuery, likeQuery);
    }

    public Book findById(Long id) {
        List<Book> books = jdbcTemplate.query("SELECT * FROM book WHERE id = ?", rowMapper, id);
        return books.isEmpty() ? null : books.get(0);
    }

    public void save(Book book) {
        if (book.getId() == null) {
            jdbcTemplate.update("INSERT INTO book (title, author, isbn, total_quantity, available_quantity) VALUES (?, ?, ?, ?, ?)",
                book.getTitle(), book.getAuthor(), book.getIsbn(), book.getTotalQuantity(), book.getAvailableQuantity());
        } else {
            jdbcTemplate.update("UPDATE book SET title=?, author=?, isbn=?, total_quantity=?, available_quantity=? WHERE id=?",
                book.getTitle(), book.getAuthor(), book.getIsbn(), book.getTotalQuantity(), book.getAvailableQuantity(), book.getId());
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM book WHERE id = ?", id);
    }

    public int countAll() {
        Integer count = jdbcTemplate.queryForObject("SELECT SUM(total_quantity) FROM book", Integer.class);
        return count != null ? count : 0;
    }
    
    public int countAvailable() {
        Integer count = jdbcTemplate.queryForObject("SELECT SUM(available_quantity) FROM book", Integer.class);
        return count != null ? count : 0;
    }
}
