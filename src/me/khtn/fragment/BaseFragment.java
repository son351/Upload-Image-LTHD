package me.khtn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		
		View rootView = inflater.inflate(getLayoutResource(), container, false);

		loadControls(rootView);
		
		loadData();
		
		updateView();
		
		return rootView;
	}

    public abstract void updateView();

	public abstract void loadData();

	public abstract void loadControls(View rootView);
	
	public abstract int getLayoutResource();
}
