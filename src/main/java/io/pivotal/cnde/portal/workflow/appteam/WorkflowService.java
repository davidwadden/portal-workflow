package io.pivotal.cnde.portal.workflow.appteam;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

  private final StateMachineService<States, Events> stateMachineService;

  public WorkflowService(StateMachineService<States, Events> stateMachineService) {
    this.stateMachineService = stateMachineService;
  }

  public void triggerWorkflow(String workflowId, String projectName, String ownerEmail) {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine(workflowId);

    States currentState = stateMachine.getState().getId();
    if (currentState != States.START) {
      throw new IllegalStateException(String.format("Invalid state: %s", currentState));
    }

    stateMachine.getExtendedState().getVariables().put("projectName", projectName);
    stateMachine.getExtendedState().getVariables().put("ownerEmail", ownerEmail);

    stateMachine.sendEvent(Events.TRACKER_STARTED);
  }

  public void finishTracker(String workflowId) {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine(workflowId);

    stateMachine.sendEvent(Events.TRACKER_FINISHED);
  }
}
