package main.java;

import java.util.List;

public class MainClass {
	public static void main(String[] args) {
		List<Ville> listeVille = Ville.chargerVilles("villes.csv");
		listeVille.forEach(v -> System.out.println(v));

		List<Trajet> listeTrajets = Trajet.chargerTrajets("bus.csv", "car.csv", "train.csv");
		//listeTrajets.forEach(t -> System.out.println(t));
		System.out.println(listeTrajets.size());

		System.out.println("========================");

		List<Trajet> listeTrajetsAtoB = Trajet.trouverTrajetSimple(listeTrajets, "a", "b");
		listeTrajetsAtoB.forEach(t -> System.out.println(t));

		System.out.println("========================");

		TrajetCompose voyage = new TrajetCompose();
		voyage.add(listeTrajetsAtoB.get(0));
		voyage.add(Trajet.trouverTrajetSimple(listeTrajets, "b", "d").get(0));
		
		System.out.println(voyage);
	}
}
