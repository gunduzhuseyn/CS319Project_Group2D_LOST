package GameManagement;
/*
** GameEngine Class

** GameEngine class is where all of game operations are carried out.
** 
**
** Author: Eren Bilaloglu
** File created:  29.04.2017
** Last modified: 30.04.2017
** Last modified by: Eren Bilaloglu
*/

import GameObjectsManagement.AreaManagement.*;
import GameObjectsManagement.EventManagement.*;
import GameObjectsManagement.CharacterManagement.Character;
import GameObjectsManagement.CharacterManagement.*;

import GameObjectsManagement.ItemManagement.*;

import DatabaseManagement.DatabaseManager;

import java.util.*;
import javax.swing.*;

public class GameEngine {

	private DatabaseManager databaseManager;
	private MapManager mapManager;
	private UpdateManager updateManager;
	private Area positionOfUser;
	private Player player;
	private RadioTower radioTower; 
	private SailingAway sailingAway; 
	private OldWiseMan oldWiseMan; 
	private DragonLiar dragonLiar; 
	private ArrayList<Event> eventList;
	private boolean isEntered;
	
	public GameEngine(){
		String uniqueID = UUID.randomUUID().toString();
		databaseManager = new DatabaseManager(uniqueID);
		updateManager = new UpdateManager();
		mapManager = new MapManager();
		radioTower = new RadioTower();
		sailingAway = new SailingAway();
		oldWiseMan = new OldWiseMan();
		dragonLiar = new DragonLiar();
		eventList.add(radioTower);
		eventList.add(sailingAway);
		eventList.add(oldWiseMan);
		eventList.add(dragonLiar);
	}


	public void createGameEnvironment(boolean isNewGame){

		updateManager.createGameEnvironment(isNewGame,databaseManager);//creating areas
		positionOfUser = updateManager.getPositionOfUser();//initial position of user

	}

	public void navigate(String direction){

		if(direction.equals("left")){
			positionOfUser = positionOfUser.getLeftNeighbour();
		}
		else if(direction.equals("right")){
			positionOfUser = positionOfUser.getRightNeighbour();

		}
		else if(direction.equals("up")){
			positionOfUser = positionOfUser.getUpNeighbour();

		}
		else if(direction.equals("down")){
			positionOfUser = positionOfUser.getDownNeighbour();
		}
		
		String currentAreaName = positionOfUser.getAreaType().getAreaName();
		player.setCurrentPosition(currentAreaName);
		databaseManager.setWorkingArea(currentAreaName);
		databaseManager.processData(player,DatabaseManager.WriteAction.WRITE_PLAYER);
		mapManager.processMapp(positionOfUser);//updating map
	}

	
	public ArrayList<String> getCraftableItems(String itemName){
		
		CraftableItem item = (CraftableItem)player.getInventory().getItem(itemName);
		ArrayList<String> itemListString = new ArrayList<String>();
		ArrayList<Item> itemListObject = item.getCraftableItemsList();
			
		if(player.hasItem(itemName)){
			
			for(int i=0; i<itemListObject.size(); i++)
				itemListString.add(itemListObject.get(i).getName());
		}
			return itemListString;	
	}
	
	public ArrayList<String> getRequiredItems(String itemName){
		          
		CraftableItem item = (CraftableItem)player.getInventory().getItem(itemName);
		ArrayList<String> itemListString = new ArrayList<>();
		ArrayList<Item> itemListObject = item.getRequiredItemsList();
			
		for(int i=0; i<itemListObject.size(); i++)
			itemListString.add(itemListObject.get(i).getName());
		
		return itemListString;	
	}
	
	public boolean craft(String craftedItemName){
		
		ArrayList<String> requiredItems = getRequiredItems(craftedItemName);
		
		for(int i=0; i<requiredItems.size(); i++)
			if(!player.hasItem(requiredItems.get(i)))
				return false;
		
		player.craft(craftedItemName);
		player.updateHourCount(5.5);
		return true;
		
	}
	
	
	public String fight(Character character){
		
		//just in case
		if(player.getHealth() <= 0 || character.getHealth() <= 0)
			return "Dead man cannot fight";
	
		else{			
			Random randomGen = new Random();
			int missedShotPlayer = randomGen.nextInt(1+(int)(1-character.getEscapeChance())*10)+1; //character's chance of escape from attack
			int missedShotCharacter = randomGen.nextInt(1+(int)(1-player.getEscapeChance())*10)+1; //player's chance of escape from attack
			
			if(missedShotPlayer == 1){							
				if(character instanceof AggresiveCharacter){			
					if(missedShotCharacter == 1){
						player.updateHourCount(4);
						return "You missed your shot! " + character.getName() + " did not get any damage!\n"
								+ character.getName() + "missed its shot! You did not get any damage!";
					}
					
					else{
						if(player.getDefense() == ((AggresiveCharacter)character).getAttack())
							player.updateHealth(-10);
						
						if(player.getDefense() > ((AggresiveCharacter)character).getAttack())
							player.updateHealth(-Math.abs((((AggresiveCharacter)character).getAttack()-player.getDefense()))/2);
						
						if(player.getDefense() < ((AggresiveCharacter)character).getAttack())
							player.updateHealth(-((AggresiveCharacter)character).getAttack());		
						
						if(player.getHealth() > 0){
							player.updateHourCount(4);
							return "You missed your shot! " + character.getName() + " did not get any damage!\n"
									+ "You got wounded!";	
						}
						else{
							player.updateHourCount(4);
							return "You missed your shot! " + character.getName() + " did not get any damage!\n"
									+ "You got killed...";	
						}
						}
					}	
				
				else{
					player.updateHourCount(4);
					return "You missed your shot! " + character.getName() + " did not get any damage!";
				}
				}
			
			else{			
				if(player.getAttack() == character.getDefense())
					character.updateHealth(-10);
				
				if(player.getAttack() < character.getDefense())
					character.updateHealth(-Math.abs((player.getAttack()-character.getDefense()))/2);
				
				if(player.getAttack() > character.getDefense())
					character.updateHealth(-player.getAttack());
				
				//if character dies, player gets all of its items
				if(character.getHealth() <= 0){
					ArrayList<Item> characterItems = character.getInventory().getStoredItems();
					for(int i=0; i<characterItems.size(); i++)
						player.addItem(characterItems.get(i).getName());	
					
					player.updateHourCount(4);
					return "You killed " + character.getName();
				}
				
				else{			
					if(character instanceof AggresiveCharacter){						
						if(missedShotCharacter == 1)
							return "You wounded  " + character.getName() + "!\n"
							+ character.getName() + "missed its shot! You did not get any damage!";
						
						else{						
							if(player.getDefense() == ((AggresiveCharacter)character).getAttack())
								player.updateHealth(-10);
							
							if(player.getDefense() > ((AggresiveCharacter)character).getAttack())
								player.updateHealth(-Math.abs((((AggresiveCharacter)character).getAttack()-player.getDefense()))/2);
							
							if(player.getDefense() < ((AggresiveCharacter)character).getAttack())
								player.updateHealth(-((AggresiveCharacter)character).getAttack());		
							
							if(player.getHealth() > 0){
								player.updateHourCount(4);
								return "You wounded  " + character.getName() + "!\n" + "You got wounded!";	
							}
							
							else{
								player.updateHourCount(4);
								return "You wounded  " + character.getName() + "!\n" + "You got killed...";
							}
						}
					}	
				
					else{
						player.updateHourCount(4);
						return "You wounded  " + character.getName() + "!\n";
					}
				}
			}					
		}			
	}

	
	public boolean isCampfireLit(){
		
		return positionOfUser.isCampFireExists();
		
	}
	
	public boolean makeCampfire(){
		
		if(!positionOfUser.isCampFireExists() && player.hasItem("Fire") && player.hasItem("Wood")){
			positionOfUser.setCampFireExists(true);
			return true;
		}
		
		return false;
	}
	
	public boolean cookMeat(){
		
		return player.cookMeat();
	}
	
	public boolean boilWater(){
		
		return player.boilWater();
		
	}
	
	public void rest(int duration){
		
		if(duration == 1)
			player.setEnergy(player.getEnergy()+10);
		
		if(duration == 4)
			player.setEnergy(player.getEnergy()+40);
			
		if(duration == 8)
			player.setEnergy(player.getEnergy()+80);
	}
	
	public boolean buildShelter(){
		
		if(!positionOfUser.isShelterExists() && player.hasItem("Wood") && player.hasItem("Branch")
				&& player.hasItem("Stone") && player.hasItem("Rope")){
			positionOfUser.setShelterExists(true);
			return true;
		}
		
		return false;		
		
	}

	public boolean isGameOver(){
		
		if(player.getHealth() <= 0)
			return true;
		
		return false;
	}

	public DatabaseManager getDatabaseManager(){
		return databaseManager;
	}

	public Area getPositionOfUser(){
		return positionOfUser;
	}
	
	public boolean isEventCompleted(){
		
		if(isEntered){
			for(Event e: eventList)
				if(e.playStory(positionOfUser, player))
					return true;
		}
	
		return false;			
	}
	
	public String enterEvent(String eventName){
		
		if(eventName.equals("Radio Tower")){
			if(radioTower.checkRequirements(player))
				return "You entered Radio Tower story event" + radioTower.getDescription();			
			else
				return radioTower.getRequirements();
		}
		
		if(eventName.equals("Sailing Away")){
			if(sailingAway.checkRequirements(player))
				return "You entered Sailing Away story event" + sailingAway.getDescription();		
			else
				return sailingAway.getRequirements();
		}
		
		if(eventName.equals("Dragon Liar")){
			if(radioTower.checkRequirements(player))
				return "You entered Dragon Liar story event" + dragonLiar.getDescription();
			else
				return dragonLiar.getRequirements();		
		}
		
		if(eventName.equals("Old Wise Man")){
			if(radioTower.checkRequirements(player))
				return "You entered Old Wise Man story event" + oldWiseMan.getDescription();
			else
				return oldWiseMan.getRequirements();
		}
		
		return "You did not enter in any story event";		
	}
}
	
