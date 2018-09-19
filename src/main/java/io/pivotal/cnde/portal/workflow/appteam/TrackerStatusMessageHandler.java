package io.pivotal.cnde.portal.workflow.appteam;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Component;

@Component
public class TrackerStatusMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(TrackerStatusMessageHandler.class);

  private final StateMachineService<States, Events> stateMachineService;

  public TrackerStatusMessageHandler(
      StateMachineService<States, Events> stateMachineService) {
    this.stateMachineService = stateMachineService;
  }

  @StreamListener(Tracker.STATUS)
  public void handleMessage(Message<WorkerStatusDto> message) {
    logger.info("trackerStatus:handleMessage(message: {}, payload: {})", message,
        message.getPayload());

    if (!Objects.equals(message.getPayload().getType(), "create-tracker-project")) {
      throw new IllegalArgumentException(
          String.format("Unexpected status type: %s", message.getPayload().getType()));
    }

    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine(message.getPayload().getWorkflowId());

    stateMachine.sendEvent(Events.TRACKER_FINISHED);
  }

}

class WorkerStatusDto {

  private final String workflowId;
  private final String type;

  @JsonCreator
  public WorkerStatusDto(
      @JsonProperty("workflowId") String workflowId,
      @JsonProperty("@type") String type) {
    this.workflowId = workflowId;
    this.type = type;
  }

  public String getWorkflowId() {
    return workflowId;
  }

  public String getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkerStatusDto that = (WorkerStatusDto) o;
    return Objects.equals(workflowId, that.workflowId) &&
        Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workflowId, type);
  }

  @Override
  public String toString() {
    return "WorkerStatusDto{" +
        "workflowId='" + workflowId + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
