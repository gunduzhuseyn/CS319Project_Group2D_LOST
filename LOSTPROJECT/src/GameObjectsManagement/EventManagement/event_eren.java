package GameObjectsManagement.EventManagement;

import GameObjectsManagement.ObjectManagement.*;
import GameObjectsManagement.AreaManagement.*;
import GameObjectsManagement.CharacterManagement.*;

public abstract class Event extends GameObject {

	 public abstract String playStory(Area a, Player p);
	 
}
