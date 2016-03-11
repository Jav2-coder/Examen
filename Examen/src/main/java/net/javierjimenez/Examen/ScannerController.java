package net.javierjimenez.Examen;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ScannerController {
	@FXML
	private Button carregarClasse;
	@FXML
	private Label nomModul = new Label();
	@FXML
	private Button comprovarScanner;
	@FXML
	private ListView<String> nets = new ListView<>();
	@FXML
	private ListView<String> bruts = new ListView<>();
	
	private List<String> alumnes = new ArrayList<String>();
	
	private Set<String> alumnesNets = new HashSet<String>();
	
	private Set<String> alumnesBruts = new HashSet<String>();

	private boolean existeix;
	
	/**
	 * Metode per capturar les dades dels arxius.txt
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void dadesClasse(ActionEvent event) throws IOException {
		
		FileChooser dades = new FileChooser();
		dades.getExtensionFilters().addAll(new ExtensionFilter("TXT Files", "*.txt"));
		
		File selectedFile = dades.showOpenDialog(null);
		
		BufferedReader br;
		FileReader fl;
		
		try {
			if (selectedFile != null) {

				fl = new FileReader(selectedFile);
				
				br = new BufferedReader(fl);
				
				String primeraLinea = br.readLine();
				
				if (primeraLinea != null){
					
					String segonaLinea = br.readLine();
					
					if(primeraLinea.contains("Classe de ") && segonaLinea.contains("--------")) {
						
						nomModul.setText(primeraLinea.substring(10, primeraLinea.length()));
						
						String alumne = "";
						
						while((alumne = br.readLine()) != null){
							alumnes.add(alumne);
						}
						
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Dialeg Informatiu");
						alert.setHeaderText(null);
						alert.setContentText("El document esta mal redactat i/o esta buit!");

						alert.showAndWait();
					}
				}
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	/**
	 * Metode encarregat de comprovar si els alumnes que tenim guardats son nets o bruts
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void comprovarScanner(ActionEvent event) throws IOException {
		
		existeix = false;
		
		nets.getItems().clear();
		bruts.getItems().clear();
		
		Document doc = Jsoup.connect("http://localhost:4567/resultat").get();
		
		Elements resultats = doc.select("resultat"); 
		
		for(int i = 0; i < alumnes.size(); i++){
			
			if(alumnes.get(i).contains(resultats.text())){
				
				existeix = true;
				
			}
		}
		
		if(existeix) {
			Elements netes = resultats.select("part");
			
			System.out.println("Tamaño etiquetas netes: " + netes.size());
			
			if (netes.size() < 5){
				
				alumnesBruts.add(resultats.text());
				
			} else {
				
				alumnesNets.add(resultats.text());
				
			}
		}	
		
		for (String alumneBrut : alumnesBruts){
			
			if(!alumnesNets.contains(alumneBrut)){
				bruts.getItems().add(alumneBrut);
			}			
		}
		
		for (String alumneNet : alumnesNets){
			
			if(alumnesBruts.contains(alumneNet)){
				alumnesBruts.remove(alumneNet);
			}
			nets.getItems().add(alumneNet);
		}
		
		System.out.println("Bruts: " + alumnesBruts.size() + " | Nets: " + alumnesNets.size());
		
		if((alumnes.size() - 2) == alumnesNets.size()){
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Dialeg Informatiu");
			alert.setHeaderText(null);
			alert.setContentText("Ja s'han comprovat tots els alumnes!");

			alert.showAndWait();
			
			nets.getItems().clear();
			bruts.getItems().clear();
			
			alumnesNets.clear();
			alumnesBruts.clear();
		}
	}
	/**
	 * Metode encarregat de realitzar el metode comprovarScanner cada pocs segons.
	 * 
	 * @param event
	 * @throws IOException 
	 */
	@FXML
	public void habilitarCompContinua(ActionEvent event) {
		
	}
}
