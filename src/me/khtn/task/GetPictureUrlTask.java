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
import android.os.AsyncTask;
import android.util.Log;

public class GetPictureUrlTask extends AsyncTask<String, Void, String> {
	private AuthenticationHandler handler;

	public GetPictureUrlTask(AuthenticationHandler handler) {
		super();
		this.handler = handler;
	}

	@Override
	protected String doInBackground(String... params) {
		String address = params[0];
		// GET PICTURE URL
    	StringBuilder builder2 = new StringBuilder();
    	HttpClient client2 = new DefaultHttpClient();
    	HttpGet httpGet2 = new HttpGet(address);
    	try{
    		HttpResponse response = client2.execute(httpGet2);
    		StatusLine statusLine = response.getStatusLine();
    		int statusCode = statusLine.getStatusCode();
    		if(statusCode == 200){
    			HttpEntity entity = response.getEntity();
    			InputStream content = entity.getContent();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    			String line;
    			while((line = reader.readLine()) != null){
    				builder2.append(line);
    			}
    		} else {
    			Log.e(AuthenticationActivity.class.toString(),"Failed to get JSON object");
    		}
    	}catch(ClientProtocolException e){
    		e.printStackTrace();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
		return builder2.toString();
	}
	
	@Override
	protected void onPostExecute(String result) {
		handler.onPicJsonLoaded(result);
	}

}
