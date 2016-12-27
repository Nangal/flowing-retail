package io.flowing.retail.commands.orchestration.dsl;

import java.util.List;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderService;

public class OrderServiceFlowImpl extends OrderService{

  public void processOrder(Order order) {
    System.out.println("order will be processed: " + order);
    
    Object payload = null;
    
    new FlowBuilder()

    // issue ReserveGoodsCommand
      .issueCommand("ReserveGoodsCommand", payload)
    
    // issue DoPaymentCommand
      .issueCommand("DoPaymentCommand", payload)
    
    // wait for occurrence of the events:
    // - GoodsReservedEvent
    // - PaymentReceivedEvent
    // or some error message, in which case we have to cleanup
      .waitForEvents("GoodsReservedEvent", "PaymentReceivedEvent")
    
    // issue GoodsPickedEvent
    // wait for occurrence of the events:
    // - GoodsPickedEvent
      .issueCommand("GoodsPickedEvent", payload)
      .waitForEvents("GoodsPickedEvent")
    
    // issue ShipCommand
    // wait for occurrence of the events:
    // - ShipmentShippedEvent
      .issueCommand("ShipCommand", payload)
      .waitForEvents("ShipmentShippedEvent")
      
    .execute();
    
  }
  
  public static class FlowBuilder {

    public FlowBuilder issueCommand(String string, Object payload) {
      // TODO Auto-generated method stub
      return null;
    }

    public void execute() {
      // TODO Auto-generated method stub
      
    }

    public FlowBuilder waitForEvents(String... events) {
      // TODO Auto-generated method stub
      return null;
    }
    
  }

  public Order getOrder(String orderId) {
    // TODO Auto-generated method stub
    return null;
  }

  public List<Order> findOrders() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
}
