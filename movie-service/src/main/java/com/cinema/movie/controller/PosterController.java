package com.cinema.movie.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/movies")
public class PosterController {
    private static final Set<String> EXT = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private final Path dir;

    public PosterController(@Value("${poster.dir:uploads/posters}") String d) throws IOException {
        this.dir = Paths.get(d).toAbsolutePath().normalize();
        Files.createDirectories(dir);
    }

    // Gateway chỉ cho ADMIN gọi POST /api/movies/**
    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";
        String ct = file.getContentType();
        if (file.isEmpty() || !EXT.contains(ext) || ct == null || !ct.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ nhận ảnh jpg, png, webp, gif");
        }
        String name = UUID.randomUUID() + "." + ext;   // tên ngẫu nhiên, tránh trùng và path traversal
        file.transferTo(dir.resolve(name));
        return Map.of("url", "/api/movies/posters/" + name);
    }

    @GetMapping("/posters/{name:.+}")
    public ResponseEntity<Resource> get(@PathVariable String name) throws IOException {
        Path p = dir.resolve(name).normalize();
        if (!p.startsWith(dir) || !Files.exists(p)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ảnh");
        }
        String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        MediaType type = switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.IMAGE_JPEG;
        };
        return ResponseEntity.ok()
                .contentType(type)
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .body(new UrlResource(p.toUri()));
    }
}