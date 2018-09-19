package io.pivotal.cnde.portal.workflow.appteam;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import io.pivotal.cnde.portal.workflow.TestApplicationRunner;
import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Import(TestApplicationRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false",
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
})
class AppTeamWorkflowServiceIntegrationTest {

  private final AppTeamWorkflowService appTeamWorkflowService;
  private final StateMachineService<States, Events> stateMachineService;
  private final MessageChannel trackerRequestChannel;
  private final MessageCollector messageCollector;

  @Autowired
  AppTeamWorkflowServiceIntegrationTest(
      AppTeamWorkflowService appTeamWorkflowService,
      StateMachineService<States, Events> stateMachineService,
      @Qualifier(Tracker.REQUEST) MessageChannel trackerRequestChannel,
      MessageCollector messageCollector) {
    this.appTeamWorkflowService = appTeamWorkflowService;
    this.stateMachineService = stateMachineService;
    this.trackerRequestChannel = trackerRequestChannel;
    this.messageCollector = messageCollector;
  }

  @Test
  void triggerWorkflow() {
    appTeamWorkflowService
        .triggerWorkflow("some-workflow-id", "some-project-name", "some-owner-email");

    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine("some-workflow-id");
    assertThat(stateMachine.getState().getId()).isEqualTo(States.TRACKER_PROVISIONING);

    Message<?> message = messageCollector
        .forChannel(trackerRequestChannel)
        .poll();
    assertThat(message).isNotNull();
    assertThatJson(message.getPayload())
        .isObject()
        .containsEntry("@type", "create-tracker-project")
        .containsEntry("workflowId", "some-workflow-id")
        .containsEntry("projectName", "some-project-name")
        .containsEntry("ownerEmail", "some-owner-email");
  }

  @Disabled
  @Test
  void finishTracker() {
    appTeamWorkflowService
        .triggerWorkflow("some-workflow-id", "some-project-name", "some-owner-email");

    appTeamWorkflowService.finishTracker("some-workflow-id");

    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine("some-workflow-id");
    assertThat(stateMachine.getState().getId()).isEqualTo(States.FINISH);
  }
}
