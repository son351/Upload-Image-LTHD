package me.khtn.fragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import me.khtn.adapter.ImageItemAdapter;
import me.khtn.handler.UploadHandler;
import me.khtn.task.UploadImageTask;
import me.khtn.uploadimagekhtn.R;
import me.khtn.utils.MediaStoreUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UploadFragment extends BaseFragment implements ImageItemAdapter.DeleteListener, UploadHandler {
	private GridView gridView;
	private ImageItemAdapter adapter;
	private static final int PADDING = 25;
	private static final String POSITION = "position";
	private static final int CAPTURE_IMAGE = 2;
	private int mPosition = 0;
	private SharedPreferences preferences;
	private Editor editor;
	private Uri mCapturedImageURI;
	private static final int PIC_CROP = 1;
	private ArrayList<Uri> lstUri = new ArrayList<Uri>();
	private static ArrayList<String> fullPaths = new ArrayList<String>();
	private Context context;
	private String token;
	
	public UploadFragment() {		
	}
	
	public static UploadFragment newInstance(Context context, String token) {
		UploadFragment fm = new UploadFragment();
		fm.context = context;
		fm.token = token;
		return fm;
	}

	@Override
	public void updateView() {		
	}

	@Override
	public void loadData() {
		setHasOptionsMenu(true);
		initSharePreferences();
		initView();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.upload, menu);	
		MenuItem menuItem = menu.findItem(R.id.upload_menu);
		menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (fullPaths.size() > 0)					
					upload();
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(getString(R.string.choose_pic));
					builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.show();
				}
				return true;
			}
		});
	}
	
	private void upload() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(R.string.upload_check_sure));
		builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String address = "http://apiservice.uhurucloud.com/api/uploading?Token=" + token;
				UploadImageTask task = new UploadImageTask(context, fullPaths, UploadFragment.this);
				task.execute(address);
			}
		});		
		builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	private void initSharePreferences() {
		preferences = context.getSharedPreferences(POSITION, Context.MODE_PRIVATE);
		editor=  preferences.edit();
		editor.clear();
		editor.commit();
	}
	
	private void initView() {
		
		Point point = new Point();
		getActivity().getWindow().getWindowManager().getDefaultDisplay().getSize(point);
		adapter = new ImageItemAdapter(context, point.x/2 - PADDING);
		adapter.setDeleteListener(this);		
		adapter.addDefault();
		gridView.setAdapter(adapter);

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle(R.string.select)
	    	   .setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
	    	   })
	           .setItems(R.array.select_picture, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   editor.putInt(POSITION, mPosition);
		   				editor.commit();
	               if(which == 0){		   				
	            	   startActivityForResult(MediaStoreUtils.getPickImageIntent(context), CAPTURE_IMAGE);
	               }else{
	            	   String fileName = "temp" + System.currentTimeMillis() + ".jpg";  
	                   ContentValues values = new ContentValues();  
	                   values.put(MediaStore.Images.Media.TITLE, fileName);  
	                   mCapturedImageURI = context.getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  

	                   Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);  
	                   intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);  
	                   intent.putExtra("return-data", true);
	                   startActivityForResult(intent, CAPTURE_IMAGE);
	               }
	           }
	    });
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				if (adapter.getCount() - 1 == position && adapter.getCount() - 1 < 4) {
					builder.show();
					mPosition = position;
				}				
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {					
				if (data != null) {
					crop(data.getData());					
				} else {
					crop(mCapturedImageURI);
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
			} else {
			}
		} else if (requestCode == PIC_CROP && data!=null) {
			Bundle extras = data.getExtras();
			Bitmap thePic = extras.getParcelable("data");
			Uri cropUri = getImageUri(context, thePic);
			String cropFullPath = MediaStoreUtils.getLocalMediaPath(context, cropUri);	
//			lstUri.add(cropUri);
			fullPaths.add(cropFullPath);
			thePic.setHasAlpha(false);
			mPosition = preferences.getInt(POSITION, 0);
			adapter.setData(thePic, mPosition);
			if(mPosition < 3){
				adapter.addDefault();
			}			
		}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		  String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "image.jpg", null);
		  return Uri.parse(path);
	}
	
	private void crop(Uri imageUri) {
		try {			
			Intent intent = new Intent(context, com.android.camera.CropImage.class);
		    intent.setData(imageUri);
		    intent.putExtra("return-data", true);
		    startActivityForResult(intent, PIC_CROP);

		} catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(context, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public void loadControls(View rootView) {
		gridView = (GridView) rootView.findViewById(R.id.gridView);		
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_main;
	}
	
	public void onDeleteItemAtPosition(int position) {
		lstUri.remove(position);		
		fullPaths.remove(position);
	}

	@Override
	public void ondeleteReturn(int listSize) {
	}

	@Override
	public void onUploaded(final int statusCode) {
		if (statusCode == 200) {
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(context.getString(R.string.finished_upload));
					builder.setPositiveButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.show();
				}
			});			
		} else {
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(context.getString(R.string.error_upload) + " " + statusCode);
					builder.setPositiveButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.show();
				}
			});			
		}
	}	

}
