package biz;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingService {
    public static Logger logger;
    public static FileHandler fileHandler;
    private static final String logFile = " login_activity.txt";

    public static void LogInfo(String logText){
        try{
            if(logger == null){
                logger = Logger.getLogger("LogFile");
            }
            fileHandler = new FileHandler(logFile, true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            logger.info(logText);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
