package com.a4455jkjh.apktool.dialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.a4455jkjh.apktool.R;
import com.a4455jkjh.apktool.util.PrintHandler;
import java.text.SimpleDateFormat;

public abstract class DialogCommon extends Dialog
implements Runnable
{
	private String time;
	protected Object input;
	private static SimpleDateFormat sdf = new SimpleDateFormat("（mm分ss秒）");
	protected abstract void start() throws BuildException;
	public DialogCommon(Context c)
	{
		super(c, R.style.AppTheme_Dialog);
		input = null;
		
	}

	public void setInput(Object input){
		this.input = input;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_info);
		TextView out = (TextView) findViewById(R.id.out);
		PrintHandler.setOut(out);
	}

	@Override
	public void show()
	{
		super.show();
		new Thread(this).
			start();
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
				}
			});
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
			e.printStackTrace();
			long time = System.currentTimeMillis() - start_time;
			long2time(time);
			Err();
			return;
		}
	}

	private void long2time(long t){
		time=sdf.format(t);
	}
}
