package io.pivotal.cnde.portal.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
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
    EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents> {

  private static final Logger logger = LoggerFactory.getLogger(StateMachineConfig.class);

  @Autowired
  private StateMachineRuntimePersister<OrderStates, OrderEvents, String> stateMachineRuntimePersister;

  @Override
  public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states)
      throws Exception {

    states.withStates()
        .initial(OrderStates.SUBMITTED)
        .state(OrderStates.PAID)
        .end(OrderStates.FULFILLED)
        .end(OrderStates.CANCELLED);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions)
      throws Exception {
    transitions
        .withExternal().source(OrderStates.SUBMITTED).target(OrderStates.PAID)
        .event(OrderEvents.PAY).action(payAction())
        .and()
        .withExternal().source(OrderStates.PAID).target(OrderStates.FULFILLED)
        .event(OrderEvents.FULFILL)
        .and()
        .withExternal().source(OrderStates.SUBMITTED).target(OrderStates.CANCELLED)
        .event(OrderEvents.CANCEL)
        .and()
        .withExternal().source(OrderStates.PAID).target(OrderStates.CANCELLED)
        .event(OrderEvents.CANCEL);
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config)
      throws Exception {

    StateMachineListener<OrderStates, OrderEvents> listener = new StateMachineListenerAdapter<OrderStates, OrderEvents>() {
      @Override
      public void stateChanged(State<OrderStates, OrderEvents> from,
          State<OrderStates, OrderEvents> to) {
        logger.info(String.format("stateChanged(from: %s, to: %s)", from, to));
      }

      @Override
      public void eventNotAccepted(Message<OrderEvents> event) {
        logger.info(String.format("eventNotAccepted(event: %s)", event));
      }
    };

    config
        .withConfiguration()
        .autoStartup(false)
        .listener(listener)
        .and()
        .withPersistence()
        .runtimePersister(stateMachineRuntimePersister);
  }


  @Bean
  public Action<OrderStates, OrderEvents> payAction() {
    return context -> {
      Float paid = context.getMessage().getHeaders().get("paid", Float.class);
      context.getExtendedState().getVariables().put("paid", paid);
      logger.info("payAction:execute(paid: {})", paid);
    };
  }


}

enum OrderStates {
  OTHER,
  SUBMITTED,
  PAID,
  FULFILLED,
  CANCELLED;
}

enum OrderEvents {
  FULFILL,
  PAY,
  CANCEL;
}
