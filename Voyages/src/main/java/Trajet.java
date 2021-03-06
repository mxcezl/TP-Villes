package main.java;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;

public class Trajet {
	private String origine;
	private String destination;
	private Moyen means;
	private LocalTime departureTime;
	private LocalTime arrivalTime;
	private int duration;
	private int prix;
	private int co2;
	private int confort;
	
	// Constructeur
	public Trajet() {}
	

	// Methode statique renvoyant un trajet allant de start � end qui est dans listeIn
	public static List<Trajet> trouverTrajetSimple(List<Trajet> listeIn, String start, String end) {
		List<Trajet> listeTrajet = new ArrayList<>();
		
		listeIn.stream().forEach(t -> {
			if(start.equals(t.getOrigine()) && end.equals(t.getDestination())) {
				listeTrajet.add(t);
			}
		});
		
		return listeTrajet;
	}
	
	// Methode statique renvoyant tout les trajets partant � departTime avec un d�lai de 'delai' dans une liste de trajets listeTrajets
	public static List<Trajet> trouverTrajetHeure(List<Trajet> listeTrajet, LocalTime departTime, int delai) {
		
		List<Trajet> output = new ArrayList<>();
		LocalTime departDelaiMax = departTime.plusMinutes(delai);
		listeTrajet.stream().forEach(t -> {
			
			// On verifie pour chaque trajet dans la liste si l'heure de d�part est comprise entre departTime et departTime + delai
			if((t.getDepartureTime().equals(departTime) || t.getDepartureTime().isAfter(departTime)) && t.getDepartureTime().isBefore(departDelaiMax)) {
				output.add(t);
			}
		});
		
		return output;
	}
	
	// Methode statique renvoyant une liste de trajets charg�e depuis un fichier CSV
	public static List<Trajet> chargerTrajets(String... paths) {
		List<Trajet> listeTrajets = new ArrayList<>();
		
		for(String path : paths) {
			try {
				Reader reader = Files.newBufferedReader(Paths.get(path));
				CSVReader csvReader = new CSVReader(reader);
				String[] nextRecord;
				
				// Passer l'entete
				csvReader.readNext();
				
				while ((nextRecord = csvReader.readNext()) != null) {
					int time = Integer.parseInt(nextRecord[3].trim());
					int h = time / 100;
					int m = time - h * 100;
					
					LocalTime dateDepartLocal = LocalTime.of(h, m);
					String originLocal = nextRecord[0];
					String destinationLocal = nextRecord[1];
					Moyen meansLocal = Moyen.valueOf(nextRecord[2].trim().toUpperCase());
					int durationLocal = Integer.parseInt(nextRecord[4].trim());
					int prixLocal = Integer.parseInt(nextRecord[5].trim());
					int co2Local = Integer.parseInt(nextRecord[6].trim());
					int confortLocal = Integer.parseInt(nextRecord[7].trim());
					
					int repetitions = Integer.parseInt(nextRecord[8].trim());
					int frequence = Integer.parseInt(nextRecord[9].trim());
					
					HashMap<LocalTime,LocalTime> departsEtArrivees = calcule(dateDepartLocal, durationLocal, frequence, repetitions);
					
					departsEtArrivees.entrySet().stream().forEach(entry -> {
						Trajet tempLambda = new Trajet();
						tempLambda.setOrigine(originLocal);
						tempLambda.setDestination(destinationLocal);
						tempLambda.setMeans(meansLocal);
						tempLambda.setDuration(durationLocal);
						tempLambda.setPrix(prixLocal);
						tempLambda.setCo2(co2Local);
						tempLambda.setConfort(confortLocal);
						tempLambda.setDepartureTime(entry.getKey());
						tempLambda.setArrivalTime(entry.getValue());
						listeTrajets.add(tempLambda);
					});
				}
				
				csvReader.close(); // Ferme le stream
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return listeTrajets;
	}
	
	// Retourne une liste de trajets "simples" avec uniquement la ville de depart, d'arriv�e et le moyen
	public static List<Trajet> chargerTrajetsSimple(String... paths) {
		List<Trajet> listeTrajets = new ArrayList<>();
		
		for(String path : paths) {
			try {
				Reader reader = Files.newBufferedReader(Paths.get(path));
				CSVReader csvReader = new CSVReader(reader);
				String[] nextRecord;
				
				// Passer l'entete
				csvReader.readNext();
				
				while ((nextRecord = csvReader.readNext()) != null) {
					Trajet temp = new Trajet();
					
					String originLocal = nextRecord[0];
					String destinationLocal = nextRecord[1];
					Moyen meansLocal = Moyen.valueOf(nextRecord[2].trim().toUpperCase());
					
					temp.setOrigine(originLocal);
					temp.setDestination(destinationLocal);
					temp.setMeans(meansLocal);
					
					listeTrajets.add(temp);
				}
				
				csvReader.close(); // Ferme le stream
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return listeTrajets;
	}
	
	// Methode qui calcule les heures de departs et heures d'arriv�es en fonction du premier horaire et du nombre de r�p�titions
	public static HashMap<LocalTime, LocalTime> calcule(LocalTime departureTime, int duration, int frequence, int repetitions) {
		HashMap<LocalTime, LocalTime> map = new HashMap<>();
		
		for(int i = 0; i < repetitions; i++) {
			LocalTime dateDepart = departureTime.plusMinutes(i * frequence);
			LocalTime dateArrivee = dateDepart.plusMinutes(duration);
			map.put(dateDepart, dateArrivee);
		}
		
		return map;
	}
	
	// Getters / Setters
	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getOrigine() {
		return origine;
	}
	public void setOrigine(String origine) {
		this.origine = origine;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Moyen getMeans() {
		return means;
	}
	public void setMeans(Moyen means) {
		this.means = means;
	}
	public LocalTime getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getPrix() {
		return prix;
	}
	public void setPrix(int prix) {
		this.prix = prix;
	}
	public int getCo2() {
		return co2;
	}
	public void setCo2(int co2) {
		this.co2 = co2;
	}
	public int getConfort() {
		return confort;
	}
	public void setConfort(int confort) {
		this.confort = confort;
	}

	@Override
	public String toString() {
		return "Trajet [origine=" + origine + ", destination=" + destination + ", means=" + means + ", departureTime="
				+ departureTime + ", arrivalTime=" + arrivalTime + ", duration=" + duration + ", prix=" + prix
				+ ", co2=" + co2 + ", confort=" + confort + "]";
	}

}
