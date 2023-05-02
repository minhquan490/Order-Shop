package com.bachlinh.order.web.service.business;

import java.io.IOException;

public interface ImageCompressService {
    byte[] compressImage(String imagePath, String contentType) throws IOException;
}
