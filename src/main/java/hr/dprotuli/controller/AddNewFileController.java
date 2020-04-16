package hr.dprotuli.controller;

import hr.dprotuli.model.Picture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddNewFileController {
    private PhotoMainFXMLController parentController;

    public void setParentController(PhotoMainFXMLController parentController) {
        this.parentController = parentController;
    }

    private File pictureFile;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField fileTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private Button selectButton;

    @FXML
    private Button addButton;

    @FXML
    private void cancelButtonAction(ActionEvent action) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        Picture newPicture = new Picture();
        newPicture.setName(nameTextField.getText());
        newPicture.setDescription(descriptionTextField.getText());
        if (!pictureFile.isFile()) {
            pictureFile = new File(fileTextField.getText());
        }
        newPicture.setPath("/pict/" + pictureFile.getName());
        parentController.setNewPicture(newPicture);
        parentController.setNewPictureFile(pictureFile);
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void selectFileAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        pictureFile = fileChooser.showOpenDialog((Stage) selectButton.getScene().getWindow());
        fileTextField.setText(pictureFile.getPath());
    }


}
