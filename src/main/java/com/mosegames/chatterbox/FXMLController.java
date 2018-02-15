package com.mosegames.chatterbox;

import java.io.*;
import java.net.*;
import java.util.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public class FXMLController implements Initializable {
    public static class Console extends OutputStream {
        private final TextArea output;

        public Console(TextArea ta) {
            this.output = ta;
        }

        @Override
        public void write(int i) throws IOException {
            output.appendText(String.valueOf((char) i));
        }
    }
    
    public PrintStream out;
    public InputStream in;
    
    @FXML private TextArea output;
    @FXML private TextField input;
    
    @FXML private void quit(ActionEvent event) {
    	Chatterbox.close();
    }
    
    public void write(int i) throws IOException {
       output.appendText(String.valueOf((char) i));
    }
    
    @Override public void initialize(URL url, ResourceBundle rb)  {
        
        //out put
        Console console = new Console(output);
        out = new PrintStream(console, true);
        System.setOut(out);
        //System.setErr(out);
        
        //input
        try {
            File file = File.createTempFile( "interface" , ".tmp" );
            file.deleteOnExit();
            
            System.setIn( new FileInputStream( file ) );

            PrintStream inpt = new PrintStream(new FileOutputStream(file));
            
            input.setOnAction(e -> {
                inpt.println( input.getText() );
                input.clear();
            });
        } catch (IOException ex) {}
    }
}