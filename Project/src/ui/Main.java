package ui;
import audio.AudioIO;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;

public class Main extends Application {

 public void start(Stage primaryStage) {
    try {
            BorderPane root = new BorderPane();
            root.setTop(createToolbar());
            root.setBottom(createStatusbar());
            root.setCenter(createMainContent());
            Scene scene = new Scene(root,1500,800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("The JavaFX audio processor");
            primaryStage.show();
            }
    catch(Exception e) {e.printStackTrace();}
 }
 private Node createToolbar(){
        Button button = new Button("Launch audio processing");
        ToolBar tb = new ToolBar(button, new Separator());
        Label lb1 = new Label("Frequency :");
        Label lb2 = new Label("Hz");
        Separator sp1 = new Separator();
        Separator sp2 = new Separator();
        Separator sp3 = new Separator();
        Label lb3 = new Label("Frame size :");
        Label lb4 = new Label("seconds");
        Label lb5 = new Label("Input Mixer");
        Label lb6 = new Label("Output Mixer");
        ComboBox<Integer> cbfreq = new ComboBox<>();
        cbfreq.getItems().addAll(48000,44100,24000,16000,8000);
        ComboBox<Integer> cbframe = new ComboBox<>();
        cbframe.getItems().addAll(8,4,2,1);
        ComboBox<String> cbInputMix = new ComboBox<String>();
        cbInputMix.getItems().addAll(AudioIO.getMixerNames());
        ComboBox<String> cbOutputMix = new ComboBox<String>();
        cbOutputMix.getItems().addAll(AudioIO.getMixerNames());


        tb.getItems().add(lb1);
        tb.getItems().add(cbfreq);
        tb.getItems().add(lb2);
        tb.getItems().add(sp1);
        tb.getItems().add(lb3);
        tb.getItems().add(cbframe);
        tb.getItems().add(lb4);
        tb.getItems().add(sp2);
        tb.getItems().add(lb5);
        tb.getItems().add(cbInputMix);
        tb.getItems().add(sp3);
        tb.getItems().add(lb6);
        tb.getItems().add(cbOutputMix);

        button.setOnAction(event -> AudioIO.startAudioProcessing(cbInputMix.getValue(),cbOutputMix.getValue(),cbfreq.getValue(),cbframe.getValue()*cbfreq.getValue()));
        return tb;
 }

        private Node createStatusbar(){
        HBox statusbar = new HBox();
        statusbar.getChildren().addAll(new Label("Name:"), new TextField(" "));
        return statusbar;
 }

        private Node createMainContent(){
        Group g = new Group();
        // ici en utilisant g.getChildren().add(...) vous pouvez ajouter tout ´element graphique souhaite de type Node
        return g;
        }
}
