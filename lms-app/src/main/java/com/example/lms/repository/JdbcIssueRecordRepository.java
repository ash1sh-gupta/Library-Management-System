package com.example.lms.repository;

import com.example.lms.model.IssueRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcIssueRecordRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcIssueRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<IssueRecord> rowMapper = (rs, rowNum) -> {
        IssueRecord record = new IssueRecord();
        record.setId(rs.getLong("id"));
        record.setBookId(rs.getLong("book_id"));
        record.setMemberId(rs.getLong("member_id"));
        record.setIssueDate(rs.getDate("issue_date") != null ? rs.getDate("issue_date").toLocalDate() : null);
        record.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
        record.setStatus(rs.getString("status"));
        
        try {
            record.setBookTitle(rs.getString("book_title"));
            record.setMemberName(rs.getString("member_name"));
        } catch (Exception e) {
            // columns might not exist in simple queries
        }
        return record;
    };

    public List<IssueRecord> findAllWithDetails() {
        String sql = "SELECT i.*, b.title as book_title, m.name as member_name " +
                     "FROM issue_record i " +
                     "JOIN book b ON i.book_id = b.id " +
                     "JOIN member m ON i.member_id = m.id " +
                     "ORDER BY i.issue_date DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<IssueRecord> searchWithDetails(String query) {
        String likeQuery = "%" + query + "%";
        String sql = "SELECT i.*, b.title as book_title, m.name as member_name " +
                     "FROM issue_record i " +
                     "JOIN book b ON i.book_id = b.id " +
                     "JOIN member m ON i.member_id = m.id " +
                     "WHERE b.title LIKE ? OR m.name LIKE ? OR i.status LIKE ? " +
                     "ORDER BY i.issue_date DESC";
        return jdbcTemplate.query(sql, rowMapper, likeQuery, likeQuery, likeQuery);
    }

    public IssueRecord findById(Long id) {
        List<IssueRecord> records = jdbcTemplate.query("SELECT * FROM issue_record WHERE id = ?", rowMapper, id);
        return records.isEmpty() ? null : records.get(0);
    }

    public void save(IssueRecord record) {
        if (record.getId() == null) {
            jdbcTemplate.update("INSERT INTO issue_record (book_id, member_id, issue_date, return_date, status) VALUES (?, ?, ?, ?, ?)",
                record.getBookId(), record.getMemberId(), record.getIssueDate(), record.getReturnDate(), record.getStatus());
        } else {
            jdbcTemplate.update("UPDATE issue_record SET book_id=?, member_id=?, issue_date=?, return_date=?, status=? WHERE id=?",
                record.getBookId(), record.getMemberId(), record.getIssueDate(), record.getReturnDate(), record.getStatus(), record.getId());
        }
    }
    
    public int countIssued() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM issue_record WHERE status = 'ISSUED'", Integer.class);
        return count != null ? count : 0;
    }
}
