package Model;

import Constructor.Enums.Operator;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class MethodTime extends CodeBlockTime{
    public MethodTime(String name, CommitCodeChange cmt, Operator tp, CodeBlock own, CodeBlock parent, String params) {//add method
        this.name = name;
        time = cmt;
        refactorTypes.add(tp);
        owner = own;
        parentCodeBlock = parent;
        cmt.addCodeChange(this);
        own.addHistory(this);

        if(parent != null){
            CodeBlockTime parentTime = parent.getLastHistory();
            if(parentTime.getMethods() != null){
                parentTime.getMethods().add(own);
            }
        }
    }


//    public MethodTime(MethodTime methodTimeOld, CommitCodeChange commitTime, Operator tp) {//rename_method
//        name = methodTimeOld.name;
//        time = commitTime;
//        refactorType = tp;
//        owner = methodTimeOld.owner;
//        parentCodeBlock = methodTimeOld.parentCodeBlock;
//    }

    @Override
    public Object clone() {
        MethodTime methodTime = null;
        methodTime = (MethodTime) super.clone();
//        System.out.println("Method");
        return methodTime;
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
