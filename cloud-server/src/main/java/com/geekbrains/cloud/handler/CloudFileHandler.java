package com.geekbrains.cloud.handler;

import com.geekbrains.cloud.model.CloudMessage;
import com.geekbrains.cloud.model.FileModel;
import com.geekbrains.cloud.model.FileRepository;
import com.geekbrains.cloud.model.FileRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Path;

public class CloudFileHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path currentDir;
    private FileModel fileModel;
    private FileRequest fileRequest;
    private final Path rootDir;

    public CloudFileHandler() {
        currentDir = Path.of("server_files");
        rootDir = Path.of("server_files");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new FileRepository(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileModel(currentDir.resolve(fileRequest.getName())));
        }
        if (cloudMessage instanceof FileModel fileModel) {
            Files.write(currentDir.resolve(fileModel.getName()), fileModel.getData());
            ctx.writeAndFlush(new FileRepository(currentDir));
        }
        if (fileRequest.isClicked()) {
            String fileName = fileModel.getName();
            Path file = currentDir.resolve(fileName).normalize();
            if (fileName.equals("..")) {
                if (!currentDir.equals(rootDir)) {
                    currentDir = file;
                }
            }
            ctx.writeAndFlush(new FileModel(currentDir));
        } else {
            ctx.writeAndFlush(new FileModel(currentDir.resolve(fileModel.getName())));
        }
        if (cloudMessage instanceof FileModel fileModel) {
            Files.write(currentDir.resolve(fileModel.getName()), fileModel.getData());
            FileRepository repository = new FileRepository(currentDir);
            if (currentDir.equals(rootDir)) {
                repository.getFiles().add(0, "..");
                ctx.writeAndFlush(repository);
            }
        }
    }
}
