package containers;
import agents.Seller2Agent;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Seller2Container extends Application{

	private ObservableList<String> observableList;

	public static void main(String[] args) {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		primaryStage.setTitle("Seller Container");
		BorderPane borderPane = new BorderPane();
		observableList = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(observableList);
		Label label = new Label("List Des demandes");
		label.setPadding(new Insets(20));
		
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10));
		vbox.getChildren().addAll(listView);
		
		borderPane.setTop(label);
		borderPane.setCenter(vbox);
		
		
		Scene scene = new Scene(borderPane, 400, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void startContainer() throws Exception {
		Runtime runtime = Runtime.instance();
		
		ProfileImpl profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		AgentContainer container = runtime.createAgentContainer(profile);
		
		AgentController consumerController1 = container.createNewAgent("seller_2", "agents.Seller2Agent",new Object[] {this});

		consumerController1.start();
	}
	public void logReceiveMessage(ACLMessage message) {
		observableList.add("Receive :  Demande "+message.getContent()+" book from "+message.getSender().getLocalName());
	}
	public void logSendMessage(String bookName, double price,String agentName) {
		observableList.add("Send :  Proposition "+price+" DH for "+bookName+" to "+agentName);
	}

}
