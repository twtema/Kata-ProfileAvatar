package org.kata.service.util;

import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@Component
@Getter
public class ImageProcessor {

    private static final long MAX_SIZE = 110000;

    private String hex;

    private String filename;

    private byte[] image;

    public void process(MultipartFile file) {
        int size = (int) file.getSize();
        filename = "ava.jpg";

        try {
            image = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (size > MAX_SIZE) {
            compress(image, size);
        }
        hex = SHAsum(image);
    }

    private void compress(byte[] file, int size) {
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        double compression = size > MAX_SIZE * 10 ? 0.15 : (double) MAX_SIZE / size;
        int height;
        int width;
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(file));
            height = bufferedImage.getHeight();
            width = bufferedImage.getWidth();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thumbnails.of(bufferedImage)
                    .height((int) (height * compression))
                    .width((int) (width * compression))
                    .outputFormat("jpg")
                    .toOutputStream(bAOS);
            image = bAOS.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String SHAsum(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return byteArray2Hex(md.digest(convertme));
    }

    public String byteArray2Hex(final byte[] hash) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
}
