package de.henniges.heatmap;

public class Heatpoint {
	
    public int x;
    public int y;
    public int intensity;
	
    public Heatpoint(int x, int y, int intensity) {
            this.x = x;
            this.y = y;
            this.intensity = intensity;
    }
    
    public Heatpoint(int x, int y){
            this(x,y,1);
    }
    
}