package com.example.septaviewer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrainAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String,String>> data;
	private static LayoutInflater inflater = null;

	public TrainAdapter(Activity activity, ArrayList<HashMap<String,String>> data){
		this.activity = activity;
		this.data = data;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(convertView == null)
			view = inflater.inflate(R.layout.list_row, null);
		
		TextView trainNo = (TextView)view.findViewById(R.id.trainText);
		TextView departTime = (TextView)view.findViewById(R.id.departTime);
		TextView arriveTime = (TextView)view.findViewById(R.id.arriveTime);
		TextView stationText = (TextView)view.findViewById(R.id.stationText);
		
		HashMap<String, String> train = new HashMap<String,String>();
		train = data.get(position);
		
		trainNo.setText("Train: " + train.get("trainno"));
		departTime.setText("Departs at: " + train.get("net"));
		arriveTime.setText("Arrives at: " + train.get("nst"));
		stationText.setText("Approaching: " + train.get("ns"));
		return view;
	}

}
