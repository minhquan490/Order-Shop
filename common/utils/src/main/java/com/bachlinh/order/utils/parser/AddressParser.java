package com.bachlinh.order.utils.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class AddressParser {

    public static String parseVietNamAddress(String houseAddress, String ward, String district, String province) {
        var template = "{0}, xã {1}, huyện/quận {2}, {3}, Việt Nam";
        return MessageFormat.format(template, houseAddress, ward, district, province);
    }
}
