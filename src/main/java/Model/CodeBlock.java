package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Constructor.Enums.CodeBlockType;
import lombok.Data;

@Data
public class CodeBlock {
    Integer codeBlockID;
    CodeBlockType type;//package, class, method, attribute
    List<CodeBlockTime> history;

    public CodeBlock(Integer id, CodeBlockType tp) {
        this.codeBlockID = id;
        type = tp;
        history = new ArrayList<>();
    }

    public void addHistory(CodeBlockTime cbt) {
        if (this.getLastHistory() == null) {
            history.add(cbt);
            cbt.setPre(null);
            cbt.setOwner(this);
        } else {
            this.getLastHistory().setPost(cbt);
            cbt.setPre(this.getLastHistory());
            history.add(cbt);
            cbt.setOwner(this);
        }
    }

    public CodeBlockTime getLastHistory() {
        return history.isEmpty() ? null : history.get(history.size() - 1);
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null||getClass()!=o.getClass()) return false;
        CodeBlock codeBlock = (CodeBlock) o;
        return Objects.equals(codeBlockID, codeBlock.codeBlockID) && Objects.equals(type, codeBlock.type);
    }
    @Override
    public int hashCode(){
        return Objects.hash(codeBlockID, type);
    }

}
