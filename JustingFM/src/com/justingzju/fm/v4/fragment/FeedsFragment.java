package com.justingzju.fm.v4.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.justingzju.fm.R;
import com.justingzju.fm.adapter.FeedAdapter;
import com.justingzju.fm.service.DownloadService;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.util.LogUtil;

public class FeedsFragment extends Fragment implements LoaderCallbacks<Cursor>,
		OnItemClickListener {

	public static final String TAG = FeedsFragment.class.getSimpleName();

	private static final LogUtil mLog = new LogUtil(
			FeedsFragment.class.getSimpleName(), true);

	private FeedAdapter mFeedAdapter;

	private onFeedSelectListener mOnFeedSelectListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mOnFeedSelectListener = (onFeedSelectListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.gridview, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GridView gridView = (GridView) view.findViewById(R.id.gridview);
		gridView.setOnItemClickListener(this);

		mFeedAdapter = new FeedAdapter(getActivity(), R.layout.gridview_items,
				null, new String[] { Feed.TITLE, Feed.OWNER }, new int[] {
						R.id.gridview_line_one, R.id.gridview_line_two }, 0);
		gridView.setAdapter(mFeedAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// init feeds if feeds is empty
		new InitFeedsTask().execute();

		getLoaderManager().initLoader(0, null, this);
	}

	private class InitFeedsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if (isFeedsEmpty()) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(Feed.TITLE, "静雅思听-中文选集");
				contentValues.put(Feed.OWNER, "静雅思听");
				contentValues
						.put(Feed.LINK,
								"http://www.justing.com.cn:8081/podcast/podxml/free/justing_free.xml");
				contentValues
						.put(Feed.IMAGE_LINK,
								"http://www.justing.com.cn/static/podcastimg/justpod_cn.jpg");
				getActivity().getContentResolver().insert(
						PodProvider.CONTENT_URI_FEEDS, contentValues);
				try {
					String[] feedLinks = loadInitFeedLink();
					Intent addIntent = new Intent(
							DownloadService.ACTION_INIT_FEED);
					addIntent.setClass(getActivity(), DownloadService.class);
					addIntent.putExtra(DownloadService.EXTRA_FEED_LINK,
							feedLinks);
					getActivity().startService(addIntent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private String[] loadInitFeedLink() throws XmlPullParserException,
				IOException {
			XmlPullParser parser = getResources().getXml(R.xml.feeds);
			ArrayList<String> feedLinks = new ArrayList<String>();
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equals("feed")) {
						feedLinks.add(parser.getAttributeValue(null, "link"));
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}

			if (feedLinks.size() <= 0) {
				return null;
			}

			return feedLinks.toArray(new String[feedLinks.size()]);
		}

		private boolean isFeedsEmpty() {
			Cursor cursor = getActivity().getContentResolver().query(
					PodProvider.CONTENT_URI_FEEDS, null, null, null, null);
			boolean isEmpty = (cursor == null || cursor.getCount() <= 0);
			if (cursor != null) {
				cursor.close();
			}
			return isEmpty;
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), PodProvider.CONTENT_URI_FEEDS,
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mFeedAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mFeedAdapter.changeCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mLog.v("onItemClick: position " + position + ", id " + id);
		Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
		mOnFeedSelectListener.onFeedSelect(new Feed(cursor));
	}

	public interface onFeedSelectListener {

		void onFeedSelect(Feed feed);

	}

}
