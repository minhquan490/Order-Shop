package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Arrays;
import java.util.Objects;

@JsonRootName("product")
public record ProductResp(String id,
                          String name,
                          String price,
                          String size,
                          String color,
                          String taobaoUrl,
                          String description,
                          String[] pictures,
                          String[] categories,
                          @JsonIgnore boolean isSuccess) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductResp that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(size, that.size) && Objects.equals(color, that.color) && Objects.equals(taobaoUrl, that.taobaoUrl) && Objects.equals(description, that.description) && Arrays.equals(pictures, that.pictures) && Arrays.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, price, size, color, taobaoUrl, description);
        result = 31 * result + Arrays.hashCode(pictures);
        result = 31 * result + Arrays.hashCode(categories);
        return result;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", taobaoUrl='" + taobaoUrl + '\'' +
                ", description='" + description + '\'' +
                ", pictures=" + Arrays.toString(pictures) +
                ", categories=" + Arrays.toString(categories) +
                '}';
    }

    public static ProductResp toDto(Product product) {
        if (product == null) {
            return new ProductResp(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    new String[0],
                    new String[0],
                    false
            );
        } else {
            return new ProductResp(
                    product.getId(),
                    product.getName(),
                    String.valueOf(product.getPrice()),
                    product.getSize(),
                    product.getColor(),
                    product.getTaobaoUrl(),
                    product.getDescription(),
                    (String[]) product.getMedias().stream().map(ProductMedia::getUrl).toArray(),
                    (String[]) product.getCategories().stream().map(Category::getName).toArray(),
                    true
            );
        }
    }
}
