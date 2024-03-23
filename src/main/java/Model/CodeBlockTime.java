package Model;

import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public abstract class CodeBlockTime implements Cloneable, Serializable {
    String name;
    CommitCodeChange time;
    CodeBlockTime pre = null;
    CodeBlockTime post = null;
    List<Operator> refactorTypes = new ArrayList<>();
    CodeBlock parentCodeBlock;
    CodeBlock owner;

    boolean isMoved = false;

    int oldStartLineNum = -1;
    int oldEndLineNum = -1;
    int newStartLineNum = -1;
    int newEndLineNum = -1;
    int oldDeclarationLineNum=-1;
    int newDeclarationLineNum=-1;
    HashMap<Integer, OpeTypeEnum> oldChangeLines = new HashMap<>();
    HashMap<Integer, OpeTypeEnum> newChangeLines = new HashMap<>();

    public abstract String getSignature();
    public abstract Set<CodeBlock> getPackages();
    public abstract Set<CodeBlock> getClasses();
    public abstract Set<CodeBlock> getMethods();
    public abstract Set<CodeBlock> getAttributes();

    @Override
    public Object clone() {
        CodeBlockTime codeBlockTime = null;
        try {
            codeBlockTime = (CodeBlockTime) super.clone();
            codeBlockTime.setOldChangeLines(new HashMap<>());
            codeBlockTime.setNewChangeLines(new HashMap<>());
            codeBlockTime.setRefactorTypes(new ArrayList<>());
            codeBlockTime.setOldStartLineNum(newStartLineNum);
            codeBlockTime.setOldEndLineNum(newEndLineNum);
            codeBlockTime.setOldDeclarationLineNum(newDeclarationLineNum);
            codeBlockTime.setNewStartLineNum(-1);
            codeBlockTime.setNewEndLineNum(-1);
            codeBlockTime.setNewDeclarationLineNum(-1);
            codeBlockTime.setMoved(false);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return codeBlockTime;
    }

    public Boolean containsNew(Integer i){
        return i>=newStartLineNum&&i<=newEndLineNum;
    }

    public Boolean containsOld(Integer i){
        return i>=oldStartLineNum&&i<=oldEndLineNum;
    }

    public void addRefactorType(Operator refactorType){
        this.refactorTypes.add(refactorType);
    }

    @Override
    public boolean equals(Object o){
        return this == o;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time.commitID, owner.codeBlockID);
    }
}
