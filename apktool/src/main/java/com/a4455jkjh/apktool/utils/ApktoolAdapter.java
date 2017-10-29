package com.a4455jkjh.apktool.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.pm.PackageManager;
import android.widget.Spinner;

public abstract class ApktoolAdapter<T> extends BaseAdapter
implements AdapterView.OnItemClickListener,Comparator<T> {
	protected MainActivity main;
	protected final List<T> list;
	protected PackageManager pm;
	public ApktoolAdapter (MainActivity main) {
		this.main = main;
		pm = main.getPackageManager();
		list = new ArrayList<T>();
		refresh();
	}
	public abstract boolean goBack ();
	protected abstract void refresh1 ();
	public abstract void init (TextView path, Spinner location);
	protected abstract void setup (T obj, TextView title, ImageView icon);
	public void refresh () {
		list.clear();
		refresh1();
		Collections.sort(list, this);
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount () {
		return list.size();
	}

	@Override
	public T getItem (int p1) {
		return list.get(p1);
	}

	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
		//Empty
	}

	@Override
	public long getItemId (int p1) {
		return p1;
	}

	@Override
	public View getView (int p1, View p2, ViewGroup p3) {
		if (p2 == null)
			p2 = main.getLayoutInflater().
				inflate(R.layout.file_entry, p3, false);
		TextView title = (TextView) p2.findViewById(R.id.filename);
		ImageView icon = (ImageView) p2.findViewById(R.id.icon);
		setup(getItem(p1), title, icon);
		return p2;
	}
	public void reset () {}
}
