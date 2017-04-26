/*
** CraftableItem Class
** CraftableItem class represents all the items that can be crafted into another item. These items has no additional effects on the player.
** CraftableItem extends the Item class which in turn extends GameObject class. 
**
** Author: Gunduz Huseyn Lee
** File created:  24.04.2017
** Last modified: 24.04.2017
** Last modified by: Gunduz Huseyn Lee
*/
package ItemManagement;
import java.util.*;

public class CraftableItem extends Item{
	//Properties
	private ArrayList<Item> craftableItemsList;
	private ArrayList<Item> requiredItemsList;

	//Constructors
	public CraftableItem(String name){
		super(name);

		craftableItemsList = null;
		requiredItemsList = null;
	}

	public CraftableItem(int id, String name, String description, double weight, int renewalTime, boolean renewable, boolean visible, ArrayList<String> actionList, ArrayList<Item> craftableItemsList, ArrayList<Item> requiredItemsList){
		super(id, name, description, weight, renewalTime, renewable, visible, actionList);
		this.craftableItemsList = craftableItemsList;
		this.requiredItemsList = requiredItemsList;
	}

	//Methods
	public ArrayList<Item> getCraftableItemsList(){
		return this.craftableItemsList;
	}

	public ArrayList<Item> getRequiredItemsList(){
		return this.requiredItemsList;
	}
}