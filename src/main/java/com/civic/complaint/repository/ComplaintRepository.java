
package com.civic.complaint.repository;

import com.civic.complaint.entity.Complaint;
import com.civic.complaint.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByCitizen(Citizen citizen);

    Optional<Complaint> findByIdAndCitizen(Long id, Citizen citizen);
    

    long countByStatus(String status);

long countByStatusIn(java.util.Collection<String> statuses);

long count();
}
