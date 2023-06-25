package com.bachlinh.order.web.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.web.dto.form.ProductSearchForm;
import com.bachlinh.order.web.dto.form.admin.ProductCreateForm;
import com.bachlinh.order.web.dto.form.admin.ProductUpdateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.business.ProductSearchingService;
import com.bachlinh.order.web.service.common.ProductService;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ServiceComponent
@ActiveReflection
public class ProductServiceImpl implements ProductService, ProductSearchingService {
    private final ProductRepository productRepository;
    private final EntityFactory entityFactory;
    private final CategoryRepository categoryRepository;
    private final String resourceUrl;

    @ActiveReflection
    @DependenciesInitialize
    public ProductServiceImpl(ProductRepository productRepository, EntityFactory entityFactory, CategoryRepository categoryRepository, @Value("${active.profile}") String profile) {
        this.productRepository = productRepository;
        this.entityFactory = entityFactory;
        this.categoryRepository = categoryRepository;
        Environment environment = Environment.getInstance(profile);
        String urlPattern = "https://{0}:{1}";
        resourceUrl = MessageFormat.format(urlPattern, environment.getProperty("server.address"), environment.getProperty("server.port"));
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
        return productRepository.getProductsByCondition(conditions, pageable).map(product -> ProductResp.toDto(product, resourceUrl));
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
        return productRepository.getProductsWithUnion(productIds, conditions, pageable).map(product -> ProductResp.toDto(product, resourceUrl));
    }

    @Override
    public Page<ProductResp> productList(Pageable pageable) {
        Map<String, Object> conditions = new HashMap<>();
        Page<Product> products = productRepository.getProductsByCondition(conditions, pageable);
        return products.map(product -> ProductResp.toDto(product, resourceUrl));
    }

    @Override
    public Page<ProductResp> getProductsWithId(Collection<Object> ids) {
        Pageable pageable = Pageable.ofSize(ids.size());
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("IDS", ids);
        return productRepository.getProductsByCondition(conditions, pageable).map(product -> ProductResp.toDto(product, resourceUrl));
    }

    @Override
    public ProductResp updateProduct(ProductUpdateForm form) {
        var conditions = new HashMap<String, Object>(1);
        conditions.put(Product_.ID, form.getProductId());
        var product = productRepository.getProductByCondition(conditions);
        product.setName(form.getProductName());
        product.setPrice(Integer.parseInt(form.getProductPrice()));
        product.setSize(form.getProductSize());
        product.setColor(form.getProductColor());
        product.setTaobaoUrl(form.getProductTaobaoUrl());
        product.setDescription(form.getProductDescription());
        product.setEnabled(Boolean.parseBoolean(form.getProductEnabled()));
        product.setOrderPoint(Integer.parseInt(form.getProductOrderPoint()));
        var categories = new HashSet<>(product.getCategories());
        var updatedCategories = Stream.of(form.getProductCategoriesId()).map(categoryRepository::getCategoryById).toList();
        categories.addAll(updatedCategories);
        product.setCategories(categories);
        product = productRepository.updateProduct(product);
        return ProductResp.toDto(product, resourceUrl);
    }

    @Override
    public ProductResp createProduct(ProductCreateForm form) {
        var product = entityFactory.getEntity(Product.class);
        product.setName(form.getProductName());
        product.setPrice(Integer.parseInt(form.getProductPrice()));
        product.setSize(form.getProductSize());
        product.setColor(form.getProductColor());
        product.setTaobaoUrl(form.getProductTaobaoUrl());
        product.setDescription(form.getProductDescription());
        product.setEnabled(Boolean.parseBoolean(form.getProductEnabled()));
        product.setOrderPoint(Integer.parseInt(form.getProductOrderPoint()));
        var categories = new HashSet<>(product.getCategories());
        var updatedCategories = Stream.of(form.getProductCategoriesId()).map(categoryRepository::getCategoryById).toList();
        categories.addAll(updatedCategories);
        product.setCategories(categories);
        product = productRepository.saveProduct(product);
        return ProductResp.toDto(product, resourceUrl);
    }

    @Override
    public ProductResp getProductById(String productId) {
        var conditions = new HashMap<String, Object>(1);
        conditions.put(Product_.ID, productId);
        var product = productRepository.getProductByCondition(conditions);
        return ProductResp.toDto(product, resourceUrl);
    }

    @Override
    public boolean deleteProduct(String productId) {
        var conditions = new HashMap<String, Object>(1);
        conditions.put(Product_.ID, productId);
        var product = productRepository.getProductByCondition(conditions);
        if (product == null) {
            return false;
        } else {
            return productRepository.deleteProduct(product);
        }
    }
}
