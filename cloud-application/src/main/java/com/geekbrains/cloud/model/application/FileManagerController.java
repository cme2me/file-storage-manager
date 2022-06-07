package com.geekbrains.cloud.model.application;

import com.geekbrains.cloud.model.CloudMessage;
import com.geekbrains.cloud.model.FileRepository;
import com.geekbrains.cloud.model.FileModel;
import com.geekbrains.cloud.model.FileRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class FileManagerController implements Initializable {


    private String homeDir;

    @FXML
    public ListView<String> clientView;

    @FXML
    public ListView<String> serverView;

    private ClientHandler clientHandler;

    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = clientHandler.read();
                if (message instanceof FileRepository fileRepository) {
                    Platform.runLater(() -> {
                        serverView.getItems().clear();
                        serverView.getItems().addAll(fileRepository.getFiles());
                    });
                }  if (message instanceof FileModel fileModel) {
                    Path current = Path.of(homeDir).resolve(fileModel.getName());
                    Files.write(current, fileModel.getData());
                    Platform.runLater(() -> {
                        clientView.getItems().clear();
                        clientView.getItems().addAll(getFiles(homeDir));
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Connection disappeared");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            homeDir = "client_files";
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(homeDir));
            clientHandler = new ClientHandler(8088);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String file = clientView.getSelectionModel().getSelectedItem();
        clientHandler.write(new FileModel(Path.of(homeDir).resolve(file)));
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String file = serverView.getSelectionModel().getSelectedItem();
        clientHandler.write(new FileRequest(file));
    }
}