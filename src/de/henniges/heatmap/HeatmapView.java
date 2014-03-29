package de.henniges.heatmap;

import java.util.List;

import de.henniges.heatmap.Store.OnRedrawListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


/**
 * s
 * 
 * @author robin henniges
 *
 */
public class HeatmapView extends SurfaceView implements SurfaceHolder.Callback {


	// Colors for gradient in argb
    private int COLORS[] =  {0xffff0000, 0xeaff9900, 0xc7ffcc00, 0x9500cc00,
    						 0x63003399, Color.TRANSPARENT};
	
	private static final int MAX_RADIUS = 50;

	private Bitmap heatmapBitmap;
	
	private Bitmap heatmapBitmapPrint;

	private SurfaceHolder mSurfaceHolder;
	
	private int mCanvasWidth;
	
	private int mCanvasHeight;	
	
    private Context context;
    
	private SurfaceThread thSurf;

	private Store store;


    public HeatmapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		heatmapBitmap = Bitmap.createBitmap(1, 1, conf);
		
		if(store==null){
        	store = new Store();
    		store.setOnRedrawListener(listener);
		}
        
	}
    
    public Store getStore() {
		return store;
	}

    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heatmapBitmapPrint = Bitmap.createBitmap(heatmapBitmap);
        
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    private void refreshBitmap(int max) {
    	
    	float radiusFaktor = MAX_RADIUS / max;
        
    	heatmapBitmap.eraseColor(android.graphics.Color.TRANSPARENT);

    	synchronized (store.getHeat()) {
		
	    	for (Heatpoint heatpoint : store.getHeat()) {
	    		float rad = (radiusFaktor * heatpoint.intensity);
	    		if(rad <= 0) rad=0.0004f;
	    		
	            RadialGradient g = new RadialGradient(heatpoint.x, heatpoint.y, 
	            									  rad, 
	            									  COLORS, null, TileMode.CLAMP);
	
	            Paint gp = new Paint();
	            gp.setShader(g);
	            Canvas c = new Canvas(heatmapBitmap);
	            c.drawCircle(heatpoint.x, heatpoint.y, rad, gp);
			}
    	}
    }

	@Override
	public void surfaceChanged(SurfaceHolder surHolder, int format, int width, int height) {
		setSurfaceSize(width, height);		
	}

    public void setSurfaceSize(int width, int height) {
        synchronized (mSurfaceHolder) {
            mCanvasWidth = width;
            mCanvasHeight = height;

            // adjust floorplance size
            heatmapBitmap  = Bitmap.createScaledBitmap(
            		heatmapBitmap, mCanvasWidth, mCanvasHeight, true);
	        heatmapBitmapPrint = Bitmap.createBitmap(heatmapBitmap);

        }
    }
	
	@Override
	public void surfaceCreated(SurfaceHolder surHolder) {
		thSurf = new SurfaceThread(surHolder, context, this);
		thSurf.setRun();
		thSurf.start();	
	}

	private void doDraw(Canvas canvas) {
         
		canvas.drawBitmap(heatmapBitmapPrint, 0, 0, null);

	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder sh) {		
	}
	
	
	   /**
	* Thread for updating the canvas. 
	* 
	*/
	   protected class SurfaceThread extends Thread{
	    	
		  private boolean isRunning;
		  private SurfaceHolder surfaceHolder;
		  private Context context;
		  private HeatmapView mView;
		  
		  public SurfaceThread(SurfaceHolder surHolder,Context con, HeatmapView flpView) {
			this.surfaceHolder = surHolder;
			this.context = con;
			this.mView = flpView;
			isRunning = false;
		  }
		  
		  protected void setRun(){
			  isRunning = true;
		  }
		  
		  protected void setStop(){
			  isRunning = false;
		  }
		  
		  
		  @Override
		  public void run() {
			super.run();
			
			while(isRunning){
				Canvas canvas = surfaceHolder.lockCanvas();
				if(canvas!=null){
					mView.doDraw(canvas);
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		  }
	   }
   
	
	   private void colorize() {
		   
		   if(heatmapBitmap.getWidth()==mCanvasWidth && 
				   heatmapBitmap.getHeight()==mCanvasHeight){
			   
	           int[] pixels = new int[(int) (mCanvasWidth * mCanvasHeight)];
	           heatmapBitmap.getPixels(pixels, 0, mCanvasWidth, 0, 0, mCanvasWidth,
	                           mCanvasHeight);

	           heatmapBitmapPrint = Bitmap.createBitmap(heatmapBitmap);

	           
	           for (int i = 0; i < pixels.length; i++) {
	                   int r = 0, g = 0, b = 0, tmp = 0;
	                   int alpha = pixels[i] >>> 24;
	                   if (alpha == 0) {
	                           continue;
	                   }
	                   if (alpha <= 255 && alpha >= 235) {
	                           tmp = 255 - alpha;
	                           r = 255 - tmp;
	                           g = tmp * 12;
	                   } else if (alpha <= 234 && alpha >= 200) {
	                           tmp = 234 - alpha;
	                           r = 255 - (tmp * 8);
	                           g = 255;
	                   } else if (alpha <= 199 && alpha >= 150) {
	                           tmp = 199 - alpha;
	                           g = 255;
	                           b = tmp * 5;
	                   } else if (alpha <= 149 && alpha >= 100) {
	                           tmp = 149 - alpha;
	                           g = 255 - (tmp * 5);
	                           b = 255;
	                   } else
	                           b = 255;
	                   pixels[i] = Color.argb((int) alpha / 2, r, g, b);
	           }
	           heatmapBitmapPrint.setPixels(pixels, 0, this.mCanvasWidth, 0, 0, this.mCanvasWidth,
	                           this.mCanvasHeight);
		   }else{
			   setSurfaceSize(heatmapBitmap.getWidth(), 
					   		  heatmapBitmap.getHeight());
		   }
   }
	   
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	
	   public interface OnFingerprintClickListener{

			public void onClick(View v);
			   
	   }
	   
	OnRedrawListener listener = new OnRedrawListener() {
		
		@Override
		public void onReqRedraw(int max) {
			if(store!=null)	
				refreshBitmap(max);	
				colorize();
		}
	};
	   	   
}
