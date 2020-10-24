package main.java;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class MainClass extends Application{


	// Variables grobales pour JavaFX
	private static int tailleFx = 900;
	private static Group root = new Group();
	private static int space = 15;
	private static double scale = 15;

	public static void initFx(Scene scene, List<Ville> listeVille, List<Trajet> listeTrajets) {
		listeTrajets.stream().forEach(t -> {
			Line l = new Line();
			l.setStartX(listeVille.stream().filter(v -> v.getNom().equals(t.getOrigine())).findFirst().get().getX());
			l.setStartY(listeVille.stream().filter(v -> v.getNom().equals(t.getOrigine())).findFirst().get().getY());

			l.setEndX(listeVille.stream().filter(v -> v.getNom().equals(t.getDestination())).findFirst().get().getX());
			l.setEndX(listeVille.stream().filter(v -> v.getNom().equals(t.getDestination())).findFirst().get().getY());

			root.getChildren().add(l);
		});
	}

	@Override     
	public void start(Stage primaryStage) throws Exception { 
		/* 
       Code for JavaFX application. 
       (Stage, scene, scene graph) 
		 */
		List<Ville> listeVille = Ville.chargerVilles("villes.csv");
		List<Trajet> listeTrajetSimple = Trajet.chargerTrajetsSimple("bus.csv", "car.csv", "train.csv");


		Scene scene = new Scene(root, tailleFx, tailleFx);
		initFx(scene, listeVille, listeTrajetSimple);

		scene.setFill(Color.LIGHTSLATEGRAY); 

		primaryStage.setTitle("Trajets - Voyage");
		primaryStage.setScene(scene); 
		primaryStage.show();
	} 

	public static void main(String[] args) {

		//listeTrajetSimple.forEach(t -> System.out.println(t));
		//System.out.println(listeTrajetSimple.size());

		launch(args); // -> Lancer la Scene JavaFX

		List<Trajet> listeTrajets = Trajet.chargerTrajets("bus.csv", "car.csv", "train.csv"); // -> 960 Trajets
		//listeTrajets.forEach(t -> System.out.println(t));
		//System.out.println(listeTrajets.size());

		List<Trajet> listeTrajetsAtoB = Trajet.trouverTrajetSimple(listeTrajets, "a", "b");
		//listeTrajetsAtoB.forEach(t -> System.out.println(t));

		/*
		TrajetCompose voyage = new TrajetCompose();
		voyage.add(listeTrajetsAtoB.get(0));
		voyage.add(Trajet.trouverTrajetSimple(listeTrajets, "b", "d").get(0));		
		System.out.println(voyage);
		 */
	}
}
