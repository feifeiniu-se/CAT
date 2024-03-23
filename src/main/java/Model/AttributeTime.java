package Model;
import Constructor.Enums.Operator;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
public class AttributeTime extends CodeBlockTime{
    public AttributeTime(String name, CommitCodeChange cmt, Operator tp, CodeBlock own, CodeBlock parent) {//add attribute
        this.name = name;
        time = cmt;
        refactorTypes.add(tp);
        owner = own;
        parentCodeBlock = parent;
        cmt.addCodeChange(this);
        own.addHistory(this);

        if(parent != null){
            CodeBlockTime parentTime = parent.getLastHistory();
            if(parentTime.getAttributes() != null){
                parentTime.getAttributes().add(own);
            }
        }
    }


    @Override
    public String getSignature() {
        return this.getParentCodeBlock().getLastHistory().getSignature()+":"+this.getName();
    }

    @Override
    public Set<CodeBlock> getPackages() {
        return null;
    }

    @Override
    public Set<CodeBlock> getClasses() {
        return null;
    }

    @Override
    public Set<CodeBlock> getMethods() {
        return null;
    }

    @Override
    public Set<CodeBlock> getAttributes() {
        return null;
    }


//    @Override
//    public CodeBlockTime deepCopy() throws JsonProcessingException {
//        return null;
//    }
    @Override
    public boolean equals(Object o){
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
