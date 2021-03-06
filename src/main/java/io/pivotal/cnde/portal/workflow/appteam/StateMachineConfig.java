package io.pivotal.cnde.portal.workflow.appteam;

import io.pivotal.cnde.portal.workflow.appteam.TrackerStreamConfig.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends
    EnumStateMachineConfigurerAdapter<States, Events> {

  private static final Logger logger =
      LoggerFactory.getLogger(StateMachineConfig.class);

  @Autowired
  private StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister;

  @Qualifier(Tracker.REQUEST)
  @Autowired
  private MessageChannel trackerRequestChannel;

  @Override
  public void configure(StateMachineStateConfigurer<States, Events> states)
      throws Exception {

    states.withStates()
        .initial(States.START)
        .state(States.TRACKER_PROVISIONING)
        .end(States.FINISH);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
      throws Exception {

    //@formatter:off
    transitions
        .withExternal()
          .source(States.START)
          .target(States.TRACKER_PROVISIONING)
          .event(Events.TRACKER_STARTED)
          .action(createTrackerProjectAction())
        .and()
        .withExternal()
          .source(States.TRACKER_PROVISIONING)
          .target(States.FINISH)
          .event(Events.TRACKER_FINISHED);
    //@formatter:on
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<States, Events> config)
      throws Exception {

    StateMachineListener<States, Events> listener = new LoggingStateMachineListenerAdapter();

    config
        .withConfiguration()
        .listener(listener)
        .and()
        .withPersistence()
        .runtimePersister(stateMachineRuntimePersister);
  }

  @Bean
  public Action<States, Events> createTrackerProjectAction() {

    return context -> {
      CreateTrackerProjectDto commandDto = new CreateTrackerProjectDto(
          context.getStateMachine().getId(),
          context.getExtendedState().get("projectName", String.class),
          context.getExtendedState().get("ownerEmail", String.class));

      Message<CreateTrackerProjectDto> message = MessageBuilder
          .withPayload(commandDto)
          .build();

      logger.info(String.format(
          "createTrackerProject(message: %s, payload: %s)", message, message.getPayload()));
      trackerRequestChannel.send(message);
    };
  }

  private static class LoggingStateMachineListenerAdapter extends
      StateMachineListenerAdapter<States, Events> {

    private static final Logger logger =
        LoggerFactory.getLogger(LoggingStateMachineListenerAdapter.class);

    @Override
    public void stateChanged(State<States, Events> from,
        State<States, Events> to) {
      logger.info(String.format("stateChanged(from: %s, to: %s)", from, to));
    }

    @Override
    public void eventNotAccepted(Message<Events> event) {
      logger.info(String.format("eventNotAccepted(event: %s)", event));
    }
  }
}

enum States {
  START,
  TRACKER_PROVISIONING,
  FINISH
}

enum Events {
  TRACKER_STARTED,
  TRACKER_FINISHED
}
