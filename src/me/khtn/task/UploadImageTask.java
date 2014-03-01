package me.khtn.task;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import me.khtn.handler.UploadHandler;
import me.khtn.uploadimagekhtn.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UploadImageTask extends AsyncTask<String, Void, Void> {
	private ProgressDialog dialog = null;
	private Context context;
	private ArrayList<String> paths = new ArrayList<String>();
	private UploadHandler handler;

	public UploadImageTask(Context context, ArrayList<String> paths, UploadHandler handler) {
		super();
		this.context = context;
		this.paths = paths;
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(context.getString(R.string.waiting));
		dialog.show();
	}

	@Override
	protected Void doInBackground(String... params) {
		String address = params[0];
		for (String i : paths) {
			upload(i, address);
		}
		return null;
	}

	private void upload(String sourceFileUri, String address) {
		String fileName = sourceFileUri;
		int serverResponseCode;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);
		try {

			// open a URL connection to the Servlet
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(address);

			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", fileName);

			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=" + "asd.png" + ";filename=\""
                    + fileName + "\"" + lineEnd);

			dos.writeBytes(lineEnd);

			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();

			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();

			Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
			Log.i("uploadFile", "ADDRESS is : " + address);
			
			// this block will give the response of upload link
	        try {
	            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = rd.readLine()) != null)
	            	Log.i("uploadFile", "RESULT Message: " + line);
	            rd.close();
	        } catch (IOException ioex) {
	            Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
	        }
			
			// close the streams //
			fileInputStream.close();
			dos.flush();
			dos.close();
			
			dialog.dismiss();
			handler.onUploaded(serverResponseCode);

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		dialog.dismiss();
	}

}
