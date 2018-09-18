package io.pivotal.cnde.portal.workflow.appteam;

import io.pivotal.cnde.portal.workflow.appteam.TrackerWorkerStreamConfig.TrackerWorker;
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
  public Message<?> handleMessage(Message<?> message) {

    logger.info("trackerProvision:handleMessage(message: {}, payload: {})", message, message.getPayload());

    return MessageBuilder.withPayload("acknowledge-tracker-provision")
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
