package com.android.volley.toolbox;

import com.android.volley.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ClrBtnEditText extends EditText {

	private ClrBtnEditText mClrBtnEditText;
	public ClrBtnEditText(Context context) {
        super(context);
        setClrBtnEnable(true);
    }

    public ClrBtnEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClrBtnEnable(true);
    }

    public ClrBtnEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setClrBtnEnable(true);
//        mClrBtnEditText = this;
//        EditText edt = new EditText(context);
    }
    
    public void setClrBtnEnable(boolean isClearable){
    	if(isClearable){
    		Drawable image = getContext().getResources().getDrawable( R.drawable.clear );
        	image.setBounds( 0, 0,40 , 40 );
        	setCompoundDrawables(null, null, image, null);
        	setClearable(true);
    	}else{
    		setCompoundDrawables(null, null, null, null);
    		setClearable(false);
    	}
    	
    }
    private void setClearable(boolean isClearable){
    	if(isClearable){
    		OnTouchListener touchListener = new OnTouchListener() {
    			@Override
    			public boolean onTouch(View v, MotionEvent event) {
    				if (event.getAction() == MotionEvent.ACTION_DOWN){
    					ClrBtnEditText edittext = (ClrBtnEditText)v;
    	                Log.w("EditTextTouch","x = "+event.getX()+", y="+event.getY());
    	                if(event.getX()>v.getWidth() - v.getHeight()){
    	                	edittext.setText("");
    	                }
    	            }
    	            return false;
    			}
    		};
    		setOnTouchListener(touchListener);
    	}else{
    		setClickable(false);
    	}
		
    }
}
