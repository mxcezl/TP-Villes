package main.java;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainClass extends Application{


	// Variables globales pour JavaFX
	private static int tailleFx = 1000;
	private static Group root = new Group();
	private static double scale = 14;
	private static double space = 18;
	
	public static void initFx(Scene scene, List<Ville> listeVille) {


		Scanner scanner = new Scanner(System.in);

		System.out.println("Entrez une ville de départ : ");
		String villeDepart = scanner.nextLine();

		System.out.println("Entrez une ville d'arrivée : ");
		String villeArrivee = scanner.nextLine();

		System.out.println("Entrez une heure de départ (format 800 pour 8h00) : ");
		int time = Integer.valueOf(scanner.nextLine());
		int h = time / 100;
		int m = time - h * 100;
		LocalTime heureDepart = LocalTime.of(h, m);

		System.out.println("Entrez un délai maximum en minutes : ");
		int delai = Integer.valueOf(scanner.nextLine());


		List<Trajet> listeTrajets = Trajet.chargerTrajets("bus.csv", "car.csv", "train.csv"); // -> 960 Trajets

		List<Trajet> listeTrajetsGlobal = Trajet.trouverTrajetSimple(listeTrajets, villeDepart, villeArrivee); // Tout les trajets entre villeDepart et villeArrivee
		List<Trajet> listeTrajetTime = Trajet.trouverTrajetHeure(listeTrajetsGlobal, heureDepart, delai);      // Tout les trajets entre villeDepart et villeArivee + critere d'heure

		List<Trajet> trajetsTrouve = null;
		switch(listeTrajetTime.size()) {
		case 0:
			System.out.println("Aucun trajet trouvé.\n");
			break;
		case 1:
			System.out.println("Un seul trajet disponible.\n");
			trajetsTrouve = Arrays.asList(listeTrajetTime.get(0));
			break;
		default:
			System.out.println("\n" + listeTrajetTime.size() + " trajets trouvés.\n\nVeuillez selectionner un critère de selection :\n\t- 1 : Durée\n\t- 2 : Prix\n\t- 3 : Co2\n\t- 4 : Confort");
			System.out.println("Votre choix : ");
			int choix = Integer.valueOf(scanner.nextLine());
			System.out.println();
			switch(choix) {
			case 1:
				System.out.println("Trajets les plus courts : \n");
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				break;
			case 2:
				System.out.println("Trajets les moins cheres : \n");
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				break;
			case 3:
				System.out.println("Trajets les plus écologiques : \n");
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				break;
			case 4:
				System.out.println("Trajets les plus confortables : \n");
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				Collections.reverse(trajetsTrouve);
				break;
			default:
				trajetsTrouve = new ArrayList<>();
			}
			break;
		}
		scanner.close();

		//        Ajout des labels
		Text textBus = new Text("Trajet BUS");
		textBus.setStyle("-fx-font: 25 arial;");
		textBus.setFill(Color.DARKORANGE);
		textBus.setX(20);
		textBus.setY(30);

		Text textTrain = new Text("Trajet TRAIN");
		textTrain.setStyle("-fx-font: 25 arial;");
		textTrain.setFill(Color.GREEN);
		textTrain.setX(20);
		textTrain.setY(30*2);

		Text textCar = new Text("Trajet VOITURE");
		textCar.setStyle("-fx-font: 25 arial;");
		textCar.setFill(Color.CRIMSON);
		textCar.setX(20);
		textCar.setY(30 * 3);

		root.getChildren().add(textBus);
		root.getChildren().add(textTrain);
		root.getChildren().add(textCar);

		// ---------------------------------------
		if(trajetsTrouve != null) {

			// Affichage de tout les trajets
			trajetsTrouve.stream().forEach(System.out::println);
			
			trajetsTrouve.stream().forEach(t -> { // Pour chaque trajet, crée une ligne entre le point de départ et le point d'arrivée
				Line l = new Line();

				// Point de départ
				l.setStartX(listeVille.stream().filter(v -> v.getNom().equals(t.getOrigine())).findFirst().get().getX() * scale);
				l.setStartY(listeVille.stream().filter(v -> v.getNom().equals(t.getOrigine())).findFirst().get().getY() * scale);

				//Point d'arrivée
				l.setEndX(listeVille.stream().filter(v -> v.getNom().equals(t.getDestination())).findFirst().get().getX() * scale);
				l.setEndY(listeVille.stream().filter(v -> v.getNom().equals(t.getDestination())).findFirst().get().getY() * scale);

				// Choix de la couleur en fonction du moyen
				switch(t.getMeans()) {
				case BUS:
					l.setStroke(Color.DARKORANGE);
					break;
				case CAR:
					l.setStroke(Color.CRIMSON);
					break;
				case TRAIN:
					l.setStroke(Color.GREEN);
					break;
				}
				root.getChildren().add(l);
			});
		}
		listeVille.stream().forEach(v -> {
			Circle c = new Circle(v.getX() * scale, v.getY() * scale, scale);
			c.setFill(Color.BLACK);

			root.getChildren().add(c);

			Text text = new Text(v.getNom().toUpperCase());
			text.setStyle("-fx-font: 25 arial;");
			text.setFill(Color.WHITE);
			text.setX(v.getX() * scale - (space/2));
			text.setY(v.getY() * scale - space);

			root.getChildren().add(text);
		});
	}

	@Override     
	public void start(Stage primaryStage) throws Exception { 
		/* 
       Code for JavaFX application. 
       (Stage, scene, scene graph) 
		 */
		List<Ville> listeVille = Ville.chargerVilles("villes.csv");

		Scene scene = new Scene(root, tailleFx, tailleFx-(tailleFx*0.2));
		initFx(scene, listeVille);

		scene.setFill(Color.LIGHTSLATEGRAY); 

		primaryStage.setTitle("Trajets - Voyage");
		primaryStage.setScene(scene);
		primaryStage.show();
	} 

	public static void main(String[] args) {

		launch(args);
	}
}
