package com.say5.learnloop.data;

import com.say5.learnloop.domain.Response;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResponseRepository extends JpaRepository<Response, Long> {

  @Query(
      "select r.questionId from Response r "
          + "where r.learnerId = :learnerId and r.skillId = :skillId "
          + "order by r.answeredAt desc, r.id desc")
  List<Long> recentQuestionIds(
      @Param("learnerId") Long learnerId, @Param("skillId") Long skillId, Pageable pageable);

  List<Response> findByLearnerIdAndSkillIdOrderByAnsweredAtAscIdAsc(Long learnerId, Long skillId);
}
