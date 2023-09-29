package com.bachlinh.order.trigger;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.entity.trigger.AbstractTrigger;
import com.bachlinh.order.repository.CartRepository;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.EmailTrashRepository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;

@ActiveReflection
@ApplyOn(entity = Customer.class)
public class CreateCustomerAssociateDataTrigger extends AbstractTrigger<Customer> {

    private EntityFactory entityFactory;
    private CartRepository cartRepository;
    private EmailTrashRepository emailTrashRepository;
    private EmailFoldersRepository emailFoldersRepository;

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT};
    }

    @Override
    public String getTriggerName() {
        return "createCustomerAssociateTrigger";
    }

    @Override
    protected void doExecute(Customer entity) {
        createCart(entity);
        createEmailTrash(entity);
        createEmailFolder(entity);
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
        if (cartRepository == null) {
            cartRepository = resolveRepository(CartRepository.class);
        }
        if (emailTrashRepository == null) {
            emailTrashRepository = resolveRepository(EmailTrashRepository.class);
        }
        if (emailFoldersRepository == null) {
            emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        }
    }

    private void createCart(Customer entity) {
        Cart cart = entityFactory.getEntity(Cart.class);
        cart.setCustomer(entity);
        cartRepository.saveCart(cart);
    }

    private void createEmailTrash(Customer entity) {
        EmailTrash emailTrash = entityFactory.getEntity(EmailTrash.class);
        emailTrash.setCustomer(entity);
        emailTrashRepository.saveEmailTrash(emailTrash);
    }

    private void createEmailFolder(Customer entity) {
        EmailFolders emailFolders = entityFactory.getEntity(EmailFolders.class);
        emailFolders.setOwner(entity);
        emailFolders.setEmailClearPolicy(-1);
        emailFolders.setTimeCreated(Timestamp.from(Instant.now(Clock.systemDefaultZone())));
        emailFolders.setName("Default");
        emailFoldersRepository.saveEmailFolder(emailFolders);
    }
}
