package com.say5.learnloop.data;

import com.say5.learnloop.domain.ReviewSchedule;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewScheduleRepository
    extends JpaRepository<ReviewSchedule, ReviewSchedule.Key> {

  @Query(
      "select r from ReviewSchedule r "
          + "where r.learnerId = :learnerId and r.dueAt <= :now order by r.dueAt asc")
  List<ReviewSchedule> findDue(@Param("learnerId") Long learnerId, @Param("now") Instant now);
}
