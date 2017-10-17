package com.a4455jkjh.apktool.utils;
import android.content.pm.ApplicationInfo;
import android.widget.ImageView;
import android.widget.TextView;
import com.a4455jkjh.apktool.MainActivity;
import java.util.List;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.Spinner;

public class AppAdapter extends ApktoolAdapter<ApplicationInfo>
implements AdapterView.OnItemSelectedListener {
	private AppUtils appUtils;
	private int location;
	public AppAdapter (MainActivity main) {
		super(main);
		appUtils = new AppUtils(main);
		location = 0;
	}
	@Override
	public int compare (ApplicationInfo p1, ApplicationInfo p2) {
		return p1.sourceDir.compareTo(p2.sourceDir);
	}

	@Override
	public boolean goBack () {
		return false;
	}

	@Override
	public void init (TextView path, Spinner location) {
		path.setVisibility(View.GONE);
		location.setVisibility(View.VISIBLE);
		location.setSelection(0);
		location.setOnItemSelectedListener(this);
	}


	@Override
	protected void refresh1 () {
		String start;
		if (location == 0)
			start = "/data/";
		else
			start = "/system/";
		List<ApplicationInfo> apps = pm.getInstalledApplications(0);
		for (ApplicationInfo app:apps)
			if (app.sourceDir.startsWith(start))
				list.add(app);
	}

	@Override
	protected void setup (ApplicationInfo app, TextView title, ImageView icon) {
		title.setText(app.loadLabel(pm));
		icon.setImageDrawable(app.loadIcon(pm));
	}

	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
		if(location==1)
			return;
		ApplicationInfo app = list.get(position);
		appUtils.process(app);
	}

	@Override
	public void onItemSelected (AdapterView<?> p1, View p2, int p3, long p4) {
		location = p3;
		refresh();
	}

	@Override
	public void onNothingSelected (AdapterView<?> p1) {
		// Empty
	}

}
