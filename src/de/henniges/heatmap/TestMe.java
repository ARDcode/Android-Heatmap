package de.henniges.heatmap;

import java.io.File;
import java.io.FileOutputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Environment;
import android.webkit.WebView;

public class TestMe {
//
//	File file;
//	
//	private void generateImg(WebView webview) {
//	    // TODO Auto-generated method stub
//	      try{
//
//	          Picture p = webview.capturePicture();
//	           Bitmap bitmap=pictureDrawable2Bitmap(p);
//	            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
//	            File myDir = new File(root + "/Aack");
//	            if(myDir.exists())
//	             {
//	                //Log.e("Directory","Existed");
//	             }
//	             else
//	             {
//	                myDir.mkdir();
//	             }
//	             String fname = System.currentTimeMillis()+".png";
//	             //Log.e("file name...",""+fname);
//	             file = new File (myDir, fname);
//	             try 
//	             {
//	                 FileOutputStream out = new FileOutputStream(file);
//	                 bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//	                 out.flush();
//	                 out.close();
//
//	             }
//	             catch (Exception e) 
//	             {
//	                    e.printStackTrace();
//	             }
//	             file_name=myDir+"/"+fname;
//
//
//	       }catch(Exception e)
//	       {
//	        e.printStackTrace();
//	       }
//
//	}
//
//
//
//
//	private static Bitmap pictureDrawable2Bitmap(Picture picture){
//	        PictureDrawable pictureDrawable = new PictureDrawable(picture);
//	        Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
//	        Canvas canvas = new Canvas(bitmap);
//	        canvas.drawPicture(pictureDrawable.getPicture());
//	        return bitmap;
//	    }
//	
//    	public static Bitmap decodeSampledBitmapFromResource(Bitmap bm) {
//
//		    // First decode with inJustDecodeBounds=true to check dimensions
//		    final BitmapFactory.Options options = new BitmapFactory.Options();
//		    options.inBitmap = bm;
//		    options.inJustDecodeBounds = true;
//		    BitmapFactory.decodeResource(res, resId, options);
//	
//		    // Calculate inSampleSize
//		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//	
//		    Picture picture = new Picture();
//		    
//		    
//		    // Decode bitmap with inSampleSize set
//		    options.inJustDecodeBounds = false;
//		    return BitmapFactory.decodeResource(res, resId, options);
//	}
//
//    	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//    	    // Raw height and width of image
//    	    final int height = options.outHeight;
//    	    final int width = options.outWidth;
//
//    	    int stretch_width = Math.round((float)width / (float)reqWidth);
//    	    int stretch_height = Math.round((float)height / (float)reqHeight);
//
//    	    if (stretch_width <= stretch_height) return stretch_height;
//    	    else return stretch_width;
//	}
//

}
