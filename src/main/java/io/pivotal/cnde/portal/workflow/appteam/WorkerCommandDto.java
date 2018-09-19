package io.pivotal.cnde.portal.workflow.appteam;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Objects;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateTrackerProjectDto.class, name = "create-tracker-project"),
})
public abstract class WorkerCommandDto {

  private final String workflowId;

  protected WorkerCommandDto(String workflowId) {
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
    WorkerCommandDto that = (WorkerCommandDto) o;
    return Objects.equals(workflowId, that.workflowId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workflowId);
  }

  @Override
  public String toString() {
    return "WorkerCommandDto{" +
        "workflowId='" + workflowId + '\'' +
        '}';
  }
}
