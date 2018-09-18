package io.pivotal.cnde.portal.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

@Service
public class JobService {

  private static final Logger logger = LoggerFactory.getLogger(JobService.class);

  private final StateMachineService<OrderStates, OrderEvents> stateMachineService;

  public JobService(
      StateMachineService<OrderStates, OrderEvents> stateMachineService) {
    this.stateMachineService = stateMachineService;
  }

  public void submitJob() {
    logger.info("submitting job");

    StateMachine<OrderStates, OrderEvents> machine = stateMachineService
        .acquireStateMachine("some-machine-id");

    Message<OrderEvents> payEvent = MessageBuilder
        .withPayload(OrderEvents.PAY)
        .setHeader("paid", 7.32f)
        .build();
    machine.sendEvent(payEvent);
  }

  public StateMachine<OrderStates, OrderEvents> getJob() {
    return stateMachineService.acquireStateMachine("some-machine-id");
  }
}
