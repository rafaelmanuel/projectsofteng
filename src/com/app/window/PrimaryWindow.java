package com.app.window;

import java.io.IOException;
import java.net.URL;

import com.app.main.Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PrimaryWindow {
	
	private static Stage stage;
	
	public static void display(Stage stage) throws IOException{
		PrimaryWindow.stage = stage;
		PrimaryWindow.stage.setMaximized(true);
		PrimaryWindow.stage.setOnCloseRequest(e ->{
			e.consume();
			if(ConfirmWindow.display("Are you sure you want to exit?"))
				PrimaryWindow.stage.close();
		});
		
		Parent root = FXMLLoader.load(new URL(Main.getInstance().DIRECTORY_PATH + "/imp/fxml/primary.fxml"));
		Scene scene = new Scene(root,800,600);
		
		PrimaryWindow.stage.setScene(scene);
		PrimaryWindow.stage.show();
	}

}
