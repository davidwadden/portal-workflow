package io.pivotal.cnde.portal.workflow.appteam;

import static org.mockito.Mockito.verify;

import io.pivotal.cnde.portal.workflow.TestApplicationRunner;
import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(TestApplicationRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    properties = {
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false",
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    }
)
class TrackerResponseMessageHandlerTest {

  @MockBean
  private AppTeamWorkflowService mockAppTeamWorkflowService;

  @Qualifier(Tracker.RESPONSE)
  @Autowired
  private MessageChannel trackerResponseChannel;

  @Test
  void handleMessage() {
    String responsePayload = "{\"workflowId\":\"some-workflow-id\",\"@type\":\"create-tracker-project\"}";
    Message<byte[]> message = MessageBuilder.withPayload(responsePayload.getBytes())
        .build();
    trackerResponseChannel.send(message);

    verify(mockAppTeamWorkflowService).finishTracker("some-workflow-id");
  }
}
