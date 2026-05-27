package com.say5.learnloop.data;

import com.say5.learnloop.domain.MasteryRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasteryRepository extends JpaRepository<MasteryRecord, MasteryRecord.Key> {
  List<MasteryRecord> findByLearnerId(Long learnerId);

  List<MasteryRecord> findBySkillId(Long skillId);
}
