package com.app.controller;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.app.database.DBLoginManager;
import com.app.event.ControllerEvent;
import com.app.event.LoginEvent;
import com.app.listener.ControllerListener;
import com.app.main.Main;
import com.app.model.User;
import com.app.util.Initializer;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable, ControllerListener{
	
	@FXML
	private Label welcomeLabel;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button buttonLogin;
	@FXML
	private ImageView imageView;
	@FXML
	private VBox messagePanel;
	@FXML
	private Label messageLabel;
	
	private static DBLoginManager manager;
	private static LoginController loginController;
	
	@FXML
	public synchronized void handleOnAction(ActionEvent event) throws Exception{
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		User user = null;
		Boolean isSuccess = false;

		if((user = manager.getUserWithUsernameAndPassword(username, password)) != null){
			isSuccess = true;
			messagePanel.setStyle("-fx-background-color:#2ecc71");
			messageLabel.setText("welcome back " + user.getFirstName() + " " + user.getLastName() + "!");
			addFadeAnimation(messagePanel);
			
			LoginEvent loginEvent = new LoginEvent();
			loginEvent.setIsSuccess(isSuccess);
			loginEvent.setUser(user);
			
			((Stage) ((Node)event.getSource()).getScene().getWindow()).close();
			
			Initializer.addLoginListener(Main.getInstance());
			Initializer.callLoginListener(loginEvent);
		}else{
			messagePanel.setStyle("-fx-background-color:#e74c3c");
			messageLabel.setText("Incorrect username or password.");
			addFadeAnimation(messagePanel);
		}
		
		
	
	}

	@Override
	public void controllerLoad(ControllerEvent event) {
		if(event.getClazz().trim().equals(getClass().getCanonicalName().trim())){
			manager = (DBLoginManager) event.getManager();
		}
		
	}
	
	public void addFadeAnimation(Node node){
		FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.play();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		try{
			Image image = new Image(new FileInputStream("imp/img/icon/spin.gif"));
			imageView.setImage(image);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static LoginController getInstance(){
		if(loginController == null)
			loginController = new LoginController();
		return loginController;
	}

}
