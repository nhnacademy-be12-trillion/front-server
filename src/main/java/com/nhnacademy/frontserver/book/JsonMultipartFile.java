package com.nhnacademy.frontserver.book;

import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonMultipartFile implements MultipartFile {
    private final byte[] content;
    private final String name;

    public JsonMultipartFile(String jsonStr, String name) {
        this.name = name;
        this.content = jsonStr.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getOriginalFilename() { return name + ".json"; }

    @Override
    public String getContentType() { return MediaType.APPLICATION_JSON_VALUE; }

    @Override
    public boolean isEmpty() { return content.length == 0; }

    @Override
    public long getSize() { return content.length; }

    @Override
    public byte[] getBytes() throws IOException { return content; }

    @Override
    public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(content); }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(content, dest);
    }
}
