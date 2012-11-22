package com.bspif.app.mobilemechanic;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class ImageViewer extends RelativeLayout {

	private final static String TAG = "ImgViewer";
	
	private ImageView mImageView = null;
	private ZoomControls mZoomControls = null;
	private Bitmap mBitmap = null;
	private int mOriginBitmapWidth = -1;
	private int mOriginBitmapHeight = -1;
	
	public ImageViewer(Context context, String filename) {
		super(context);
		init(context, filename);
	}
	
	private void init(Context context, String filename) {
		// create zoom controls
//		mZoomControls = new ZoomControls(context);
//		RelativeLayout.LayoutParams paramsZoom = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		paramsZoom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		paramsZoom.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		paramsZoom.bottomMargin = 50;
//		mZoomControls.setLayoutParams(paramsZoom);
//		mZoomControls.setOnZoomInClickListener(new OnZoomIn());
//		mZoomControls.setOnZoomOutClickListener(new OnZoomOut());
		
		// create image view
		try {
			mBitmap = Util.getBitmapFromAsset(context, filename);
			mOriginBitmapWidth = mBitmap.getWidth();
			mOriginBitmapHeight = mBitmap.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mImageView = new ImageView(context);
		mImageView.setImageBitmap(mBitmap);
		RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mImageView.setLayoutParams(paramsImageView);
		
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
				mZoomControls.setIsZoomInEnabled(false);
			}
			mZoomControls.setIsZoomOutEnabled(true);
		}
	}
	
	private class OnZoomOut implements OnClickListener {
		public void onClick(View v) {
			if (!zoomOut()) {
				mZoomControls.setIsZoomOutEnabled(false);
			}
			mZoomControls.setIsZoomInEnabled(true);
		}
	}
	
	private boolean zoomIn() {
		float scale = getCurrentScale();
		if (scale >= 1) {
			return false;
		}
		scale = scale + 0.1f < 1f ? scale + 0.1f : 1f;
		scaleImageView(scale);
		return true;
	}
	
	private boolean zoomOut() {
		float scale = getCurrentScale();
		if (scale <= 0.5) {
			return false;
		}
		scale = scale - 0.1f > 0.5f ? scale - 0.1f : 0.5f;
		scaleImageView(scale);
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
		scale = mImageView.getMeasuredWidth() / mOriginBitmapWidth;
		
		Log.d(TAG, "get current scale %.2f", scale);
		return scale;
	}
}
