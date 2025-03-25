/*
 * @file ViewTimesListAdapter.java
 * @author Holger Mueller
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.euhm.jlt.listadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.euhm.jlt.R;
import de.euhm.jlt.dao.Times;
import de.euhm.jlt.utils.TimeUtil;

/**
 * List adapter for Times
 *
 * @author hmueller
 * @version $Rev: 46 $
 */
public class ViewTimesListAdapter extends ArrayAdapter<Times> {
	private final String LOG_TAG = ViewTimesListAdapter.class.getSimpleName();

	private Context mContext;
	private List<Times> mListTimes;
	private int mLayoutId;

	public ViewTimesListAdapter(Context context, int layoutId,
								List<Times> listTimes) {
		super(context, layoutId, listTimes);
		Log.d(LOG_TAG, "Creating list adapter");

		mContext = context;
		mLayoutId = layoutId;
		mListTimes = listTimes;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		//Log.d(LOG_TAG, "Start getView position " + position);

		if (convertView == null) {
			//Log.d(LOG_TAG, "Creating new line in the list");
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(mLayoutId, parent, false);
		}
		// If convertView is not null, we can just reuse it from the recycler

		Times times = mListTimes.get(position);
		Calendar timeStart = Calendar.getInstance();
		timeStart.setTimeInMillis(times.timeStart);
		Calendar timeEnd = Calendar.getInstance();
		timeEnd.setTimeInMillis(times.timeEnd);

		TextView dayOfMonth = convertView.findViewById(R.id.view_times_day_of_month);
		TextView dayOfWeek = convertView.findViewById(R.id.view_times_day_of_week);
		TextView monthAndYear = convertView.findViewById(R.id.view_times_month_and_year);
		TextView hours = convertView.findViewById(R.id.view_times_hours);
		TextView duration = convertView.findViewById(R.id.view_times_duration);
		TextView homeOffice = convertView.findViewById(R.id.view_times_homeoffice);

		dayOfMonth.setText(String.format(Locale.getDefault(), "%td", timeStart));
		dayOfWeek.setText(String.format(Locale.getDefault(), "%tA (%d)", timeStart, timeStart.get(Calendar.WEEK_OF_YEAR)));
		monthAndYear.setText(String.format(Locale.getDefault(), "%1$tB %1$tY", timeStart));
		hours.setText(String.format(Locale.getDefault(), "%tR - %tR", timeStart, timeEnd));
		duration.setText(TimeUtil.formatTimeString24(TimeUtil.getWorkedTime(mContext, timeStart, timeEnd, times.homeOffice)) + " (" +
				TimeUtil.formatTimeString24(TimeUtil.getOverTime(mContext, timeStart, timeEnd, times.homeOffice)) + ")");
		homeOffice.setVisibility(times.homeOffice ? View.VISIBLE : View.INVISIBLE);

		return convertView;
	}

	/**
	 * Remove object based on list id.
	 * <p>
	 * If list is rebuild and object is from the list before, object.equals(a[i]) in class.
	 * ArrayList fails. So do the compare with the object.getId().
	 * <p>
	 * Does <b>not</b> do a notifyDataSetChanged(), you have to do this manually!
	 *
	 * @param object Times object
	 */
	@Override
	public void remove(Times object) {
		if (object != null) {
			int s = mListTimes.size();
			for (int i = 0; i < s; i++) {
				Times o = mListTimes.get(i);
				if (o.id == object.id) {
					mListTimes.remove(i);
					return;
				}
			}
		}
	}

	/**
	 * Update object based on list id.
	 * Does <b>not</b> do a notifyDataSetChanged(), you have to do this manually!
	 *
	 * @param object Times object
	 */
	public void update(Times object) {
		if (object != null) {
			int s = mListTimes.size();
			for (int i = 0; i < s; i++) {
				Times o = mListTimes.get(i);
				if (o.id == object.id) {
					mListTimes.set(i, object);
					return;
				}
			}
		}
	}

	/**
	 * Sort the list
	 */
	public void sort() {
		sort((a, b) -> Long.compare(b.timeStart, a.timeStart));
	}

	/**
	 * Refill the list. Does a notifyDataSetChanged().
	 *
	 * @param listTimes List of new times values
	 */
	public void refill(List<Times> listTimes) {
		mListTimes.clear();
		mListTimes.addAll(listTimes);
		notifyDataSetChanged();
	}
}