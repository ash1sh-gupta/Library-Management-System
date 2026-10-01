package com.example.lms.model;

public class DashboardStats {
    private int totalBooks;
    private int availableBooks;
    private int issuedBooks;
    private int totalMembers;

    public DashboardStats(int totalBooks, int availableBooks, int issuedBooks, int totalMembers) {
        this.totalBooks = totalBooks;
        this.availableBooks = availableBooks;
        this.issuedBooks = issuedBooks;
        this.totalMembers = totalMembers;
    }

    public int getTotalBooks() { return totalBooks; }
    public int getAvailableBooks() { return availableBooks; }
    public int getIssuedBooks() { return issuedBooks; }
    public int getTotalMembers() { return totalMembers; }
}
