package Model;

import Constructor.Enums.Operator;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class PackageTime extends CodeBlockTime implements Cloneable, Serializable {
    Set<CodeBlock> classes = new HashSet<>();
    Set<CodeBlock> packages = new HashSet<>();

    public PackageTime(String name, CommitCodeChange commitTime, Operator type, CodeBlock own) {// add new package
        this.name = name;
        time = commitTime;
        refactorTypes.add(type);
        owner = own;
        own.addHistory(this);
        commitTime.addCodeChange(this);
    }

    @Override
    public Object clone() {
        PackageTime packageTime = null;
        packageTime = (PackageTime) super.clone();
        packageTime.setClasses(new HashSet<>(classes));
        return packageTime;
    }

    @Override
    public Set<CodeBlock> getClasses() {
        return classes;
    }


    @Override
    public String getSignature() {
        return this.getName();
    }

    @Override
    public Set<CodeBlock> getPackages() {
        return packages;
    }

    @Override
    public Set<CodeBlock> getMethods() {
        return null;
    }

    @Override
    public Set<CodeBlock> getAttributes() {
        return null;
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
