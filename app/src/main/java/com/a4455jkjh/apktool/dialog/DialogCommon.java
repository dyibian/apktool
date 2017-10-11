package com.a4455jkjh.apktool.dialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import brut.util.Log;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.util.PrintHandler;
import java.text.SimpleDateFormat;
import android.content.res.TypedArray;

public abstract class DialogCommon extends Dialog
implements Runnable,OnClickListener {
	private String time;
	protected Object input;
	private static SimpleDateFormat sdf = new SimpleDateFormat("（mm分ss秒）");
	protected MainActivity main;
	protected TextView out;
	private PrintHandler handler;
	private PrintHandler.TextColor color;
	
	protected abstract void start() throws BuildException;
	public DialogCommon(MainActivity main, int theme) {
		super(main, theme);
		input = null;
		this.main = main;
		TypedArray i = main.obtainStyledAttributes(theme,R.styleable.infoColor);
		int resId = i.getResourceId(R.styleable.infoColor_infoColor,R.style.infoColorLight);
		i.recycle();
		TypedArray c = main.obtainStyledAttributes(resId, R.styleable.colors);
		color = new PrintHandler.TextColor();
		color.infoForeColor = c.getColor(R.styleable.colors_infoForeColor, 0);
		color.infoBackColor = c.getColor(R.styleable.colors_infoBackColor, 0);
		color.warnForeColor = c.getColor(R.styleable.colors_warnForeColor, 0xff000000);
		color.warnBackColor = c.getColor(R.styleable.colors_warnBackColor, 0xffffff00);
		color.fatalForeColor = c.getColor(R.styleable.colors_fatalForeColor, 0xff000000);
		color.fatalBackColor = c.getColor(R.styleable.colors_fatalBackColor, 0xffff0000);
		color.fineForeColor = c.getColor(R.styleable.colors_fineForeColor, 0xff000000);
		color.fineBackColor = c.getColor(R.styleable.colors_fineBackColor, 0xff00ff00);
		c.recycle();
	}
	public DialogCommon(MainActivity c) {
		this(c, R.style.AppTheme_Dialog);
	}

	public void setInput(Object input) {
		this.input = input;
	}
	protected void onCreate1(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_info);
		out = (TextView) findViewById(R.id.out);
		ScrollView scroll = (ScrollView)findViewById(R.id.scroll);
		handler = new PrintHandler(out, scroll, color, getContext());
		setCancelable(false);
		findViewById(R.id.close).
			setOnClickListener(this);
		findViewById(R.id.select).
			setOnClickListener(this);
	}

	@Override
	public void show() {
		super.show();
		new Thread(this).
			start();
	}

	protected void show1() {
		super.show();
	}
	protected void Done() {
		handler.post(new Runnable(){
				@Override
				public void run() {
					setTitle("完成" + time);
					findViewById(R.id.progress).
						setVisibility(View.GONE);
					findViewById(R.id.btn).
						setVisibility(View.VISIBLE);
				}
			});
	}

	@Override
	public void setTitle(CharSequence title) {
		main.refresh();
		super.setTitle(title);
	}


	protected void Err() {
		handler.post(new Runnable(){
				@Override
				public void run() {
					setTitle("失败" + time);
					findViewById(R.id.progress).
						setVisibility(View.GONE);
					findViewById(R.id.close).
						setVisibility(View.VISIBLE);
				}
			});
	}

	@Override
	public void run() {
		Log.setCallback(handler);
		long start_time = System.currentTimeMillis();
		try {
			start();
			long time = System.currentTimeMillis() - start_time;
			long2time(time);
			Done();
		} catch (BuildException e) {
			Log.log(Log.LogLevel.WARN, null, e);
			long time = System.currentTimeMillis() - start_time;
			long2time(time);
			Err();
			return;
		}
		Log.reset();
	}

	@Override
	public void onClick(View p1) {
		switch (p1.getId()) {
			case R.id.close:
				dismiss();
				break;
			case R.id.select:
				select();
				dismiss();
				break;
		}
	}


	protected void select() {}
	private void long2time(long t) {
		time = sdf.format(t);
	}
	
}
