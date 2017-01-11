package io.flowing.retail.commands.orchestration.camunda;

import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import io.flowing.retail.commands.Customer;
import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderItem;
import io.flowing.retail.commands.OrderRepository;
import io.flowing.retail.commands.channel.EventConsumer;

public class OrderEventConsumerCamunda extends EventConsumer {

  private ProcessEngine engine;
  private OrderRepository orderRepository = OrderRepository.instance;

  public OrderEventConsumerCamunda() {
    init();
  }

  private void init() {
    StandaloneInMemProcessEngineConfiguration config = new StandaloneInMemProcessEngineConfiguration();
    engine = config.buildProcessEngine();

    engine.getRepositoryService().createDeployment().addClasspathResource("order.bpmn").deploy();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          engine.close();
        } catch (Exception e) {
          throw new RuntimeException("Could not disconnect: " + e.getMessage(), e);
        }
      }
    });
  }

  private boolean correlateResponseEvent(String eventName, VariableMap correlationKeys, VariableMap variables) {
    ExecutionQuery query = engine.getRuntimeService().createExecutionQuery() //
        .variableValueEquals("responseEventName", eventName);

    for (String key : correlationKeys.keySet()) {
      query.processVariableValueEquals(key, correlationKeys.get(key));
    }

    if (query.count() == 0) {
      return false;
    }

    Execution execution = query.singleResult();
    engine.getRuntimeService().signal(execution.getId(), variables);
    return true;
  }

  public void handleOrderPlaced(String correlationId, Order order) {
    // "Persist" order
    orderRepository.persistOrder(order);

    // Start workflow for order
    engine.getRuntimeService().startProcessInstanceByKey( //
        "order",
        Variables.createVariables() //
            .putValue("correlationId", correlationId) //
            .putValue("orderId", order.getId()) //
    );
  }

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    if ("Event".equals(type) && "OrderPlaced".equals(name)) {
      String correlationId = event.getString("correlationId");
      Order order = parseOrder(event.getJsonObject("order"));

      // OrderService.instance.processOrder(correlationId, order);
      handleOrderPlaced(correlationId, order);
      return true;
    } else {
      // Try to correlate by possible ids in this priority
      VariableMap correlationKeys = Variables.createVariables();
      if (event.get("orderId") != null) {
        correlationKeys.put("orderId", event.getString("orderId"));
      } else if (event.get("refId") != null) {
        correlationKeys.put("orderId", event.getString("refId"));
      } else if (event.get("pickId") != null) {
        correlationKeys.put("pickId", event.getString("pickId"));
      } else if (event.get("shipmentId") != null) {
        correlationKeys.put("shipmentId", event.getString("shipmentId"));
      }

      // add all possible additional correlation keys as variables to the flow
      VariableMap variables = Variables.createVariables();
      if (event.get("pickId") != null) {
        variables.putValue("pickId", event.getString("pickId"));
      }
      if (event.get("shipmentId") != null) {
        variables.putValue("shipmentId", event.getString("shipmentId"));
      }

      return correlateResponseEvent(name, correlationKeys, variables);
    }
  }

  private Order parseOrder(JsonObject orderJson) {
    Order order = new Order();

    // Order Service is NOT interested in customer id - ignore:
    JsonObject customerJson = orderJson.getJsonObject("customer");
    orderJson.getString("customerId");

    Customer customer = new Customer() //
        .setName(customerJson.getString("name")) //
        .setAddress(customerJson.getString("address"));
    order.setCustomer(customer);

    JsonArray jsonArray = orderJson.getJsonArray("items");
    for (JsonObject itemJson : jsonArray.getValuesAs(JsonObject.class)) {
      order.addItem( //
          new OrderItem() //
              .setArticleId(itemJson.getString("articleId")) //
              .setAmount(itemJson.getInt("amount")));
    }

    return order;
  }

}
