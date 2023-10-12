package com.bachlinh.order;

import com.bachlinh.order.core.utils.graphic.ImageUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class Test {
    public static void main(String[] args) throws IOException {
        String imagePath = "C:\\Users\\MinhQuan\\Downloads\\12067358_4882066.jpg";
        byte[] resized = ImageUtils.resizeImage(imagePath);

        String outputPath = "C:\\Users\\MinhQuan\\Downloads\\compressed.jpg";
        Path out = Path.of(outputPath);

        if (!Files.exists(out)) {
            Files.createFile(out);
        } else {
            Files.delete(out);
        }
        
        RandomAccessFile randomAccessFile = new RandomAccessFile(out.toFile(), "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        fileChannel.write(ByteBuffer.wrap(resized));

        fileChannel.close();
        randomAccessFile.close();
    }
}
