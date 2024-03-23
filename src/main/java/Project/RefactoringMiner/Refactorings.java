package Project.RefactoringMiner;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Refactorings {
    private String repository;
    private String sha1;
    private String url;
    private List<Refactoring> refactorings;

    private static Set<String> filterTypes = new HashSet<>(Arrays.asList(
            "Rename Package",
            "Extract Superclass", "Move And Rename Class",
            "Extract Interface",
            "Move Class","Extract Class",
            "Extract Subclass",
            "Rename Class",
            "Move Method", "Extract Method", "Pull Up Method", "Push Down Method","Extract And Move Method",
            "Inline Method",
            "Rename Method",
            "Parameterize Variable",
            "Move Attribute",
            "Pull Up Attribute",
            "Push Down Attribute", "Move And Rename Attribute", "Rename Attribute",
            "Merge Parameter",
            "Merge Attribute",
            "Split Parameter",
            "Split Attribute",
            "Change Parameter Type",
            "Change Return Type",
            "Change Attribute Type",
            "Extract Attribute",
            "Move And Rename Method",
            "Move And Inline Method",
            "Add Parameter",
            "Remove Parameter",
            "Reorder Parameter",
            "Encapsulate Attribute",//TODO maybe method&attribute not sure
            //Attribute encapsulation is useful when you have an attribute that is affected by several different methods,
            // each of which needs that attribute to be in a known state. To prevent programmers from changing the attribute
            // in the 4GL code, you can make the attribute private so that programmers can only access it from the object's methods.
            "Parameterize Attribute",
            "Replace Attribute With Variable",
            "Move Package",
            "Split Package",
            "Merge Package",
            "Change Type Declaration Kind",
            "Collapse Hierarchy",
            "Replace Anonymous With Lambda",
            "Merge Class",
            "Inline Attribute",

            "Extract Variable",
            "Inline Variable",
            "Rename Variable",
            "Rename Parameter",
            "Replace Variable With Attribute",
//            "Replace Attribute (With Attribute)",
            "Merge Variable",
            "Split Variable",
            "Change Variable Type",
            "Add Method Annotation",
            "Remove Method Annotation",
            "Modify Method Annotation",
            "Add Attribute Annotation",
            "Remove Attribute Annotation",
            "Modify Attribute Annotation",
            "Add Class Annotation",
            "Remove Class Annotation",
            "Modify Class Annotation",
            "Add Parameter Annotation",
            "Remove Parameter Annotation",
            "Modify Parameter Annotation",
            "Add Variable Annotation",
            "Remove Variable Annotation",
            "Modify Variable Annotation",
            "Add Thrown Exception Type",
            "Remove Thrown Exception Type",
            "Change Thrown Exception Type",
            "Change Method Access Modifier",
            "Change Attribute Access Modifier",
            "Add Method Modifier",
            "Remove Method Modifier",
            "Add Attribute Modifier",
            "Remove Attribute Modifier",
            "Add Variable Modifier",
            "Add Parameter Modifier",
            "Remove Variable Modifier",
            "Remove Parameter Modifier",
            "Change Class Access Modifier",
            "Add Class Modifier",
            "Remove Class Modifier",
            "Localize Parameter",
            "Replace Loop With Pipeline"
    ));

    /**
     * 将不在 filterTypes 中的 type 过滤出去
     */
    public void filter() {
        refactorings = refactorings.stream()
                .filter(refactoring -> filterTypes.contains(refactoring.getType()) && !refactoring.getDescription().contains("new "))
                .collect(Collectors.toList());
    }

    private static Set<String> parameterLevelTypes = new HashSet<>(Arrays.asList(
            "Parameterize Variable",
            "Merge Parameter",
            "Split Parameter",
            "Change Parameter Type",
            "Add Parameter",
            "Remove Parameter",
            "Reorder Parameter",
            "Parameterize Attribute",
            "Change Return Type",
            "Rename Attribute",
            "Change Attribute Type"
    ));
    private static Set<String> methodAndAttributeLevelTypes = new HashSet<>(Arrays.asList(
            "Extract Method",
            "Inline Method",
            "Rename Method",
            "Move Method",
            "Pull Up Method",
            "Push Down Method",
            "Extract And Move Method",
            "Move And Rename Method",
            "Move And Inline Method",
            "Pull Up Attribute",
            "Push Down Attribute",
            "Move And Rename Attribute",
            "Move Attribute",
            "Merge Attribute",
            "Split Attribute",
            "Extract Attribute",
            "Encapsulate Attribute",
            "Replace Attribute With Variable",
            "Inline Attribute"
    ));
    private static Set<String> classLevelTypes = new HashSet<>(Arrays.asList(
            "Extract Superclass",
            "Extract Interface",
            "Move Class",
            "Rename Class",
            "Move And Rename Class",
            "Extract Class",
            "Extract Subclass",
            "Change Type Declaration Kind",
            "Collapse Hierarchy",
            "Merge Class"
    ));
    private static Set<String> packageLevelTypes = new HashSet<>(Arrays.asList(
            "Rename Package",
            "Move Package",
            "Split Package",
            "Merge Package"
    ));
    private static Set<String> renameLevelTypes = new HashSet<>(Arrays.asList(
            "Rename Package",
            "Move Package",
            "Split Package",
            "Merge Package",

            "Extract Superclass",
            "Extract Interface",
            "Move Class",
            "Rename Class",
            "Move And Rename Class",
            "Extract Class",
            "Extract Subclass",
            "Change Type Declaration Kind",
            "Collapse Hierarchy",
            "Merge Class",

            "Extract Method",
            "Inline Method",
            "Rename Method",
            "Move Method",
            "Pull Up Method",
            "Push Down Method",
            "Extract And Move Method",
            "Move And Rename Method",
            "Move And Inline Method",
            "Pull Up Attribute",
            "Push Down Attribute",
            "Move And Rename Attribute",
            "Move Attribute",
            "Merge Attribute",
            "Split Attribute",
            "Extract Attribute",
            "Encapsulate Attribute",
            "Replace Attribute With Variable",
            "Inline Attribute",

            "Parameterize Variable",
            "Merge Parameter",
            "Split Parameter",
            "Change Parameter Type",
            "Add Parameter",
            "Remove Parameter",
            "Reorder Parameter",
            "Parameterize Attribute",
            "Change Return Type",
            "Rename Attribute",
            "Change Attribute Type",

            "Rename Variable",
            "Rename Parameter"
    ));
    private static Set<String> othersLevelTypes = new HashSet<>(Arrays.asList(
            "Extract Variable",
            "Inline Variable",
            "Rename Variable",
            "Rename Parameter",
            "Replace Variable With Attribute",
//            "Replace Attribute (With Attribute)",
            "Merge Variable",
            "Split Variable",
            "Change Variable Type",
            "Add Method Annotation",
            "Remove Method Annotation",
            "Modify Method Annotation",
            "Add Attribute Annotation",
            "Remove Attribute Annotation",
            "Modify Attribute Annotation",
            "Add Class Annotation",
            "Remove Class Annotation",
            "Modify Class Annotation",
            "Add Parameter Annotation",
            "Remove Parameter Annotation",
            "Modify Parameter Annotation",
            "Add Variable Annotation",
            "Remove Variable Annotation",
            "Modify Variable Annotation",
            "Add Thrown Exception Type",
            "Remove Thrown Exception Type",
            "Change Thrown Exception Type",
            "Change Method Access Modifier",
            "Change Attribute Access Modifier",
            "Add Method Modifier",
            "Remove Method Modifier",
            "Add Attribute Modifier",
            "Remove Attribute Modifier",
            "Add Variable Modifier",
            "Add Parameter Modifier",
            "Remove Variable Modifier",
            "Remove Parameter Modifier",
            "Change Class Access Modifier",
            "Add Class Modifier",
            "Remove Class Modifier",
            "Localize Parameter",
            "Replace Loop With Pipeline"
    ));
    public List<Refactoring> filter(String types){
        List<Refactoring> res = new ArrayList<>();
        if(types.equals("package")){
            for(Refactoring r: refactorings){
                if(packageLevelTypes.contains(r.getType())){
                    res.add(r);
                }
            }

        }else if(types.equals("class")){
            for(Refactoring r: refactorings){
                if(classLevelTypes.contains(r.getType())){
                    res.add(r);
                }
            }
        }
        else if(types.equals("methodAndAttribute")){
            for(Refactoring r: refactorings){
                if(methodAndAttributeLevelTypes.contains(r.getType())){
                    res.add(r);
                }
            }
        }else if(types.equals("parameter")){
            for(Refactoring r:refactorings){
                if(parameterLevelTypes.contains(r.getType())){
                    res.add(r);
                }
            }
        }else if(types.equals("rename")){
            for(Refactoring r:refactorings){
                if(renameLevelTypes.contains(r.getType())){
                    res.add(r);
                }
            }
        }else if(types.equals("others")){
            for(Refactoring r:refactorings){
                if(othersLevelTypes.contains(r.getType())){
                    res.add(r);
                }
            }
        }
        else{
            return null;
        }
        return res;
    }

}
