package com.a4455jkjh.apktool.util;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ScrollView;
import android.widget.TextView;
import brut.util.Log;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import brut.util.Log.LogLevel;
import android.text.SpannableStringBuilder;

public class PrintHandler extends Handler implements Log.LogCallback {
	private TextView out=null;
	private ScrollView scroll = null;
	private Context context;
	private TextColor color;

	public PrintHandler(TextView out, ScrollView scroll, TextColor color, Context context) {
		this.out = out;
		this.scroll = scroll;
		this.context = context;
		this.color = color;
	}

	@Override
	public void log(Log.LogLevel level, CharSequence str, Exception e) {if (level.code > 0)
			return;
		StringBuilder sb;
		if (str != null)
			sb = new StringBuilder(str);
		else
			sb = new StringBuilder();
		sb.append("\n").
			append(e.getMessage()).
			append("\n");
		for (StackTraceElement ste : e.getStackTrace())
			sb.append(ste).
				append("\n");
		log(level, sb);
	}

	@Override
	public void log(Log.LogLevel level, CharSequence str) {
		if (level.code > 0)
			return;
		SpannableStringBuilder ss = new SpannableStringBuilder(str);
		int backColor = 0;
		int foreColor = 0;
		switch (level) {
			case WARN:
				backColor = color.warnBackColor;
				foreColor = color.warnForeColor;
				ss.insert(0, "W:");
				break;
			case FATAL:
				backColor = color.fatalBackColor;
				foreColor = color.fatalForeColor;
				ss.insert(0, "F:");
				break;
			case ERROR:
				backColor = color.fatalBackColor;
				foreColor = color.fatalForeColor;
				ss.insert(0, "E:");
				break;
			case FINE:
				ss.insert(0, "F:");
				backColor = color.fineBackColor;
				foreColor = color.fineForeColor;
				break;
			case DEBUG:
				ss.insert(0, "D:");
				backColor = color.fineBackColor;
				foreColor = color.fineForeColor;
				break;
			case INFO:
				ss.insert(0, "I:");
				backColor = color.infoBackColor;
				foreColor = color.infoForeColor;
				break;
			default:
				break;
		}
		if (foreColor != 0) {
			ForegroundColorSpan span = new ForegroundColorSpan(foreColor);
			ss.setSpan(span, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (backColor != 0) {
			BackgroundColorSpan span = new BackgroundColorSpan(backColor);
			ss.setSpan(span, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		Message msg = Message.obtain();
		msg.obj = ss;
		sendMessage(msg);
	}

	@Override
	public void logResources(Log.LogLevel level, int res, Object... args) {
		if (level.code > 0)
			return;
		CharSequence cs = context.getString(res, args);
		log(level, cs);
	}

	@Override
	public void handleMessage(Message msg) {
		if (out == null)
			return;
		CharSequence c = (CharSequence)msg.obj;
		out.append(c);
		out.append("\n");
		scroll.scrollTo(0, out.getHeight());
	}
	public static class TextColor {
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
