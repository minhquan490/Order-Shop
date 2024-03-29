package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.web.repository.spi.ProductCartRepository;
import com.bachlinh.order.web.repository.spi.ProductCategoryRepository;
import com.bachlinh.order.web.repository.spi.ProductMediaRepository;

@ApplyOn(entity = Product.class)
@ActiveReflection
public class ProductAssociateDeletionTrigger extends AbstractRepositoryTrigger<Product> {

    private ProductCategoryRepository productCategoryRepository;
    private ProductCartRepository productCartRepository;
    private ProductMediaRepository productMediaRepository;

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
        return "ProductAssociateDeletion";
    }

    @Override
    protected void doExecute(Product entity) {
        productCategoryRepository.deleteProductCategory(entity);
        productCartRepository.deleteProductCart(entity);
        productMediaRepository.deleteMedia(entity);
    }

    @Override
    protected void inject() {
        if (productCategoryRepository == null) {
            productCategoryRepository = resolveRepository(ProductCategoryRepository.class);
        }
        if (productCartRepository == null) {
            productCartRepository = resolveRepository(ProductCartRepository.class);
        }
        if (productMediaRepository == null) {
            productMediaRepository = resolveRepository(ProductMediaRepository.class);
        }
    }
}
