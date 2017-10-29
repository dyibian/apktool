package com.a4455jkjh.apktool.dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import brut.util.Log;
import com.a4455jkjh.apktool.ApktoolActivity;
import com.a4455jkjh.apktool.R;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import android.widget.ProgressBar;

public abstract class ProcessDialog<T> extends ApktoolDialog<T>
implements Runnable {
	private ScrollView scrollView;
	private TextView textView;
	private TextColor color;
	private CharSequence title;
	protected boolean notify;

	protected abstract boolean appendInfo ();
	protected abstract void start () throws Exception;
	protected abstract CharSequence getTitle (boolean success);
	public ProcessDialog (Context a, CharSequence t) {
		super(a, t);
		notify = false;
		title = t;
		TypedArray res = a.obtainStyledAttributes(R.styleable.infoColor);
		int colorId = res.getResourceId(R.styleable.infoColor_infoColor, R.style.infoColorLight);
		res.recycle();
		TypedArray colors = a.obtainStyledAttributes(colorId, R.styleable.colors);
		color = new TextColor();
		color.infoForeColor = colors.getColor(R.styleable.colors_infoForeColor, 0);
		color.infoBackColor = colors.getColor(R.styleable.colors_infoBackColor, 0);
		color.warnForeColor = colors.getColor(R.styleable.colors_warnForeColor, 0xff000000);
		color.warnBackColor = colors.getColor(R.styleable.colors_warnBackColor, 0xffffff00);
		color.fatalForeColor = colors.getColor(R.styleable.colors_fatalForeColor, 0xff000000);
		color.fatalBackColor = colors.getColor(R.styleable.colors_fatalBackColor, 0xffff0000);
		color.fineForeColor = colors.getColor(R.styleable.colors_fineForeColor, 0xff000000);
		color.fineBackColor = colors.getColor(R.styleable.colors_fineBackColor, 0xff00ff00);
		colors.recycle();
	}

	@Override
	protected final int getViewId () {
		return R.layout.dialog_info;
	}

	@Override
	protected final void setup () {
		scrollView = findViewById(R.id.scroll);
		textView = findViewById(R.id.out);
		textView.setText("");
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		ProcessHandler handler= new ProcessHandler();
		Log.setCallback(handler);
	}

	@Override
	public void show () {
		super.show();
		new Thread(this).
			start();
	}


	@Override
	public void run () {
		try {
			if (data == null)
				throw new Exception("未设置数据");
			start();
			done();
		} catch (Exception e) {
			fail(e);
		}
	}

	private void done () {
		Log.done();
	}
	private void fail (Exception e) {
		Log.fail(e);
	}
	private void finish (boolean success) {
		setPositiveButton("确定");
		setTitle(getTitle(success));
		ProgressBar bar=findViewById(R.id.progress);
		bar.setVisibility(View.GONE);
		finish();
		ApktoolActivity act = (ApktoolActivity)context;
		act.refresh();
	}
	private void append (CharSequence msg) {
		textView.append(msg);
	}
	protected void finish () {}
	@Override
	public void onDismiss (DialogInterface p1) {
		Log.reset();
	}
	private static final Map<Log.LogLevel,String> hint;
	static{
		hint = new HashMap<Log.LogLevel,String>();
		for (Log.LogLevel level:Log.LogLevel.values()) {
			hint.put(level, level.name().charAt(0) + ":");
		}
	}
	private class ProcessHandler extends Handler
	implements Log.LogCallback {
		private boolean first = true;

		@Override
		public void log (Log.LogLevel level, CharSequence str) {
			if (level.code > 0)
				return;
			SpannableStringBuilder ss = new SpannableStringBuilder(str);
			int backColor = 0;
			int foreColor = 0;
			switch (level) {
				case WARN:
					backColor = color.warnBackColor;
					foreColor = color.warnForeColor;
					break;
				case FATAL:
					backColor = color.fatalBackColor;
					foreColor = color.fatalForeColor;
					break;
				case ERROR:
					backColor = color.fatalBackColor;
					foreColor = color.fatalForeColor;
					break;
				case FINE:
					backColor = color.fineBackColor;
					foreColor = color.fineForeColor;
					break;
				case DEBUG:
					backColor = color.fineBackColor;
					foreColor = color.fineForeColor;
					break;
				case INFO:
					backColor = color.infoBackColor;
					foreColor = color.infoForeColor;
					break;
				default:
					break;
			}
			if (appendInfo())
				ss.insert(0, hint.get(level));
			if (!first)
				ss.insert(0, "\n");
			first = false;
			if (foreColor != 0) {
				ForegroundColorSpan span = new ForegroundColorSpan(foreColor);
				ss.setSpan(span, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (backColor != 0) {
				BackgroundColorSpan span = new BackgroundColorSpan(backColor);
				ss.setSpan(span, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			Message msg = Message.obtain();
			msg.what = level.code;
			msg.obj = ss;
			sendMessage(msg);
		}

		@Override
		public void log (Log.LogLevel level, CharSequence str, Exception e) {
			StringBuilder sb = new StringBuilder();
			if (str != null)
				sb.append(str);
			sb.append('\n').
				append(e.getMessage());
			if (e != null)
				for (StackTraceElement track:e.getStackTrace()) {
					sb.append("\n").
						append(track);
				}
			log(level, sb);
		}

		@Override
		public void logResources (Log.LogLevel level, int res, Object[] args) {
			log(level, getString(res, args));
		}

		@Override
		public void handleMessage (Message msg) {
			switch (msg.what) {
				case -4:
					finish(true);
					break;
				case -5:
					finish(false);
					CharSequence ch = (CharSequence) msg.obj;
					append(ch);
					break;
				default:
					CharSequence ch1 = (CharSequence) msg.obj;
					append(ch1);
					break;
			}
		}

	}
	private static class TextColor {
		public int infoForeColor;
		public int infoBackColor;
		public int warnForeColor;
		public int warnBackColor;
		public int fatalForeColor;
		public int fatalBackColor;
		public int fineForeColor;
		public int fineBackColor;
	}
}
