package com.a4455jkjh.apktool.util;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import android.widget.ScrollView;

public class PrintHandler extends Handler implements Runnable
{
	private static TextView out=null;
	private static ScrollView scroll = null;
	private static PrintHandler handler;
	private static final int OUT = 1;
	public static void init()
	{
		if (handler == null)
			handler = new PrintHandler();
		setupLogging(Verbosity.NORMAL);
	}
	public static void setOut(TextView out,ScrollView scroll)
	{
		PrintHandler.out = out;
		PrintHandler.scroll = scroll;
	}
	public static void reset()
	{
		out = null;
	}

	@Override
	public void run()
	{
		scroll.scrollTo(0,out.getHeight());
	}

	public static void post1(Runnable run)
	{
		handler.post(run);
	}
	@Override
	public void handleMessage(Message msg)
	{
		if (out == null)
			return;
		CharSequence c = (CharSequence)msg.obj;
		out.append(c);
		post(this);
	}
	private static void setupLogging(final Verbosity verbosity)
	{
        Logger logger = Logger.getLogger("");
        for (java.util.logging.Handler handler : logger.getHandlers())
		{
            logger.removeHandler(handler);
        }
        LogManager.getLogManager().reset();

        if (verbosity == Verbosity.QUIET)
		{
            return;
        }

        java.util.logging.Handler handler = new java.util.logging.Handler(){
            @Override
            public void publish(LogRecord record)
			{
                if (getFormatter() == null)
				{
                    setFormatter(new SimpleFormatter());
                }

				String message = getFormatter().format(record);
				Message msg = Message.obtain();
				msg.obj = message;
				PrintHandler.handler.sendMessage(msg);
            }
            @Override
            public void close() throws SecurityException
			{}
            @Override
            public void flush()
			{}
        };

        logger.addHandler(handler);

        if (verbosity == Verbosity.VERBOSE)
		{
            handler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
        }
		else
		{
            handler.setFormatter(new Formatter() {
					@Override
					public String format(LogRecord record)
					{
						return record.getLevel().toString().charAt(0) + ": "
                            + record.getMessage()
                            + System.getProperty("line.separator");
					}
				});
        }

    }
	enum Verbosity
	{
        NORMAL, VERBOSE, QUIET
	}
}
