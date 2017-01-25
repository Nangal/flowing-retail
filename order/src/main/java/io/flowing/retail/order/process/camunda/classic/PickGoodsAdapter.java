package io.flowing.retail.order.process.camunda.classic;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;

public class PickGoodsAdapter extends CommandPubEventSubAdapter {

  @Override
  public void execute(ActivityExecution execution) throws Exception {
    OrderEventProducer eventProducer = new OrderEventProducer();
    Order order = orderRepository.getOrder((String)execution.getVariable("orderId")); 
    execution.setVariableLocal("responseEventName", "GoodsPicked");
    // TODO: Maybe use eventId and responseId?
    // what about transactionId
    eventProducer.publishCommandPickGoods(order);
    // Can response be faster than the current transaction?
    // Kafka Client Commit?
  }

}
