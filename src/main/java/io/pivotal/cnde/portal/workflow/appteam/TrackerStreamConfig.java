package io.pivotal.cnde.portal.workflow.appteam;

import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@EnableBinding(Tracker.class)
@Configuration
public class TrackerStreamConfig {

  interface Tracker {

    String PROVISION = "tracker-provision-send";
    String STATUS = "tracker-status-listen";

    @Output(PROVISION)
    MessageChannel trackerProvision();

    @Input(STATUS)
    SubscribableChannel trackerStatus();
  }

}
