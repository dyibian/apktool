package com.mycompany.apktool;
import android.os.*;
import android.view.*;
import android.text.*;

public class handler extends Handler
{
	MainActivity m;
	public handler(MainActivity m)
	{
		this.m = m;
	}

	@Override
	public void handleMessage(Message msg)
	{
		// TODO: Implement this method
		super.handleMessage(msg);
		switch (msg.what)
		{
			case 100:
				m.b.setVisibility(View.VISIBLE);
				break;
			case 0:
				String str=(String)msg.obj;
				m.setTitle(str);
				m.tv.append("\n" + str);
				break;
			case 10:
				Spanned s=(Spanned)msg.obj;
				m.tv.append("\n");
				m.tv.append(s);
				break;
		}
		m.sv.scrollTo(0, m.tv.getHeight());
	}

}
