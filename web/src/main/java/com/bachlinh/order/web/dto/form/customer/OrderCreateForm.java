package com.bachlinh.order.web.dto.form.customer;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;

@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Getter
@Setter(onMethod = @__({@ActiveReflection}))
public class OrderCreateForm implements ValidatedDto {

    @JsonAlias("bank_transaction_code")
    private String bankTransactionCode;

    @JsonAlias("details")
    private Detail[] details;

    @ActiveReflection
    @NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
    @Getter
    @Setter(onMethod = @__({@ActiveReflection}))
    public static class Detail {

        @JsonAlias("amount")
        private String amount;

        @JsonAlias("product_name")
        private String productName;

        @JsonAlias("product_id")
        private String productId;
    }
}
