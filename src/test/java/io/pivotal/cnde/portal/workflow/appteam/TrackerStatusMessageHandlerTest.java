package io.pivotal.cnde.portal.workflow.appteam;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.pivotal.cnde.portal.workflow.TestApplicationRunner;
import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import io.pivotal.cnde.portal.workflow.support.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(TestApplicationRunner.class)
@ExtendWith({
    SpringExtension.class,
    MockitoExtension.class,
})
@SpringBootTest(
    properties = {
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false",
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    }
)
class TrackerStatusMessageHandlerTest {

  @MockBean
  private StateMachineService<States, Events> mockStateMachineService;
  @MockBean
  private StateMachine<States, Events> mockStateMachine;

  @Qualifier(Tracker.STATUS)
  @Autowired
  private MessageChannel trackerStatusChannel;

  @Test
  void handleMessage() {
    when(mockStateMachineService.acquireStateMachine(any())).thenReturn(mockStateMachine);

    String statusPayload = "{\"workflowId\":\"some-workflow-id\",\"@type\":\"create-tracker-project\"}";
    Message<byte[]> message = MessageBuilder.withPayload(statusPayload.getBytes())
        .build();
    trackerStatusChannel.send(message);

    verify(mockStateMachineService).acquireStateMachine("some-workflow-id");
    verify(mockStateMachine).sendEvent(Events.TRACKER_FINISHED);
  }
}
