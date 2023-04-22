package org.pjc;

public class GameObject {
	private GameObjectTypes type;
	private int[] size;
	private int[] position;
	
	public GameObject(GameObjectTypes type, int size[], int position[]) {
		this.type = type;
		this.size = size;
		this.position = position;
	}

	public GameObjectTypes getType() {
		return type;
	}

	public int[] getSize() {
		return size;
	}

	public int[] getPosition() {
		return position;
	}

	public boolean collides(GameObject gameObject) {
		if (gameObject.getPosition()[0] < position[0] + size[0] && 
				gameObject.getPosition()[0] + gameObject.getSize()[0] > position[0] &&
			     gameObject.getPosition()[1] > position[1] + size[1] && 
			     	gameObject.getPosition()[1] + gameObject.getSize()[1] < position[1])
			return false;
		
		return true;
	}
	
	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof GameObject) {
			GameObject obj = (GameObject)anotherObject;
			if(type == obj.getType() && 
					size[0] == obj.getSize()[0] && size[1] == obj.getSize()[1] &&
						position[0] == obj.getPosition()[0] && position[1] == obj.getPosition()[1])
				return true;
			
			return false;
		}
		else 
			return super.equals(anotherObject);
	}
}
