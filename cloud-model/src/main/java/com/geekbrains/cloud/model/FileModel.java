package com.geekbrains.cloud.model;


import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileModel implements CloudMessage {

    private long size;
    private byte[] data;

    private String name;

    public FileModel(Path path) throws IOException {
        size = Files.size(path);
        data = Files.readAllBytes(path);
        name = path.getFileName().toString();
    }

}
