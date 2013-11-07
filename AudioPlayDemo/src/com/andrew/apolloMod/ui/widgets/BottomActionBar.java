/**
 * 
 */

package com.andrew.apolloMod.ui.widgets;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justingzju.Constant;
import com.justingzju.audioplay.AudioClient;
import com.justingzju.audioplay.R;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * @author Andrew Neal
 */
public class BottomActionBar extends LinearLayout {
	 
    public BottomActionBar(Context context) {
        super(context);
    }

    public BottomActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
	 * Updates the bottom ActionBar's info
	 */
	public void update() {
		if(AudioClient.getService()==null) {
			return;
		}
		
		// Track name
        TextView mTrackName = (TextView) findViewById(R.id.bottom_action_bar_track_name);
        ImageView mTrackImage = (ImageView) findViewById(R.id.bottom_action_bar_album_art);
        TextView mArtistName = (TextView) findViewById(R.id.bottom_action_bar_artist_name);
        try {
			mTrackName.setText(AudioClient.getService().getAudioName());
			ImageLoader.getInstance().displayImage(AudioClient.getService().getAudioImage(), mTrackImage, Constant.DISPLAY_IMAGE_OPTIONS);
			mArtistName.setText(AudioClient.getService().getAuthor());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	
	    updatePlayButton();
	}

	/**
	 * Set the play and pause image
	 */
	public void updatePlayButton() {
		ImageButton mPlay = (ImageButton) findViewById(R.id.bottom_action_bar_play);
		try {
			if (AudioClient.getService().isPlaying()) {
				mPlay.setImageResource(R.drawable.apollo_holo_light_pause);
			} else {
				mPlay.setImageResource(R.drawable.apollo_holo_light_play);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
