package main.java;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller implements Initializable{

	// Elements JavaFX
	@FXML
	private ComboBox<String> villeDepartCombo;
	@FXML
	private ComboBox<String> villeArriveeCombo;
	@FXML
	private ComboBox<String> selectionCombo;
	@FXML
	private ComboBox<String> heureDepartCombo;
	@FXML
	private ComboBox<String> delaiCombo;
	@FXML
	private TableView<Trajet> tableViewTrajets;
	
	// Kiste des trajets
	List<Trajet> listeTrajets = Trajet.chargerTrajets("data/bus.csv", "data/car.csv", "data/train.csv"); // -> 960 Trajets

	// V�rification des inputs
	private boolean checkInputs() {
		
		// On v�rifie que les champs ne sont pas nulls
		if (null == villeDepartCombo.getValue() 
				|| null == villeArriveeCombo.getValue() 
				|| null == selectionCombo.getValue() 
				|| null == heureDepartCombo.getValue()
				|| null == delaiCombo.getValue()) {
			return false;
		}
		
		// Si ils ne sont pas nulls (pour �viter NullPointerException), on v�rifie qu'ils sont renseign�s
		return !(villeDepartCombo.getValue().isEmpty() 
				|| villeArriveeCombo.getValue().isEmpty()
				|| villeArriveeCombo.getValue().equals(villeDepartCombo.getValue())
				|| selectionCombo.getValue().isEmpty()
				|| heureDepartCombo.getValue().isEmpty()
				|| Arrays.asList(heureDepartCombo.getValue().split(":")).size() != 2
				|| delaiCombo.getValue().isEmpty()
			);
	}
	
	// Affiche un message d'alerte pass� en param�tre dans une fenetre d�di�e
	private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle("Attention !");
        alert.setContentText(message);
 
        alert.showAndWait();
    }
	
	// Ajoute des trajets dans le tableau de sortie
	private void ajouterTrajetTableau(List<Trajet> listeTrajets) {
		tableViewTrajets.getItems().addAll(listeTrajets);
	}
	
	// Ajoute la l�gende des trajets dans un groupe
	private void ajouterLegende(Group root) {
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
	}
	
	// Ajoute les villes sur un Group
	private void ajouterVilles(Group root) {
		
		double scale = 14;
		double space = 18;
		List<Ville> listeVille = Ville.chargerVilles("data/villes.csv");

		// Ajout des traits entre les villes
		listeTrajets.stream().forEach(t -> { // Pour chaque trajet, cr�e une ligne entre le point de d�part et le point d'arriv�e
			Line l = new Line();

			// Point de d�part
			l.setStartX(listeVille.stream().filter(v -> v.getNom().equals(t.getOrigine())).findFirst().get().getX() * scale);
			l.setStartY(listeVille.stream().filter(v -> v.getNom().equals(t.getOrigine())).findFirst().get().getY() * scale);

			//Point d'arriv�e
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

		// Ajout des points des villes
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
	
	// Fonction utilis� lorsque l'on clique sur "Afficher la carte"
	public void afficherCarte() {

		double tailleFx = 1000;
		
		// Cr�e une nouvelle fenetre JavaFX
		Group root = new Group();
		Scene scene = new Scene(root, tailleFx, tailleFx-(tailleFx*0.2));
		scene.setFill(Color.LIGHTGRAY);
		
		Stage stage = new Stage();
		stage.getIcons().add(new Image("main/ressources/icon.png"));
        stage.setTitle("Carte - Trajets");
        stage.setScene(scene);
		
        // Ajout des elements de la carte
		ajouterLegende(root);
		ajouterVilles(root);
		
		// On affiche la fenetre
		stage.show();
	}
	
	// Fonction de recherche des trajets avec les crit�res donn�s
	private void searchTrajets(String villeDepart, String villeArrivee, String order, LocalTime heureDepart, int delai) {
		List<Trajet> listeTrajetsGlobal = Trajet.trouverTrajetSimple(listeTrajets, villeDepart, villeArrivee); // Tout les trajets entre villeDepart et villeArrivee
		List<Trajet> listeTrajetTime = Trajet.trouverTrajetHeure(listeTrajetsGlobal, heureDepart, delai);      // Tout les trajets entre villeDepart et villeArivee + critere d'heure

		// Trie la liste des trajets en fonction de l'ordre choisi par l'utilisateur
		List<Trajet> trajetsTrouve = null;
		if(listeTrajetTime.size() > 0) { // Si des trajets correspondent aux cri�res de recherche
			switch(order) {
			case "dur�e": // Tri� par la dur�e la plus courte
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				break;
			case "economique": // Tri� par prix
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				break;
			case "ecologique": // Tri� par �mission de Co2
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				break;
			case "confort": // Tri� en fonction du confort du trajet
				trajetsTrouve = new ArrayList<>(listeTrajetTime.stream().sorted(Comparator.comparing(Trajet::getDuration)).collect(Collectors.toList()));
				Collections.reverse(trajetsTrouve);
				break;
			default:
				trajetsTrouve = new ArrayList<>();
			}

			// On ajoute tout les trajets correspondants dans le tableau
			ajouterTrajetTableau(trajetsTrouve);
		} else { // Si aucun trajet a �t� trouve
			
			showAlert("Aucun trajet trouv�."); // On affiche une erreur
		}
	}
	
	// Fonction utilis�e lorsque l'on clique sur "Chercher"
	// V�rifie si les inputs sont corrects et recupere les valeurs pour la recherche de trajets
	public void search() {
		if(checkInputs()) { // Si les inputs sont corrects
			
			// Vide le tableau de sortie a chaque nouvelle recherche
			tableViewTrajets.getItems().clear();
			
			// R�cuperation des valeurs
			String depart = villeDepartCombo.getValue().toLowerCase();
			String arrivee = villeArriveeCombo.getValue().toLowerCase();
			String order = selectionCombo.getValue().toLowerCase();
			int delai = Integer.valueOf(delaiCombo.getValue());
			
			List<String> hDepart = Arrays.asList(heureDepartCombo.getValue().split(":"));
			LocalTime hDepartTime = LocalTime.of(Integer.valueOf(hDepart.get(0)), Integer.valueOf(hDepart.get(1)));
			
			// Recherche des trajets
			searchTrajets(depart, arrivee, order, hDepartTime, delai);
		} else { // Sinon on affiche une erreur
			
			showAlert("Veuillez v�rifier vos crit�res de recherche");
		}
	}
	
	// Methode interne utilis�e dans initialize()
	// Cr�e les colonnes du tableau de sortie
	private void ajouterColonnesTableView() {
		TableColumn<Trajet, String> origine = new TableColumn<Trajet, String>("Origine");
		origine.setCellValueFactory(new PropertyValueFactory<>("origine"));
		
		TableColumn<Trajet, String> destination = new TableColumn<Trajet, String>("Destination");
		destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
		
		TableColumn<Trajet, Moyen> moyen = new TableColumn<Trajet, Moyen>("Moyen");
		moyen.setCellValueFactory(new PropertyValueFactory<>("means"));
		
		TableColumn<Trajet, LocalTime> depart = new TableColumn<Trajet, LocalTime>("Depart");
		depart.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
		
		TableColumn<Trajet, LocalTime> arrivee = new TableColumn<Trajet, LocalTime>("Arrivee");
		arrivee.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
		
		TableColumn<Trajet, Integer> duree = new TableColumn<Trajet, Integer>("Duree");
		duree.setCellValueFactory(new PropertyValueFactory<>("duration"));
		
		TableColumn<Trajet, Integer> prix = new TableColumn<Trajet, Integer>("Prix");
		prix.setCellValueFactory(new PropertyValueFactory<>("prix"));
		
		TableColumn<Trajet, Integer> co2 = new TableColumn<Trajet, Integer>("Co2");
		co2.setCellValueFactory(new PropertyValueFactory<>("co2"));
		
		TableColumn<Trajet, Integer> confort = new TableColumn<Trajet, Integer>("Confort");
		confort.setCellValueFactory(new PropertyValueFactory<>("confort"));
		
		tableViewTrajets.getColumns().clear();
		tableViewTrajets.getColumns().add(origine);
		tableViewTrajets.getColumns().add(destination);
		tableViewTrajets.getColumns().add(moyen);
		tableViewTrajets.getColumns().add(depart);
		tableViewTrajets.getColumns().add(arrivee);
		tableViewTrajets.getColumns().add(duree);
		tableViewTrajets.getColumns().add(prix);
		tableViewTrajets.getColumns().add(co2);
		tableViewTrajets.getColumns().add(confort);
		
		// Aligner les elements texts au milieu
		tableViewTrajets.getColumns().stream().forEach(c -> c.setStyle( "-fx-alignment: CENTER;"));
	}
	
	// Fonction d'initialisation du GUI
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		// Options des listes d�roulantes utilisant les Villes
		ObservableList<String> optionsVille = FXCollections.observableArrayList( "A", "B", "C", "D", "E", "F" );
		
		// Option de la liste d�roulante de crit�res de recherche
		ObservableList<String> optionsRecherche = FXCollections.observableArrayList( "Confort", "Ecologique", "Economique", "Dur�e" );
		
		// Option de la liste d�roulante des horaires de depart
		List<LocalTime> times = new ArrayList<>(Arrays.asList(LocalTime.of(0, 0)));
		for(int i = 1; i < 24*2; i++) { times.add(times.get(0).plusMinutes(30 * i)); }
		
		// Option de la liste d�roulante pour le d�lai maximal entre l'heure de d�part et l'heure recherch�e
		List<Integer> delais = new ArrayList<>(Arrays.asList(0));
		for(int i = 1; i < 7; i++) { delais.add(delais.get(0) + i * 15); }
		
		// Convert en objet Observable list
		ObservableList<String> optionsHeuresDepart = FXCollections.observableArrayList(times.stream().map(t -> t.toString()).collect(Collectors.toList()));
		ObservableList<String> optionsDelai = FXCollections.observableArrayList(delais.stream().map(t -> t.toString()).collect(Collectors.toList()));
		
		// Update des listes d�roulantes
		villeDepartCombo.setItems(optionsVille);
		villeArriveeCombo.setItems(optionsVille);
		selectionCombo.setItems(optionsRecherche);
		heureDepartCombo.setItems(optionsHeuresDepart);
		delaiCombo.setItems(optionsDelai);

		// Creation du tableau de sortie
		ajouterColonnesTableView();
	}
}
