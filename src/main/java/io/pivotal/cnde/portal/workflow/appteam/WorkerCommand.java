package io.pivotal.cnde.portal.workflow.appteam;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Objects;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateTrackerProject.class, name = "create-tracker-project"),
})
public abstract class WorkerCommand {

  private final String workflowId;

  protected WorkerCommand(String workflowId) {
    this.workflowId = workflowId;
  }

  public String getWorkflowId() {
    return workflowId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkerCommand that = (WorkerCommand) o;
    return Objects.equals(workflowId, that.workflowId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workflowId);
  }

  @Override
  public String toString() {
    return "WorkerCommand{" +
        "workflowId='" + workflowId + '\'' +
        '}';
  }
}
