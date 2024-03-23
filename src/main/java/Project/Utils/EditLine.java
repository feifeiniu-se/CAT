package Project.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class EditLine {
    private final Map<String, Integer> oldLineTokens;
    private final Map<String, Integer> newLineTokens;
    private int oldLineNum;
    private int newLineNum;

    public EditLine(int oldLineNum, int newLineNum, String oldLineContent, String newLineContent){
        this.oldLineNum = oldLineNum;
        this.newLineNum = newLineNum;

        this.oldLineTokens = tokenizeCode(oldLineContent);
        this.newLineTokens = tokenizeCode(newLineContent);
    }

    public boolean isEdit(){
        if (oldLineTokens.size() != newLineTokens.size()){
            return true;
        }

        for(String token : oldLineTokens.keySet()){
            if(oldLineTokens.get(token) <= 0){
                continue;
            }

            if (!(oldLineTokens.get(token).equals(newLineTokens.get(token)))){
                return true;
            }
        }

        return false;
    }

    private static Map<String, Integer> tokenizeCode(String code) {
        String[] tokens = code.split("[\\s;{}(),.]|(?<!\\[)]|\\[(?!])");

        return Arrays.stream(tokens)
                .filter(token -> !token.isEmpty())
                .collect(Collectors.groupingBy(s -> s, Collectors.summingInt(s -> 1)));
    }

    public void addModifier(String modifier){
        changeTokenNum(oldLineTokens, modifier, 1);
    }

    public void removeModifier(String modifier){
        changeTokenNum(oldLineTokens, modifier, -1);
    }

    public void changeOneToken(String oldToken, String newToken){
        changeTokenNum(oldLineTokens, oldToken, -1);
        changeTokenNum(oldLineTokens, newToken, 1);
    }

    public void changeType(String oldType, String newType){
        String[] oldTokens = oldType.split("[\\s;{}(),.]|(?<!\\[)]|\\[(?!])");
        for (String token : oldTokens){
            if (!token.isEmpty()){
                changeTokenNum(oldLineTokens, token, -1);
            }
        }

        String[] newTokens = newType.split("[\\s;{}(),.]|(?<!\\[)]|\\[(?!])");
        for (String token : newTokens){
            if (!token.isEmpty()){
                changeTokenNum(oldLineTokens, token, 1);
            }
        }
    }


    public void addParameter(String parameter, String parameterType){
        changeTokenNum(oldLineTokens, parameter, 1);
        changeTokenNum(oldLineTokens, parameterType, 1);
    }

    public void removeParameter(String parameter, String parameterType){
        changeTokenNum(oldLineTokens, parameter, -1);
        changeTokenNum(oldLineTokens, parameterType, -1);
    }

    public void addAnnotation(String annotation){
        annotation = annotation.replace("\\\"", "\"");
        String[] tokens = annotation.split("[\\s;{}(),.]|(?<!\\[)]|\\[(?!])");
        for (String token : tokens){
            if (!token.isEmpty()){
                changeTokenNum(oldLineTokens, token, 1);
            }
        }
    }

    public void removeAnnotation(String annotation){
        annotation = annotation.replace("\\\"", "\"");
        String[] tokens = annotation.split("[\\s;{}(),.]|(?<!\\[)]|\\[(?!])");
        for (String token : tokens){
            if (!token.isEmpty()){
                changeTokenNum(oldLineTokens, token, -1);
            }
        }
    }

    public void addException(String exceptionType){
        if (!oldLineTokens.containsKey("throws")){
            oldLineTokens.put("throws", 1);
        }
        changeTokenNum(oldLineTokens, exceptionType, 1);
    }

    public void removeException(String exceptionType){
        if (!newLineTokens.containsKey("throws")){
            oldLineTokens.remove("throws");
        }
        changeTokenNum(oldLineTokens, exceptionType, -1);
    }

    public void changeTokenNum(Map<String, Integer> lineTokens, String token, int num){
        if (lineTokens.containsKey(token)){
            lineTokens.put(token, lineTokens.get(token)+num);
        } else {
            lineTokens.put(token, num);
        }

        if (lineTokens.get(token) <= 0){
            lineTokens.remove(token);
        }
    }

}
