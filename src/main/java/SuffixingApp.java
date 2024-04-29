import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.*;


public class SuffixingApp {

    public static final Logger LOGGER = Logger.getLogger(SuffixingApp.class.getName());

    public static void main(String[] args){
        if(args.length != 1){
            System.exit(1);
        }

        String filePath = args[0];
        Properties properties = new Properties();

        try(FileInputStream fileInputStream = new FileInputStream(filePath)){
            properties.load(fileInputStream);

            if(properties.getProperty("mode") == null
            || properties.getProperty("suffix") == null
            || properties.getProperty("files") == null){
                LOGGER.log(Level.SEVERE, "Not Configured");
                System.exit(1);
            }

            if(!properties.getProperty("mode").equalsIgnoreCase("copy")
            && !properties.getProperty("mode").equalsIgnoreCase("move")){
                LOGGER.log(Level.SEVERE, "Mode is not recognized: "+properties.getProperty("mode"));
                System.exit(1);
            }

            if(properties.getProperty("suffix").isEmpty()){
                LOGGER.log(Level.SEVERE, "No suffix is configured");
                System.exit(1);
            }

            if(properties.getProperty("files").isEmpty()){
                LOGGER.log(Level.WARNING, "No files are configured to be copied/moved");
                System.exit(1);
            }

            String[] files = properties.getProperty("files").split(":");

            for(String file : files){
                Path path = Paths.get(file.replace( '\\', '/'));

                if(!Files.exists(path)){
                    LOGGER.log(Level.SEVERE, "No such file: " +path);
                    continue;
                }

                Path destinationPath = Paths.get(path + "-" + properties.getProperty("suffix"));

                if (properties.getProperty("mode").equalsIgnoreCase("move")) {
                    // Move the file and log the operation

                    try {
                        Files.move(path, destinationPath);
                        LOGGER.log(Level.INFO, path + " -> " + destinationPath);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Failed to move file: " + path, e);
                    }
                } else if (properties.getProperty("mode").equalsIgnoreCase("copy")) {
                    try {
                        Files.copy(path, destinationPath);
                        LOGGER.log(Level.INFO, path + " => " + destinationPath);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Failed to copy file: " + path, e);
                    }
                }

            }




        }catch (IOException e){
            LOGGER.log(Level.SEVERE, "Error");
        }

    }
}
