package me.khtn.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import me.khtn.app.AuthenticationActivity;
import me.khtn.handler.AuthenticationHandler;
import me.khtn.uploadimagekhtn.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetProfileTask extends AsyncTask<String, Void, String> {
	private ProgressDialog dialog = null;
	private Context context;
	private AuthenticationHandler handler;
	
	public GetProfileTask(Context context, AuthenticationHandler handler) {
		super();
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(context.getString(R.string.waiting));
		dialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		String address = params[0];
		
		// GET USER PROFILE
		StringBuilder builder1 = new StringBuilder();
    	HttpClient client1 = new DefaultHttpClient();
    	HttpGet httpGet1 = new HttpGet(address);
    	try{
    		HttpResponse response = client1.execute(httpGet1);
    		StatusLine statusLine = response.getStatusLine();
    		int statusCode = statusLine.getStatusCode();
    		if(statusCode == 200){
    			HttpEntity entity = response.getEntity();
    			InputStream content = entity.getContent();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    			String line;
    			while((line = reader.readLine()) != null){
    				builder1.append(line);
    			}
    		} else {
    			Log.e(AuthenticationActivity.class.toString(),"Failed to get JSON object");
    		}
    	}catch(ClientProtocolException e){
    		e.printStackTrace();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    	
    	
		return builder1.toString();
	}
	
	@Override
	protected void onPostExecute(String result) {
		dialog.dismiss();
		handler.onJsonLoaded(result);
	}

}
