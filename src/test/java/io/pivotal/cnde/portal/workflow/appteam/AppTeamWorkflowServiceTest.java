package io.pivotal.cnde.portal.workflow.appteam;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.pivotal.cnde.portal.workflow.support.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;

@ExtendWith(MockitoExtension.class)
class AppTeamWorkflowServiceTest {

  @Mock
  private StateMachineService<States, Events> mockStateMachineService;
  @Mock
  private StateMachine<States, Events> mockStateMachine;
  @Mock
  private State<States, Events> mockState;

  private AppTeamWorkflowService workflowService;

  @BeforeEach
  void setUp() {
    workflowService = new AppTeamWorkflowService(mockStateMachineService);
  }

  @Test
  void triggerWorkflow() {
    when(mockState.getId()).thenReturn(States.START);
    when(mockStateMachine.getState()).thenReturn(mockState);
    when(mockStateMachineService.acquireStateMachine(any())).thenReturn(mockStateMachine);

    workflowService.triggerWorkflow("some-app-team-name");

    verify(mockStateMachineService).acquireStateMachine("some-app-team-name");
  }

}
