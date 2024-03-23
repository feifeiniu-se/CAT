package Constructor;

import Constructor.Enums.OpeTypeEnum;
import Project.Utils.DiffFile;
import Project.Utils.EditLine;

import java.util.Map;

public class RenameInfluenceProcessor {
    Map<String, String> renameCodeBlockName;

    RenameInfluenceProcessor(Map<String, String> renameCodeBlockName){
        this.renameCodeBlockName = renameCodeBlockName;
    }

    public void renameInfluence(String name, int line, DiffFile diffFile){
        if (!renameCodeBlockName.containsKey(name)){
            return;
        }
        String oldName = renameCodeBlockName.get(name);

        name = getOriginalName(name);
        oldName = getOriginalName(oldName);

        if (diffFile.notContainsAddEditLine(line)){
            int oldLine = diffFile.findOldInfluencedLine(line, oldName, name);

            if (oldLine == -1){
                return;
            }

            diffFile.getCodeChangeLineLabel().put(line, OpeTypeEnum.A_I);
            diffFile.getOldCodeChangeLineLabel().put(oldLine, OpeTypeEnum.D_I);
            diffFile.putEditLine(oldLine, line);
        }

        EditLine editLine = diffFile.getAddEditLine(line);
        editLine.changeOneToken(oldName, name);
    }

    private String getOriginalName(String name){
        if (!name.contains(":")){
            // example : org.project.Code
            return name.substring(name.lastIndexOf(".")+1);
        } else if (name.indexOf(":") == name.lastIndexOf(":")){
            // example : org.project.Code:Integer_num or org.project.Code:String_Method() or org.project.Code:Path()
            if (name.contains("_") && !name.contains("(")){
                return name.substring(name.indexOf("_")+1);
            } else if (name.contains("_") && name.contains("(")){
                return name.substring(name.indexOf("_")+1, name.indexOf("("));
            }else {
                return name.substring(name.indexOf(":")+1, name.indexOf("("));
            }
        } else {
            // example : org.project.Code:String_Method():name
            return name.substring(name.lastIndexOf(":")+1);
        }
    }

    public boolean isEmpty(){
        return renameCodeBlockName.size() == 0;
    }
}
