/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import notesfx.service.DataSession;
import static notesfx.util.ApplicationProperties.JAR_UPDATER_NAME;
import notesfx.util.Downloader;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class PromptController implements Initializable {

    @FXML
    private AnchorPane parent;
    @FXML
    private VBox ap;
    @FXML
    private Button bUpdate;
    @FXML
    private Button bClose;
    private Downloader downloader;
    public String targetVersion;
    public void setTargetVersion(String targetVersion){
        this.targetVersion = targetVersion;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        parent.setBackground(Background.EMPTY);
        downloader = new Downloader();
        Platform.runLater(() -> {
            buttonProperties();
        });
    }   
    double xOffset = 0;
    double yOffset = 0;
    private void buttonProperties(){
        ap.setOnMousePressed((event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        ap.setOnMouseDragged((event) -> {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        bClose.setOnAction((event) -> {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        });
        bUpdate.setOnAction((event) -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    initUpdate();
                }
            }, 0);
        });
    }
    private void initUpdate(){
        File file = downloader.downloadApp(System.getProperty("user.dir"), JAR_UPDATER_NAME);
        if (file != null) {
            try {
                runUpdater();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 1000);
                System.out.println(targetVersion);
                new DataSession().setVersion(targetVersion);
            } catch (Exception e) {
                Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
       
    }
    private void runUpdater(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    Process p = Runtime.getRuntime().exec("java -jar "+JAR_UPDATER_NAME);
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null){
                        System.out.println(line+" aa");
                    }
                }catch(Exception e){
                   System.err.println("Error on exec() method");
                   e.printStackTrace();  
                }
            }
        }, 0);
    }
}
