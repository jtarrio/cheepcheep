package org.tarrio.cheepcheep;

import java.util.List;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.model.Tweet;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TweetListAdapter extends ArrayAdapter<Tweet> {

	private LayoutInflater inflater;

	public TweetListAdapter(Context context, List<Tweet> objects) {
		super(context, R.layout.tweet, objects);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.tweet, parent, false);
		Tweet tweet = getItem(position);
		TextView author = (TextView) view.findViewById(R.id.Author);
		TextView date = (TextView) view.findViewById(R.id.Date);
		TextView text = (TextView) view.findViewById(R.id.Text);
		
		author.setText(tweet.getScreenName());
		date.setText(DateUtils.getRelativeDateTimeString(getContext(), tweet
				.getDateTime().getTime(), DateUtils.SECOND_IN_MILLIS,
				DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
		text.setText(tweet.getText());
		return view;
	}
	
}
