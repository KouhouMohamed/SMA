package containers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import agents.BuyerAgent;
import agents.ConsumerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
public class BuyerContainer extends Application{
	private ObservableList<String> observableListDem;
	private ObservableList<String> observableListPrps;
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		startConainer();
		primaryStage.setTitle("Buyer Container");
		BorderPane borderPane = new BorderPane();
		observableListDem = FXCollections.observableArrayList();
		observableListPrps = FXCollections.observableArrayList();
		
		
		ListView<String> listViewDemandes = new ListView<String>(observableListDem);
		ListView<String> listViewPropos = new ListView<String>(observableListPrps);
		
		Label label3 = new Label("Consumer			Demande");
		label3.setPadding(new Insets(20));
		
		Label label4 = new Label("Proposition			Buyer");
		label4.setPadding(new Insets(20));
		
		VBox demandeVbox = new VBox();
		demandeVbox.setPadding(new Insets(10));
		demandeVbox.getChildren().addAll(label3,listViewDemandes);
		
		VBox proposvbox = new VBox();
		proposvbox.setPadding(new Insets(10));
		proposvbox.getChildren().addAll(label4,listViewPropos);
		
		HBox listsHbox = new HBox();
		listsHbox.getChildren().addAll(demandeVbox,proposvbox);
		listsHbox.setPadding(new Insets(10));
		
		borderPane.setCenter(listsHbox);
		
		
		Scene scene = new Scene(borderPane, 600, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	private void startConainer() throws ControllerException{
		Runtime runtime = Runtime.instance();
		
		ProfileImpl profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		AgentContainer container = runtime.createAgentContainer(profile);
		
		AgentController consumerController = container.createNewAgent("buyer", "agents.BuyerAgent",new Object[] {this});
		consumerController.start();
	}
	
	public void logMessage(ACLMessage message) {
		if(message.getContent() == null) {
			observableListPrps.add("No proposition found");
		}
		else if(message.getLanguage().equals("Bu")) {
			observableListDem.add(message.getContent()+"			"+message.getSender().getLocalName());
			
		}
		else if(message.getLanguage().equals("Se")) {
			observableListPrps.add(message.getContent()+"			"+message.getSender().getLocalName());	
		}
		
	}
	
}
