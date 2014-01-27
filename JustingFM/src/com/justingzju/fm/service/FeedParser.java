package com.justingzju.fm.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.util.Xml;

import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;

import static com.justingzju.util.Constant.INVALID_TIME;

public class FeedParser {

	private FileInputStream inputStream;

	public FeedParser(FileInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public ContentValues[] parseAudioValues(long feedId, long lastUpdateTime, int maxInitNum) throws XmlPullParserException, IOException {
		final List<ContentValues> valueslist = new ArrayList<ContentValues>();
		ContentValues values = null;
		long pubDate = INVALID_TIME;
		
		final XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		int eventType = parser.getEventType();
		boolean done = false;
		
		while(eventType!=XmlPullParser.END_DOCUMENT && !done ) {
			String name;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if(name.equalsIgnoreCase("item")) {
					values = new ContentValues();
				} else if (values!=null) {
					if (name.equalsIgnoreCase("title")){
                        values.put(Audio.TITLE, parser.nextText());
                    } else if (name.equalsIgnoreCase("pubDate")){
                    	pubDate = parsePubDate(parser.nextText());
                    	values.put(Audio.PUB_DATE, pubDate);
                    } else if (name.equalsIgnoreCase("duration")){
                    	values.put(Audio.DURATION, parseDuration(parser.nextText()));
                    } else if (name.equalsIgnoreCase("enclosure")){
                    	values.put(Audio.LINK, parser.getAttributeValue(null, "url"));
                    } else if (name.equalsIgnoreCase("summary")) {
                    	values.put(Audio.SUMMARY, parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("item") && values!=null) {
					values.put(Audio.AUTHOR, "佚名");
					values.put(Audio.ANNOUNCER, "未知");
					values.put(Audio.FEED, feedId);
					if (pubDate!=INVALID_TIME && pubDate>lastUpdateTime) {
						valueslist.add(values);
						pubDate = INVALID_TIME;
						done = valueslist.size()>=maxInitNum;
					} else {
						done = true;
					}
					values = null;
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		
		if (valueslist == null || valueslist.size() <= 0) {
			return null;
		}
		return valueslist.toArray(new ContentValues[valueslist.size()]);
	}

	private int parseDuration(String time) {
		if (!time.matches("[0-9]{1,2}:[0-9]{2}")) {
			return INVALID_TIME;
		}
		String[] numbers = time.split(":");
		int minute = Integer.valueOf(numbers[0]);
		int second = Integer.valueOf(numbers[1]);
		return minute*60 + second;
	}

	private long parsePubDate(String time) {
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.US);
		try {
			Date date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return INVALID_TIME;
		}
	}

	public ContentValues parseFeedValues() throws XmlPullParserException, IOException {
		final XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		int eventType = parser.getEventType();
		ContentValues values = new ContentValues();
		int count = 0;
		
		while (eventType!=XmlPullParser.END_DOCUMENT && count!=3 ) {
			String name;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("title")) {
					values.put(Feed.TITLE, parser.nextText());
					count++;
				} else if (name.equalsIgnoreCase("image")) {
					values.put(Feed.IMAGE_LINK, parser.getAttributeValue(null, "href"));
					count++;
				} else if (name.equalsIgnoreCase("name")) {
					// name in owner
					values.put(Feed.OWNER, parser.nextText());
					count++;
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		return values;
	}

}
