package io.pivotal.cnde.portal.workflow;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Commands {

  private final JobService jobService;

  public Commands(JobService jobService) {
    this.jobService = jobService;
  }

  @ShellMethod(value = "Submit job with random set of parameters", key = "submit-job")
  public String submitJob() {

    jobService.submitJob();

    return "completed";
  }


}
