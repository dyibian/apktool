package com.a4455jkjh.apktool.dialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import com.a4455jkjh.apktool.ApktoolActivity;
import android.content.Context;
import android.view.LayoutInflater;

public abstract class ApktoolDialog<T>
implements DialogInterface.OnClickListener,DialogInterface.OnDismissListener{
	private final View view;
	private final AlertDialog dialog;
	protected final Context context;
	private Button positive,negative,neutral;
	T data;

	public ApktoolDialog (Context context, CharSequence title) {
		this.context=context;
		view = LayoutInflater.from(context).
			inflate(getViewId(), null);
		dialog = new AlertDialog.Builder(context).
			setTitle(title).
			setOnDismissListener(this).
			setPositiveButton(" ",this).
			setNegativeButton(" ",this).
			setNeutralButton(" ",this).
			setCancelable(false).
			setView(view).create();
		setup();
	}
	protected final void setTitle(CharSequence title){
		dialog.setTitle(title);
	}
	protected final String getString(int id,Object... args){
		return context.getString(id,args);
	}
	protected <V extends View> V findViewById(int id){
		return (V)view.findViewById(id);
	}
	public final void setData(T data){
		this.data = data;
	}
	protected final void setPositiveButton(CharSequence text){
		showButton(positive,text);
	}
	private void showButton(Button btn,CharSequence text){
		btn.setText(text);
		btn.setVisibility(View.VISIBLE);
	}
	protected final void setNegativeButton(CharSequence text){
		showButton(negative,text);
	}
	protected final void setNeutralButton(CharSequence text){
		showButton(neutral,text);
	}
	protected abstract void setup();
	protected abstract int getViewId ();
	public void show(){
		dialog.show();
		positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		neutral=dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		positive.setVisibility(View.GONE);
		neutral.setVisibility(View.GONE);
		negative.setVisibility(View.GONE);
	}
	protected final void dismiss(){
		dialog.dismiss();
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
	@Override
	public void onDismiss (DialogInterface p1) {
		// Empty
	}
	
	protected void onPotisitiveButtonClicked(){}
	protected void onNegativeButtonClicked(){}
	protected void onNeutralButtonClicked(){}
}
