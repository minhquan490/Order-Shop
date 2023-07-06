package com.bachlinh.order.trigger.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.handler.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ActiveReflection
public class NewOrderPushingTrigger extends AbstractTrigger<Order> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private WebSocketSessionManager webSocketSessionManager;

    @ActiveReflection
    public NewOrderPushingTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    protected String getTriggerName() {
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
        Map<String, String> message = new HashMap<>(2);
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
            webSocketSessionManager = getDependenciesResolver().resolveDependencies(WebSocketSessionManager.class);
        }
    }
}
