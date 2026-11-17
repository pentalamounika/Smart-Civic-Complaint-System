
package com.civic.complaint.controller;

import com.civic.complaint.entity.Citizen;
import com.civic.complaint.service.CitizenService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final CitizenService citizenService;

    public RegistrationController(CitizenService citizenService) {
        this.citizenService = citizenService;
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerCitizen(
            @ModelAttribute Citizen citizen,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            citizenService.registerCitizen(citizen, confirmPassword);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Registration successful! Please login."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/register";
        }
    }
}
