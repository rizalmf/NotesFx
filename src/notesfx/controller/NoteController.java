/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextArea;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import notesfx.NotesFx;
import notesfx.model.Note;
import notesfx.service.DataSession;
import notesfx.service.JdbcService;
import static notesfx.util.ApplicationProperties.VERSION_FILE;
import static notesfx.util.ApplicationProperties.VERSION;
import notesfx.util.Downloader;
import notesfx.util.FXResizeHelper;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class NoteController implements Initializable {

    @FXML
    private CustomTextField tfTitle;
    @FXML
    private JFXColorPicker cp;
    @FXML
    private AnchorPane ap;
    @FXML
    private HBox paneHead;
    @FXML
    private HBox paneM;
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
    public List<Note> noteList;
    private Stage thisStage;
    private Downloader downloader;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        downloader = new Downloader();
        service = new JdbcService();
        cp.setValue(Color.web("#dc5a5a"));
        getFromOther();
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
                Image img = new Image(new FileInputStream(files.get(0)), 
                        ap.getWidth(), ap.getHeight(), false, false);
                imageView.setImage(img);
//                cp.setValue(Color.BLACK);
//                ap.setStyle("-fx-background-color:black");
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
        Platform.runLater(() -> {
            if (thisStage == null) {
                thisStage = (Stage) ap.getScene().getWindow();
            }
            new FXResizeHelper(thisStage, -5, 24, 325, 171);
            imageView.fitWidthProperty().bind(ap.widthProperty());
            imageView.fitHeightProperty().bind(ap.heightProperty());
        });
        
    }  
    
    private void setNewNote(Note n){
        cp.setValue(Color.web(n.getColor_bg()));//ds.getColorBg().replaceAll("-fx-background-color:", "")
        cpTxt.setValue(Color.web(n.getColor_txt()));
        ap.setStyle("-fx-background-color:"+toRGB(cp.getValue()));
        taNote.setStyle("-fx-text-fill:"+toRGB(cpTxt.getValue()));
        lTitile.setStyle("-fx-text-fill:"+toRGB(cpTxt.getValue())+"; -fx-font-weight:bold");
        lTitile.setText(n.getTitle());
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
                        System.out.println("img not found");
//                        Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    imageView.setImage(img);
                }
            }
        }
        if (thisStage == null) {
            thisStage = (Stage) ap.getScene().getWindow();
        }
        thisStage.setX(n.getX());
        thisStage.setY(n.getY());
        if (n.getWidth() > 0) {
            thisStage.setWidth(n.getWidth());
        }
        if (n.getHeight() > 0) {
            thisStage.setHeight(n.getHeight());
        }
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
                }else{
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            checkVersion();
                        }
                    }, 0);
                }
            }else{
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        checkVersion();
                    }
                }, 0);
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
        if (thisStage == null) {
            thisStage = (Stage) ap.getScene().getWindow();
        }
        int x =(int) thisStage.getX();
        int y =(int) thisStage.getY();
        String title = tfTitle.getText();
        String text = taNote.getText();
        String color_bg = toRGB(cp.getValue());
        String color_txt = toRGB(cpTxt.getValue());
        int width =(int) thisStage.getWidth();
        int height =(int) thisStage.getHeight();
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
                note.setWidth(width);
                note.setHeight(height);
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
        if (thisStage == null) {
            thisStage = (Stage) ap.getScene().getWindow();
        }
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
        thisStage = (Stage) ap.getScene().getWindow();
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

    private void checkVersion() {
        DataSession session = new DataSession();
        String savedVer = session.getVersion().replaceAll(" ", "");
        if (savedVer.isEmpty()) {
            session.setVersion(VERSION);
        }
        File file = downloader.downloadApp(System.getProperty("user.dir"), VERSION_FILE);
        if (file != null) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String read;
                while((read = br.readLine()) != null){
                    String v = read.replaceAll("\\.", "").replaceAll(" ", "");
                    try {
                        int iv = Integer.parseInt(v);
                        int ivNow = Integer.parseInt(session.getVersion().replaceAll("\\.", "").replaceAll(" ", ""));
                        System.out.println(iv+", "+ivNow);
                        if (iv > ivNow) {
                            Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (event) -> {
                                initPrompt(v);
                            }));
                            tl.play();
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
                br.close();
            } catch (Exception e) {
                Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    private void initPrompt(String vers){
        FXMLLoader fxmlLoader = new FXMLLoader(NotesFx.class.getResource("views/prompt.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage stage = new Stage();
        stage.setOnCloseRequest((WindowEvent event1) -> {
//            System.exit(0);
            stage.close();
        });
        PromptController pc = fxmlLoader.getController();
        pc.setTargetVersion(vers);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("update available");
        stage.getIcons().add(new Image(NotesFx.class.getResourceAsStream("icon-min.png")));
        Scene scene = new Scene(root1);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);  
        stage.show();
    }
    
}
