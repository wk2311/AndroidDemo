/**
 * 
 */

package com.andrew.apolloMod.ui.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.andrew.apolloMod.ui.widgets.BottomActionBar;
import com.justingzju.Constant;
import com.justingzju.LogUtil;
import com.justingzju.audioplay.AudioBarActivity;
import com.justingzju.audioplay.AudioClient;
import com.justingzju.audioplay.R;
import com.justingzju.database.AudioProvider;
//import com.andrew.apolloMod.R;
//import com.andrew.apolloMod.helpers.utils.MusicUtils;
//import com.andrew.apolloMod.helpers.utils.ThemeUtils;
//import com.andrew.apolloMod.service.ApolloService;
import com.justingzju.service.AudioService;
import com.justingzju.service.DownloadService;

/**
 * @author Andrew Neal
 */
public class BottomActionBarFragment extends Fragment {
	
	private final static LogUtil mLog = new LogUtil(BottomActionBarFragment.class.getSimpleName(), true);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private ImageButton mPrev, mPlay, mNext;
    private BottomActionBar mBottomActionBar;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View root = inflater.inflate(R.layout.bottom_action_bar, container);
    	mBottomActionBar = (BottomActionBar) root.findViewById(R.id.bottom_action_bar);
    	
    	mBottomActionBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLog.w("bottom_action_bar onClick");
			}
		});
    	
    	mBottomActionBar.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				mLog.w("bottom_action_bar onLongClick");
				getActivity().getContentResolver().delete(Constant.PROVIDER_AUDIO, "name LIKE ?", new String[]{"%福利%"});
				return false;
			}
		});
    	
        mPrev = (ImageButton)root.findViewById(R.id.bottom_action_bar_previous);
        mPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	System.out.println("bottom_action_bar_previous onClick");
            	try {
					AudioClient.getService().prev();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
//                if (MusicUtils.mService == null)
//                    return;
//                try {
//                    if (MusicUtils.mService.position() < 2000) {
//                        MusicUtils.mService.prev();
//                    } else {
//                        MusicUtils.mService.seek(0);
//                        MusicUtils.mService.play();
//                    }
//                } catch (RemoteException ex) {
//                    ex.printStackTrace();
//                }
            }
        });

        mPlay = (ImageButton)root.findViewById(R.id.bottom_action_bar_play);
        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	mLog.w("bottom_action_bar_play onClick");
				try {
					if(AudioClient.getService().isPlaying()) {
						AudioClient.getService().pause();
					} else {
						AudioClient.getService().play();
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        });

        mNext = (ImageButton)root.findViewById(R.id.bottom_action_bar_next);
        mNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	mLog.w("bottom_action_bar_next onClick");
            	try {
					AudioClient.getService().next();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        });

//        ThemeUtils.setImageButton(getActivity(), mPrev, "apollo_previous");
//        ThemeUtils.setImageButton(getActivity(), mNext, "apollo_next");
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioService.PLAYSTATE_CHANGED);
        filter.addAction(AudioService.META_CHANGED);
        getActivity().registerReceiver(mediaStatusReceiver, filter);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mediaStatusReceiver);
        super.onStop();
    }

	/**
	 * Update the list as needed
	 */
	private final BroadcastReceiver mediaStatusReceiver = new BroadcastReceiver() {
	
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	mBottomActionBar.update();
	    }
	};
}
