/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextArea;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import notesfx.NotesFx;
import notesfx.model.Note;
import notesfx.service.JdbcService;
import notesfx.util.FXResizeHelper;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * FXML Controller class
 *
 * @author PKane_NS
 */
public class NoteController implements Initializable {

    @FXML
    private CustomTextField tfTitle;
    @FXML
    private JFXColorPicker cp;
    @FXML
    private AnchorPane ap;
    @FXML
    private Pane paneHead;
    @FXML
    private Pane paneM;
    @FXML
    private AnchorPane apParent;
    @FXML
    private JFXButton bPaw;
    @FXML
    private JFXColorPicker cpTxt;
    @FXML
    private JFXTextArea taNote;
    @FXML
    private Label lTitile;
    @FXML
    private ImageView imageView;
    public NoteController noteController;
    public void setNoteController(NoteController controller){
        this.noteController = controller;
    }
    private Note note;
    private Note note2;
    private JdbcService service;
    private boolean use_image;
    private String is_picture;
    private String url_picture;
    public List<Note> noteList;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = new JdbcService();
        cp.setValue(Color.web("#6affae"));
        getFromOther();
        use_image = false;
        apParent.setBackground(Background.EMPTY);
        paneHead.setVisible(false);
        bPaw.setVisible(false);
        ap.setOnDragOver((event) -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });
        ap.setOnDragDropped((event) -> {
            try {
                List<File> files = event.getDragboard().getFiles();
                Image img = new Image(new FileInputStream(files.get(0)), 296, 160, false, false);
                imageView.setImage(img);
//                cp.setValue(Color.BLACK);
//                ap.setStyle("-fx-background-color:black");
                use_image = true;
                if (note == null) {
                    //new Note
                    note = new Note();
                }
                note.setIs_picture("Y");
                note.setUrl_picture(files.get(0).getAbsolutePath());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //aaa();
        // TODO
    }  
    private void aaa(){
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            Stage thisStage = (Stage) ap.getScene().getWindow();
            new FXResizeHelper(thisStage, 100, 40, taNote);
            
        }));
        tl.setCycleCount(1);
        tl.setDelay(Duration.seconds(2));
        tl.play();
    }
    private void setNewNote(Note n){
        cp.setValue(Color.web(n.getColor_bg()));//ds.getColorBg().replaceAll("-fx-background-color:", "")
        cpTxt.setValue(Color.web(n.getColor_txt()));
        ap.setStyle("-fx-background-color:"+toRGB(cp.getValue()));
        taNote.setStyle("-fx-text-fill:"+toRGB(cpTxt.getValue()));
        lTitile.setStyle("-fx-text-fill:"+toRGB(cpTxt.getValue())+"; -fx-font-weight:bold");
        tfTitle.setText(n.getTitle());
        taNote.setText(n.getText());
        if (n.getIs_picture() != null) {
            if (n.getIs_picture().equals("Y")) {
                String url = n.getUrl_picture();
                if (url != null) {
                    Image img = null;
                    try {
                        img = new Image(new FileInputStream(new File(url)), 296, 160, false, false);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    imageView.setImage(img);
                }
            }
        }
        Stage thisStage = (Stage) ap.getScene().getWindow();
        thisStage.setX(n.getX());
        thisStage.setY(n.getY());
    }
    private void getFromOther(){
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            if (noteController != null) {
//                note = noteController.note;
//                setNewNote(note);
                noteList = noteController.noteList;
            }
            if (noteList == null) {
                noteList = service.getNotes();
            }
            if (noteList.size() > 0) {
                note = noteList.get(0);
                noteList.remove(0);
                setNewNote(note);
                if (noteList.size() > 0) {
                    newNoteStart(note);
                }
            }
        }));
        tl.setCycleCount(1);
        tl.play();
    }
    @FXML
    private void changeColor(ActionEvent event) {
        Color color = cp.getValue();
        String s = "-fx-background-color:"+toRGB(color);
        ap.setStyle(s);
//        new DataSession().setColorBg(s);
    }
    
    @FXML
    private void changeColorTxt(ActionEvent event) {
        Color color = cpTxt.getValue();
        String s = "-fx-text-fill:"+toRGB(color);
        taNote.setStyle(s);
        lTitile.setStyle(s+"; -fx-font-weight:bold");
//        new DataSession().setColorTxt(s);
    }
    
    private String toRGB(Color color){
        return String.format("#%02X%02X%02X", 
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue()* 255)
                );
    }

    @FXML
    private void onDrag(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    double xOffset = 0;
    double yOffset = 0;
    @FXML
    private void onPress(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
        apParent.setOpacity(0.8);
    }
    
    @FXML
    private void onRelease(MouseEvent event) {
        apParent.setOpacity(1);
    }
    
    @FXML
    private void mouseExited(MouseEvent event) {
        paneHead.setVisible(false);
        bPaw.setVisible(false);
        paneM.setVisible(true);
        lTitile.setText(tfTitle.getText());
        String text = taNote.getText();
        if (text.replaceAll(" ", "").isEmpty()) {
            return;
        }
        saveNote();
    }
    private void saveNote(){
        Stage thisStage = (Stage) ap.getScene().getWindow();
        int x =(int) thisStage.getX();
        int y =(int) thisStage.getY();
        String title = tfTitle.getText();
        String text = taNote.getText();
        String color_bg = toRGB(cp.getValue());
        String color_txt = toRGB(cpTxt.getValue());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (note == null) {
                    //new Note
                    note = new Note();
                }
                note.setTitle(title);
                note.setText(text);
//                note.setUrl_picture(url_picture);
//                note.setIs_picture(is_picture);
                note.setColor_bg(color_bg);
                note.setColor_txt(color_txt);
                note.setX(x);
                note.setY(y);
                note = service.saveNote(note);
//                System.out.println("save with note_id="+note.getId());
            }
        }, 0);
    }
    @FXML
    private void mouseEntered(MouseEvent event) {
        paneHead.setVisible(true);
        paneM.setVisible(false);
        bPaw.setVisible(true);
    }

    @FXML
    private void onKeyRelease(KeyEvent event) {
        //KEY RELEASE tfTitle
    }
    private void newNoteStart(Note n){
        FXMLLoader fxmlLoader = new FXMLLoader(NotesFx.class.getResource("views/note.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage stage = new Stage();
        stage.setOnCloseRequest((WindowEvent event1) -> {
            System.exit(0);
//            stage.close();
        });
        stage.initStyle(StageStyle.TRANSPARENT);
        if (n != null) {
            NoteController nc = fxmlLoader.getController();
            nc.setNoteController(NoteController.this);
        }else{
            noteList = new ArrayList<>();
            NoteController nc = fxmlLoader.getController();
            nc.setNoteController(NoteController.this);
        }
        stage.setTitle("NotesFx");
        stage.getIcons().add(new Image(NotesFx.class.getResourceAsStream("icon-min.png")));
        Scene scene = new Scene(root1);
        scene.setFill(Color.TRANSPARENT);
        Stage thisStage = (Stage) ap.getScene().getWindow();
        stage.setX(thisStage.getX() - 300);
        stage.setY(thisStage.getY());
        stage.setScene(scene);  
        stage.show();
    }
    @FXML
    private void getNewNote(ActionEvent event) {
        newNoteStart(null);
    }

    @FXML
    private void getDeleteNote(ActionEvent event) {
        if (note !=null) {
            service.deleteNote(note.getId());
            service.close();
        }
        Stage thisStage = (Stage) ap.getScene().getWindow();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int i = service.getNotes().size();
                if (i == 0) {
                    System.out.println("exiting ..");
                    System.exit(0);
                }
            }
        }, 0);
        thisStage.close();
    }


    
}
