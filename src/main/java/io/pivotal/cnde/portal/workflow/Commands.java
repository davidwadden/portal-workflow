package io.pivotal.cnde.portal.workflow;

import io.pivotal.cnde.portal.workflow.appteam.WorkflowService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Commands {

  private final WorkflowService workflowService;

  public Commands(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  @ShellMethod(value = "Trigger workflow", key = "trigger-workflow")
  public String triggerWorkflow(String workflowId, String projectName, String ownerEmail) {

    workflowService.triggerWorkflow(workflowId, projectName, ownerEmail);

    return "completed";
  }
}
