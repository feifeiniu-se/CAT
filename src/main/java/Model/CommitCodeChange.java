package Model;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class CommitCodeChange {
    String commitID;
    List<CodeBlockTime> codeChange = new ArrayList<>();
    @ToString.Exclude
    CommitCodeChange postCommit;
    @ToString.Exclude
    CommitCodeChange preCommit;

    public CommitCodeChange(String id) {
        commitID = id;
    }

    public void setPreCommit(CommitCodeChange preCommit) {
        this.preCommit = preCommit;
    }

    public void setPostCommit(CommitCodeChange postCommit) {
        this.postCommit = postCommit;
    }

    public String getCommitID() {
        return commitID;
    }

    public List<CodeBlockTime> getCodeChange(){return codeChange;}

    public void addCodeChange(CodeBlockTime cbt) {
        codeChange.add(cbt);
        cbt.setTime(this);
    }

    public CommitCodeChange getPreCommit() {
        return this.preCommit;
    }

    public CommitCodeChange getPostCommit() {
        return this.postCommit;
    }
}
