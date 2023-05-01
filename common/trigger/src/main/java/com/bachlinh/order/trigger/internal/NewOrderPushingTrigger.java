package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.utils.SharedCustomerUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ActiveReflection
public class NewOrderPushingTrigger extends AbstractTrigger<Order> {
    private SimpMessagingTemplate simpMessagingTemplate;

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
    public void doExecute(Order entity) {
        Map<String, String> message = new HashMap<>(2);
        message.put("order_id", (entity).getId());
        message.put("time_order", entity.getTimeOrder().toString());
        Collection<String> adminIs = SharedCustomerUtils.getAllId();
        adminIs.forEach(id -> simpMessagingTemplate.convertAndSendToUser(id, "/queue/connect", JacksonUtils.writeObjectAsString(message)));
    }

    @Override
    protected void inject() {
        if (simpMessagingTemplate == null) {
            simpMessagingTemplate = getDependenciesResolver().resolveDependencies(SimpMessagingTemplate.class);
        }
    }
}
