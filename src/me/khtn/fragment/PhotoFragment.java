package me.khtn.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import me.khtn.adapter.PhotoAdapter;
import me.khtn.app.FullImageActivity;
import me.khtn.handler.PhotoHandler;
import me.khtn.task.GetPhotoTask;
import me.khtn.uploadimagekhtn.R;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class PhotoFragment extends BaseFragment implements PhotoHandler, OnScrollListener {
	private Context context;
	private GridView gridView;
	private PhotoAdapter adapter;
	private boolean loaded = false;
	private boolean ended = false;
	private boolean loading = false;
	private boolean flag = false;
	private int currentPage;
	
	public PhotoFragment(){ }
	
	public static PhotoFragment newInstance(Context context) {
		PhotoFragment fm = new PhotoFragment();
		fm.context = context;
		return fm;
	}

	@Override
	public void updateView() {
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.photo, menu);
		MenuItem item = menu.findItem(R.id.refresh_photo);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				adapter.setDataSource(null);
				doLoad();
				return true;
			}
		});
	}
	
	public void doLoad() {
		String address = "http://apiservice.uhurucloud.com/api/explore?Time=" + 1;
		GetPhotoTask task = new GetPhotoTask(context, this, 1);
		task.execute(address);
	}

	@Override
	public void loadData() {
		doLoad();
	}

	@Override
	public void loadControls(View rootView) {		
		gridView = (GridView) rootView.findViewById(R.id.grid_view);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos, long arg3) {
				Intent intent = new Intent(context, FullImageActivity.class);
				intent.putExtra("URL", (String) adapter.getItem(pos));
				startActivity(intent);
			}
		});
		gridView.setOnScrollListener(this);		
		adapter = new PhotoAdapter(context);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.photo_layout;
	}

	@Override
	public void onPhotoLoaded(String result, int page) {
		loaded = true;
		loading = false;
		currentPage = page;
		List<String> urls = new ArrayList<String>();
		try {
			JSONArray arr = new JSONArray(result);
			if (arr.length() > 0) {
				for (int i = 0; i < arr.length(); i++) {
					JSONObject ob = arr.getJSONObject(i);
					if (ob.has("FileName"))
						urls.add(ob.getString("FileName"));
				}
			} else {
				ended = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!ended) {	
				if (page == 1)
					adapter.setDataSource(urls);
				else {
					List<String> data = adapter.getDataSource();
					data.addAll(urls);
				}
				gridView.setAdapter(adapter);
			}			
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (loaded && !loading) {
			int lastItem = firstVisibleItem + visibleItemCount;
			if (lastItem == totalItemCount) {
				if (!ended) {
					currentPage++;
					String address = "http://apiservice.uhurucloud.com/api/explore?Time=" + currentPage;
					GetPhotoTask task = new GetPhotoTask(context, this, currentPage);
					task.execute(address);
				} else {
					if (!flag) {
						Toast.makeText(context, R.string.no_more, Toast.LENGTH_SHORT).show();
						flag = true;
					}
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

}
