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

  @SendTo(TrackerWorker.RESPONSE)
  @StreamListener(TrackerWorker.REQUEST)
  public Message<?> handleMessage(Message<CreateTrackerProjectWorkerDto> message)
      throws InterruptedException {

    logger.info("createTrackerProject:handleMessage(message: {}, payload: {})",
        message, message.getPayload());

    Thread.sleep(2000L);

    String responsePayloadTemplate = "{\"workflowId\":\"%s\",\"@type\":\"create-tracker-project\"}";
    String responsePayload =
        String.format(responsePayloadTemplate, message.getPayload().getWorkflowId());

    return MessageBuilder.withPayload(responsePayload)
        .build();
  }

  interface TrackerWorker {

    String REQUEST = "tracker-request-listen";
    String RESPONSE = "tracker-response-send";

    @Input(REQUEST)
    SubscribableChannel request();

    @Output(RESPONSE)
    MessageChannel response();
  }

}

class CreateTrackerProjectWorkerDto {

  private final String workflowId;
  private final String type;
  private final String ownerEmail;

  @JsonCreator
  public CreateTrackerProjectWorkerDto(
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
    CreateTrackerProjectWorkerDto that = (CreateTrackerProjectWorkerDto) o;
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
    return "CreateTrackerProjectWorkerDto{" +
        "workflowId='" + workflowId + '\'' +
        ", type='" + type + '\'' +
        ", ownerEmail='" + ownerEmail + '\'' +
        '}';
  }
}
