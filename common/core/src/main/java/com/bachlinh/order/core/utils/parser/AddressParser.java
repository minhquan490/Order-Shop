package com.bachlinh.order.core.utils.parser;

import java.text.MessageFormat;

public final class AddressParser {

    public static String parseVietNamAddress(String houseAddress, String ward, String district, String province) {
        var template = "{0}, xã {1}, huyện/quận {2}, {3}, Việt Nam";
        return MessageFormat.format(template, houseAddress, ward, district, province);
    }
}
