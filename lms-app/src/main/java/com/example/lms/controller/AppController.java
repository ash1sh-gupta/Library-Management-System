package com.example.lms.controller;

import com.example.lms.model.Book;
import com.example.lms.model.Member;
import com.example.lms.service.LmsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {
    private final LmsService lmsService;

    public AppController(LmsService lmsService) {
        this.lmsService = lmsService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", lmsService.getDashboardStats());
        return "dashboard";
    }

    // Books
    @GetMapping("/books")
    public String listBooks(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("books", lmsService.getAllBooks(query));
        model.addAttribute("query", query);
        return "books";
    }

    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", lmsService.getBookById(id));
        return "book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        lmsService.saveBook(book);
        redirectAttributes.addFlashAttribute("message", "Book saved successfully.");
        return "redirect:/books";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lmsService.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete book. It might be issued.");
        }
        return "redirect:/books";
    }

    // Members
    @GetMapping("/members")
    public String listMembers(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("members", lmsService.getAllMembers(query));
        model.addAttribute("query", query);
        return "members";
    }

    @GetMapping("/members/add")
    public String addMemberForm(Model model) {
        model.addAttribute("member", new Member());
        return "member-form";
    }

    @GetMapping("/members/edit/{id}")
    public String editMemberForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", lmsService.getMemberById(id));
        return "member-form";
    }

    @PostMapping("/members/save")
    public String saveMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        lmsService.saveMember(member);
        redirectAttributes.addFlashAttribute("message", "Member saved successfully.");
        return "redirect:/members";
    }

    @GetMapping("/members/delete/{id}")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lmsService.deleteMember(id);
            redirectAttributes.addFlashAttribute("message", "Member deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete member. They might have issued books.");
        }
        return "redirect:/members";
    }

    // Issue/Return
    @GetMapping("/records")
    public String listRecords(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("records", lmsService.getAllRecords(query));
        model.addAttribute("query", query);
        return "records";
    }

    @GetMapping("/issue")
    public String issueForm(Model model) {
        model.addAttribute("books", lmsService.getAllBooks(null)); // Should filter available in a real big app
        model.addAttribute("members", lmsService.getAllMembers(null));
        return "issue-form";
    }

    @PostMapping("/issue")
    public String issueBook(@RequestParam Long bookId, @RequestParam Long memberId, RedirectAttributes redirectAttributes) {
        try {
            lmsService.issueBook(bookId, memberId);
            redirectAttributes.addFlashAttribute("message", "Book issued successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/records";
    }

    @GetMapping("/return/{id}")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lmsService.returnBook(id);
            redirectAttributes.addFlashAttribute("message", "Book returned successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/records";
    }

    // Global Search
    @GetMapping("/search")
    public String search(@RequestParam("q") String query) {
        return "redirect:/books?q=" + query; // simple global search redirecting to books
    }

    // Auth
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttributes) {
        if ("admin".equals(username) && "admin".equals(password)) {
            session.setAttribute("admin_logged_in", true);
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid credentials. Please use admin/admin.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
