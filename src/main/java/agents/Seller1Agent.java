package agents;
import java.util.HashMap;
import java.util.Map;

import containers.Seller1Container;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class Seller1Agent extends Agent{

	private Seller1Container sellerContainer;
	Map<String, Double> listBooks = new HashMap<String, Double>();
	ParallelBehaviour parallelBehaviour;
	@Override
	protected void setup() {
		
		listBooks.put("XML", Double.valueOf(120));
		listBooks.put("JAVA", Double.valueOf(1000));
		listBooks.put("PYTHON", Double.valueOf(1200));
		listBooks.put("C++", Double.valueOf(1450));
		listBooks.put("ANGUAR", Double.valueOf(2120));
		listBooks.put("FLUTTER", Double.valueOf(1120));
		
		if(this.getArguments().length == 1) {
			this.sellerContainer =(Seller1Container) this.getArguments()[0];
			
		}
		//Publication du service
		DFAgentDescription dfd=new DFAgentDescription();

		dfd.setName(this.getAID());

		ServiceDescription sd=new ServiceDescription();

		sd.setType("book-selling");

		sd.setName("book-trading");
		
		dfd.addServices(sd);
		try {
		DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
		fe.printStackTrace();
		}
		
		parallelBehaviour =  new ParallelBehaviour();
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage message = receive();
				if(message != null) {
					System.out.println("I receive a requets ");
					System.err.println("Request body : "+message.getContent());
					sellerContainer.logReceiveMessage(message);
					String bookName = message.getContent().toUpperCase();
					String agentName = message.getSender().getLocalName();
					ACLMessage response = new ACLMessage(ACLMessage.INFORM);
					
					if(listBooks.containsKey(bookName)) {
						double price = listBooks.get(bookName);
						sellerContainer.logSendMessage(bookName, price, agentName);
						response.setContent(String.valueOf(price));
					}
					else {
						response.setContent(null);
					}
					response.addReceiver(message.getSender());
					response.setLanguage("Se");
					send(response);
				}
				
			}
		});
		addBehaviour(parallelBehaviour);
	}
@Override
protected void takeDown() {
	try {
		DFService.deregister(this);
		}
		catch (FIPAException fe) {
		fe.printStackTrace();
		}
}
	
	

}
