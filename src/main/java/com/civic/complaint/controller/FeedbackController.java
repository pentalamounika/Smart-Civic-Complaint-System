
package com.civic.complaint.controller;

import com.civic.complaint.entity.Citizen;
import com.civic.complaint.entity.Complaint;
import com.civic.complaint.entity.Feedback;
import com.civic.complaint.repository.CitizenRepository;
import com.civic.complaint.repository.ComplaintRepository;
import com.civic.complaint.service.FeedbackService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final CitizenRepository citizenRepository;
    private final ComplaintRepository complaintRepository;

    public FeedbackController(
            FeedbackService feedbackService,
            CitizenRepository citizenRepository,
            ComplaintRepository complaintRepository) {
        this.feedbackService = feedbackService;
        this.citizenRepository = citizenRepository;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("/feedback/{complaintId}")
    public String showFeedbackForm(
            @PathVariable Long complaintId,
            Principal principal,
            Model model) {

        Citizen citizen = citizenRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        Complaint complaint = complaintRepository
                .findByIdAndCitizen(complaintId, citizen)
                .orElseThrow(() ->
                        new IllegalArgumentException("Complaint not found"));

        if (!"RESOLVED".equals(complaint.getStatus())) {
            throw new IllegalArgumentException(
                    "Feedback can be submitted only for resolved complaints");
        }

        Feedback feedback = new Feedback();
        model.addAttribute("feedback", feedback);
        model.addAttribute("complaint", complaint);

        return "feedback";
    }

    @PostMapping("/feedback/submit/{complaintId}")
    public String submitFeedback(
            @PathVariable Long complaintId,
            @ModelAttribute Feedback feedback,
            @RequestParam int rating,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Citizen citizen = citizenRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        Complaint complaint = complaintRepository
                .findByIdAndCitizen(complaintId, citizen)
                .orElseThrow(() ->
                        new IllegalArgumentException("Complaint not found"));

        if (!"RESOLVED".equals(complaint.getStatus())) {
            throw new IllegalArgumentException(
                    "Only resolved complaints can receive feedback");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        feedback.setCitizen(citizen);
        feedback.setComplaint(complaint);
        feedback.setRating(rating);
        feedback.setComments(comments);

        feedbackService.saveFeedback(feedback);

        redirectAttributes.addFlashAttribute(
                "success", "Feedback submitted successfully!");

        return "redirect:/complaints/my";
    }
}
