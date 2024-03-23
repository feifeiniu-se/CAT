package Model;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.Operator;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ClassTime extends CodeBlockTime {
    Set<CodeBlock> classes = new HashSet<>();
    Set<CodeBlock> methods = new HashSet<>();
    Set<CodeBlock> attributes = new HashSet<>();

//    public ClassTime(String sig, String path, CommitCodeChange cmt, Operator tp, CodeBlock own, CodeBlock parent){
//        if(!own.getHistory().isEmpty()){
//            ClassTime pre = (ClassTime) own.getLastHistory();
//            deriver = pre.getDeriver();
//            derivee = pre.getDerivee();
//        }
//        signature = sig;
//
//        time = cmt;
//        refactorType = tp;
//        owner = own;
//        parentCodeBlock = parent;
//        own.addHistory(this);
//        cmt.addCodeChange(this);
//    }

    public ClassTime(String name, CommitCodeChange cmt, Operator tp, CodeBlock own, CodeBlock parent) {//add new classBlock
        this.name = name;
        time = cmt;
        refactorTypes.add(tp);
        parentCodeBlock = parent;
        owner = own;
        own.addHistory(this);
        cmt.addCodeChange(this);

        if (parent != null){
            CodeBlockTime parentTime = parent.getLastHistory();
            if (parentTime != null){
                if(parentTime.getClasses() != null){
                    parentTime.getClasses().add(own);
                }
            }
        }
    }

    @Override
    public Set<CodeBlock> getClasses(){return classes;}

    @Override
    public Set<CodeBlock> getMethods(){return methods;}

    @Override
    public Set<CodeBlock> getAttributes(){return attributes;}

    public Set<String> getFilePath() {
        return null;
    }

    @Override
    public String getSignature() {
        return this.getParentCodeBlock().getLastHistory().getSignature()+"."+this.getName();
    }

    @Override
    public Set<CodeBlock> getPackages() {
        return null;
    }

    @Override
    public Object clone() {
        ClassTime classTime = null;
        classTime = (ClassTime) super.clone();
        classTime.setClasses(new HashSet<>(classes));
        classTime.setMethods(new HashSet<>(methods));
        classTime.setAttributes(new HashSet<>(attributes));
        return classTime;
    }
    @Override
    public boolean equals(Object o){
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
