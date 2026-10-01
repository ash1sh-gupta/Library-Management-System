package com.example.lms.service;

import com.example.lms.model.Book;
import com.example.lms.model.DashboardStats;
import com.example.lms.model.IssueRecord;
import com.example.lms.model.Member;
import com.example.lms.repository.JdbcBookRepository;
import com.example.lms.repository.JdbcIssueRecordRepository;
import com.example.lms.repository.JdbcMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LmsService {
    private final JdbcBookRepository bookRepository;
    private final JdbcMemberRepository memberRepository;
    private final JdbcIssueRecordRepository issueRecordRepository;

    public LmsService(JdbcBookRepository bookRepository, JdbcMemberRepository memberRepository, JdbcIssueRecordRepository issueRecordRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.issueRecordRepository = issueRecordRepository;
    }

    public DashboardStats getDashboardStats() {
        int totalBooks = bookRepository.countAll();
        int availableBooks = bookRepository.countAvailable();
        int issuedBooks = issueRecordRepository.countIssued();
        int totalMembers = memberRepository.countAll();
        return new DashboardStats(totalBooks, availableBooks, issuedBooks, totalMembers);
    }

    // Book Methods
    public List<Book> getAllBooks(String query) {
        return (query == null || query.isEmpty()) ? bookRepository.findAll() : bookRepository.search(query);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void saveBook(Book book) {
        if (book.getId() == null) {
            book.setAvailableQuantity(book.getTotalQuantity()); // new book
        } else {
            Book existing = bookRepository.findById(book.getId());
            int diff = book.getTotalQuantity() - existing.getTotalQuantity();
            book.setAvailableQuantity(existing.getAvailableQuantity() + diff);
        }
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Member Methods
    public List<Member> getAllMembers(String query) {
        return (query == null || query.isEmpty()) ? memberRepository.findAll() : memberRepository.search(query);
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    // Issue Methods
    public List<IssueRecord> getAllRecords(String query) {
        return (query == null || query.isEmpty()) ? issueRecordRepository.findAllWithDetails() : issueRecordRepository.searchWithDetails(query);
    }

    @Transactional
    public void issueBook(Long bookId, Long memberId) throws Exception {
        Book book = bookRepository.findById(bookId);
        if (book == null || book.getAvailableQuantity() <= 0) {
            throw new Exception("Book not available");
        }
        
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);
        
        IssueRecord record = new IssueRecord();
        record.setBookId(bookId);
        record.setMemberId(memberId);
        record.setIssueDate(LocalDate.now());
        record.setStatus("ISSUED");
        issueRecordRepository.save(record);
    }

    @Transactional
    public void returnBook(Long recordId) throws Exception {
        IssueRecord record = issueRecordRepository.findById(recordId);
        if (record == null || "RETURNED".equals(record.getStatus())) {
            throw new Exception("Invalid record");
        }
        
        record.setStatus("RETURNED");
        record.setReturnDate(LocalDate.now());
        issueRecordRepository.save(record);
        
        Book book = bookRepository.findById(record.getBookId());
        if (book != null) {
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookRepository.save(book);
        }
    }
}
