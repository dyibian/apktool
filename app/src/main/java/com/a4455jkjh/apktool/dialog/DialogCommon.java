package com.a4455jkjh.apktool.dialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.util.PrintHandler;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import android.widget.ScrollView;
import com.a4455jkjh.apktool.MainActivity;

public abstract class DialogCommon extends Dialog
implements Runnable
{
	private String time;
	protected Object input;
	private static SimpleDateFormat sdf = new SimpleDateFormat("（mm分ss秒）");
	protected MainActivity main;
	protected TextView out;
	protected abstract void start() throws BuildException;
	public DialogCommon(MainActivity c,int theme)
	{
		super(c, theme);
		input = null;
		main=c;
	}
	public DialogCommon(MainActivity c){
		this(c,R.style.AppTheme_Dialog);
	}

	public void setInput(Object input){
		this.input = input;
	}
	protected void onCreate1(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_info);
		out = (TextView) findViewById(R.id.out);
		ScrollView scroll = (ScrollView)findViewById(R.id.scroll);
		PrintHandler.setOut(out,scroll);
		setCancelable(false);
	}

	@Override
	public void show()
	{
		super.show();
		new Thread(this).
			start();
	}

	protected void show1(){
		super.show();
	}
	protected void Done()
	{
		PrintHandler.post1(new Runnable(){
				@Override
				public void run()
				{
					setTitle("完成"+time);
					findViewById(R.id.progress).
						setVisibility(View.GONE);
					setCancelable(true);
				}
			});
	}

	@Override
	public void setTitle (CharSequence title) {
		main.refresh();
		super.setTitle(title);
	}
	

	protected void Err()
	{
		PrintHandler.post1(new Runnable(){
				@Override
				public void run()
				{
					setTitle("失败"+time);
					findViewById(R.id.progress).
						setVisibility(View.GONE);
					setCancelable(true);
				}
			});
	}

	@Override
	public void run()
	{
		long start_time = System.currentTimeMillis();
		try
		{
			start();
			long time = System.currentTimeMillis() - start_time;
			long2time(time);
			Done();
		}
		catch (BuildException e)
		{
			LOGGER.warning(e.getMessage());
			for(StackTraceElement s:e.getStackTrace()){
				LOGGER.warning(s.toString());
			}
			long time = System.currentTimeMillis() - start_time;
			long2time(time);
			Err();
			return;
		}
	}

	private void long2time(long t){
		time=sdf.format(t);
	}
	Logger LOGGER = Logger.getLogger(DialogCommon.class.getName());
}
