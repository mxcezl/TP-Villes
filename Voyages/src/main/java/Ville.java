package main.java;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class Ville {
	private String nom;
	private int x;
	private int y;
	
	public Ville() { super(); }
	
	public Ville(String nom, int x, int y) {
		super();
		this.nom = nom;
		this.x = x;
		this.y = y;
	}
	
	public static List<Ville> chargerVilles(String pathCsv) {
		List<Ville> listeVille = new ArrayList<>();
		try {
			Reader reader = Files.newBufferedReader(Paths.get(pathCsv));
			CSVReader csvReader = new CSVReader(reader);
			String[] nextRecord;
			
			// Passer l'entete
			csvReader.readNext();
			
			while ((nextRecord = csvReader.readNext()) != null) {
				Ville temp = new Ville();
				
				temp.setNom(nextRecord[0]);
				temp.setX(Integer.parseInt(nextRecord[1].trim()));
				temp.setY(Integer.parseInt(nextRecord[2].trim()));
				
				listeVille.add(temp);
			}
			
			csvReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listeVille;
	}
	
	public static double getDist(Ville a, Ville b) {
		return Math.sqrt(Math.pow((a.getX() - b.getX()), 2) + Math.pow((a.getY() - b.getY()), 2));
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ville other = (Ville) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Ville [nom=" + nom + ", x=" + x + ", y=" + y + "]";
	}
}
