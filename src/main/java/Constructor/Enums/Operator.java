package Constructor.Enums;


import Model.*;
import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.SideLocation;
import Project.Utils.DiffFile;
import Project.Utils.EditLine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static Constructor.Utils.cutString;
import static Constructor.Utils.*;

public enum Operator {
    Add_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            //create new codeBlock, update mapping, commitCodeChange
            CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Package);
            mappings.put(name, codeBlock);
            PackageTime packageTime = new PackageTime(name, commitTime, Operator.Add_Package, codeBlock);
            codeBlocks.add(codeBlock);
        }
    },
    Remove_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Add_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String signature, List<DiffFile> diffList) throws Exception {
            //create codeblock, create codeblocktime, mapping update
            String name = sig2Name(signature);
            String fatherSig = sig2Father(signature);
            if (mappings.containsKey(fatherSig)) {
                CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                ClassTime classTime = new ClassTime(name, commitTime, Operator.Add_Class, codeBlock, mappings.get(fatherSig));
                mappings.put(signature, codeBlock);
                codeBlocks.add(codeBlock);
            }
        }
    },
    Remove_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            //use mapping to find codeblock, create codeblocktime,
        }
    },
    Add_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Remove_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Add_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Remove_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Rename_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Move_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
        }
    },
    Split_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Merge_Package {
        //move classes from old package to new package, update mappings for class, method, etc
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {

        }
    },
    Change_Type_Declaration_Kind {//interface class, if the name should change, just update the name, no other changes

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Type_Declaration_Kind.apply(mappings, r, diffList);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Collapse_Hierarchy {

        //todo
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Extract_Superclass {
        //add a new class, the last filepath on the rightfilepath is the new superclass
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original sub-type declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("sub-type declaration after extraction"), 'R');
            refactoredLine(diffList, r.rightFilter("extracted super-type declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Interface {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
           refactoredLine(diffList, r.rightFilter("extracted super-type declaration"), 'R');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original sub-type declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("sub-type declaration after extraction"), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Class {

        @Override
        //create new classBlock, move classes & methods & attributes from old class to new class
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.R);
            List<SideLocation> right1 = r.rightFilter("type declaration after extraction");
            List<SideLocation> right2 = r.rightFilter("extracted type declaration");
            refactoredLine(mappings, diffList, right1, 'R', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, right2, 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Extract_Subclass {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.R);
            List<SideLocation> right1 = r.rightFilter("type declaration after extraction");
            List<SideLocation> right2 = r.rightFilter("extracted type declaration");
            refactoredLine(mappings, diffList, right1, 'R', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, right2, 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Merge_Class {//merge methods & attributes in two or more classes to one new class

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Move_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Rename_Class {
        @Override
        // update class name, update mappings of methods, attributes, etc.
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Rename_Class.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
//            System.out.println(commitTime.getCommitID());
        }
    },
    Move_And_Rename_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Move_And_Rename_Class.apply(mappings, r, diffList);

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("extracted code from source method declaration");
            List<SideLocation> right = r.rightFilter("extracted code to extracted method declaration");
            List<SideLocation> right2 = r.rightFilter("extracted method invocation");
            List<SideLocation> right3 = r.rightFilter("extracted method declaration");
            List<SideLocation> addedStatement = r.rightFilter("added statement in extracted method declaration");
            refactoredLine(mappings, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, right, 'R', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, right2, 'R', OpeTypeEnum.R);
            refactorFirstLine(r, mappings, diffList, right3, 'R');
            refactoredLine(mappings, diffList, addedStatement, 'R', OpeTypeEnum.A);
            System.out.println(r.getType());
        }
    },
    Inline_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("inlined code from inlined method declaration");
            List<SideLocation> right = r.rightFilter("inlined code in target method declaration");

            refactoredLine(mappings, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, right, 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Pull_Up_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Push_Down_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Extract_And_Move_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("extracted code from source method declaration");
            List<SideLocation> right = r.rightFilter("extracted code to extracted method declaration");
            List<SideLocation> right2 = r.rightFilter("extracted method invocation");
            List<SideLocation> right3 = r.rightFilter("extracted method declaration");
            List<SideLocation> addedStatement = r.rightFilter("added statement in extracted method declaration");
            refactoredLine(mappings, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, right, 'R', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, right2, 'R', OpeTypeEnum.R);
            refactorFirstLine(r, mappings, diffList, right3, 'R');
            refactoredLine(mappings, diffList, addedStatement, 'R', OpeTypeEnum.A);

            System.out.println(r.getType());
        }
    },
    Move_And_Inline_Method {// move & inline(inverse of extract method)

        //move inlined method from old class to new class, then inline method to target method, in new class.
        // left.get(0) is the declaration of inlined method, left.get(right.size()) is the declaration of target method before inline.
        //right.get(0) is the declaration of target method after inline
        //move inline method, inline to targe method
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> right = r.getRightSideLocations();

            List<SideLocation> left1 = r.leftFilter("inlined code from inlined method declaration");
            List<SideLocation> left2 = r.leftFilter("deleted statement in inlined method declaration");
            right = r.rightFilter("inlined code in target method declaration");
            refactoredLine(mappings, diffList, left1, 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, left2, 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, right, 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Move_And_Rename_Method {

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Move_And_Rename_Method.apply(mappings, r, diffList);

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Move_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Change_Return_Type {

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Return_Type.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original return type"), 'L');
            refactoredLine(diffList, r.rightFilter("changed return type"), 'R');
            System.out.println(r.getType());
        }
    },
    Rename_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Rename_Method.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Parameterize_Variable {

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Parameterize_Variable.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("method declaration with renamed variable"), 'R');
            refactoredLine(mappings, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("renamed variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Merge_Parameter {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Merge_Parameter.apply(mappings, r, diffList);

            refactoredLine(mappings, diffList, r.leftFilter("merged variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("new variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Split_Parameter {//method name change, parameterList change
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Split_Parameter.apply(mappings, r, diffList);

            refactoredLine(mappings, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("split variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Change_Parameter_Type {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Parameter_Type.apply(mappings, r, diffList);

            refactoredLine(mappings, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("changed-type variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Add_Parameter {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Add_Parameter.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("added parameter"), 'R');
            System.out.println(r.getType());
        }
    },
    Remove_Parameter {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Remove_Parameter.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("removed parameter"), 'L');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("method declaration with removed parameter"), 'R');
            System.out.println(r.getType());
        }
    },
    Reorder_Parameter {// only change method name & method parameterList, parameterType
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Reorder_Parameter.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original parameter declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("reordered parameter declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Parameterize_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Parameterize_Attribute.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Pull_Up_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Push_Down_Attribute {// move attribute from father class to son class, normally cross class files. ≈move attribute

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Move_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Rename_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Rename_Attribute.apply(mappings, r, diffList);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Merge_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Split_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Change_Attribute_Type {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Attribute_Type.apply(mappings, r, diffList);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Encapsulate_Attribute {
        @Override
        //Attribute encapsulation is useful when you have an attribute that is affected by several different methods,
        // each of which needs that attribute to be in a known state. To prevent programmers from changing the attribute
        // in the 4GL code, you can make the attribute private so that programmers can only access it from the object's methods.
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            //add new method
            String className = r.getLastClassName();
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Inline_Attribute {//remove_attribute
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Move_And_Rename_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Move_And_Rename_Attribute.apply(mappings, r, diffList);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Replace_Attribute {
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Replace_Attribute_With_Variable {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("original variable declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Replace_Anonymous_With_Lambda {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.leftFilter("anonymous class declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("lambda expression"), 'R');
            System.out.println(r.getType());
        }
    },

    Add_Class_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> right = r.rightFilter("added annotation");
            refactoredLine(diffList, right, 'R');
        }
    },
    Remove_Class_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("removed annotation");
            refactoredLine(diffList, left, 'L');
        }
    },
    Modify_Class_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> right = r.rightFilter("modified annotation");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },

    Add_Class_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Add_Class_Modifier.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Class_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Remove_Class_Modifier.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Class_Access_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Class_Access_Modifier.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Method_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> right = r.rightFilter("added annotation");
            refactoredLine(diffList, right, 'R');
        }
    },
    Remove_Method_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("removed annotation");
            refactoredLine(diffList, left, 'L');
        }
    },
    Modify_Method_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> right = r.rightFilter("modified annotation");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Add_Method_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Add_Method_Modifier.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Method_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Remove_Method_Modifier.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Method_Access_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Method_Access_Modifier.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Thrown_Exception_Type{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Add_Thrown_Exception_Type.apply(mappings, r, diffList);

            List<SideLocation> right = r.rightFilter("added thrown exception type");
            refactoredLine(diffList, right, 'R');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("method declaration with added thrown exception type"), 'R');
        }
    },
    Remove_Thrown_Exception_Type{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Remove_Thrown_Exception_Type.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("removed thrown exception type"), 'L');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("method declaration with removed thrown exception type"), 'R');
        }
    },
    Change_Thrown_Exception_Type{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Thrown_Exception_Type.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original exception type"), 'L');
            refactoredLine(diffList, r.rightFilter("changed exception type"), 'R');
        }
    },
    Rename_Parameter{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Rename_Parameter.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Add_Parameter_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Add_Parameter_Annotation.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with added annotation"), 'R');
            refactoredLine(diffList, r.rightFilter("added annotation"), 'R');
        }
    },
    Remove_Parameter_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Remove_Parameter_Annotation.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("removed annotation"), 'L');
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with removed annotation"), 'R');
        }
    },
    Modify_Parameter_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Modify_Parameter_Annotation.apply(mappings, r, diffList);

            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> left2 = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("modified annotation");
            List<SideLocation> right2 = r.rightFilter("variable declaration with modified annotation");
            refactoredLine(diffList,left, 'L');
            refactoredLine(diffList,left2, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, right2, 'R');
        }
    },
    Add_Parameter_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Add_Parameter_Modifier.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with added modifier"), 'R');
        }
    },
    Remove_Parameter_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Remove_Parameter_Modifier.apply(mappings, r, diffList);

            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with removed modifier"), 'R');
        }
    },
    Localize_Parameter{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Localize_Parameter.apply(mappings, r, diffList);

            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("method declaration with renamed variable"), 'R');
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Add_Attribute_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Attribute_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Modify_Attribute_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Attribute_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Add_Attribute_Modifier.apply(mappings, r, diffList);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Attribute_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Remove_Attribute_Modifier.apply(mappings, r, diffList);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Attribute_Access_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            LineChangeOperator.Change_Attribute_Access_Modifier.apply(mappings, r, diffList);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },

    Extract_Variable{
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList){
            List<SideLocation> left = r.leftFilter("statement with the initializer of the extracted variable");
            List<SideLocation> right = r.rightFilter("extracted variable declaration");
            List<SideLocation> right2 = r.rightFilter("statement with the name of the extracted variable");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, right2, 'R');

        }
    },
    Change_Variable_Type{
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("changed-type variable declaration");

            LineChangeOperator.Change_Variable_Type.apply(mappings, r, diffList);

            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Inline_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("inlined variable declaration");
            List<SideLocation> left2 = r.leftFilter("statement with the name of the inlined variable");
            List<SideLocation> right = r.rightFilter("statement with the initializer of the inlined variable");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, left2, 'L');
            refactoredLine(diffList, right, 'R');

        }
    },
    Rename_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("renamed variable declaration");

            LineChangeOperator.Rename_Variable.apply(mappings, r, diffList);

            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Merge_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("merged variable declaration");
            List<SideLocation> right = r.rightFilter("new variable declaration");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');

        }
    },
    Split_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("split variable declaration");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Add_Variable_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("added annotation");
            List<SideLocation> right2 = r.rightFilter("variable declaration with added annotation");
            assert right2!=null;
            refactoredLine(diffList,left, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, right2, 'R');
        }
    },
    Remove_Variable_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("removed annotation");
            List<SideLocation> left2 = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with removed annotation");
            refactoredLine(diffList,left, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, left2, 'L');
        }
    },
    Modify_Variable_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> left2 = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("modified annotation");
            List<SideLocation> right2 = r.rightFilter("variable declaration with modified annotation");
            refactoredLine(diffList,left, 'L');
            refactoredLine(diffList,left2, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, right2, 'R');
        }
    },
    Add_Variable_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with added modifier");

            LineChangeOperator.Add_Variable_Modifier.apply(mappings, r, diffList);

            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Remove_Variable_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with removed modifier");

            LineChangeOperator.Remove_Variable_Modifier.apply(mappings, r, diffList);

            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Replace_Variable_With_Attribute{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Replace_Loop_With_Pipeline{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception {
            List<SideLocation> left = r.leftFilter("original code");
            List<SideLocation> right = r.rightFilter("pipeline code");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    }
    ;
    public abstract void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) throws Exception;

}
