package Constructor;


import Constructor.Enums.CodeBlockType;
import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import Model.*;
import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.SideLocation;
import Project.Utils.DiffFile;
import Project.Utils.EditLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static String cutString(String str, String start, String end){
        Integer s = str.indexOf(start);
        Integer e = str.indexOf(end);
        return str.substring(s+1, e);
    }
    public static String findPackageName(String[] newPkgNames, String path) {
        for(String n: newPkgNames){
            if(path.replace("/", ".").contains(n)){
                return n;
            }
        }
        return null;
    }
    public static String toRoot(String str){

        return str.startsWith(".") ? str.substring(1) : str;
    }
    public static String defaultPackage(String classSignature){
//        if(!classSignature.contains(".")){
//            classSignature = "default.package." + classSignature;
//        }
        return classSignature;
    }

    public static boolean isNestedClass(String filePath, String classSignature){
        filePath = filePath.replace("/", ".");
        filePath = filePath.substring(0, filePath.length()-5);
        return !filePath.contains(classSignature);
    }

    public static String sig2Name(String sig){
        return sig.substring(sig.lastIndexOf(".")+1);
    }
    public static String sig2Father(String sig){
        if(sig.lastIndexOf(".") != -1){
            return sig.substring(0, sig.lastIndexOf("."));
        } else {
            return "";
        }

    }
    public static void add_fatherClass(String filePath, String sig, HashMap<String, CodeBlock> mappings){
//        String father
//
//        Operator.Add_Class.apply(codeBlocks, mappings, null, commitTime, fatherSig);

    }
    public static String sig2Package(String filePath, String sig){
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.replace("/", ".");
        while (!filePath.contains(sig)){
            sig = sig.substring(0, sig.lastIndexOf("."));
        }
        return sig;
    }

    public static void refactoredLine(List<DiffFile> diffList, List<SideLocation> locations, char flag){
        if(flag=='L'){
            for(SideLocation l:locations){
                DiffFile oldPath = findOldFile(diffList, l.getFilePath());
                if(oldPath == null) {
                    return;
                }
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    oldPath.getOldCodeChangeLineLabel().put(i, OpeTypeEnum.D_R);
                }
            }
        }else if(flag=='R'){
            for(SideLocation l:locations){
                DiffFile newPath = findFile(diffList, l.getFilePath());
                if(newPath == null) {
                    return;
                }
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    newPath.getCodeChangeLineLabel().put(i, OpeTypeEnum.A_R);
                }
            }
        }
    }
    public static void refactorFirstLine(Refactoring r, HashMap<String, CodeBlock> mappings, List<DiffFile> diffList, List<SideLocation> locations, char flag){
        if(flag=='L'){
            for(SideLocation l:locations){
                DiffFile oldPath = findOldFile(diffList, l.getFilePath());
                if(oldPath == null) {
                    return;
                }
                int line = getFirstLine(r, mappings, l, 'L');
                oldPath.getOldCodeChangeLineLabel().put(line, OpeTypeEnum.D_R);
            }
        }else if(flag=='R'){
            for(SideLocation l:locations){
                DiffFile newPath = findFile(diffList, l.getFilePath());
                if(newPath == null) {
                    return;
                }
                int line = getFirstLine(r, mappings, l, 'R');
                newPath.getCodeChangeLineLabel().put(line, OpeTypeEnum.A_R);
            }
        }
    }

    public static void refactoredLine(HashMap<String, CodeBlock> mappings, List<DiffFile> diffList, List<SideLocation> locations, char flag, OpeTypeEnum type){
        OpeTypeEnum currentType = null;
        if(flag=='L'){
            for(SideLocation l:locations){
                DiffFile oldPath = findOldFile(diffList, l.getFilePath());
                if(oldPath == null) {
                    return;
                }
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    if (type.equals(OpeTypeEnum.R)){
                        if (OpeTypeEnum.D_I.equals(oldPath.getOldCodeChangeLineLabel().get(i)) || OpeTypeEnum.D_R_I.equals(oldPath.getOldCodeChangeLineLabel().get(i))){
                            currentType= OpeTypeEnum.D_R_I;
                        } else {
                            currentType= OpeTypeEnum.D_R;
                        }
                    } else if (type.equals(OpeTypeEnum.M)){
                        if (OpeTypeEnum.D_I.equals(oldPath.getOldCodeChangeLineLabel().get(i))){
                            continue;
                        }
                        currentType = OpeTypeEnum.D_M;
                    } else if (type.equals(OpeTypeEnum.I)){
                        if (OpeTypeEnum.D_R.equals(oldPath.getOldCodeChangeLineLabel().get(i)) || OpeTypeEnum.D_R_I.equals(oldPath.getOldCodeChangeLineLabel().get(i))){
                            currentType= OpeTypeEnum.D_R_I;
                        } else {
                            currentType = OpeTypeEnum.D_I;
                        }
                    } else if (type.equals(OpeTypeEnum.D)){
                        currentType = type;
                    }
                    if (currentType != null){
                        oldPath.getOldCodeChangeLineLabel().put(i, currentType);
                    }
                }
            }
        }else if(flag=='R'){
            for(SideLocation l:locations){
                DiffFile newPath = findFile(diffList, l.getFilePath());
                if(newPath == null) {
                    return;
                }
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    if (type.equals(OpeTypeEnum.R)){
                        if (OpeTypeEnum.A_I.equals(newPath.getCodeChangeLineLabel().get(i)) || OpeTypeEnum.A_R_I.equals(newPath.getCodeChangeLineLabel().get(i))){
                            currentType = OpeTypeEnum.A_R_I;
                        } else {
                            currentType = OpeTypeEnum.A_R;
                        }
                    } else if (type.equals(OpeTypeEnum.M)){
                        if (OpeTypeEnum.A_I.equals(newPath.getCodeChangeLineLabel().get(i))){
                            continue;
                        }
                        currentType = OpeTypeEnum.A_M;
                    } else if (type.equals(OpeTypeEnum.I)){
                        if (OpeTypeEnum.A_R.equals(newPath.getCodeChangeLineLabel().get(i)) || OpeTypeEnum.A_R_I.equals(newPath.getCodeChangeLineLabel().get(i))){
                            currentType = OpeTypeEnum.A_R_I;
                        } else {
                            currentType = OpeTypeEnum.A_I;
                        }
                    } else if (type.equals(OpeTypeEnum.A)){
                        currentType = type;
                    }
                    if (currentType != null){
                        newPath.getCodeChangeLineLabel().put(i, currentType);
                    }
                }
            }
        }
    }

    public static DiffFile findOldFile(List<DiffFile> diffList, String filePath){
        for(DiffFile f:diffList){
            if(f.getOldPath().equals(filePath)){
                return f;
            }
        }
        System.out.println("old diff path error");
        System.out.println(filePath);
        return null;
    }

    public static DiffFile findFile(List<DiffFile> diffList, String filePath){
        for(DiffFile f:diffList){
            if(f.getPath().equals(filePath)){
                return f;
            }
        }
        System.out.println("new diff path error");
        System.out.println(filePath);
        return null;
    }

    public static CodeBlockTime getMethodTime(String className, String methodName, HashMap<String, CodeBlock> mappings, Map<String, String> renameCodeBlockName) throws Exception{
        String fullMethodName = className + ":" + methodName;
        if (mappings.get(fullMethodName) == null && renameCodeBlockName.containsValue(fullMethodName)){
            for(Map.Entry<String, String> entry : renameCodeBlockName.entrySet()){
                if(entry.getValue().equals(fullMethodName)){
                    fullMethodName = entry.getKey();
                    break;
                }
            }
        }

        return mappings.get(fullMethodName).getLastHistory();
    }

    public static int getFirstLine(Refactoring r, HashMap<String, CodeBlock> mappings, SideLocation l, char flag){
        String name;
        CodeBlockTime c;
        switch (l.getCodeElementType()) {
            case "METHOD_DECLARATION":
                name = r.getLastClassName() + ":" + l.parseMethodDeclaration().get("MN");
                if (mappings.get(name) == null) {
                    return l.getStartLine();
                }
                c = mappings.get(name).getLastHistory();
                if (flag == 'L'){
                    return c.getOldDeclarationLineNum();
                }else {
                    return c.getNewDeclarationLineNum();
                }
            case "TYPE_DECLARATION":
                name = toRoot(l.getCodeElement());
                if (mappings.get(name) == null) {
                    return l.getStartLine();
                }
                c = mappings.get(name).getLastHistory();
                if (flag == 'L'){
                    return c.getOldDeclarationLineNum();
                }else {
                    return c.getNewDeclarationLineNum();
                }
            case "FIELD_DECLARATION":
                name = l.parseAttributeOrParameter();
                if (mappings.get(name) == null) {
                    return l.getStartLine();
                }
                c = mappings.get(name).getLastHistory();
                if (flag == 'L'){
                    return c.getOldDeclarationLineNum();
                }else {
                    return c.getNewDeclarationLineNum();
                }
            default:
                return l.getStartLine();
        }
    }
}
