
package com.civic.complaint.controller;

import com.civic.complaint.entity.Citizen;
import com.civic.complaint.entity.Complaint;
import com.civic.complaint.repository.CitizenRepository;
import com.civic.complaint.repository.ComplaintRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    private final CitizenRepository citizenRepository;
    private final ComplaintRepository complaintRepository;

    public DashboardController(
            CitizenRepository citizenRepository,
            ComplaintRepository complaintRepository) {

        this.citizenRepository = citizenRepository;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        String email = principal.getName();

        Citizen citizen = citizenRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        model.addAttribute("citizen", citizen);

        List<Complaint> complaints =
                complaintRepository.findByCitizen(citizen);

        long totalComplaints = complaints.size();

        long resolvedComplaints = complaints.stream()
                .filter(c -> "RESOLVED".equals(c.getStatus()))
                .count();

        long pendingComplaints = complaints.stream()
                .filter(c -> "SUBMITTED".equals(c.getStatus())
                        || "IN_PROGRESS".equals(c.getStatus()))
                .count();

        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);

        return "dashboard";
    }
}
