
package com.civic.complaint.controller;

import com.civic.complaint.entity.Admin;
import com.civic.complaint.entity.Complaint;
import com.civic.complaint.repository.AdminRepository;
import com.civic.complaint.repository.ComplaintRepository;
import com.civic.complaint.repository.FeedbackRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final AdminRepository adminRepository;
    private final ComplaintRepository complaintRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminController(
            AdminRepository adminRepository,
            ComplaintRepository complaintRepository,
            FeedbackRepository feedbackRepository) {

        this.adminRepository = adminRepository;
        this.complaintRepository = complaintRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Principal principal, Model model) {

        Admin admin = adminRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Admin not found"));

        model.addAttribute("admin", admin);
        model.addAttribute("totalComplaints", complaintRepository.count());
        model.addAttribute("submittedComplaints",
                complaintRepository.countByStatus("SUBMITTED"));
        model.addAttribute("inProgressComplaints",
                complaintRepository.countByStatus("IN_PROGRESS"));
        model.addAttribute("resolvedComplaints",
                complaintRepository.countByStatus("RESOLVED"));

        return "admin-dashboard";
    }

    @GetMapping("/admin/complaints")
    public String viewAllComplaints(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            Model model) {

        List<Complaint> complaints = complaintRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String search = keyword.trim().toLowerCase();

            complaints = complaints.stream()
                    .filter(c ->
                            String.valueOf(c.getId()).contains(search)
                            || (c.getTitle() != null
                                && c.getTitle().toLowerCase().contains(search)))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.isEmpty()) {
            complaints = complaints.stream()
                    .filter(c -> category.equals(c.getCategory()))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            complaints = complaints.stream()
                    .filter(c -> status.equals(c.getStatus()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("complaints", complaints);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);

        return "admin-complaints";
    }

    @PostMapping("/admin/assign/{id}")
    public String assignDepartment(
            @PathVariable Long id,
            @RequestParam String department,
            RedirectAttributes redirectAttributes) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Complaint not found"));

        complaint.setDepartment(department);
        complaintRepository.save(complaint);

        redirectAttributes.addFlashAttribute(
                "success", "Department assigned successfully!");

        return "redirect:/admin/complaints";
    }

    @PostMapping("/admin/update-status/{id}")
    public String updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Complaint not found"));

        complaint.setStatus(status);
        complaintRepository.save(complaint);

        redirectAttributes.addFlashAttribute(
                "success", "Complaint status updated successfully!");

        return "redirect:/admin/complaints";
    }

    @GetMapping("/admin/feedback")
    public String viewAllFeedback(Model model) {

        model.addAttribute("feedbackList",
                feedbackRepository.findAll());

        return "admin-feedback";
    }
}
