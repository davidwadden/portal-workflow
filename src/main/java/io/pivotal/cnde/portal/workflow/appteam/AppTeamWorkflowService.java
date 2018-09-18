package io.pivotal.cnde.portal.workflow.appteam;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

@Service
public class AppTeamWorkflowService {

  private final StateMachineService<States, Events> stateMachineService;

  public AppTeamWorkflowService(StateMachineService<States, Events> stateMachineService) {
    this.stateMachineService = stateMachineService;
  }

  public void triggerWorkflow(String appTeamName) {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine(appTeamName);

    if (stateMachine.getState().getId() != States.START) {
      throw new IllegalStateException();
    }

    Message<Events> approveEvent = MessageBuilder.withPayload(Events.APPROVE)
        .setHeader("approver", "some-approver")
        .build();
    stateMachine.sendEvent(approveEvent);
  }

}
