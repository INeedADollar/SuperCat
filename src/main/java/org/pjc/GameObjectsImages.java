package org.pjc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameObjectsImages {
	private BufferedImage pinkStar;
	private BufferedImage redStar;
	private BufferedImage yellowStar;
	private BufferedImage blueStar;
	
	private BufferedImage constellationAquarius;
	private BufferedImage constellationAries;
	private BufferedImage constellationCancer;
	private BufferedImage constellationCapricorn;
	private BufferedImage constellationGemini;
	private BufferedImage constellationLeo;
	private BufferedImage constellationLibra;
	private BufferedImage constellationPisces;
	private BufferedImage constellationSagittarius;
	private BufferedImage constellationScorpio;
	private BufferedImage constellationTaurus;
	private BufferedImage constellationVirgo;
	
	private BufferedImage planet1;
	private BufferedImage planet2;
	private BufferedImage planet3;
	private BufferedImage planet4;
	private BufferedImage planet5;
	private BufferedImage planet6;
	
	private BufferedImage asteroid;
	
	public GameObjectsImages() {
		for(GameObjectTypes type : GameObjectTypes.values()) {
			loadImage(type);
		}
	}
	
	public BufferedImage getObjectImage(GameObjectTypes type) {
		switch(type) {
			case OBJECT_ASTEROID:
				return asteroid;
			case OBJECT_BLUE_STAR:
				return blueStar;
			case OBJECT_CONSTELLATION_AQUARIUS:
				return constellationAquarius;
			case OBJECT_CONSTELLATION_ARIES:
				return constellationAries;
			case OBJECT_CONSTELLATION_CANCER:
				return constellationCancer;
			case OBJECT_CONSTELLATION_CAPRICORN:
				return constellationCapricorn;
			case OBJECT_CONSTELLATION_GEMINI:
				return constellationGemini;
			case OBJECT_CONSTELLATION_LEO:
				return constellationLeo;
			case OBJECT_CONSTELLATION_LIBRA:
				return constellationLibra;
			case OBJECT_CONSTELLATION_PISCES:
				return constellationPisces;
			case OBJECT_CONSTELLATION_SAGITTARIUS:
				return constellationSagittarius;
			case OBJECT_CONSTELLATION_SCORPIO:
				return constellationScorpio;
			case OBJECT_CONSTELLATION_TAURUS:
				return constellationTaurus;
			case OBJECT_CONSTELLATION_VIRGO:
				return constellationVirgo;
			case OBJECT_PINK_STAR:
				return pinkStar;
			case OBJECT_PLANET_1:
				return planet1;
			case OBJECT_PLANET_2:
				return planet2;
			case OBJECT_PLANET_3:
				return planet3;
			case OBJECT_PLANET_4:
				return planet4;
			case OBJECT_PLANET_5:
				return planet5;
			case OBJECT_PLANET_6:
				return planet6;
			case OBJECT_RED_STAR:
				return redStar;
			case OBJECT_YELLOW_STAR:
				return yellowStar;
			default:
				return null;
		}
	}
	
	private void loadImage(GameObjectTypes type) {
		switch(type) {
			case OBJECT_ASTEROID: {
				this.asteroid = loadImage("assets/game objects/meteor-1.png");
				break;
			}
			case OBJECT_BLUE_STAR: {
				this.blueStar = loadImage("assets/game objects/star-4.png");
				break;
			}
			case OBJECT_CONSTELLATION_AQUARIUS: {
				this.constellationAquarius = loadImage("assets/game objects/aquarius.png");
				break;
			}
			case OBJECT_CONSTELLATION_ARIES: {
				this.constellationAries = loadImage("assets/game objects/aries.png");
				break;
			}
			case OBJECT_CONSTELLATION_CANCER: {
				this.constellationCancer = loadImage("assets/game objects/cancer.png");
				break;
			}
			case OBJECT_CONSTELLATION_CAPRICORN: {
				this.constellationCapricorn = loadImage("assets/game objects/capricorn.png");
				break;
			}
			case OBJECT_CONSTELLATION_GEMINI: {
				this.constellationGemini = loadImage("assets/game objects/gemini.png");
				break;
			}
			case OBJECT_CONSTELLATION_LEO: {
				this.constellationLeo = loadImage("assets/game objects/leo.png");
				break;
			}
			case OBJECT_CONSTELLATION_LIBRA: {
				this.constellationLibra = loadImage("assets/game objects/libra.png");
				break;
			}
			case OBJECT_CONSTELLATION_PISCES: {
				this.constellationPisces = loadImage("assets/game objects/pisces.png");
				break;
			}
			case OBJECT_CONSTELLATION_SAGITTARIUS: {
				this.constellationSagittarius = loadImage("assets/game objects/sagittarius.png");
				break;
			}
			case OBJECT_CONSTELLATION_SCORPIO: {
				this.constellationScorpio = loadImage("assets/game objects/scorpio.png");
				break;
			}
			case OBJECT_CONSTELLATION_TAURUS: {
				this.constellationTaurus = loadImage("assets/game objects/taurus.png");
				break;
			}
			case OBJECT_CONSTELLATION_VIRGO: {
				this.constellationVirgo = loadImage("assets/game objects/virgo.png");
				break;
			}
			case OBJECT_PINK_STAR: {
				this.pinkStar = loadImage("assets/game objects/star-1.png");
				break;
			}
			case OBJECT_PLANET_1: {
				this.planet1 = loadImage("assets/game objects/planet-1.png");
				break;
			}
			case OBJECT_PLANET_2: {
				this.planet2 = loadImage("assets/game objects/planet-2.png");
				break;
			}
			case OBJECT_PLANET_3: {
				this.planet3 = loadImage("assets/game objects/planet-3.png");
				break;
			}
			case OBJECT_PLANET_4: {
				this.planet4 = loadImage("assets/game objects/planet-4.png");
				break;
			}
			case OBJECT_PLANET_5: {
				this.planet5 = loadImage("assets/game objects/planet-5.png");
				break;
			}
			case OBJECT_PLANET_6: {
				this.planet6 = loadImage("assets/game objects/planet-6.png");
				break;
			}
			case OBJECT_RED_STAR: {
				this.redStar = loadImage("assets/game objects/star-3.png");
				break;
			}
			case OBJECT_YELLOW_STAR: {
				this.yellowStar = loadImage("assets/game objects/star-2.png");
				break;
			}
			default:
				break;
		}
	}
	
	private BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(new File(path));
		}
		catch(IOException e) {
			System.out.println(e);
			e.printStackTrace();
			
			return null;
		}
	}
}
