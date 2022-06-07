package com.geekbrains.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileRequest implements CloudMessage {
    private String name;
}
