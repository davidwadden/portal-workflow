package io.pivotal.cnde.portal.workflow.appteam;

import java.util.Objects;

public class CreateTrackerProject extends WorkerCommand {

  private final String projectName;
  private final String ownerEmail;

  public CreateTrackerProject(String workflowId, String projectName, String ownerEmail) {
    super(workflowId);
    this.projectName = projectName;
    this.ownerEmail = ownerEmail;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CreateTrackerProject that = (CreateTrackerProject) o;
    return Objects.equals(projectName, that.projectName) &&
        Objects.equals(ownerEmail, that.ownerEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectName, ownerEmail);
  }

  @Override
  public String toString() {
    return "CreateTrackerProject{" +
        "projectName='" + projectName + '\'' +
        ", ownerEmail='" + ownerEmail + '\'' +
        "} " + super.toString();
  }
}
