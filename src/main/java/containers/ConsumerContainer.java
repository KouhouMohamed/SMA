package containers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
public class ConsumerContainer extends Application{

	private ConsumerAgent consumerAgent;
	ObservableList<String> observableList;
	String bookname;
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		startConainer();
		primaryStage.setTitle("Consumer Container");
		BorderPane borderPane = new BorderPane();
		
		
		HBox hbox1 = new HBox();
		hbox1.setPadding(new Insets(10));
		hbox1.setSpacing(20);
		Label label = new Label("Book name");
		TextField bookName = new TextField();
		Button searchBtn = new Button("Search");
		hbox1.getChildren().addAll(label,bookName,searchBtn);
		
		observableList = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(observableList);
		
		HBox hbox2 = new HBox();
		hbox2.setPadding(new Insets(10));
		hbox2.getChildren().add(listView);
		
		borderPane.setTop(hbox1);
		borderPane.setCenter(hbox2);
		
		searchBtn.setOnAction(event ->{
			bookname = bookName.getText();
			//bookName.setText(null);
			GuiEvent evt = new GuiEvent(this,1);
			evt.addParameter(bookname);
			
			consumerAgent.guitEvent(evt);
		});
		
		Scene scene = new Scene(borderPane, 400, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	private void startConainer() throws ControllerException{
		Runtime runtime = Runtime.instance();
		
		ProfileImpl profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		AgentContainer container = runtime.createAgentContainer(profile);
		
		AgentController consumerController = container.createNewAgent("consumer", "agents.ConsumerAgent",new Object[] {this});
		consumerController.start();
	}
	public void logMessage(ACLMessage message) {
		if(message.getContent() != null) {
			observableList.add(message.getContent());
		}
		else {
			observableList.add("No buyer found for "+bookname);
		}
			
	}

	public ConsumerAgent getConsumerAgent() {
		return consumerAgent;
	}

	public void setConsumerAgent(ConsumerAgent consumerAgent) {
		this.consumerAgent = consumerAgent;
	}
}
