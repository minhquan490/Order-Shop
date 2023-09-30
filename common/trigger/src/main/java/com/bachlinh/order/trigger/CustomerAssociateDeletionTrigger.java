package com.bachlinh.order.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.entity.model.CustomerMedia;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.entity.model.TemporaryToken;
import com.bachlinh.order.entity.trigger.AbstractTrigger;
import com.bachlinh.order.repository.AddressRepository;
import com.bachlinh.order.repository.CartRepository;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.repository.CustomerInfoChangeHistoryRepository;
import com.bachlinh.order.repository.CustomerMediaRepository;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.repository.EmailTrashRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.repository.OrderRepository;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.repository.TemporaryTokenRepository;
import com.bachlinh.order.repository.UserAssignmentRepository;
import org.springframework.util.StringUtils;

import java.util.Collection;

@ApplyOn(entity = Customer.class)
@ActiveReflection
public class CustomerAssociateDeletionTrigger extends AbstractTrigger<Customer> {

    private CartRepository cartRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private EmailTrashRepository emailTrashRepository;
    private CustomerMediaRepository customerMediaRepository;
    private TemporaryTokenRepository temporaryTokenRepository;
    private AddressRepository addressRepository;
    private OrderRepository orderRepository;
    private CustomerAccessHistoryRepository customerAccessHistoryRepository;
    private UserAssignmentRepository userAssignmentRepository;
    private LoginHistoryRepository loginHistoryRepository;
    private CustomerInfoChangeHistoryRepository customerInfoChangeHistoryRepository;
    private EmailFoldersRepository emailFoldersRepository;
    private EmailRepository emailRepository;

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_DELETE};
    }

    @Override
    public String getTriggerName() {
        return "CustomerAssociateDeletion";
    }

    @Override
    protected void doExecute(Customer entity) {
        if (!StringUtils.hasText(entity.getId())) {
            return;
        }
        Cart cart = cartRepository.getCartOfCustomer(entity.getId());
        cartRepository.deleteCart(cart);

        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByCustomer(entity);
        refreshTokenRepository.deleteRefreshToken(refreshToken);

        EmailTrash emailTrash = emailTrashRepository.getTrashOfCustomer(entity);
        emailTrashRepository.deleteTrash(emailTrash);

        CustomerMedia customerMedia = customerMediaRepository.getCustomerMedia(entity);
        customerMediaRepository.deleteCustomerMedia(customerMedia);

        TemporaryToken temporaryToken = temporaryTokenRepository.getTemporaryToken(entity);
        temporaryTokenRepository.deleteTemporaryToken(temporaryToken);

        Collection<Address> addresses = addressRepository.getAddressOfCustomer(entity);
        addressRepository.deleteAddresses(addresses);

        Collection<Order> orders = orderRepository.getOrdersOfCustomerForDelete(entity);
        orderRepository.deleteOrders(orders);

        Collection<CustomerAccessHistory> customerAccessHistories = customerAccessHistoryRepository.getHistoriesOfCustomer(entity);
        customerAccessHistoryRepository.deleteAll(customerAccessHistories);

        userAssignmentRepository.deleteUserAssignmentOfCustomer(entity);

        Collection<LoginHistory> loginHistories = loginHistoryRepository.getHistories(entity);
        loginHistoryRepository.deleteLoginHistories(loginHistories);

        Collection<CustomerInfoChangeHistory> customerInfoChangeHistories = customerInfoChangeHistoryRepository.getHistoriesChangeOfCustomer(entity, -1);
        customerInfoChangeHistoryRepository.deleteHistories(customerInfoChangeHistories);

        Collection<EmailFolders> emailFolders = emailFoldersRepository.getEmailFoldersOfCustomer(entity);
        emailFolders.forEach(folder -> emailFoldersRepository.delete(folder.getId()));

        Collection<Email> emails = emailRepository.getEmailsOfCustomer(entity);
        emailRepository.deleteEmail(emails);
    }

    @Override
    protected void inject() {
        if (cartRepository == null) {
            cartRepository = resolveRepository(CartRepository.class);
        }
        if (refreshTokenRepository == null) {
            refreshTokenRepository = resolveRepository(RefreshTokenRepository.class);
        }
        if (emailTrashRepository == null) {
            emailTrashRepository = resolveRepository(EmailTrashRepository.class);
        }
        if (customerMediaRepository == null) {
            customerMediaRepository = resolveRepository(CustomerMediaRepository.class);
        }
        if (temporaryTokenRepository == null) {
            temporaryTokenRepository = resolveRepository(TemporaryTokenRepository.class);
        }
        if (addressRepository == null) {
            addressRepository = resolveRepository(AddressRepository.class);
        }
        if (orderRepository == null) {
            orderRepository = resolveRepository(OrderRepository.class);
        }
        if (customerAccessHistoryRepository == null) {
            customerAccessHistoryRepository = resolveRepository(CustomerAccessHistoryRepository.class);
        }
        if (userAssignmentRepository == null) {
            userAssignmentRepository = resolveRepository(UserAssignmentRepository.class);
        }
        if (loginHistoryRepository == null) {
            loginHistoryRepository = resolveRepository(LoginHistoryRepository.class);
        }
        if (customerInfoChangeHistoryRepository == null) {
            customerInfoChangeHistoryRepository = resolveRepository(CustomerInfoChangeHistoryRepository.class);
        }
        if (emailFoldersRepository == null) {
            emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        }
        if (emailRepository == null) {
            emailRepository = resolveRepository(EmailRepository.class);
        }
    }
}
