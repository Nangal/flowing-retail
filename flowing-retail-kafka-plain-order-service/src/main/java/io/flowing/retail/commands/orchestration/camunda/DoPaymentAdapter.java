package io.flowing.retail.commands.orchestration.camunda;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderEventProducer;
import io.flowing.retail.commands.OrderRepository;

public class DoPaymentAdapter extends CommandPubEventSubAdapter {

  @Override
  public void execute(ActivityExecution execution) throws Exception {
    OrderEventProducer eventProducer = new OrderEventProducer();
    Order order = OrderRepository.instance.getOrder((String)execution.getVariable("orderId")); 
    execution.setVariableLocal("responseEventName", "PaymentReceived");
    // TODO: Maybe use eventId and responseId?
    // what about transactionId
    eventProducer.publishCommandDoPayment(order);
    // Can response be faster than the current transaction?
    // Kafka Client Commit?
  }

}
