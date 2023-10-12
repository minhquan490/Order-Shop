package com.bachlinh.order.core.utils.graphic;

import com.bachlinh.order.core.enums.ImageScaleOption;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ImageUtils {

    private ImageUtils() {
    }

    public static byte[] resizeImage(String targetPath) {
        if (!isExist(targetPath)) {
            return new byte[0];
        }

        int division = 5;

        try {
            Path imageTargetPath = Paths.get(targetPath);
            File imageFile = imageTargetPath.toFile();
            BufferedImage inputImage = ImageIO.read(imageFile);

            int height = inputImage.getHeight() / division;
            int width = inputImage.getWidth() / division;

            return resizeImage(targetPath, ImageScaleOption.of(width, height));
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static byte[] resizeImage(String targetPath, ImageScaleOption resizeOption) {
        if (!isExist(targetPath)) {
            return new byte[0];
        }

        try {
            Path imageTargetPath = Paths.get(targetPath);
            File imageFile = imageTargetPath.toFile();
            BufferedImage inputImage = ImageIO.read(imageFile);
            Image transferredImage = inputImage.getScaledInstance(resizeOption.getWidth(), resizeOption.getHeight(), resizeOption.getHint());
            BufferedImage outputImage = new BufferedImage(resizeOption.getWidth(), resizeOption.getHeight(), BufferedImage.TYPE_INT_BGR);

            Graphics graphics = outputImage.getGraphics();
            graphics.drawImage(transferredImage, 0, 0, null);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String contentType = Files.probeContentType(imageTargetPath);
            ImageIO.write(outputImage, contentType.split("/")[1], outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private static boolean isExist(String path) {
        Path imageTargetPath = Paths.get(path);
        return Files.exists(imageTargetPath);
    }
}
