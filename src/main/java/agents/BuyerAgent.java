package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import containers.BuyerContainer;
import containers.ConsumerContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

public class BuyerAgent extends Agent{

	protected BuyerContainer buyerContainer;
	Map<String, Double> listBooks = new HashMap<String, Double>();
	Map<String, Double> prices = new HashMap<String, Double>();
	String bookName;
	AID[] sellerAgents;
	Double minPrice=null;
	AID consumer;
	private boolean isDemanded=false;
	/*
	 * Premiere methode qui s'éxécute pour initialiser l'agent*/
	@Override
	protected void setup() {
		
		if(this.getArguments().length == 1) {
			buyerContainer =(BuyerContainer)this.getArguments()[0];
		}
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,6000) {
			
			@Override
			protected void onTick() {
				// Update the list of seller agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("book-selling"); template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					sellerAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						sellerAgents[i] = result[i].getName();
					}
					//System.out.println("there are "+sellerAgents.length +" sellers ");
				}
				catch (FIPAException fe) { 
					fe.printStackTrace(); 
				}
				
			}
		});
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				
				ACLMessage message = receive();
				//System.out.println("Waiting ...");
				if(message != null) {
					if(message.getLanguage().equals("Bu")) {
						isDemanded=true;
						consumer = message.getSender();
						//System.out.println("Receive demande");
						String bookname = message.getContent();
						bookName = bookname;
						//System.out.println("I receive "+bookname+" from "+message.getSender().getLocalName());
						
						for(int i=0;i<sellerAgents.length;i++) {
							//System.out.println("Request Sent to "+sellerAgents[i].getLocalName());
							ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
							request.setContent(bookname);
							request.addReceiver(new AID(sellerAgents[i].getLocalName(),AID.ISLOCALNAME));
							send(request);
						}
						
					}
					else if(message.getLanguage().equals("Se")) {
						
						//System.out.println("Get proposition");
						Double price;
						
						if(message.getContent() != null) {
							price = Double.valueOf(message.getContent());
							prices.put(message.getSender().getLocalName(), price);
							//response.setContent("Book : "+bookName+", Price : "+String.valueOf(price)+", Seller : "+message.getSender().getLocalName());
						}
						/*else {
							response.setContent(null);
						}
						response.addReceiver(new AID("consumer",AID.ISLOCALNAME));
						send(response);
						*/
					}
					buyerContainer.logMessage(message);
				}
				else {
					block();
					System.out.println("Block");
				}
					
				
			}
		});
		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,1000) {
			
			@Override
			protected void onTick() {
				if(isDemanded && prices.size()==sellerAgents.length) {
					List<AID> agents = getMinPrice();
					ACLMessage response = new ACLMessage(ACLMessage.INFORM);
					response.addReceiver(consumer);
					for(AID agent : agents) {
						response.setContent("Book : "+bookName+", Price : "+String.valueOf(minPrice)+", Seller : "+agent.getLocalName());
						send(response);
					}
					isDemanded=false;
					
				}
			}
		});
		addBehaviour(parallelBehaviour);
		System.out.println("Initialisation de l'agent "+this.getAID().getName());
		System.out.println("I'm a "+ this.getLocalName()+" traying to help consumer buying books");
		

	}
	
	/*
	 * Methode s'éxécute avant la migration*/
	@Override
	protected void beforeMove() {
		try {
			System.out.println("Before migration from " +this.getContainerController().getContainerName());
			System.out.println(".....................");
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void afterMove() {
		try {
			System.out.println("After migration to "+this.getContainerController().getContainerName());
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(".....................");
	}
	
	/*
	 * Avant la distruction de l'agent*/
	@Override
	protected void takeDown() {
		System.out.println("I'm going ti die");
		System.out.println(".....................");
	}
	// trouver le minimum des prix reçu
	private List<AID> getMinPrice(){
		List<String> agents = new ArrayList<String>();
		for(Entry<String, Double> e : this.prices.entrySet()) {
			if(minPrice == null)
				minPrice = e.getValue();
			else {
				minPrice = (minPrice>e.getValue())?e.getValue():minPrice;
			}
		}
		
		for(Entry<String, Double> e : this.prices.entrySet()) {
			if(minPrice == e.getValue())
				agents.add(e.getKey());
		}
		return getSellers(agents);
	}
	private List<AID> getSellers(List<String> agents){
		List<AID> sellers = new ArrayList<AID>();
		for(int i=0; i<sellerAgents.length;i++) {
			if(agents.contains(sellerAgents[i].getLocalName()))
				sellers.add(sellerAgents[i]);
		}
		return sellers;
	}
}
