package main.java;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TrajetCompose {
	private List<Trajet> trajets;
	private String depart;
	private String arrivee;
	private int dureeTotale;
	private LocalTime dateDepart;
	private LocalTime dateArrivee;
	private double coutTotal;

	// Constructeur
	public TrajetCompose() {
		super();
		this.trajets = new ArrayList<>();
	}

	// Ajoute un trajet au trajet composé
	public boolean add(Trajet nouveau) {
		boolean ajout = true;
		int nbTrajets = trajets.size();
		if(nbTrajets==0) {
			trajets.add(nouveau);
			ajout = true;
		} else {
			Trajet dernier = trajets.get(nbTrajets-1);
			if (nouveau.getOrigine().equals(dernier.getDestination()) && dernier.getArrivalTime().isBefore(nouveau.getDepartureTime())) {
				trajets.add(nouveau);
				ajout = true;
			} else {
				ajout = false;
			}
		}
		if(ajout) {
			calcule();
		}
		return ajout;
	}

	public void add(List<Trajet> listeTrajets) {
		trajets.addAll(listeTrajets);
		calcule();
	}
	
	// Calcul du cout et de la duree totale
	private void calcule() {
		int nbTrajets = trajets.size();
		
		// On récupère le premier trajet de la liste
		Trajet premier = trajets.get(0);
		LocalTime depart = premier.getDepartureTime();
		this.setDateDepart(depart);
		this.setDepart(premier.getOrigine());
		
		// On récupère le dernier trajet de la liste
		Trajet dernier = trajets.get(nbTrajets-1);
		LocalTime arrivee = dernier.getArrivalTime();
		this.setArrivee(dernier.getDestination());
		this.setDateArrivee(arrivee);
		
		this.setDureeTotale((int) ChronoUnit.MINUTES.between(depart, arrivee)); // Difference entre l'heure de départ et l'heure d'arrivée
		this.setCoutTotal(trajets.stream().mapToDouble(Trajet::getPrix).sum()); // Somme de tout les prix des Trajets
	}

	
	// Getteurs - Setteurs
	public List<Trajet> getTrajets() {
		return trajets;
	}
	public void setTrajets(List<Trajet> trajets) {
		this.trajets = trajets;
	}
	public String getDepart() {
		return depart;
	}
	public void setDepart(String depart) {
		this.depart = depart;
	}
	public String getArrivee() {
		return arrivee;
	}
	public void setArrivee(String arrivee) {
		this.arrivee = arrivee;
	}
	public int getDureeTotale() {
		return dureeTotale;
	}
	public void setDureeTotale(int dureeTotale) {
		this.dureeTotale = dureeTotale;
	}
	public LocalTime getDateDepart() {
		return dateDepart;
	}
	public void setDateDepart(LocalTime dateDepart) {
		this.dateDepart = dateDepart;
	}
	public LocalTime getDateArrivee() {
		return dateArrivee;
	}
	public void setDateArrivee(LocalTime dateArrivee) {
		this.dateArrivee = dateArrivee;
	}
	public double getCoutTotal() {
		return coutTotal;
	}
	public void setCoutTotal(double coutTotal) {
		this.coutTotal = coutTotal;
	}

	@Override
	public String toString() {
		return "TrajetCompose [nombre de trajets =" + trajets.size() + ", depart=" + depart + ", arrivee=" + arrivee + ", dureeTotale="
				+ dureeTotale + ", dateDepart=" + dateDepart + ", dateArrivee=" + dateArrivee + ", coutTotal="
				+ coutTotal + "]";
	}
}
