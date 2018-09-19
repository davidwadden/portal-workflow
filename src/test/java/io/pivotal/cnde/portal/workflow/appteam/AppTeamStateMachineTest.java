package io.pivotal.cnde.portal.workflow.appteam;

import static org.assertj.core.api.Assertions.assertThat;

import io.pivotal.cnde.portal.workflow.TestApplicationRunner;
import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@EnableAutoConfiguration
@Import(TestApplicationRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    properties = {
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false",
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    }
)
class AppTeamStateMachineTest {

  @Autowired
  private StateMachineService<States, Events> stateMachineService;

  @Autowired
  private MessageCollector messageCollector;

  @Qualifier(Tracker.PROVISION)
  @Autowired
  private MessageChannel trackerProvisionChannel;

  @AfterEach
  void tearDown() {
    stateMachineService.releaseStateMachine("some-machine-id");
  }

  @Test
  void workflow() throws Exception {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine("some-machine-id");

    //@formatter:off
    StateMachineTestPlan<States, Events> plan =
        StateMachineTestPlanBuilder.<States, Events>builder()
            .stateMachine(stateMachine)
            .step()
              .expectState(States.START)
            .and()
            .step()
              .sendEvent(Events.TRACKER_STARTED)
              .expectState(States.TRACKER_PROVISIONING)
            .and()
            .step()
              .sendEvent(Events.TRACKER_FINISHED)
              .expectState(States.FINISH)
            .and()
            .build();
    //@formatter:on

    plan.test();
  }

  @Test
  void provisionTracker() throws Exception {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine("some-machine-id");

    //@formatter:off
    StateMachineTestPlan<States, Events> plan =
        StateMachineTestPlanBuilder.<States, Events>builder()
            .stateMachine(stateMachine)
            .step()
              .expectState(States.START)
            .and()
            .step()
              .sendEvent(Events.TRACKER_STARTED)
              .expectState(States.TRACKER_PROVISIONING)
            .and()
            .build();
    //@formatter:on

    plan.test();

    Message<?> message = messageCollector
        .forChannel(trackerProvisionChannel)
        .poll();
    assertThat(message).isNotNull();

    String expectedPayload = "{\"@type\":\"create-tracker-project\",\"workflowId\":\"some-machine-id\",\"projectName\":\"some-project-name\",\"ownerEmail\":\"some-owner-email\"}";
    assertThat(message.getPayload()).isEqualTo(expectedPayload);
  }

  @Test
  void trackerStatus() throws Exception {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine("some-machine-id");
    stateMachine.sendEvent(Events.TRACKER_STARTED);

    //@formatter:off
    StateMachineTestPlan<States, Events> plan =
        StateMachineTestPlanBuilder.<States, Events>builder()
            .stateMachine(stateMachine)
            .step()
              .expectState(States.TRACKER_PROVISIONING)
            .and()
            .step()
              .sendEvent(Events.TRACKER_FINISHED)
              .expectState(States.FINISH)
            .and()
            .build();
    //@formatter:on

    plan.test();
  }

}
