package io.pivotal.cnde.portal.workflow.appteam;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.pivotal.cnde.portal.workflow.appteam.TrackerWorkerStreamConfig.TrackerWorker;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(TrackerWorker.class)
@Configuration
public class TrackerWorkerStreamConfig {

  private static final Logger logger = LoggerFactory.getLogger(TrackerWorkerStreamConfig.class);

  @SendTo(TrackerWorker.STATUS)
  @StreamListener(TrackerWorker.PROVISION)
  public Message<?> handleMessage(Message<CreateTrackerProjectDto> message)
      throws InterruptedException {

    logger.info("createTrackerProject:handleMessage(message: {}, payload: {})",
        message, message.getPayload());

    Thread.sleep(2000L);

    String statusPayloadTemplate = "{\"workflowId\":\"%s\",\"@type\":\"create-tracker-project\"}";
    String statusPayload =
        String.format(statusPayloadTemplate, message.getPayload().getWorkflowId());

    return MessageBuilder.withPayload(statusPayload)
        .build();
  }

  interface TrackerWorker {

    String PROVISION = "tracker-provision-listen";
    String STATUS = "tracker-status-send";

    @Input(PROVISION)
    SubscribableChannel trackerProvision();

    @Output(STATUS)
    MessageChannel trackerStatus();
  }

}

class CreateTrackerProjectDto {

  private final String workflowId;
  private final String type;
  private final String ownerEmail;

  @JsonCreator
  public CreateTrackerProjectDto(
      @JsonProperty("workflowId") String workflowId,
      @JsonProperty("@type") String type,
      @JsonProperty("ownerEmail") String ownerEmail) {
    this.workflowId = workflowId;
    this.type = type;
    this.ownerEmail = ownerEmail;
  }

  public String getWorkflowId() {
    return workflowId;
  }

  public String getType() {
    return type;
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
    CreateTrackerProjectDto that = (CreateTrackerProjectDto) o;
    return Objects.equals(workflowId, that.workflowId) &&
        Objects.equals(type, that.type) &&
        Objects.equals(ownerEmail, that.ownerEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workflowId, type, ownerEmail);
  }

  @Override
  public String toString() {
    return "CreateTrackerProjectDto{" +
        "workflowId='" + workflowId + '\'' +
        ", type='" + type + '\'' +
        ", ownerEmail='" + ownerEmail + '\'' +
        '}';
  }
}
