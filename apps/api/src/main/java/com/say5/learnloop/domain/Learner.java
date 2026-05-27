package com.say5.learnloop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "learners")
public class Learner {

  @Id private Long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(name = "class_group")
  private String classGroup;

  protected Learner() {}

  public Learner(Long id, String name, Role role, String classGroup) {
    this.id = id;
    this.name = name;
    this.role = role;
    this.classGroup = classGroup;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Role getRole() {
    return role;
  }

  public String getClassGroup() {
    return classGroup;
  }
}
