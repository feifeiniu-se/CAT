package Project.ChangedLine;

import Constructor.Enums.OpeTypeEnum;

import java.util.HashSet;
import java.util.Set;

public class ChangedLabelLine {
    String project;
    String commit_hash;

    Set<String> added_code;
    Set<String> added_moved_code;
    Set<String> added_refactored_code;
    Set<String> added_influenced_code;
    Set<String> added_edited_code;
    Set<String> added_refactored_influenced_code;
    Set<String> added_refactored_edited_code;
    Set<String> added_influenced_edited_code;
    Set<String> added_refactored_influenced_edit_code;

    Set<String> deleted_code;
    Set<String> deleted_moved_code;
    Set<String> deleted_refactored_code;
    Set<String> deleted_influenced_code;
    Set<String> deleted_edited_code;
    Set<String> deleted_refactored_influenced_code;
    Set<String> deleted_refactored_edited_code;
    Set<String> deleted_influenced_edited_code;
    Set<String> deleted_refactored_influenced_edit_code;

    public ChangedLabelLine(String projectName, String commitHash){
        project = projectName;
        commit_hash = commitHash;

        added_code = new HashSet<>();
        added_moved_code = new HashSet<>();
        added_refactored_code = new HashSet<>();
        added_influenced_code = new HashSet<>();
        added_edited_code = new HashSet<>();
        added_refactored_influenced_code = new HashSet<>();
        added_refactored_edited_code = new HashSet<>();
        added_influenced_edited_code = new HashSet<>();
        added_refactored_influenced_edit_code = new HashSet<>();

        deleted_code = new HashSet<>();
        deleted_moved_code = new HashSet<>();
        deleted_refactored_code = new HashSet<>();
        deleted_influenced_code = new HashSet<>();
        deleted_edited_code = new HashSet<>();
        deleted_refactored_influenced_code = new HashSet<>();
        deleted_refactored_edited_code = new HashSet<>();
        deleted_influenced_edited_code = new HashSet<>();
        deleted_refactored_influenced_edit_code = new HashSet<>();
    }

    public void putLine(String line, OpeTypeEnum type){
        switch (type){
            case A -> added_code.add(line);
            case A_R -> added_refactored_code.add(line);
            case A_I -> added_influenced_code.add(line);
            case A_M -> added_moved_code.add(line);
            case A_R_I -> added_refactored_influenced_code.add(line);
            case A_E -> added_edited_code.add(line);
            case A_R_E -> added_refactored_edited_code.add(line);
            case A_I_E -> added_influenced_edited_code.add(line);
            case A_R_I_E -> added_refactored_influenced_edit_code.add(line);

            case D -> deleted_code.add(line);
            case D_R -> deleted_refactored_code.add(line);
            case D_I -> deleted_influenced_code.add(line);
            case D_M -> deleted_moved_code.add(line);
            case D_R_I -> deleted_refactored_influenced_code.add(line);
            case D_E -> deleted_edited_code.add(line);
            case D_R_E -> deleted_refactored_edited_code.add(line);
            case D_I_E -> deleted_influenced_edited_code.add(line);
            case D_R_I_E -> deleted_refactored_influenced_edit_code.add(line);
        }
    }
}
