package io.pivotal.cnde.portal.workflow.appteam;

import io.pivotal.cnde.portal.workflow.TestApplicationRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.boot.autoconfigure.StateMachineJpaRepositoriesAutoConfiguration;
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
    classes = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        StateMachineJpaRepositoriesAutoConfiguration.class,
        AppTeamStateMachineConfig.class,
        AppTeamStateMachinePersistConfig.class,
    },
    properties = {
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false",
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    }
)
class AppTeamStateMachineTest {

  @Autowired
  private StateMachineService<States, Events> stateMachineService;

  @AfterEach
  void tearDown() {
    stateMachineService.releaseStateMachine("some-machine-id");
  }

  @Test
  void approve() throws Exception {
    StateMachine<States, Events> stateMachine = stateMachineService
        .acquireStateMachine("some-machine-id");

    Message<Events> approveEvent = MessageBuilder.withPayload(Events.APPROVE)
        .setHeader("approver", "some-approver")
        .build();
    StateMachineTestPlan<States, Events> plan =
        StateMachineTestPlanBuilder.<States, Events>builder()
            .stateMachine(stateMachine)
            .step().expectState(States.START).and()
            .step().sendEvent(approveEvent)
            .expectState(States.APPROVED)
            .expectVariable("approver", "some-approver").and()
            .build();
    plan.test();
  }
}
