package Project.Utils;

import Project.RefactoringMiner.Commits;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadJsonFiles {
    private static final Logger logger = LoggerFactory.getLogger(ReadJsonFiles.class);

    public static String readFile(String path) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }


}
