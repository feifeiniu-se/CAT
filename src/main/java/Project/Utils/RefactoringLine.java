package Project.Utils;

import Constructor.Enums.OpeTypeEnum;
import lombok.Data;

@Data
public class RefactoringLine {
    String commitHash;
    String path;
    int lineNum;
    OpeTypeEnum type;
    String lineContent;

    public RefactoringLine(String commitHash, String path, int lineNum, OpeTypeEnum type, String lineContent){
        this.commitHash = commitHash;
        this.path = path;
        this.lineNum = lineNum;
        this.type = type;
        this.lineContent = lineContent;
    }
}
