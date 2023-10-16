package com.bachlinh.order.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.http.server.channel.stomp.publisher.NotificationPublisher;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.trigger.AbstractTrigger;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@ApplyOn(entity = Order.class)
public class NewOrderPushingTrigger extends AbstractTrigger<Order> {

    private NotificationPublisher notificationPublisher;
    private String subscribePath;

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

        notificationPublisher.pushNotification(message);
    }

    @Override
    protected void inject() {
        if (notificationPublisher == null) {
            notificationPublisher = getDependenciesResolver().resolveDependencies("nettyConnectionManager", NotificationPublisher.class);
        }
        if (subscribePath == null) {
            subscribePath = getEnvironment().getProperty("server.stomp.subscribe.path");
        }
    }
}
