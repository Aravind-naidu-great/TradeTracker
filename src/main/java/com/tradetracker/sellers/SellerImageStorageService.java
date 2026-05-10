package com.tradetracker.sellers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class SellerImageStorageService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg");

    private final Path uploadRoot;

    public SellerImageStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).resolve("seller-products");
    }

    public String store(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        String originalFilename = image.getOriginalFilename() == null ? "" : image.getOriginalFilename();
        String extension = extension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Product image must be jpg, png, gif, webp, or svg.");
        }

        try {
            Files.createDirectories(uploadRoot);
            String filename = UUID.randomUUID() + extension;
            Path target = uploadRoot.resolve(filename);
            image.transferTo(target);
            return "/uploads/seller-products/" + filename;
        } catch (IOException error) {
            throw new IllegalArgumentException("Could not save product image.", error);
        }
    }

    private String extension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return dotIndex < 0 ? "" : filename.substring(dotIndex).toLowerCase();
    }
}
