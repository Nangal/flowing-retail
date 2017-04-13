package io.flowing.retail.inventory.application;

import javax.json.JsonObjectBuilder;

import io.flowing.retail.adapter.EventProducer;

public class InventoryEventProducer extends EventProducer {

  public void publishEventGoodsReserved(String transactionId, String refId, String reason) {
    JsonObjectBuilder json = createEventPayloadJson("GoodsReserved", transactionId);
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

  public void publishEventGoodsNotReserved(String transactionId, String refId, String reason) {
    JsonObjectBuilder json = createEventPayloadJson("GoodsNotReserved", transactionId);
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

  public void publishEventGoodsFetched(String transactionId, String refId, String reason, String pickId) {
    JsonObjectBuilder json = createEventPayloadJson("GoodsFetched", transactionId);
    json //
        .add("refId", refId) //
        .add("reason", reason) //
        .add("pickId", pickId);
    send(json);
  }

  public void publishEventPickError(String transactionId, String refId, String reason) {
    JsonObjectBuilder json = createEventPayloadJson("GoodsNotFetched", transactionId);
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

}
