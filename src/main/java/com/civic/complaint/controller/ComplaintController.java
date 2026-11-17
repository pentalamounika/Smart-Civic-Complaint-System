
package com.civic.complaint.controller;

import com.civic.complaint.entity.Citizen;
import com.civic.complaint.entity.Complaint;
import com.civic.complaint.repository.CitizenRepository;
import com.civic.complaint.service.ComplaintService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class ComplaintController {

    private final ComplaintService complaintService;
    private final CitizenRepository citizenRepository;

    public ComplaintController(
            ComplaintService complaintService,
            CitizenRepository citizenRepository) {

        this.complaintService = complaintService;
        this.citizenRepository = citizenRepository;
    }

    @GetMapping("/complaints/new")
    public String showComplaintForm() {
        return "submit-complaint";
    }

    // Submit complaint with photo
    @PostMapping("/complaints/submit")
    public String submitComplaint(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String location,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Principal principal,
            RedirectAttributes redirectAttributes) throws IOException {

        Citizen citizen = citizenRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        Complaint complaint = new Complaint();

        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setCategory(category);
        complaint.setLocation(location);

        // Save uploaded photo
        if (photo != null && !photo.isEmpty()) {

            if (photo.getContentType() == null ||
                    !photo.getContentType().startsWith("image/")) {
                redirectAttributes.addFlashAttribute(
                        "error", "Please upload a valid image."
                );
                return "redirect:/complaints/new";
            }

            Path uploadDir = Paths.get("uploads/complaints");

            Files.createDirectories(uploadDir);

            String extension = "";

            String originalName = photo.getOriginalFilename();

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf(".")
                );
            }

            String fileName = UUID.randomUUID() + extension;

            Path filePath = uploadDir.resolve(fileName);

            Files.copy(
                    photo.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            complaint.setPhotoPath("/uploads/complaints/" + fileName);
        }

        Complaint savedComplaint =
                complaintService.saveComplaint(complaint, citizen);

        redirectAttributes.addFlashAttribute(
                "success",
                "Complaint submitted successfully! Your Complaint ID is "
                        + savedComplaint.getId()
        );

        return "redirect:/dashboard";
    }

    // View citizen complaints
    @GetMapping("/complaints/my")
    public String viewMyComplaints(
            Principal principal,
            org.springframework.ui.Model model) {

        Citizen citizen = citizenRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        model.addAttribute(
                "complaints",
                complaintService.getCitizenComplaints(citizen)
        );

        return "my-complaints";
    }

    // Show edit complaint page
    @GetMapping("/complaints/edit/{id}")
    public String showEditComplaintForm(
            @PathVariable Long id,
            Principal principal,
            org.springframework.ui.Model model) {

        Citizen citizen = citizenRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        Complaint complaint =
                complaintService.getComplaintForEditing(id, citizen);

        model.addAttribute("complaint", complaint);

        return "edit-complaint";
    }

    // Update complaint
    @PostMapping("/complaints/update/{id}")
    public String updateComplaint(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String location,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Citizen citizen = citizenRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        complaintService.updateComplaint(
                id, citizen, title, description, category, location
        );

        redirectAttributes.addFlashAttribute(
                "success", "Complaint updated successfully!"
        );

        return "redirect:/complaints/my";
    }

    // Delete complaint
    @PostMapping("/complaints/delete/{id}")
    public String deleteComplaint(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Citizen citizen = citizenRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Citizen not found"));

        complaintService.deleteComplaint(id, citizen);

        redirectAttributes.addFlashAttribute(
                "success", "Complaint deleted successfully!"
        );

        return "redirect:/complaints/my";
    }
}
