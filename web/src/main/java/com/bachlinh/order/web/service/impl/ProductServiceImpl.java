package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.web.dto.form.admin.product.ProductCreateForm;
import com.bachlinh.order.web.dto.form.admin.product.ProductUpdateForm;
import com.bachlinh.order.web.dto.form.common.ProductSearchForm;
import com.bachlinh.order.web.dto.resp.AdminProductResp;
import com.bachlinh.order.web.dto.resp.AnalyzeProductPostedInMonthResp;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.business.ProductAnalyzeService;
import com.bachlinh.order.web.service.business.ProductSearchingService;
import com.bachlinh.order.web.service.common.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ServiceComponent
public class ProductServiceImpl extends AbstractService implements ProductService, ProductSearchingService, ProductAnalyzeService {
    private final ProductRepository productRepository;
    private final EntityFactory entityFactory;
    private final CategoryRepository categoryRepository;
    private final DtoMapper dtoMapper;
    private final String resourceUrl;

    private ProductServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.productRepository = resolveRepository(ProductRepository.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.categoryRepository = resolveRepository(CategoryRepository.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
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
            var categories = categoryRepository.getCategoryByNames(Arrays.asList(form.categories()));
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
            var categories = categoryRepository.getCategoryByNames(Arrays.asList(form.categories()));
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
        Page<Product> products = productRepository.getAllProducts(pageable);
        return products.map(product -> ProductResp.toDto(product, resourceUrl));
    }

    @Override
    public Page<ProductResp> getProductsWithId(Collection<Object> ids) {
        var results = productRepository.getProductInfos(ids.stream().map(o -> (String) o).toList()).stream().map(product -> ProductResp.toDto(product, resourceUrl)).toList();
        return new PageImpl<>(results);
    }

    @Override
    public Collection<AdminProductResp> getProducts(String page) {
        int productPerPage = 500;
        Pageable pageable;
        if (!StringUtils.hasText(page)) {
            pageable = PageRequest.of(1, 500);
        } else {
            try {
                pageable = PageRequest.of(Integer.parseInt(page), productPerPage);
            } catch (NumberFormatException e) {
                throw new BadVariableException("Page must be a number");
            }
        }
        return dtoMapper.map(productRepository.getProducts(pageable), AdminProductResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductResp updateProduct(ProductUpdateForm form) {
        var product = productRepository.getProductForUpdate(form.getProductId());
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
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
        var product = productRepository.getProductInfo(productId);
        return ProductResp.toDto(product, resourceUrl);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean deleteProduct(String productId) {
        var product = productRepository.getProductForDelete(productId);
        if (product == null) {
            return false;
        } else {
            return productRepository.deleteProduct(product);
        }
    }

    @Override
    public AnalyzeProductPostedInMonthResp analyzeProductPostedInMonth() {
        var template = "select t.* from (select count(p.id) as first, ({0}) as second, ({1}) as third, ({2}) as fourth, ({3}) as last from Product p where p.created_date between :firstStart and :firstEnd) as t";
        var secondStatement = "select count(p.id) from Product p where p.created_date between :secondStart and :secondEnd";
        var thirdStatement = "select count(p.id) from Product p where p.created_date between :thirdStart and :thirdEnd";
        var fourthStatement = "select count(p.id) from Product p where p.created_date between :fourthStart and :fourthEnd";
        var lastStatement = "select count(p.id) from Product p where p.created_date between :lastStart and :lastEnd";
        var query = MessageFormat.format(template, secondStatement, thirdStatement, fourthStatement, lastStatement);
        Map<String, Object> attributes = HashMap.newHashMap(10);
        var now = LocalDateTime.now();
        var firstParam = Timestamp.valueOf(now.plusWeeks(-5));
        var secondParam = Timestamp.valueOf(now.plusWeeks(-4));
        var thirdParam = Timestamp.valueOf(now.plusWeeks(-3));
        var fourthParam = Timestamp.valueOf(now.plusWeeks(-2));
        var fifthParam = Timestamp.valueOf(now.plusWeeks(-1));
        attributes.put("firstStart", firstParam);
        attributes.put("firstEnd", secondParam);
        attributes.put("secondStart", secondParam);
        attributes.put("secondEnd", thirdParam);
        attributes.put("thirdStart", thirdParam);
        attributes.put("thirdEnd", fourthParam);
        attributes.put("fourthStart", fourthParam);
        attributes.put("fourthEnd", fifthParam);
        attributes.put("lastStart", fifthParam);
        attributes.put("lastEnd", Timestamp.valueOf(now));
        var resultSet = productRepository.getResultList(query, attributes, AnalyzeProductPostedInMonthResp.ResultSet.class).get(0);
        return dtoMapper.map(resultSet, AnalyzeProductPostedInMonthResp.class);
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new ProductServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{ProductService.class, ProductSearchingService.class, ProductAnalyzeService.class};
    }
}
