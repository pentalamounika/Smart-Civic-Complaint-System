
package com.civic.complaint.repository;

import com.civic.complaint.entity.Feedback;
import com.civic.complaint.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByCitizen(Citizen citizen);

}
