package com.app.window;

import com.app.util.Config;
import com.app.util.ResourceLoader;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmWindow {
	
	private static Boolean flag;
	public static synchronized Boolean display(String message){
		try{
			
			Stage stage = new Stage();
			stage.setTitle("Confirmation");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
			stage.setMaximized(false);
			stage.setOnCloseRequest(e -> {
				flag = false;
			});
			
			VBox box = new VBox(10);
			box.setAlignment(Pos.TOP_CENTER);
			
			VBox header = new VBox();
			header.setPrefHeight(5);
			header.setMinSize(0, 5);
			header.setStyle("-fx-background-color: #ecf0f1");
			
			Label label = new Label(message);
			label.setTextAlignment(TextAlignment.CENTER);
			
			HBox buttonContainer = new HBox(10);
			buttonContainer.setAlignment(Pos.CENTER);
			Button buttonYes = new Button("Yes");
			buttonYes.setId("button-yes");
			buttonYes.setPrefSize(70, 30);
			buttonYes.setOnAction(e -> {
				flag = true;
				if(Config.getConnectionConfig() != null){
					if(Config.getConnectionConfig().getClassName().trim().equals("org.sqlite.JDBC"))
						Config.setConnectionConfig(null, null, null, null, null, null, null);
				}
				
				stage.close();
			});
			
			Button buttonNo = new Button("No");
			buttonNo.setId("button-no");
			buttonNo.setPrefSize(70, 30);
			buttonNo.setOnAction(e ->{
				flag = false;
				stage.close();
			});
			
			buttonContainer.getChildren().addAll(buttonYes,buttonNo);
			box.getChildren().addAll(header, label , buttonContainer);
			
			Scene scene = new Scene(box , 300 , 80);
			scene.getStylesheets().add(ResourceLoader.source("css/confirm_window.css").toString());
			stage.setScene(scene);
			stage.showAndWait();
			
			return flag;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

}
