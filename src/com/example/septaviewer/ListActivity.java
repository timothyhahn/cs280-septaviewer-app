package com.example.septaviewer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ListActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		Intent intent = getIntent();
		ArrayList<HashMap<String,String>> listData = (ArrayList<HashMap<String,String>>)intent.getSerializableExtra("listData");
		String source = intent.getStringExtra("source");
		String destination = intent.getStringExtra("destination");
		final ArrayList<HashMap<String, String>> trainData = new ArrayList<HashMap<String,String>>();

		// Find those that match
		for(int i = 0; i < listData.size(); i++) {
			if(source.equals(listData.get(i).get("source")) && destination.equals(listData.get(i).get("dest"))){
				HashMap<String,String> train = new HashMap<String,String>();
				train.put("trainno", listData.get(i).get("trainno"));
				train.put("nextstop", listData.get(i).get("nextstop"));
				train.put("lat", listData.get(i).get("lat"));
				train.put("lon", listData.get(i).get("lon"));
				String trainJSON = downloadSEPTA("http://www3.septa.org/hackathon/RRSchedules/" + listData.get(i).get("trainno"));
				train = parseScheduleData(trainJSON, train);
				trainData.add(train);
			}	
		}
		
		// Add data to listadapter
		
		ListView list = (ListView)findViewById(R.id.list);
		TrainAdapter adapter = new TrainAdapter(this, trainData);
		
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			}

		
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long ud) {
				String information = "Just Departed: " + trainData.get(position).get("cs") +"\n";
				information += "Previous Scheduled Departure: " + trainData.get(position).get("cst") + "\n";
				information += "Previous Actual Departure: " + trainData.get(position).get("cat") + "\n";
 				Toast.makeText(ListActivity.this, information, Toast.LENGTH_LONG).show();

				return true;
			}
			
		});
		
		Button mapButton = (Button)findViewById(R.id.mapButton);
		mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				HashMap<String, String> train = trainData.get(0);
				String label = Uri.encode(String.format("%s going towards %s",train.get("trainno") ,train.get("ns")));
				String url = String.format("https://maps.google.com/maps?q=%s+%s+(%s)", train.get("lat"), train.get("lon"), label);
				
				Uri uriURL = Uri.parse(url);
				Intent launchMap = new Intent(Intent.ACTION_VIEW,uriURL);
				startActivity(launchMap);
			}
			
		});
	}
	
	public HashMap<String,String> parseScheduleData(String trainJSON, HashMap<String,String> train) {
		JsonParser jp = new JsonParser();
		JsonElement root = null;
		root = jp.parse(trainJSON);
		JsonArray stops = root.getAsJsonArray();
		// Get next stop
		int index = -1;
		for(int i = 0; i < stops.size(); i++) {
			JsonObject stop = stops.get(i).getAsJsonObject();
			if(stop.get("station").getAsString().equals(train.get("nextstop")))
				index = i - 1;
		}
		
		// Get one before it
		
		JsonObject nextObject = stops.get(index + 1).getAsJsonObject();
		
		String nextEstimatedTime = nextObject.get("est_tm").getAsString();
		String nextScheduledTime = nextObject.get("sched_tm").getAsString();
		String nextStation = nextObject.get("station").getAsString();
		
		train.put("net", nextEstimatedTime);
		train.put("nst", nextScheduledTime);
		train.put("ns", nextStation);
		
		
		JsonObject currentObject = stops.get(index).getAsJsonObject();
		String currentStation = currentObject.get("station").getAsString();
		String currentSchedTime = currentObject.get("sched_tm").getAsString();
		String currentEstimatedTime = currentObject.get("est_tm").getAsString();
		String currentActualTime = currentObject.get("act_tm").getAsString();
		
		train.put("cs", currentStation);
		train.put("cst", currentSchedTime);
		train.put("cet", currentEstimatedTime);
		train.put("cat", currentActualTime);
		
		return train;
	}
	
	public String downloadSEPTA(String url) {
		SEPTATask st = new SEPTATask(this);
		String septaJSON = "";
		try {
			st.execute(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			septaJSON = st.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return septaJSON;
	}
	
	

}
