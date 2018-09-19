package io.pivotal.cnde.portal.workflow;

import io.pivotal.cnde.portal.workflow.appteam.AppTeamWorkflowService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Commands {

  private final AppTeamWorkflowService appTeamWorkflowService;

  public Commands(AppTeamWorkflowService appTeamWorkflowService) {
    this.appTeamWorkflowService = appTeamWorkflowService;
  }

  @ShellMethod(value = "Trigger workflow", key = "trigger-workflow")
  public String triggerWorkflow(
      String workflowId,
      String projectName,
      String ownerEmail) {

    appTeamWorkflowService.triggerWorkflow(workflowId, projectName, ownerEmail);

    return "completed";
  }

}
