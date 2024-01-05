package com.cdw.meetingScheduler.repositories;

import com.cdw.meetingScheduler.entities.Team;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @Repository ------------ annotation not needed since SimpleRepository has this annotation
@Repository
@Transactional
public interface TeamRepository extends JpaRepository<Team, Integer> {
}
