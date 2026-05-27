package com.say5.learnloop.data;

import com.say5.learnloop.domain.Learner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerRepository extends JpaRepository<Learner, Long> {
  List<Learner> findByClassGroup(String classGroup);
}
