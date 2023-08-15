package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

@Entity
@Table(name = "PRODUCT_MEDIA", indexes = @Index(name = "idx_product_media_product", columnList = "PRODUCT_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class ProductMedia extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "URL", nullable = false, length = 100)
    private String url;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 100)
    private String contentType;

    @Column(name = "CONTENT_LENGTH", nullable = false)
    private Long contentLength;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of product picture must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setUrl(String url) {
        if (this.url != null && !this.url.equals(url)) {
            trackUpdatedField("URL", this.url);
        }
        this.url = url;
    }

    @ActiveReflection
    public void setContentType(String contentType) {
        if (this.contentType != null && !this.contentType.equals(contentType)) {
            trackUpdatedField("CONTENT_TYPE", this.contentType);
        }
        this.contentType = contentType;
    }

    @ActiveReflection
    public void setContentLength(Long contentLength) {
        if (this.contentLength != null && !this.contentLength.equals(contentLength)) {
            trackUpdatedField("CONTENT_LENGTH", this.contentLength.toString());
        }
        this.contentLength = contentLength;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        if (this.product != null && !Objects.requireNonNull(this.product.getId()).equals(product.getId())) {
            trackUpdatedField("PRODUCT_ID", this.product.getId());
        }
        this.product = product;
    }

    public static EntityMapper<ProductMedia> getMapper() {
        return new ProductMediaMapper();
    }

    private static class ProductMediaMapper implements EntityMapper<ProductMedia> {

        @Override
        public ProductMedia map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new ProductMedia().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public ProductMedia map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            ProductMedia result = new ProductMedia();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("PRODUCT_MEDIA")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Product.getMapper();
                var product = mapper.map(resultSet);
                product.getMedias().add(result);
                result.setProduct(product);
            }
            return result;
        }

        private void setData(ProductMedia target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "PRODUCT_MEDIA.ID" -> target.setId(mappingObject.value());
                case "PRODUCT_MEDIA.URL" -> target.setUrl((String) mappingObject.value());
                case "PRODUCT_MEDIA.CONTENT_LENGTH" -> target.setContentLength((Long) mappingObject.value());
                case "PRODUCT_MEDIA.CONTENT_TYPE" -> target.setContentType((String) mappingObject.value());
                case "PRODUCT_MEDIA.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "PRODUCT_MEDIA.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "PRODUCT_MEDIA.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "PRODUCT_MEDIA.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}