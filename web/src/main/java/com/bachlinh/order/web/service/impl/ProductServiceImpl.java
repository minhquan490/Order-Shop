package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.AbstractService;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.ProductForm;
import com.bachlinh.order.web.dto.form.ProductSearchForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.business.ProductSearchingService;
import com.bachlinh.order.web.service.common.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@ServiceComponent
@ActiveReflection
public class ProductServiceImpl extends AbstractService<ProductResp, ProductForm> implements ProductService, ProductSearchingService {
    private ProductRepository productRepository;
    private EntityFactory entityFactory;
    private CategoryRepository categoryRepository;

    @ActiveReflection
    @DependenciesInitialize
    public ProductServiceImpl(Executor executor, ContainerWrapper wrapper, String profile) {
        super(executor, wrapper, profile);
    }

    @Override
    public Page<ProductResp> searchProduct(ProductSearchForm form, Pageable pageable) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put(Product_.NAME, form.productName());
        conditions.put(Product_.PRICE, Integer.parseInt(form.price()));
        conditions.put(Product_.SIZE, form.productSize());
        conditions.put(Product_.COLOR, form.color());
        conditions.put(Product_.ENABLED, Boolean.parseBoolean(form.enable()));
        if (form.categories() != null) {
            List<Category> categories = Arrays.stream(form.categories()).map(categoryRepository::getCategoryByName).toList();
            conditions.put(Product_.CATEGORIES, categories);
        }
        conditions = conditions.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return productRepository.getProductsByCondition(conditions, pageable).map(ProductResp::toDto);
    }

    @Override
    public Page<ProductResp> fullTextSearch(ProductSearchForm form, Pageable pageable) {
        EntityContext entityContext = entityFactory.getEntityContext(Product.class);
        Collection<String> productIds = entityContext.search(form.productName());
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("IDS", productIds);
        conditions.put(Product_.COLOR, form.color());
        conditions.put(Product_.PRICE, form.price());
        conditions.put(Product_.SIZE, form.productSize());
        conditions.put(Product_.ENABLED, true);
        if (form.categories() != null) {
            List<Category> categories = Arrays.stream(form.categories()).map(categoryRepository::getCategoryByName).toList();
            conditions.put(Product_.CATEGORIES, categories);
        }
        conditions = conditions.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        return productRepository.getProductsByCondition(conditions, pageable).map(ProductResp::toDto);
        return productRepository.getProductsWithUnion(productIds, conditions, pageable).map(ProductResp::toDto);
    }

    @Override
    public Page<ProductResp> productList(Pageable pageable) {
        Map<String, Object> conditions = new HashMap<>();
        Page<Product> products = productRepository.getProductsByCondition(conditions, pageable);
        return products.map(ProductResp::toDto);
    }

    @Override
    public Page<ProductResp> getProductsWithId(Collection<Object> ids) {
        Pageable pageable = Pageable.ofSize(ids.size());
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("IDS", ids);
        return productRepository.getProductsByCondition(conditions, pageable).map(ProductResp::toDto);
    }

    @Override
    protected ProductResp doSave(ProductForm param) {
        Product product = toProduct(param);
        product = productRepository.saveProduct(product);
        return ProductResp.toDto(product);
    }

    @Override
    protected ProductResp doUpdate(ProductForm param) {
        Product product = toProduct(param);
        product = productRepository.updateProduct(product);
        return ProductResp.toDto(product);
    }

    @Override
    protected ProductResp doDelete(ProductForm param) {
        Map<String, Object> conditions = new HashMap<>(1);
        conditions.put(Product_.ID, param.getId());
        Product product = productRepository.getProductByCondition(conditions);
        if (product == null) {
            return ProductResp.toDto(null);
        } else {
            boolean result = productRepository.deleteProduct(product);
            if (result) {
                return ProductResp.toDto(product);
            } else {
                return ProductResp.toDto(null);
            }
        }
    }

    @Override
    protected ProductResp doGetOne(ProductForm param) {
        Map<String, Object> conditions = new HashMap<>(1);
        Product product = productRepository.getProductByCondition(conditions);
        return ProductResp.toDto(product);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K, X extends Iterable<K>> X doGetList(ProductForm param) {
        int page;
        int pageSize;
        try {
            page = Integer.parseInt(param.getPage());
            pageSize = Integer.parseInt(param.getPageSize());
        } catch (NumberFormatException e) {
            throw new BadVariableException("Page number and page size must be int");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        var results = productRepository.getAllProducts(pageable).stream().map(ProductResp::toDto).toList();
        return (X) new PageImpl<>(results, pageable, productRepository.countProduct());
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (productRepository == null) {
            productRepository = resolver.resolveDependencies(ProductRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolver.resolveDependencies(EntityFactory.class);
        }
        if (categoryRepository == null) {
            categoryRepository = resolver.resolveDependencies(CategoryRepository.class);
        }
    }

    private Product toProduct(ProductForm form) {
        Product product = entityFactory.getEntity(Product.class);
        product.setId(form.getId());
        product.setName(form.getName());
        try {
            product.setPrice(Integer.parseInt(form.getPrice()));
        } catch (NumberFormatException e) {
            throw new BadVariableException("Product price must be integer");
        }
        product.setColor(form.getColor());
        product.setTaobaoUrl(form.getTaobaoUrl());
        product.setDescription(form.getDescription());
        product.setEnabled(Boolean.parseBoolean(form.getEnabled()));
        List<Category> categories = Arrays.stream(form.getCategories()).map(categoryName -> {
            Category category = categoryRepository.getCategoryByName(categoryName);
            category.getProducts().add(product);
            return category;
        }).toList();
        product.setCategories(new HashSet<>(categories));
        try {
            product.setOrderPoint(Integer.parseInt(form.getOrderPoint()));
        } catch (NumberFormatException e) {
            product.setOrderPoint(0);
        }
        return product;
    }
}
