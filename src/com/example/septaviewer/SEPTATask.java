package com.example.septaviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SEPTATask extends AsyncTask<URL, Integer, String>{
	ProgressDialog progress;
	Activity activity;
	Context context;
	public SEPTATask(Activity activity) {
		this.activity = activity;
		context = activity;
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();	
	}
	
	@Override
	protected String doInBackground(URL... urls) {
    	URL septaURL = urls[0];
    	
    	String septaJSON ="";
    	try {
        	BufferedReader in = new BufferedReader(new InputStreamReader(septaURL.openStream()));
        	String jsonLine ;
	    	while((jsonLine = in.readLine())!=null){
	    		septaJSON += jsonLine;
	    		//System.out.println(jsonLine);
	    	}
	    	in.close();
    	} catch (IOException ie) {
    		ie.printStackTrace();
    	}
    	return septaJSON;
	}
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
	
}
