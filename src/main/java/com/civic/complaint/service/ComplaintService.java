
package com.civic.complaint.service;

import com.civic.complaint.entity.Citizen;
import com.civic.complaint.entity.Complaint;
import com.civic.complaint.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Complaint saveComplaint(Complaint complaint, Citizen citizen) {
        complaint.setCitizen(citizen);
        complaint.setStatus("SUBMITTED");

        return complaintRepository.save(complaint);
    }

    public List<Complaint> getCitizenComplaints(Citizen citizen) {
        return complaintRepository.findByCitizen(citizen);
    }

    public Complaint getComplaintForEditing(Long id, Citizen citizen) {
    return complaintRepository.findByIdAndCitizen(id, citizen)
            .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
}

public Complaint updateComplaint(
        Long id,
        Citizen citizen,
        String title,
        String description,
        String category,
        String location) {

    Complaint complaint = getComplaintForEditing(id, citizen);

    if (!"SUBMITTED".equals(complaint.getStatus())) {
        throw new IllegalStateException("Only submitted complaints can be edited");
    }

    complaint.setTitle(title);
    complaint.setDescription(description);
    complaint.setCategory(category);
    complaint.setLocation(location);

    return complaintRepository.save(complaint);
}
public void deleteComplaint(Long id, Citizen citizen) {

    Complaint complaint = getComplaintForEditing(id, citizen);

    if (!"SUBMITTED".equals(complaint.getStatus())) {
        throw new IllegalStateException(
                "Only submitted complaints can be deleted"
        );
    }

    complaintRepository.delete(complaint);
}
}
