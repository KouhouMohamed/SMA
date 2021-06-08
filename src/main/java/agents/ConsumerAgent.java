package agents;

import java.util.Date;

import containers.ConsumerContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

public class ConsumerAgent extends GuiAgent{

	protected ConsumerContainer consumerContainer;
	String book = ""; 
	/*
	 * Premiere methode qui s'éxécute pour initialiser l'agent*/
	@Override
	protected void setup() {
		if(this.getArguments().length == 1) {
			consumerContainer =(ConsumerContainer)this.getArguments()[0];
			consumerContainer.setConsumerAgent(this);
		}
		System.out.println("Initialisation de l'agent "+this.getAID().getName());
		System.out.println("I'm traying to buy books");
	
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage response = receive();
				if(response != null) {
					System.out.println("get response");
					if(response.getContent() == null)
						consumerContainer.logMessage(null);
					else
						consumerContainer.logMessage(response);
				}else {
					block();
				}
			}
		});
		addBehaviour(parallelBehaviour);
		
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

	@Override
	protected void onGuiEvent(GuiEvent event) {
		if(event.getType() == 1) {
			String bookName =(String) event.getParameter(0);
			System.out.println("Agent "+getAID().getLocalName()+" send "+bookName);
			
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent(bookName);
			message.addReceiver(new AID("buyer",AID.ISLOCALNAME));
			message.setLanguage("Bu");
			send(message);
		}
		
	}
	
	public void guitEvent(GuiEvent arg0) {
		onGuiEvent(arg0);
	}
}
