package io.pivotal.cnde.portal.workflow.appteam;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

@Service
public class AppTeamWorkflowService {

  private final StateMachineService<States, Events> stateMachineService;

  public AppTeamWorkflowService(StateMachineService<States, Events> stateMachineService) {
    this.stateMachineService = stateMachineService;
  }

  public void triggerWorkflow(String workflowId) {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine(workflowId);

    States currentState = stateMachine.getState().getId();
    if (currentState != States.START) {
      throw new IllegalStateException(String.format("Invalid state: %s", currentState));
    }

    stateMachine.sendEvent(Events.TRACKER_STARTED);
  }

}
