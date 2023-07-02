package com.bachlinh.order.web.dto.form.customer;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;

@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Getter
public class OrderCreateForm implements ValidatedDto {

    @JsonAlias("bank_transaction_code")
    private String bankTransactionCode;

    @JsonAlias("details")
    private Detail[] details;

    @ActiveReflection
    public void setBankTransactionCode(String bankTransactionCode) {
        this.bankTransactionCode = bankTransactionCode;
    }

    @ActiveReflection
    public void setDetails(Detail[] details) {
        this.details = details;
    }

    @ActiveReflection
    @NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
    @Getter
    public static class Detail {

        @JsonAlias("amount")
        private String amount;

        @JsonAlias("product_name")
        private String productName;

        @JsonAlias("product_id")
        private String productId;

        @ActiveReflection
        public void setAmount(String amount) {
            this.amount = amount;
        }

        @ActiveReflection
        public void setProductName(String productName) {
            this.productName = productName;
        }

        @ActiveReflection
        public void setProductId(String productId) {
            this.productId = productId;
        }
    }
}
