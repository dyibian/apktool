package brut.util;
import brut.util.Log.LogLevel;

public class Log
{

	public static void log(LogLevel level, String format, Exception ex) {
		// TODO: Implement this method
	}

	public static void severe(String str) {
		fatal(str);
	}

	public static void setCallback(LogCallback callback) {
		Log.callback = callback;
	}

	public static LogCallback getCallback() {
		return callback;
	}
	public static void reset(){
		setCallback(new DefaultCallback());
	}
	public static void info(String str){
		callback.log(LogLevel.INFO,str);
	}
	public static void warning(String str){
		callback.log(LogLevel.WARN,str);
	}
	public static void error(String str){
		callback.log(LogLevel.ERROR,str);
	}
	public static void fatal(String str){
		callback.log(LogLevel.FATAL,str);
	}
	public static void fine(String str){
		callback.log(LogLevel.FINE,str);
	}
	public static void infoResources(int res,Object... args){
		callback.logResources(LogLevel.INFO,res,args);
	}
	public static void warnResources(int res,Object... args){
		callback.logResources(LogLevel.WARN,res,args);
	}
	public static void errorResources(int res,Object... args){
		callback.logResources(LogLevel.ERROR,res,args);
	}
	public static void fatalResources(int res,Object... args){
		callback.logResources(LogLevel.FATAL,res,args);
	}
	public static void fineResources(int res,Object... args){
		callback.logResources(LogLevel.FINE,res,args);
	}
	public interface LogCallback{
		void log(LogLevel level,CharSequence str);
		void log(LogLevel level,CharSequence str,Exception e);
		void logResources(LogLevel level,int res,Object... args);
	}
	public enum LogLevel{
		INFO(0),
		WARN(-1),
		ERROR(-2),
		FATAL(-3),
		FINE(1),
		DEBUG(2),
		VERBOSE(3);
		
		public final int code;
		LogLevel(int code){
			this.code=code;
		}
	}
	private static class DefaultCallback implements LogCallback {

		@Override
		public void log(Log.LogLevel level, CharSequence str) {
			// TODO: Implement this method
		}

		@Override
		public void logResources(Log.LogLevel level, int res,Object... args) {
			// TODO: Implement this method
		}

		@Override
		public void log(Log.LogLevel level, CharSequence str, Exception e) {
			// TODO: Implement this method
		}

	}
	private static LogCallback callback;
}
