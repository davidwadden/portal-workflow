package io.pivotal.cnde.portal.workflow.appteam;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class TrackerResponseMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(TrackerResponseMessageHandler.class);

  private final WorkflowService workflowService;

  public TrackerResponseMessageHandler(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  @StreamListener(Tracker.RESPONSE)
  public void handleMessage(Message<TrackerResponseDto> message) {
    logger.info("trackerResponse(message: {}, payload: {})",
        message, message.getPayload());

    if (!Objects.equals(message.getPayload().getType(), "create-tracker-project")) {
      throw new IllegalArgumentException(
          String.format("Unexpected response type: %s", message.getPayload().getType()));
    }

    workflowService.finishTracker(message.getPayload().getWorkflowId());
  }
}

class TrackerResponseDto {

  private final String workflowId;
  private final String type;

  @JsonCreator
  public TrackerResponseDto(
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
    TrackerResponseDto that = (TrackerResponseDto) o;
    return Objects.equals(workflowId, that.workflowId) &&
        Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workflowId, type);
  }

  @Override
  public String toString() {
    return "TrackerResponseDto{" +
        "workflowId='" + workflowId + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
