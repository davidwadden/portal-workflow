package io.pivotal.cnde.portal.workflow.appteam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.pivotal.cnde.portal.workflow.support.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultExtendedState;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

  @Mock
  private StateMachineService<States, Events> mockStateMachineService;
  @Mock
  private StateMachine<States, Events> mockStateMachine;
  @Mock
  private State<States, Events> mockState;

  private WorkflowService workflowService;

  @BeforeEach
  void setUp() {
    workflowService = new WorkflowService(mockStateMachineService);
  }

  @Test
  void triggerWorkflow() {
    when(mockState.getId()).thenReturn(States.START);
    when(mockStateMachine.getState()).thenReturn(mockState);
    ExtendedState extendedState = new DefaultExtendedState();
    when(mockStateMachine.getExtendedState()).thenReturn(extendedState);
    when(mockStateMachineService.acquireStateMachine(any())).thenReturn(mockStateMachine);

    workflowService
        .triggerWorkflow("some-workflow-id", "some-project-name", "some-owner-email");

    verify(mockStateMachineService).acquireStateMachine("some-workflow-id");

    verify(mockStateMachine).sendEvent(Events.TRACKER_STARTED);

    assertThat(extendedState.getVariables())
        .containsEntry("projectName", "some-project-name")
        .containsEntry("ownerEmail", "some-owner-email");
  }

  @Test
  void triggerWorkflow_notStart() {
    when(mockState.getId()).thenReturn(States.TRACKER_PROVISIONING);
    when(mockStateMachine.getState()).thenReturn(mockState);
    when(mockStateMachineService.acquireStateMachine(any())).thenReturn(mockStateMachine);

    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(
            () -> workflowService
                .triggerWorkflow("some-workflow-id", "some-project-name", "some-owner-email"));

    verify(mockStateMachine, never()).sendEvent(any(Events.class));
  }

  @Test
  void finishTracker() {
    when(mockState.getId()).thenReturn(States.TRACKER_PROVISIONING);
    when(mockStateMachine.getState()).thenReturn(mockState);
    when(mockStateMachineService.acquireStateMachine(any())).thenReturn(mockStateMachine);

    workflowService.finishTracker("some-workflow-id");

    verify(mockStateMachineService).acquireStateMachine("some-workflow-id");

    verify(mockStateMachine).sendEvent(Events.TRACKER_FINISHED);
  }
}
