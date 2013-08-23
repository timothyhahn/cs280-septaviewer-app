package com.example.septaviewer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainActivity extends Activity {

	private  ArrayList<HashMap<String,String>> listData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listData = new ArrayList<HashMap<String,String>>();
		
		String trainJSON = downloadSEPTA("http://www3.septa.org/hackathon/TrainView");
		parseNextTrainData(trainJSON);
		
		
		ArrayList<String> sourceList = new ArrayList<String>();
		for(int i = 0; i < listData.size(); i++) {
			String source = listData.get(i).get("source");
			Log.v("sourceList", source);
			if(!sourceList.contains(source)){
				Log.v("sourceList","ADDED");
				sourceList.add(source);
			}
			
			
		}
		
		String[] sourceArray = new String[sourceList.size()];
		sourceArray = sourceList.toArray(sourceArray);
		
		
		final Spinner sourceSpinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> sourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sourceArray);
		sourceSpinner.setAdapter(sourceAdapter);
		
		final Spinner destinationSpinner = (Spinner) findViewById(R.id.spinner2);

		
		
		sourceSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String station = sourceSpinner.getItemAtPosition(position).toString();
				
				ArrayList<String> destinationList = new ArrayList<String>();
				for(int i = 0; i < listData.size(); i++) {
					Log.v("DL", "1: " + listData.get(i).get("source"));
					Log.v("DL", "2: " + station);
					if(listData.get(i).get("source").equals(station)) {
						String destination = listData.get(i).get("dest");
						Log.v("DL", destination);
						if(!destinationList.contains(destination)) {
							Log.v("DL", "ADDED");
							destinationList.add(destination);
						}
					}
				}
				
				String[] destinationArray;
				destinationArray = new String[destinationList.size()];
				destinationArray = destinationList.toArray(destinationArray);
				
				Context context = parentView.getContext();
				
				ArrayAdapter<String> destinationAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, destinationArray);
				destinationSpinner.setAdapter(destinationAdapter);
				destinationSpinner.invalidate();
				
				
			}

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        sourceSpinner.setSelection(0);
		    }
		});
		
		Button listButton = (Button)findViewById(R.id.button2);
		listButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent listIntent = new Intent(MainActivity.this, ListActivity.class);
				listIntent.putExtra("listData", listData);
				listIntent.putExtra("source", sourceSpinner.getSelectedItem().toString());
				listIntent.putExtra("destination", destinationSpinner.getSelectedItem().toString());
				MainActivity.this.startActivity(listIntent);
				
			}
			
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	
	public void parseNextTrainData(String jsonData){
    	listData.clear();
        JsonParser jp = new JsonParser();
        JsonElement root = null;
        root = jp.parse(jsonData);
        //JsonObject rootobj = root.getAsJsonObject();
        JsonArray trains = root.getAsJsonArray();
        for(int i = 0; i < trains.size(); i++) {
        	JsonObject train = trains.get(i).getAsJsonObject();
        	String lat = train.get("lat").getAsString();
        	String lon = train.get("lon").getAsString();
        	String trainno = train.get("trainno").getAsString();
        	String dest = train.get("dest").getAsString();
        	String nextstop = train.get("nextstop").getAsString();;
        	String late = train.get("late").getAsString();
        	String source = train.get("SOURCE").getAsString();
        	HashMap<String, String> trainData = new HashMap<String, String>();
        	trainData.put("lat", lat);
        	trainData.put("lon", lon);
        	trainData.put("trainno", trainno);
        	trainData.put("dest", dest);
        	trainData.put("nextstop", nextstop);
        	trainData.put("late", late);
        	trainData.put("source", source);
        	listData.add(trainData);
        }
        
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
