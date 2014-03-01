package me.khtn.adapter;

import java.util.ArrayList;
import java.util.List;

import me.khtn.uploadimagekhtn.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

public class ImageItemAdapter extends BaseAdapter {
	List<Bitmap> lists = new ArrayList<Bitmap>();
	Context context;
	int positionSelected = -1;
	private LayoutParams params;
	DeleteListener deleteListener;

	public List<Bitmap> getLists() {
		return lists;
	}

	public void setLists(List<Bitmap> lists) {
		this.lists = lists;
	}

	public void setDeleteListener(DeleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}

	public int getPositionSelected() {
		return positionSelected;
	}

	public void setPositionSelected(int positionSelected) {
		this.positionSelected = positionSelected;
		notifyDataSetChanged();
	}

	public ImageItemAdapter(Context context, int width) {
		this.context = context;
		params = new LayoutParams(width, width);

	}

	public void addDefault() {
		View rootView = (View) LayoutInflater.from(context).inflate(R.layout.image_item, null);
		rootView.setDrawingCacheEnabled(true);
		rootView.layout(0, 0, 100, 100);
		rootView.buildDrawingCache();
		Bitmap defaultBitmap = Bitmap.createBitmap(rootView.getDrawingCache());		
		defaultBitmap.setHasAlpha(true);
		this.lists.add(defaultBitmap);
		notifyDataSetChanged();
		rootView.setDrawingCacheEnabled(false);
	}
	
	public void setData(Bitmap bm, int position) {
		this.lists.set(position, bm);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.image_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
			viewHolder.camera = (ImageView) convertView.findViewById(R.id.camera);
			viewHolder.layout.setLayoutParams(params);
			viewHolder.del = (ImageView) convertView.findViewById(R.id.detete);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Bitmap pic = lists.get(position);
		if(pic.hasAlpha()){
			viewHolder.del.setVisibility(View.GONE);
			viewHolder.camera.setVisibility(View.VISIBLE);
		}else{
			viewHolder.camera.setVisibility(View.GONE);
			viewHolder.del.setVisibility(View.VISIBLE);
		}		
		
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), pic);

		if (Build.VERSION.SDK_INT >= 16) 
			viewHolder.layout.setBackground(bd);
		else 
			viewHolder.layout.setBackgroundDrawable(bd);
		
		viewHolder.del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(lists.size()==4){
					lists.remove(position);
//					ac.onDeleteItemAtPosition(position);
					if(!lists.get(2).hasAlpha()){
						addDefault();
					}
				}else{
					lists.remove(position);
//					ac.onDeleteItemAtPosition(position);
				}
				notifyDataSetChanged();
				if(deleteListener!=null){
					deleteListener.ondeleteReturn(lists.size());
				}
			}
		});
		
		return convertView;
	}
	
	private class ViewHolder {
		private RelativeLayout layout;
		private ImageView del, camera;
	}

	public interface DeleteListener{
		public void ondeleteReturn(int listSize);
	}
}
