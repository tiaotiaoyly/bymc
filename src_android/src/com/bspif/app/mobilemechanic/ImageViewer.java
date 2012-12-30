package com.bspif.app.mobilemechanic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class ImageViewer extends RelativeLayout {

	private final static String TAG = "ImgViewer";
	
	private ImageView mImageView = null;
	private ZoomControls mZoomControls = null;
	private Bitmap mBitmap = null;
	private int mOriginBitmapWidth = -1;
	private float ZOOM_DELTA = 0.3F;
	private float MAX_ZOOM_SCALE = 4.0F;
	private float MIN_ZOOM_SCALE = 1.0F;
	
	public ImageViewer(Context context, Bitmap bitmap) {
		super(context);
		init(context, bitmap);
	}
	
	private void init(Context context, Bitmap bitmap) {
		// create zoom controls
		mZoomControls = new ZoomControls(context);
		RelativeLayout.LayoutParams paramsZoom = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsZoom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		paramsZoom.addRule(RelativeLayout.CENTER_HORIZONTAL);
		paramsZoom.bottomMargin = 50;
		mZoomControls.setLayoutParams(paramsZoom);
		mZoomControls.setOnZoomInClickListener(new OnZoomIn());
		mZoomControls.setOnZoomOutClickListener(new OnZoomOut());
		
		// create image view
		mBitmap = bitmap;
		mOriginBitmapWidth = mBitmap.getWidth();
		mImageView = new ImageView(context);
		mImageView.setImageBitmap(mBitmap);
		RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsImageView.addRule(RelativeLayout.CENTER_IN_PARENT);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth  = wm.getDefaultDisplay().getWidth();  
		int screenHeight = wm.getDefaultDisplay().getHeight(); 
		paramsImageView.width = screenWidth;
		paramsImageView.height = screenHeight;
		mImageView.setLayoutParams(paramsImageView);
		mImageView.setAdjustViewBounds(true);
		
		// add child
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		this.setLayoutParams(params);
		this.setBackgroundColor(Color.argb(180, 0, 0, 0));
		this.addView(mImageView);
		//this.addView(mZoomControls);
	}

	private class OnZoomIn implements OnClickListener {
		public void onClick(View v) {
			if (!zoomIn()) {
				//mZoomControls.setIsZoomInEnabled(false);
			}
			mZoomControls.setIsZoomOutEnabled(true);
		}
	}
	
	private class OnZoomOut implements OnClickListener {
		public void onClick(View v) {
			if (!zoomOut()) {
				//mZoomControls.setIsZoomOutEnabled(false);
			}
			mZoomControls.setIsZoomInEnabled(true);
		}
	}
	
	private boolean zoomIn() {
		float scale = getCurrentScale();
		scale = scale + ZOOM_DELTA < MAX_ZOOM_SCALE ? scale + ZOOM_DELTA : MAX_ZOOM_SCALE;
		scaleImageView(scale);
		if (scale >= MAX_ZOOM_SCALE) {
			return false;
		}
		return true;
	}
	
	private boolean zoomOut() {
		float scale = getCurrentScale();
		scale = scale - ZOOM_DELTA > MIN_ZOOM_SCALE ? scale - ZOOM_DELTA : MIN_ZOOM_SCALE;
		scaleImageView(scale);
		if (scale <= ZOOM_DELTA) {
			return false;
		}
		return true;
	}
	
	private boolean scaleImageView(float scale) {
		Log.d(TAG, "scale imageview %.2f", scale);
		int bmpWidth = mBitmap.getWidth();  
        int bmpHeight = mBitmap.getHeight();    
        Matrix matrix = new Matrix();  
        matrix.postScale(scale, scale);  
        Bitmap resizeBmp = Bitmap.createBitmap(mBitmap,0,0,bmpWidth,bmpHeight,matrix,true);  
        mImageView.setImageBitmap(resizeBmp);  
		return true;
	}

	private float getCurrentScale() {
		float scale = 0;
		scale = mImageView.getMeasuredWidth() / (float)mOriginBitmapWidth;
		
		Log.d(TAG, "get current scale %.2f", scale);
		return scale;
	}
}
