package Project.RefactoringMiner;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Refactoring {
    private String type;
    private String description;
    private List<SideLocation> leftSideLocations;
    private List<SideLocation> rightSideLocations;

    private String methodSignature;

    public String getLastClassName(){// seperate the last from description
        String[] tmp = description.split(" ");
        return tmp[tmp.length-1];
    }
    public String getFirstClassName(){
        String[] tmp = description.split(" ");
        int i;
        for(i=0; i<tmp.length; i++){
            if(tmp[i].equals("class"))
                break;
        }
        return tmp[i+1];
    }

    public List<SideLocation> leftFilter(String str1){
        List<SideLocation> res = leftSideLocations.stream()
                .filter(leftSideLocation -> leftSideLocation.getDescription().equals(str1))
                .collect(Collectors.toList());
        return res;
    }
    public List<SideLocation> rightFilter(String str1){
        List<SideLocation> res = rightSideLocations.stream()
                .filter(rightSideLocation -> rightSideLocation.getDescription().equals(str1))
                .collect(Collectors.toList());
        return res;
    }


}
