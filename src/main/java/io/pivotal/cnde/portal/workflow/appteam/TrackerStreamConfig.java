package io.pivotal.cnde.portal.workflow.appteam;

import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
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

@EnableBinding(Tracker.class)
@Configuration
public class TrackerStreamConfig {

  private static final Logger logger = LoggerFactory.getLogger(TrackerStreamConfig.class);

  @StreamListener(Tracker.STATUS)
  public void handleMessage(Message<?> message) {

    logger.info("trackerStatus:handleMessage(message: {})", message);
  }

  interface Tracker {

    String PROVISION = "tracker-provision-send";
    String STATUS = "tracker-status-listen";

    @Output(PROVISION)
    MessageChannel trackerProvision();

    @Input(STATUS)
    SubscribableChannel trackerStatus();
  }

}
