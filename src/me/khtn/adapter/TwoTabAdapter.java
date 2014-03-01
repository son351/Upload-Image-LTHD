package me.khtn.adapter;

import me.khtn.fragment.PhotoFragment;
import me.khtn.fragment.UploadFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class TwoTabAdapter  extends BaseTabPagerAdapter {
	private Context context;
	private String token;

	public TwoTabAdapter(FragmentManager fm, Context context, String token) {
		super(fm);
		this.context = context;
		this.token = token;
	}

	@Override
	public Fragment getItem(int arg0) {
		switch (arg0) {
		case 0:
			PhotoFragment fm1 = PhotoFragment.newInstance(context);
			return fm1;
		case 1:
			UploadFragment fm2 = UploadFragment.newInstance(context, token);
			return fm2;
		default:
			return null;
		}		
	}

	@Override
	public int getCount() {
		return 2;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:	return "Bộ sưu tập";
		case 1:	return "Đăng ảnh";
		default: return "";
		}
	}

}
