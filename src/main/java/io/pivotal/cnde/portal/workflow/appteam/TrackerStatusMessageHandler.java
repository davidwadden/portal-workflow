package io.pivotal.cnde.portal.workflow.appteam;

import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
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
  public void handleMessage(Message<?> message) {
    logger.info("trackerStatus:handleMessage(message: {}, payload: {})", message,
        message.getPayload());

    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine("some-machine-id");

    stateMachine.sendEvent(Events.TRACKER_FINISHED);
  }
}
