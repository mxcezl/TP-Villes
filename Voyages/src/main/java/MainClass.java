package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainClass extends Application{


	@Override     
	public void start(Stage primaryStage) throws Exception { 
		
		// Ajoute un icon
		primaryStage.getIcons().add(new Image("main/ressources/icon.png"));
		
		// Charge le fichier fxml
	    Parent rootFXML = FXMLLoader.load(getClass().getResource("../ressources/sample.fxml"));
	    
	    // Ajout du titre et de la scene
	    primaryStage.setTitle("Recherche de trajets - TransPoule");
	    primaryStage.setScene(new Scene(rootFXML, 600, 419));
	    
	    // Affiche la fenetre
	    primaryStage.show();

	} 

	public static void main(String[] args) {
		launch(args);
	}
}
