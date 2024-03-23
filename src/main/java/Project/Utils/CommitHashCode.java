package Project.Utils;

import lombok.Data;
@Data
public class CommitHashCode {
    String parent;
    String hashCode;
    public CommitHashCode(String str1, String str2){
        parent = str1;
        hashCode = str2;
    }
}
