package me.khtn.adapter;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoAdapter extends BaseAdapter {
	private Context context;
	private List<String> listUrl;

	public PhotoAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return listUrl.size();
	}

	@Override
	public Object getItem(int position) {
		return listUrl.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
        UrlImageViewHelper.setUrlDrawable(imageView, listUrl.get(position));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
        return imageView;
	}

	public List<String> getDataSource() {
		return listUrl;
	}

	public void setDataSource(List<String> listUrl) {
		this.listUrl = listUrl;
	}

}
