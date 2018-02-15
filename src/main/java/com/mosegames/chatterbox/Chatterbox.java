package com.mosegames.chatterbox;

import java.io.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class Chatterbox extends Application implements Runnable {
    public static Stage stage;
    
    @Override public void start(Stage stage) throws Exception {
        Scene scene = new Scene( FXMLLoader.load(getClass().getResource("/chatterbox/UI.fxml")) );
        scene.getStylesheets().add(getClass().getResource("/chatterbox/style.css").toExternalForm());
        Chatterbox.stage = stage;
        stage.setOnCloseRequest((WindowEvent t) -> {
            Main.close();
            Platform.exit();
            System.exit(0);
        });
        stage.setScene( scene );
        stage.setTitle(" [ ChatterBox ] ");
        stage.show();
        
        
        Thread thread = new Thread(this);
        thread.start();
    }
    
    @Override public void run() {
        try {
            Main.main( new String[0] );
        } catch (IOException ex) { }
    }
    
    public static void close() {
        stage.fireEvent( new WindowEvent( stage, WindowEvent.WINDOW_CLOSE_REQUEST ) );
    }
    
    public static void main(String args[]) {
    	launch(args);
    }
}