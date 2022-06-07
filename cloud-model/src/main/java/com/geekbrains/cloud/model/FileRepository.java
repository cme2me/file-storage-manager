package com.geekbrains.cloud.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FileRepository implements CloudMessage {
    private final List<String> files;
    public FileRepository(Path path) throws IOException {
        files = Files.list(path).map(f -> f.getFileName().toString()).collect(Collectors.toList());
    }
}
