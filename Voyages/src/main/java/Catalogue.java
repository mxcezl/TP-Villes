package main.java;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
public class Catalogue {
	
	/**table origine, liste de trajets à partir de origine*/
	private Map<String, List<Trajet>> catalogueTrajets;

	public Catalogue() {
		this.catalogueTrajets = new HashMap<>();
	}

	/**ajout d'un trajet à la bonne place dans la table
	 * (fonction non utilisée ici)*/
	public void addTrajetSimple(Trajet trajet) {
		
		catalogueTrajets.compute(trajet.getOrigine(), (v,l) -> {
			if (l == null) {
				l = new ArrayList<>();
			}
			l.add(trajet);
			return l;
		});
	}


	/** trouver tous les chemins directs entre départ et arrivée à partir d'une date
	 * plus un certain retard
	 *
	 * @param depart  ville de depart
	 * @param arrivee  ville d'arrivee
	 * @param dateDepart date de depart
	 * @param delaiMax minutes de retard autorisees
	 * @return list of all the direct journeys between start and stop
	 */
	public List<Trajet> trouveCheminsDirects(String depart, String arrivee, LocalTime dateDepart, int delaiMax) {
		
		List<Trajet> cheminsDirects = null;
		List<Trajet> trajets = catalogueTrajets.get(depart);
		if (trajets != null) {
			cheminsDirects = new ArrayList<>(List.copyOf(trajets));
			LocalTime dateDepartMax = dateDepart.plusMinutes(delaiMax);
			cheminsDirects.removeIf(t->(!arrivee.equals(t.getDestination()) || t.getDepartureTime().isBefore(dateDepart) || t.getDepartureTime().isAfter(dateDepartMax) ) );
			if (cheminsDirects.isEmpty()) cheminsDirects = null;
		}
		return cheminsDirects ;
	}

	/**crée les trajets de l'énoncé et les ajoute à la table*/
	public void creerCatalogue(List<Trajet> listeTrajets)
	{
		//génère la map en groupant les trajets par ville de départ
		this.catalogueTrajets = listeTrajets.stream().collect(Collectors.groupingBy(Trajet::getOrigine));
	}

	/**
	 * calcule les chemins directs et indirects possibles entre 2 villes
	 * a partir d'une date donne avec un retard et un delai entre voyage autorise
	 * @param depart  vile de depart
	 * @param arrivee ville d'arrivee
	 * @param date date de depart souhaitee
	 * @param delai delai maximal autorise avant de partir, ou entre 2 voyages
	 * @param voyageEnCours voyage en train d'etre construit
	 * @param via liste des villes visitees par le voyage
	 * @param results liste de tous les chemins indirects possibles
	 * @return true si au moins un chemin a ete trouve
	 */
	public boolean trouverCheminIndirect(String depart, String arrivee, LocalTime date, int delai, ArrayList<Trajet> voyageEnCours, ArrayList<String> via, List<TrajetCompose> results) {
		
		boolean result = false;
		via.add(depart);
		
		//recherche des trajets à partir de depart
		List<Trajet> liste = new ArrayList<>(catalogueTrajets.get(depart));
		
		if (liste.isEmpty()) {
			return false;
		}
		
		//calcul de la date de depart au plus tard
		LocalTime dateDepartMax = date.plusMinutes(delai);
		
		//retrait des trajets partant trop tot ou trop tard
		liste.removeIf(t-> (t.getDepartureTime().compareTo(date) < 0) || t.getDepartureTime().compareTo(dateDepartMax) > 0);
		
		for (Trajet t : liste) {
			//si on trouve un trajet menant à l'arrivée
			if (t.getDestination() == arrivee) {
				
				//on l'ajoute au voyage en cours
				voyageEnCours.add(t);
				
				//on cree un nouveau trajet compose reprenant le detail du voyage
				TrajetCompose compo = new TrajetCompose();
				
				compo.add(List.copyOf(voyageEnCours));
				
				//on l'ajoute au resultat
				results.add(compo);
				
				//on retire le dernier trajet pour éventuellement en cherche un autre (plus rapide, moins cher, ...)
				voyageEnCours.remove(voyageEnCours.size() - 1);
			} else {
				
				//si le trajet ne mène pas à l'arrivee mais donc à un via
				if (!via.contains(t.getDestination())) {
					//on l'ajoute au voyage en cours
					voyageEnCours.add(t);
					//on cherche à partir du via vers l'arrivee
					trouverCheminIndirect(t.getDestination(), arrivee, t.getArrivalTime(), delai, voyageEnCours, via, results);
					//on retire les derniers ajouts pour chercher d'autres chemins
					via.remove(t.getDestination());
					voyageEnCours.remove(t);
				}
			}
		}
		result = !results.isEmpty();
		return result;
	}

	/*TEST DES CLASSES*/
	public static void main(String[] args) {
		List<Trajet> listeTrajets = Trajet.chargerTrajets("bus.csv", "car.csv", "train.csv");
		
		Catalogue cata = new Catalogue();
		cata.creerCatalogue(listeTrajets);

		List<Trajet> ts = cata.trouveCheminsDirects("a", "f", LocalTime.of(7, 30), 15);
		if(ts!=null) {
			for(Trajet t:ts) {
				System.out.println(t);
			}	
		} else {
			System.out.println("aucun trajet trouve");
		}

		List<TrajetCompose> voyages = new ArrayList<>();
		boolean result = cata.trouverCheminIndirect("a", "c", LocalTime.of(6,0), 90,  new ArrayList<Trajet>(), new ArrayList<String>(), voyages);
		if(result) {
			voyages.forEach(System.out::println);
		} else {
			System.out.println("aucun trajet trouve");
		}
		
		/*
		TrajetCompose plusRapide = Collections.min(voyages, Comparator.comparingInt(TrajetCompose::getDureeTotale));
		System.out.println("plus rapide = " + plusRapide);

		TrajetCompose moinsCouteux = Collections.min(voyages, Comparator.comparingDouble(TrajetCompose::getCoutTotal));
		System.out.println("moins couteux  = " + moinsCouteux);
		*/
	}
}