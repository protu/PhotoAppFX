package hr.dprotuli.controller;

import hr.dprotuli.model.Picture;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PhotoMainFXMLController implements Initializable {

    private PhotoServiceController photoServiceController = new PhotoServiceController();
    ObservableList<Node> tilePaneNodes;
    List<Picture> pictures;
    Integer selectedNode = 0;
    Effect selectEffect = new InnerShadow(BlurType.GAUSSIAN, Color.BROWN, 20, 0.3, 0, 0);

    public void setNewPicture(Picture newPicture) {
        this.newPicture = newPicture;
    }

    private Picture newPicture;
    private File newPictureFile;

    public File getNewPictureFile() {
        return newPictureFile;
    }

    public void setNewPictureFile(File newPictureFile) {
        this.newPictureFile = newPictureFile;
    }

    @FXML
    private TilePane tilePane;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private Button addPictureButton;

    @FXML
    private Button modifyPictureButton;

    @FXML
    private Button deletePictureButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pictures = new ArrayList<>(List.of(photoServiceController.getPictures()));
        for (Picture picture : pictures) {
            tilePane.getChildren().add(createImageView(picture));
        }
        tilePaneNodes = tilePane.getChildren();
        tilePaneNodes.get(selectedNode).setEffect(selectEffect);
        nameTextField.setText(pictures.get(selectedNode).getName());
        descriptionTextField.setText(pictures.get(selectedNode).getDescription());
    }

    private void updatePictureList() {
        List<Picture> newPictureList = new ArrayList<>(List.of(photoServiceController.getPictures()));
        newPictureList.removeAll(pictures);
        for (Picture picture : newPictureList) {
            tilePane.getChildren().add(createImageView(picture));
        }
        pictures.addAll(newPictureList);
    }

    private ImageView createImageView(Picture picture) {
        Image pictureFile = photoServiceController.getPictureFile(picture);
        ImageView imageView = new ImageView(pictureFile);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                tilePaneNodes.get(selectedNode).setEffect(null);
                selectedNode = tilePaneNodes.indexOf(imageView);
                imageView.setEffect(selectEffect);
                nameTextField.setText(picture.getName());
                descriptionTextField.setText(picture.getDescription());
            }
        });
        return imageView;
    }

    void selectNode(int nodePosition) {
        if (tilePaneNodes.size() == 0) return;
        selectedNode = nodePosition;
        tilePaneNodes.get(selectedNode).setEffect(selectEffect);
        nameTextField.setText(pictures.get(selectedNode).getName());
        descriptionTextField.setText(pictures.get(selectedNode).getDescription());
    }

    @FXML
    private void addPictureAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("New Picture");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddNewFileDialog.fxml"));
        Parent dialog = loader.load();
        Scene scene = new Scene(dialog);
        AddNewFileController addNewFileController = loader.getController();
        addNewFileController.setParentController(this);
        stage.setScene(scene);
        stage.show();

        stage.setOnHiding(p -> {
            System.out.println("Stage closed");
            if (newPictureFile != null && newPictureFile.isFile()) {
                photoServiceController.addPicture(newPicture, newPictureFile);
                updatePictureList();
            }
        });

    }

    @FXML
    private void modifyPictureAction(ActionEvent actionEvent) {
        Picture picture = pictures.get(selectedNode);
        picture.setName(nameTextField.getText());
        picture.setDescription(descriptionTextField.getText());
        photoServiceController.modifyPicture(picture);
    }

    @FXML
    private void deletePictureAction(ActionEvent actionEvent) {
        if (pictures.size() == 0) return;
        if (photoServiceController.deletePicture(pictures.get(selectedNode))) {
            tilePane.getChildren().remove(tilePaneNodes.get(selectedNode));
            pictures.remove(pictures.get(selectedNode));
            selectNode(0);
        }
    }

}
