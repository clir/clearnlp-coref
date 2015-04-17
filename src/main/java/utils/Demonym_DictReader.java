package utils;

import edu.emory.clir.clearnlp.util.IOUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Alex Lutz
 * @version 1.0
 */
public class Demonym_DictReader
{
    private static Set<Character> stopCharacters = new HashSet<>(Arrays.asList('\t', '\"', '\\', ',', '/', ' '));
    private static final char KEYBREAK = '_';
    private static final char NEWLINE = '\n';

    public static Map<String, Set<String>> init(String filepath) throws IOException
    {
        Map<String, Set<String>>  DemonymMap = new HashMap<>();
        Set<String> DemonymSet = new HashSet<>();
        FileInputStream input = IOUtils.createFileInputStream(filepath);
        int i = 0; String key = ""; String line = "";

        while((i = input.read()) != 0) {
            if ((char) i == KEYBREAK) {
                key = line.trim();
                line = "";
            } else if ((char) i == NEWLINE) {
                DemonymMap.put(key, new HashSet<>(DemonymSet));
                DemonymSet.clear();
                key = "";
                line = "";
            }
            if (!stopCharacters.contains((char) i)) line += i;
            else {
                DemonymSet.add(line.trim());
                line = "";
            }
        }
        return DemonymMap;
    }
}
