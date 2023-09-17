package com.bachlinh.order.trigger;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.trigger.AbstractTrigger;
import com.bachlinh.order.repository.ProductCategoryRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

@ApplyOn(entity = Category.class)
@ActiveReflection
public class CategoryAssociateDeletionTrigger extends AbstractTrigger<Category> {

    private ProductCategoryRepository productCategoryRepository;

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
        return "CategoryAssociateDeletion";
    }

    @Override
    protected void doExecute(Category entity) {
        productCategoryRepository.deleteProductCategory(entity);
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getDependenciesResolver();
        if (productCategoryRepository == null) {
            productCategoryRepository = resolver.resolveDependencies(ProductCategoryRepository.class);
        }
    }
}
