package me.khtn.app;

import me.khtn.handler.AuthenticationHandler;
import me.khtn.task.GetPictureUrlTask;
import me.khtn.task.GetProfileTask;
import me.khtn.uploadimagekhtn.R;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class AuthenticationActivity extends Activity implements AuthenticationHandler {
	private TextView txtName;
	private View loginView, uploadView;
	private TextView txtLogin;
	private String id, name;
	private ImageView profileImage;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		getActionBar().setTitle(getString(R.string.actionbar));
		
		txtName = (TextView) findViewById(R.id.txt_name);
		loginView = (View) findViewById(R.id.login);
		uploadView = (View) findViewById(R.id.upload);
		txtLogin = (TextView) findViewById(R.id.txt_login);
		profileImage = (ImageView) findViewById(R.id.profile_image);		
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		if (session == null) {
			txtName.setText(getString(R.string.register));
			if (savedInstanceState != null)
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			if (session == null)
				session = new Session(this);
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED))
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		}
		
		updateView();
	}
	
	public void showAlertDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	public void updateView() {
		Session session = Session.getActiveSession();
        if (session.isOpened()) {      
        	if (!isConnectingToInternet()) {
//    			showAlertDialog(getString(R.string.conect_to_internet));
    			onClickLogout();
				txtName.setText(getString(R.string.register));
				profileImage.setImageResource(R.drawable.ic_action_picture);
        	} else {
	        	getProfile(session.getAccessToken());
	        	txtLogin.setText(getString(R.string.logout));
	        	loginView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
						builder.setMessage(getString(R.string.check_sure));
						builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								onClickLogout();
								txtName.setText(getString(R.string.register));
								profileImage.setImageResource(R.drawable.ic_action_picture);
							}
						});					
						builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						builder.show();
					}
				});
	        	uploadView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(AuthenticationActivity.this, UploadActivity.class);
						intent.putExtra("TOKEN", Session.getActiveSession().getAccessToken());
						startActivity(intent);
					}
				});
        	}
        } else {
        	if (!isConnectingToInternet())
    			showAlertDialog(getString(R.string.conect_to_internet));
        	txtLogin.setText(getString(R.string.login));
        	loginView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (!isConnectingToInternet())
                		showAlertDialog(getString(R.string.conect_to_internet));
                	else
                		onClickLogin(); 
				}
			});
        	uploadView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showAlertDialog(getString(R.string.register));
				}
			});
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.authentication, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }
	
	@Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
    
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    public void getProfile(String token) {
    	String address = "https://graph.facebook.com/me/?access_token=" + token;
    	GetProfileTask task = new GetProfileTask(this, this);
    	task.execute(address);    	    	
    }

	@Override
	public void onJsonLoaded(String result) {
		try{
        	JSONObject jsonObject = new JSONObject(result);
        	if (jsonObject.has("name"))
        		name = jsonObject.getString("name");
        	if (jsonObject.has("id"))
        		id = jsonObject.getString("id");        	
        } catch(Exception e){e.printStackTrace();}
    	finally{ 
    		txtName.setText(name);    
    		
    		String picApi = "http://graph.facebook.com/" + id + "/?fields=picture";
        	GetPictureUrlTask picTask = new GetPictureUrlTask(this);
        	picTask.execute(picApi);
    	};
	}

	@Override
	public void onPicJsonLoaded(String jsonPic) {
		String picUrl = "";
		try{
			JSONObject ob = new JSONObject(jsonPic);
	    	if (ob.has("picture")) {
	    		JSONObject ob1 = ob.getJSONObject("picture");
	    		if (ob1.has("data")) {
					JSONObject ob2 = ob1.getJSONObject("data");
					if (ob2.has("url"))
						picUrl = ob2.getString("url");
				}
	    	}      	
        } catch(Exception e){e.printStackTrace();}
    	finally{ 
    		UrlImageViewHelper.setUrlDrawable(profileImage, picUrl);    		
    	};		    	
	}

}
