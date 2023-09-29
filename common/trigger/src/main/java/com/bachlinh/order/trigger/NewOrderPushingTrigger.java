package com.bachlinh.order.trigger;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.trigger.AbstractTrigger;
import com.bachlinh.order.handler.tcp.context.WebSocketSessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@ApplyOn(entity = Order.class)
public class NewOrderPushingTrigger extends AbstractTrigger<Order> {

    private WebSocketSessionManager webSocketSessionManager;

    @Override
    public String getTriggerName() {
        return "newOrderPushingTrigger";
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT};
    }

    @Override
    protected void doExecute(Order entity) {
        Map<String, String> message = HashMap.newHashMap(2);
        message.put("order_id", (entity).getId());
        message.put("time_order", entity.getTimeOrder().toString());
        try {
            webSocketSessionManager.pushMessageToAllAdmin(message);
        } catch (IOException e) {
            log.error("Can not push notification to admin", e);
        }
    }

    @Override
    protected void inject() {
        if (webSocketSessionManager == null) {
            webSocketSessionManager = resolveDependencies(WebSocketSessionManager.class);
        }
    }
}
