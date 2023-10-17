package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.repository.RepositoryManager;
import com.bachlinh.order.trigger.AbstractTrigger;

public abstract class AbstractRepositoryTrigger<T extends BaseEntity<?>> extends AbstractTrigger<T> {

    private RepositoryManager repositoryManager;

    protected <U> U resolveRepository(Class<U> repositoryType) {
        if (repositoryManager == null) {
            repositoryManager = resolveDependencies(RepositoryManager.class);
        }
        return repositoryManager.getRepository(repositoryType);
    }
}
