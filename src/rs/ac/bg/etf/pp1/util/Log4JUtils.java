package rs.ac.bg.etf.pp1.util;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

public class Log4JUtils {

	private static Log4JUtils logs = new Log4JUtils();
	
	public static Log4JUtils instance() {
		return logs;
	}
	
	public URL findLoggerConfigFile() {
		return Thread.currentThread().getContextClassLoader().getResource("log4j.xml");
	}
	
	public void prepareLogFile(Logger root) {
		Appender appender = root.getAppender("file");
		
		if (!(appender instanceof FileAppender))
			return;
		FileAppender fAppender = (FileAppender)appender;
		
		String logFileName = fAppender.getFile();
		logFileName = logFileName.substring(0, logFileName.lastIndexOf('.')) + "-test.log";
		
		File logFile = new File(logFileName);
		File renamedFile = new File(logFile.getAbsoluteFile()+"."+System.currentTimeMillis());
		
		if (logFile.exists()) {
			if (!logFile.renameTo(renamedFile))
				System.err.println("Could not rename log file!");
		}
		
		fAppender.setFile(logFile.getAbsolutePath());
		fAppender.activateOptions();
	}
	
	
	
}
