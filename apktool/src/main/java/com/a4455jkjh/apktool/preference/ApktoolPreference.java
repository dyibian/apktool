package com.a4455jkjh.apktool.preference;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Adapter;

public abstract class ApktoolPreference extends Preference
implements DialogInterface.OnClickListener,AdapterView.OnItemSelectedListener {
	protected final SharedPreferences sp;
	public ApktoolPreference (Context c, AttributeSet a) {
		super(c, a);
		sp = PreferenceManager.
			getDefaultSharedPreferences(c);
	}
	private View DialogView;
	protected abstract int getViewId ();
	protected abstract void setup ();

	protected void setButton (AlertDialog.Builder builder) {
		builder.setPositiveButton("确定", this);
	}

	protected <T extends View> T findViewById (int resId) {
		return (T)DialogView.findViewById(resId);
	}

	@Override
	protected void onClick () {
		DialogView = LayoutInflater.from(getContext()).
			inflate(getViewId(), null);
		setup();
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		setButton(builder);
		builder.setView(DialogView).
			setTitle(getTitle()).
			create().show();
	}
	@Override
	public final void onClick (DialogInterface p1, int p2) {
		switch(p2){
			case DialogInterface.BUTTON_POSITIVE:
				onPotisitiveButtonClicked();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				onNegativeButtonClicked();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				onNeutralButtonClicked();
				break;
			default:
				break;
		}
	}
	protected void onPotisitiveButtonClicked(){}
	protected void onNegativeButtonClicked(){}
	protected void onNeutralButtonClicked () {}

	@Override
	public SharedPreferences getSharedPreferences () {
		return sp;
	}

	@Override
	public void onNothingSelected (AdapterView<?> p1) {
		//Empty
	}

	protected final void error(CharSequence msg) {
		new AlertDialog.Builder(getContext()).
			setTitle("发生错误！").
			setMessage(msg).
			setPositiveButton("确定", null).
			setCancelable(false).
			create().show();
	}
}
