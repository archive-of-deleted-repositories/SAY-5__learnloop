package com.say5.learnloop.api;

import com.say5.learnloop.api.Dtos.CohortAnalytics;
import com.say5.learnloop.api.Dtos.StudentDashboard;
import com.say5.learnloop.api.Dtos.TeacherDashboard;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboards")
class DashboardController {

  private final DashboardService service;
  private final CohortAnalyticsService cohort;

  DashboardController(DashboardService service, CohortAnalyticsService cohort) {
    this.service = service;
    this.cohort = cohort;
  }

  @GetMapping("/student/{learnerId}")
  StudentDashboard student(@PathVariable long learnerId) {
    return service.student(learnerId);
  }

  @GetMapping("/teacher/{classGroup}")
  TeacherDashboard teacher(@PathVariable String classGroup) {
    return service.teacher(classGroup);
  }

  @GetMapping("/cohort/{classGroup}")
  CohortAnalytics cohort(@PathVariable String classGroup) {
    return cohort.forClass(classGroup);
  }
}
