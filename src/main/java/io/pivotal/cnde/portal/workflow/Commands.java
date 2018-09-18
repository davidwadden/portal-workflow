package io.pivotal.cnde.portal.workflow;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.statemachine.StateMachine;

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

  @ShellMethod(value = "Fetch job and print details of State Machine", key = "get-job")
  public StateMachine<OrderStates, OrderEvents> getJob() {
    return jobService.getJob();
  }

}
