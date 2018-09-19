package io.pivotal.cnde.portal.workflow.appteam;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageChannel;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {
        AppTeamStateMachineConfig.class,
    }
)
class AppTeamStateMachineTest {

  @MockBean
  private Action<States, Events> createTrackerProjectAction;
  @MockBean(name = Tracker.REQUEST)
  private MessageChannel trackerRequestChannel;
  @MockBean
  private StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister;

  @Autowired
  private StateMachineFactory<States, Events> stateMachineFactory;

  @Test
  void trackerRequest() throws Exception {
    StateMachine<States, Events> stateMachine = stateMachineFactory
        .getStateMachine("some-machine-id");
    stateMachine.start();

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

    verify(createTrackerProjectAction).execute(any());
  }

  @Test
  void trackerFinish() throws Exception {
    StateMachine<States, Events> stateMachine = stateMachineFactory
        .getStateMachine("some-machine-id");
    stateMachine.start();

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
