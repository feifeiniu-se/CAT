package Project.Utils;
import Constructor.Enums.FileType;
import Constructor.Enums.OpeTypeEnum;
import Model.AttributeTime;
import Model.ClassTime;
import Model.CodeBlockTime;
import Model.MethodTime;
import Project.ChangedLine.ChangedLabelLine;
import lombok.Data;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;


@Data
public class DiffFile {
    FileType type;// add, delete, rename, modify, derive
    String path;
    String content;
    String oldPath;
    String oldContent;
    String patch;
    List<Integer> codeChangeLineNum;
    HashMap<Integer, OpeTypeEnum> codeChangeLineLabel;
    List<Integer> oldCodeChangeLineNum;
    HashMap<Integer, OpeTypeEnum> oldCodeChangeLineLabel;
    List<CodeBlockTime> times;

    List<CodeBlockTime> deleteMoveTimes;

    HashMap<Integer, EditLine> addEditLines;
    HashMap<Integer, EditLine> deleteEditLines;

    EditList editList;


    public DiffFile(FileType type, String path, String content){//仅用于项目初始状态 全部都是add
        this.type = type;
        this.path = path;
        this.content = content;
        this.oldPath = null;
        this.oldContent = null;
        this.codeChangeLineLabel = new HashMap<>();
        this.codeChangeLineNum = new ArrayList<>();
        this.oldCodeChangeLineLabel = new HashMap<>();
        this.oldCodeChangeLineNum = new ArrayList<>();
        this.times = new ArrayList<>();

        this.deleteMoveTimes = new ArrayList<>();
        this.deleteEditLines = new HashMap<>();
        this.addEditLines = new HashMap<>();
        this.editList = new EditList();
    }
    public DiffFile(FileType type, String path, String content, String oldPath, String oldContent){
        this.type = type;
        this.path = path;
        this.content = content;
        this.oldPath = oldPath;
        this.oldContent = oldContent;
        this.codeChangeLineLabel = new HashMap<>();
        this.oldCodeChangeLineLabel = new HashMap<>();
        this.times = new ArrayList<>();

        this.deleteMoveTimes = new ArrayList<>();
        this.deleteEditLines = new HashMap<>();
        this.addEditLines = new HashMap<>();
    }

    public void patchNew(){
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++){
            this.codeChangeLineNum.add(i+1);
        }
    }

    public void patchParser(){
        this.oldCodeChangeLineNum = getLineNum('-');
        this.codeChangeLineNum = getLineNum('+');
    }

    private List<Integer> getLineNum(char separator) {
        List<Integer> resultLines = new ArrayList<Integer>(); // line number of deleted lines (the left line number of commit)<d1, d2, d3>
        patch = patch.replace("* @@", "");// this is for some special cases in bcel
        patch = patch.replace("#@@", "");// this is for codec
        patch = patch.replace("// @@", "");// this is for codec
        String[] blocks = patch.split("@@(?=\\s*[-+][0-9]) | (?<=[0-9]\\s)@@"); //split the patch into blocks according to @@
        for (int i = 0; i < (blocks.length - 1) / 2; i++) { //different blocks of code change
            Integer startLine = 0;
            Integer currentLine;
            String firstLine = blocks[i * 2 + 1]; //numbers
            String[] lines = blocks[i * 2 + 2].split("\\n"); // code changes, split change block into lines
            String[] numbers = firstLine.split("\\s+");
            for (String n : numbers) {
                if (n.startsWith(Character.toString(separator))) {
                    try {
                        String num = n.split(",")[0];
                        startLine = Integer.valueOf(num);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } //obtain the number of start line and total lines

            currentLine = abs(startLine);
            for (int j = 1; j < lines.length; j++) {
                if (!lines[j].startsWith("-") && !lines[j].startsWith("+")) {
                    currentLine = currentLine + 1;
                } else if (lines[j].startsWith(Character.toString(separator))) {
                    resultLines.add(currentLine.intValue());
                    currentLine = currentLine + 1;
                }
            }
        }
        return resultLines;
    }

    public boolean containsChangeLine(int line){
        return codeChangeLineNum.contains(line);
    }

    public boolean containsOldChangeLine(int line){
        return oldCodeChangeLineNum.contains(line);
    }

    public void label(Metric metric, ChangedLabelLine changedLabelLine){
        for(Integer i: codeChangeLineNum){
            String[] newLines = content.split("\n");
            if(!codeChangeLineLabel.containsKey(i)){
                if (isNotEditNewLine(i)){
                    codeChangeLineLabel.put(i, OpeTypeEnum.A);
                } else {
                    codeChangeLineLabel.put(i, OpeTypeEnum.A_E);
                }
            } else {
                if (addEditLines.containsKey(i)){
                    EditLine editLine = addEditLines.get(i);
                    if (editLine.isEdit()){
                        if (codeChangeLineLabel.get(i).equals(OpeTypeEnum.A_R)){
                            codeChangeLineLabel.put(i, OpeTypeEnum.A_R_E);
                        } else if (codeChangeLineLabel.get(i).equals(OpeTypeEnum.A_I)){
                            codeChangeLineLabel.put(i, OpeTypeEnum.A_I_E);
                        } else if (codeChangeLineLabel.get(i).equals(OpeTypeEnum.A_R_I)){
                            codeChangeLineLabel.put(i, OpeTypeEnum.A_R_I_E);
                        }
                    }
                }
            }

            if (i-1 >= 0 && i-1 < newLines.length){
                if (!newLines[i-1].trim().isEmpty()){
                    changedLabelLine.putLine(newLines[i-1].trim(), codeChangeLineLabel.get(i));
                }
            }
        }
        for(Integer i: oldCodeChangeLineNum){
            String[] oldLines = oldContent.split("\n");
            if(!oldCodeChangeLineLabel.containsKey(i)){
                if (isNotEditOldLine(i)){
                    oldCodeChangeLineLabel.put(i,OpeTypeEnum.D);
                } else {
                    oldCodeChangeLineLabel.put(i,OpeTypeEnum.D_E);
                }
            } else {
                if (deleteEditLines.containsKey(i)){
                    EditLine editLine = deleteEditLines.get(i);
                    if (editLine.isEdit()){
                        if (oldCodeChangeLineLabel.get(i).equals(OpeTypeEnum.D_R)){
                            oldCodeChangeLineLabel.put(i, OpeTypeEnum.D_R_E);
                        } else if (oldCodeChangeLineLabel.get(i).equals(OpeTypeEnum.D_I)){
                            oldCodeChangeLineLabel.put(i, OpeTypeEnum.D_I_E);
                        } else if (oldCodeChangeLineLabel.get(i).equals(OpeTypeEnum.D_R_I)){
                            oldCodeChangeLineLabel.put(i, OpeTypeEnum.D_R_I_E);
                        }
                    }
                }
            }

            if (i-1 >= 0 && i-1 < oldLines.length){
                if (!oldLines[i-1].trim().isEmpty()){
                    changedLabelLine.putLine(oldLines[i-1].trim(), oldCodeChangeLineLabel.get(i));
                }
            }
        }
        label_(codeChangeLineNum, codeChangeLineLabel, true, metric);
        label_(oldCodeChangeLineNum, oldCodeChangeLineLabel, false, metric);

        if (times == null){
            return;
        }

        for(CodeBlockTime c: times.stream().filter((CodeBlockTime time)->time.getNewChangeLines().size()>0 || time.getOldChangeLines().size()>0).collect(Collectors.toList())){
            if (c instanceof ClassTime){
                classSwitch(metric, c);
            } else if (c instanceof MethodTime){
                methodSwitch(metric, c);
            } else if (c instanceof AttributeTime){
                attributeSwitch(metric, c);
            }
        }
    }

    private void label_(List<Integer> nums, HashMap<Integer, OpeTypeEnum> lineLabel, boolean isNew, Metric metric) {
        for(Integer i: nums){
            OpeTypeEnum type = lineLabel.get(i);

            lineSwitch(metric, type);
            if(times==null){
                continue;
            }
            for(CodeBlockTime c: times){
                if (isNew && c.containsNew(i) && !deleteMoveTimes.contains(c)){
                    c.getNewChangeLines().put(i, type);
                } else if (!isNew && c.containsOld(i)){
                    // 如果codeBlockTime
                    if (c.isMoved() && !deleteMoveTimes.contains(c)){
                        continue;
                    }
                    c.getOldChangeLines().put(i, type);
                }
            }
        }
    }

    private boolean isNotEditOldLine(int line){
        for (Edit edit : editList){
            if (edit.getType() == Edit.Type.REPLACE && line >= edit.getBeginA()+1 && line < edit.getEndA()+1){
                String[] oldLines = oldContent.split("\n");
                String[] newLines = content.split("\n");
                if (line-1 >= oldLines.length){
                    continue;
                }
                String oldLine = oldLines[line-1];
                for (int i = edit.getBeginB(); i < edit.getEndB(); i++){
                    if (i >= newLines.length){
                        continue;
                    }
                    String newLine = newLines[i];
                    double currentSimilarity = CosineSimilarity.cosineSimilarity(oldLine, newLine);
                    // 阈值为0.8判定为edit
                    if (currentSimilarity > 0.8){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean isNotEditNewLine(int line){
        for (Edit edit : editList){
            if (edit.getType() == Edit.Type.REPLACE && line >= edit.getBeginB()+1 && line < edit.getEndB()+1){
                String[] oldLines = oldContent.split("\n");
                String[] newLines = content.split("\n");
                if (line-1 >= newLines.length){
                    continue;
                }
                String newLine = newLines[line-1];
                for (int i = edit.getBeginA(); i < edit.getEndA(); i++){
                    if (i >= oldLines.length){
                        continue;
                    }
                    String oldLine = oldLines[i];
                    double currentSimilarity = CosineSimilarity.cosineSimilarity(oldLine, newLine);
                    // 阈值为0.8判定为edit
                    if (currentSimilarity > 0.8){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private OpeTypeEnum addLabel(CodeBlockTime c){
        int len = c.getNewEndLineNum() - c.getNewStartLineNum() + 1;
        if (c.getNewChangeLines().size() == 0 || c.getNewEndLineNum() == -1 || c.getNewStartLineNum() == -1) return OpeTypeEnum.NULL;

        HashMap<OpeTypeEnum, Integer> res = new HashMap<>();
        res.put(OpeTypeEnum.A, 0);
        res.put(OpeTypeEnum.A_R, 0);
        res.put(OpeTypeEnum.A_R_E, 0);
        res.put(OpeTypeEnum.A_M, 0);
        res.put(OpeTypeEnum.A_I, 0);
        res.put(OpeTypeEnum.A_I_E, 0);
        res.put(OpeTypeEnum.A_R_I, 0);
        res.put(OpeTypeEnum.A_R_I_E, 0);
        res.put(OpeTypeEnum.A_E, 0);
        for(Map.Entry<Integer, OpeTypeEnum> entry:c.getNewChangeLines().entrySet()){
            OpeTypeEnum type = entry.getValue();
            res.put(type, res.get(type)+1);
        }

        if (res.get(OpeTypeEnum.A_I)>=len || res.get(OpeTypeEnum.A_I)==c.getNewChangeLines().size()){
            return OpeTypeEnum.I;
        }else if(res.get(OpeTypeEnum.A_M)>=len || res.get(OpeTypeEnum.A_M)==c.getNewChangeLines().size()){
            return OpeTypeEnum.M;
        }else if(res.get(OpeTypeEnum.A_R)+res.get(OpeTypeEnum.A_I)+res.get(OpeTypeEnum.A_R_I)>=len || res.get(OpeTypeEnum.A_R)+res.get(OpeTypeEnum.A_I)+res.get(OpeTypeEnum.A_R_I)==c.getNewChangeLines().size()){
            return OpeTypeEnum.R;
        }else if((res.get(OpeTypeEnum.A_R)+res.get(OpeTypeEnum.A_I)+res.get(OpeTypeEnum.A_R_I)+res.get(OpeTypeEnum.A_M))>=c.getNewChangeLines().size()){
            return OpeTypeEnum.R;
        }else if(res.get(OpeTypeEnum.A)>=len){
            return OpeTypeEnum.A;
        }else{
            return OpeTypeEnum.E;
        }
    }

    private OpeTypeEnum deleteLabel(CodeBlockTime c){
        int len = c.getOldEndLineNum() - c.getOldStartLineNum() + 1;
        if (c.getOldChangeLines().size() == 0 || c.getOldEndLineNum() == -1 || c.getOldStartLineNum() == -1) return OpeTypeEnum.NULL;

        HashMap<OpeTypeEnum, Integer> res = new HashMap<>();
        res.put(OpeTypeEnum.D, 0);
        res.put(OpeTypeEnum.D_R, 0);
        res.put(OpeTypeEnum.D_R_E, 0);
        res.put(OpeTypeEnum.D_M, 0);
        res.put(OpeTypeEnum.D_I, 0);
        res.put(OpeTypeEnum.D_I_E, 0);
        res.put(OpeTypeEnum.D_R_I, 0);
        res.put(OpeTypeEnum.D_R_I_E, 0);
        res.put(OpeTypeEnum.D_E, 0);
        for(Map.Entry<Integer, OpeTypeEnum> entry:c.getOldChangeLines().entrySet()){
            OpeTypeEnum type = entry.getValue();
            res.put(type, res.get(type)+1);
        }
        if (res.get(OpeTypeEnum.D_I)>=len || res.get(OpeTypeEnum.D_I)==c.getOldChangeLines().size()){
            return OpeTypeEnum.I;
        }else if(res.get(OpeTypeEnum.D_M)>=len || res.get(OpeTypeEnum.D_M)==c.getOldChangeLines().size()){
            return OpeTypeEnum.M;
        }else if(res.get(OpeTypeEnum.D_R)+res.get(OpeTypeEnum.D_I)+res.get(OpeTypeEnum.D_R_I)>=len || res.get(OpeTypeEnum.D_R)+res.get(OpeTypeEnum.D_I)+res.get(OpeTypeEnum.D_R_I)==c.getOldChangeLines().size()){
            return OpeTypeEnum.R;
        }else if((res.get(OpeTypeEnum.D_R)+res.get(OpeTypeEnum.D_I)+res.get(OpeTypeEnum.D_R_I)+res.get(OpeTypeEnum.D_M))>=c.getOldChangeLines().size()){
            return OpeTypeEnum.R;
        }else if(res.get(OpeTypeEnum.D)>=len){
            return OpeTypeEnum.D;
        }else{
            return OpeTypeEnum.E;
        }
    }

    private void lineSwitch(Metric metric, OpeTypeEnum typeEnum){
        switch (typeEnum){
            case A -> metric.aCount++;
            case A_R -> metric.arCount++;
            case A_I -> metric.aiCount++;
            case A_M -> metric.amCount++;
            case A_R_I -> metric.ariCount++;
            case A_E -> metric.aeCount++;
            case A_R_E -> metric.areCount++;
            case A_I_E -> metric.aieCount++;
            case A_R_I_E -> metric.arieCount++;

            case D -> metric.dCount++;
            case D_R -> metric.drCount++;
            case D_I -> metric.diCount++;
            case D_M -> metric.dmCount++;
            case D_R_I -> metric.driCount++;
            case D_E -> metric.deCount++;
            case D_R_E -> metric.dreCount++;
            case D_I_E -> metric.dieCount++;
            case D_R_I_E -> metric.drieCount++;
        }

    }

    private void attributeSwitch(Metric metric, CodeBlockTime c){
        // 对attribute检测，可能会被分开两行，而只修改一行，因此需要先找到在判单
        if (c.getNewChangeLines().size() > 0 && c.getNewEndLineNum() != -1 && c.getNewStartLineNum() != -1){
            int line = -1;
            for (int i = c.getNewStartLineNum(); i <= c.getNewEndLineNum(); i++){
                if (codeChangeLineNum.contains(i) && codeChangeLineLabel.containsKey(i)){
                    line = i;
                    break;
                }
            }
            if (line != -1){
                OpeTypeEnum addType = c.getNewChangeLines().get(line);
                switch (addType) {
                    case A -> metric.A_A.add(c);
                    case D -> metric.A_D.add(c);
                    case A_M, D_M -> metric.A_M.add(c);
                    case A_R, D_R -> metric.A_R.add(c);
                    case A_E, D_E -> metric.A_E.add(c);
                    case A_I, D_I -> metric.A_I.add(c);
                    case A_R_I, D_R_I -> metric.A_R_I.add(c);
                    case A_R_E, D_R_E -> metric.A_R_E.add(c);
                    case A_I_E, D_I_E -> metric.A_I_E.add(c);
                    case A_R_I_E, D_R_I_E -> metric.A_R_I_E.add(c);
                }
            }
        }

        if (c.getOldChangeLines().size() > 0 && c.getOldEndLineNum() != -1 && c.getOldStartLineNum() != -1){
            int line = -1;
            for (int i = c.getOldStartLineNum(); i <= c.getOldEndLineNum(); i++){
                if (oldCodeChangeLineNum.contains(i) && oldCodeChangeLineLabel.containsKey(i)){
                    line = i;
                    break;
                }
            }
            if (line != -1){
                OpeTypeEnum deleteType = c.getOldChangeLines().get(line);
                switch (deleteType) {
                    case A -> metric.A_A.add(c);
                    case D -> metric.A_D.add(c);
                    case A_M, D_M -> metric.A_M.add(c);
                    case A_R, D_R -> metric.A_R.add(c);
                    case A_E, D_E -> metric.A_E.add(c);
                    case A_I, D_I -> metric.A_I.add(c);
                    case A_R_I, D_R_I -> metric.A_R_I.add(c);
                    case A_R_E, D_R_E -> metric.A_R_E.add(c);
                    case A_I_E, D_I_E -> metric.A_I_E.add(c);
                    case A_R_I_E, D_R_I_E -> metric.A_R_I_E.add(c);
                }
            }

        }
    }
    private void methodSwitch(Metric metric, CodeBlockTime c) {
        OpeTypeEnum addType = addLabel(c);
        switch (addType) {
            case A -> metric.M_A.add(c);
            case D -> metric.M_D.add(c);
            case M -> metric.M_M.add(c);
            case R -> metric.M_R.add(c);
            case E -> metric.M_E.add(c);
            case I -> metric.M_I.add(c);
        }

        OpeTypeEnum deleteType = deleteLabel(c);
        switch (deleteType) {
            case A -> metric.M_A.add(c);
            case D -> metric.M_D.add(c);
            case M -> metric.M_M.add(c);
            case R -> metric.M_R.add(c);
            case E -> metric.M_E.add(c);
            case I -> metric.M_I.add(c);
        }
    }

    private void classSwitch(Metric metric, CodeBlockTime c) {
        OpeTypeEnum addType = addLabel(c);
        switch (addType){
            case A -> metric.C_A.add(c);
            case D -> metric.C_D.add(c);
            case M -> metric.C_M.add(c);
            case R -> metric.C_R.add(c);
            case E -> metric.C_E.add(c);
            case I -> metric.C_I.add(c);
        }

        OpeTypeEnum deleteType = deleteLabel(c);
        switch (deleteType){
            case A -> metric.C_A.add(c);
            case D -> metric.C_D.add(c);
            case M -> metric.C_M.add(c);
            case R -> metric.C_R.add(c);
            case E -> metric.C_E.add(c);
            case I -> metric.C_I.add(c);
        }

    }

    public boolean notContainsAddEditLine(int lineNum){
        return !addEditLines.containsKey(lineNum);
    }

    public void putEditLine(int oldLineNum, int newLineNum){
        if (oldContent == null || content == null){
            return;
        }

        String[] oldLines = oldContent.split("\n");
        String[] newLines = content.split("\n");

        if (oldLineNum-1 < 0 || oldLineNum-1 >= oldLines.length || newLineNum-1 < 0 || newLineNum-1 > newLines.length){
            return;
        }

        EditLine editLine = new EditLine(oldLineNum, newLineNum, oldLines[oldLineNum-1], newLines[newLineNum-1]);
        addEditLines.put(newLineNum, editLine);
        deleteEditLines.put(oldLineNum, editLine);
    }

    public void putMoveFromEditLine(int oldLineNum, EditLine editLine){
        deleteEditLines.put(oldLineNum, editLine);
    }

    public void putMoveInEditLine(int newLineNum, EditLine editLine){
        addEditLines.put(newLineNum, editLine);
    }

    public EditLine getAddEditLine(int lineNum){
        return addEditLines.get(lineNum);
    }

    public int findOldInfluencedLine(int line, String oldName, String newName){

        int ret = -1;
        for (Edit edit : editList){
            if (edit.getType() == Edit.Type.REPLACE && line >= edit.getBeginB()+1 && line < edit.getEndB()+1){
                String[] oldLines = oldContent.split("\n");
                String[] newLines = content.split("\n");
                if (line-1 >= newLines.length){
                    continue;
                }
                String newLine = newLines[line-1];
                double similarity = 0;
                for (int i = edit.getBeginA(); i < edit.getEndA(); i++){
                    if (i >= oldLines.length){
                        continue;
                    }
                    String oldLine = oldLines[i];
                    if (oldLine.contains(oldName)){
                        double currentSimilarity = CosineSimilarity.cosineRenameSimilarity(oldLine, newLine, oldName, newName);
                        // 阈值为0.8判定为受到影响，且为相似度最大的一行
                        if (currentSimilarity > 0.8 && currentSimilarity > similarity){
                            similarity = currentSimilarity;
                            ret = i+1;
                        }
                    }
                }
            }
        }
        return ret;
    }
}
