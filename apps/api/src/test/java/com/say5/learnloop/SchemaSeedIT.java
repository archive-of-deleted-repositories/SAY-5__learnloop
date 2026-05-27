package com.say5.learnloop;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.data.LearnerRepository;
import com.say5.learnloop.data.QuestionRepository;
import com.say5.learnloop.data.SkillRepository;
import com.say5.learnloop.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SchemaSeedIT extends PostgresIntegrationTest {

  @Autowired SkillRepository skills;
  @Autowired QuestionRepository questions;
  @Autowired LearnerRepository learners;

  @Test
  void seedsThreeSkills() {
    assertThat(skills.count()).isEqualTo(3);
  }

  @Test
  void seedsSixtyQuestions() {
    assertThat(questions.count()).isEqualTo(60);
    assertThat(questions.findBySkillId(1L)).hasSize(20);
  }

  @Test
  void seedsClassWithTeacherAndStudents() {
    var roster = learners.findByClassGroup("period-1");
    assertThat(roster).hasSize(7);
    assertThat(roster.stream().filter(l -> l.getRole() == Role.TEACHER)).hasSize(1);
    assertThat(roster.stream().filter(l -> l.getRole() == Role.STUDENT)).hasSize(6);
  }
}
