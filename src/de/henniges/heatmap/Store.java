package de.henniges.heatmap;

import java.util.ArrayList;
import java.util.List;

public class Store {

	private int max = 0;
	
	private List<Heatpoint> store;
	
	private OnRedrawListener listener;
	
	
	public Store() {
		this.store= new ArrayList<Heatpoint>();
	}
	
	public void addHeatPoint(int x, int y, int value) {
		
		System.out.println("HeatPoint Add:  "+ value);
		
		if(x < 0 || y < 0) return;
		
		Heatpoint hp = new Heatpoint(x,y,value);
		synchronized (store) {
			store.add(hp);		
		}
		
		if(value > max){
			max = value;
			System.out.println("new max");
		}
		if(listener!=null)listener.onReqRedraw(max++);
 
		
	}
	
	public void setOnRedrawListener(OnRedrawListener listener) {
		this.listener = listener;
	}
	
	public abstract interface OnRedrawListener {
		
		public void onReqRedraw(int max);
		
	}

	public List<Heatpoint> getHeat() {
		return store;
	}

}
