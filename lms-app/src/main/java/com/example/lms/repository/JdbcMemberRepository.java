package com.example.lms.repository;

import com.example.lms.model.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcMemberRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Member> rowMapper = (rs, rowNum) -> {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        return member;
    };

    public List<Member> findAll() {
        return jdbcTemplate.query("SELECT * FROM member", rowMapper);
    }

    public List<Member> search(String query) {
        String likeQuery = "%" + query + "%";
        return jdbcTemplate.query("SELECT * FROM member WHERE name LIKE ? OR email LIKE ?", 
            rowMapper, likeQuery, likeQuery);
    }

    public Member findById(Long id) {
        List<Member> members = jdbcTemplate.query("SELECT * FROM member WHERE id = ?", rowMapper, id);
        return members.isEmpty() ? null : members.get(0);
    }

    public void save(Member member) {
        if (member.getId() == null) {
            jdbcTemplate.update("INSERT INTO member (name, email, phone) VALUES (?, ?, ?)",
                member.getName(), member.getEmail(), member.getPhone());
        } else {
            jdbcTemplate.update("UPDATE member SET name=?, email=?, phone=? WHERE id=?",
                member.getName(), member.getEmail(), member.getPhone(), member.getId());
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM member WHERE id = ?", id);
    }

    public int countAll() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member", Integer.class);
        return count != null ? count : 0;
    }
}
