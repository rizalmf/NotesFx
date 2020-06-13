/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import notesfx.service.JdbcService;

/**
 *
 * @author PKane_NS
 */
public class NotesFx extends Application {
    private JdbcService service;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/note.fxml"));
        stage.setOnCloseRequest((WindowEvent event) -> {
//            stage.close();
            System.exit(0);
        });
        service = new JdbcService();
        service.createDB();
        System.out.println("saved size: "+service.getNotes().size()+" note");
        Scene scene = new Scene(root);
//        Platform.setImplicitExit(true);
        stage.setTitle("NotesFx");
        scene.setFill(Color.TRANSPARENT);
        stage.getIcons().add(new Image(NotesFx.class.getResourceAsStream("icon-min.png")));
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
