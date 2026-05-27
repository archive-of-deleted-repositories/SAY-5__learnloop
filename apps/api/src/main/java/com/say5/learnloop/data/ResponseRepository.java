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

  /** Per-question success counts for a skill, limited to a set of learners (a class). */
  @Query(
      "select r.questionId, "
          + "sum(case when r.correct = true then 1 else 0 end), count(r) "
          + "from Response r "
          + "where r.skillId = :skillId and r.learnerId in :learnerIds "
          + "group by r.questionId")
  List<Object[]> questionSuccessCounts(
      @Param("skillId") Long skillId, @Param("learnerIds") List<Long> learnerIds);

  /** Overall correct and total counts for a skill across a class (single grouped row). */
  @Query(
      "select sum(case when r.correct = true then 1 else 0 end), count(r) "
          + "from Response r "
          + "where r.skillId = :skillId and r.learnerId in :learnerIds")
  List<Object[]> skillSuccess(
      @Param("skillId") Long skillId, @Param("learnerIds") List<Long> learnerIds);
}
