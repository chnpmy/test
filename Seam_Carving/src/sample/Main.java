package sample;

import algo.MyEnergy;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {
    BorderPane bdPane = new BorderPane();
    GridPane grid = new GridPane();
    HBox hb = new HBox();
    Separator sptSecond = new Separator();
    Separator sptThird = new Separator();
    Text tWidth = new Text("Width:");
    Text tHeight = new Text("Height:");
    TextField tfdWidth = new TextField();
    TextField tfdHeight = new TextField();
    Button btnConvert = new Button("Convert");
    MenuItem mitOpen = new MenuItem("Open");
    MenuItem mitSave = new MenuItem("Save");
    Menu mnuFile = new Menu("File");
    MenuBar mnb = new MenuBar();
    final ToggleGroup group = new ToggleGroup();
    RadioButton rbtnRate = new RadioButton("Rate");
    RadioButton rbtnSize = new RadioButton("Size");
    ScrollPane scp = new ScrollPane();
    ImageView imgView = new ImageView();
    Slider sldWidth = new Slider();
    Slider sldHeight = new Slider();
    Label lblState = new Label("State:");

    BufferedImage oldImg = null;
    BufferedImage newImg = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("JavaFX Welcome");

        bdPane.setRight(grid);

        bdPane.setTop(hb);

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        grid.add(sptSecond, 0, 4, 2, 1);
        grid.add(tWidth, 0, 5);
        grid.add(tHeight, 0, 6);
        grid.add(tfdWidth, 1, 5);
        grid.add(tfdHeight, 1, 6);
        grid.add(btnConvert, 0, 8, 1, 2);
        grid.add(sptThird, 0, 7, 2, 1);

        mnuFile.getItems().addAll(mitOpen, mitSave);

        mnb.getMenus().addAll(mnuFile);
        hb.getChildren().addAll(mnb);

        rbtnRate.setToggleGroup(group);
        grid.add(rbtnRate, 0, 0);
        rbtnSize.setSelected(true);
        rbtnSize.setToggleGroup(group);
        grid.add(rbtnSize, 1, 0);
        Separator sptFirst = new Separator();
        grid.add(sptFirst, 0, 1, 2, 1);

        scp.setContent(imgView);
        bdPane.setCenter(scp);

        bdPane.setBottom(lblState);

        setSliders();

        setAction();

        Scene scene = new Scene(bdPane, 1366, 768);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void setSliders(){
        sldWidth.setMin(-40);
        sldWidth.setMax(80);
        sldWidth.setValue(40);
        sldWidth.setShowTickLabels(true);
        sldWidth.setShowTickMarks(true);
        sldWidth.setMajorTickUnit(20);
        sldWidth.setMinorTickCount(5);
        sldWidth.setBlockIncrement(10);
        sldWidth.setSnapToTicks(true);

        grid.add(sldWidth, 0, 2, 2, 1);

        sldHeight.setMin(-40);
        sldHeight.setMax(80);
        sldHeight.setValue(40);
        sldHeight.setShowTickLabels(true);
        sldHeight.setShowTickMarks(true);
        sldHeight.setMajorTickUnit(20);
        sldHeight.setMinorTickCount(5);
        sldHeight.setBlockIncrement(10);
        sldHeight.setSnapToTicks(true);

        grid.add(sldHeight, 0, 3, 2, 1);
    }

    private void setAction(){
        mitOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fcs = new FileChooser();
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                fcs.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
                File file = fcs.showOpenDialog(null);
                if (file != null) {
                    try {
                        oldImg = ImageIO.read(file);
                        Image image = SwingFXUtils.toFXImage(oldImg, null);
                        imgView.setImage(image);
                        tfdWidth.setText(String.valueOf(oldImg.getWidth()));
                        tfdHeight.setText(String.valueOf(oldImg.getHeight()));
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        mitSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fcs = new FileChooser();
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                fcs.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
                File file = fcs.showSaveDialog(null);
                if (file != null) {
                    try {
                        ImageIO.write(newImg, "jpg", file);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });

        btnConvert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (oldImg == null){
                    lblState.setText("Warning: no picture!");
                    return;
                }
                int oldWidth = oldImg.getWidth();
                int oldHeight = oldImg.getHeight();
                if (oldHeight <= 2 || oldWidth <= 2){
                    lblState.setText("Warning: Picture is too small!");
                    return;
                }

                lblState.setText("Converting...");
                if (group.getSelectedToggle() == rbtnSize){
                    int newWidth = Integer.parseInt(tfdWidth.getText());
                    int newHeight = Integer.parseInt(tfdHeight.getText());
                    if (newHeight >= 2*oldHeight || newWidth >= 2*oldWidth){
                        lblState.setText("Warning: Size is too big!");
                        return;
                    }
                    else{
                        convert(newWidth, newHeight);
                    }
                }
                else{
                    int newWidth = (int)((100 + sldWidth.getValue()) * oldWidth / 100);
                    tfdWidth.setText(String.valueOf(newWidth));
                    int newHeight = (int)((100 + sldHeight.getValue()) * oldHeight / 100);
                    tfdHeight.setText(String.valueOf(newHeight));
                    convert(newWidth, newHeight);
                }
            }
        });

        imgView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                List<File> files = dragboard.getFiles();
                if (files.size() == 1) {
                    File file = files.get(0);
                    try {
                        oldImg = ImageIO.read(file);
                        Image image = SwingFXUtils.toFXImage(oldImg, null);
                        imgView.setImage(image);
                        tfdWidth.setText(String.valueOf(oldImg.getWidth()));
                        tfdHeight.setText(String.valueOf(oldImg.getHeight()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        imgView.setSmooth(true);
        imgView.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if(event.getGestureSource() != imgView){
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });
    }

    private void convert(final int newWidth,final int newHeight){
        final Thread thread = new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                try {
                    newImg = new MyEnergy().convert(oldImg, newWidth, newHeight);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lblState.setText("OK.");
                            Image image = SwingFXUtils.toFXImage(newImg, null);
                            imgView.setImage(image);
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    return null;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
