package Constructor.Enums;

import Model.CodeBlock;
import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.SideLocation;
import Project.Utils.DiffFile;
import Project.Utils.EditLine;

import java.util.HashMap;
import java.util.List;

import static Constructor.Utils.*;

public enum LineChangeOperator {
    Change_Type_Declaration_Kind{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String[] kinds = getChangedTypeKind(r.getDescription());
            editLine.changeOneToken(kinds[0], kinds[1]);
        }
    },

    Add_Class_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.addModifier(modifier);
        }
    },

    Remove_Class_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            if (leftLine == -1 || rightLine == -1){
                System.out.println(leftLine + " and " + rightLine);
            }
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.removeModifier(modifier);
        }
    },

    Change_Class_Access_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String[] modifiers = getChangedModifier(r.getDescription());
            editLine.changeOneToken(modifiers[0], modifiers[1]);
        }
    },

    Add_Method_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.addModifier(modifier);

        }
    },

    Remove_Method_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.removeModifier(modifier);
        }
    },

    Change_Method_Access_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String[] modifiers = getChangedModifier(r.getDescription());
            editLine.changeOneToken(modifiers[0], modifiers[1]);
        }
    },

    Add_Thrown_Exception_Type{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("method declaration with added thrown exception type").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            for (SideLocation sideLocation : r.rightFilter("added thrown exception type")){
                String exceptionType = sideLocation.getCodeElement();
                editLine.addException(exceptionType);
            }
        }
    },

    Remove_Thrown_Exception_Type{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("method declaration with removed thrown exception type").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            for (SideLocation sideLocation : r.leftFilter("removed thrown exception type")){
                String exceptionType = sideLocation.getCodeElement();
                editLine.removeException(exceptionType);
            }
        }
    },

    Change_Thrown_Exception_Type{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("method declaration with changed thrown exception type").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            List<SideLocation> leftLocations = r.rightFilter("original exception type");
            List<SideLocation> rightLocations = r.rightFilter("changed exception type");
            for (SideLocation left : leftLocations){
                editLine.removeException(left.getCodeElement());
            }
            for (SideLocation right : rightLocations){
                editLine.addException(right.getCodeElement());
            }
        }
    },

    Rename_Parameter{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("method declaration with renamed variable").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            // parameterCodeElement like this : "r : Refactoring"
            String oldParameter = r.getLeftSideLocations().get(0).getCodeElement().split(" : ")[0];
            String newParameter = r.getRightSideLocations().get(0).getCodeElement().split(" : ")[0];
            editLine.changeOneToken(oldParameter, newParameter);
        }
    },

    Add_Parameter_Annotation {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("method declaration with added variable annotation").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            for (SideLocation sideLocation : r.rightFilter("added annotation")){
                String annotation = sideLocation.getCodeElement();
                editLine.addAnnotation(annotation);
            }
        }
    },

    Remove_Parameter_Annotation {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("method declaration with removed variable annotation").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            for (SideLocation sideLocation : r.leftFilter("removed annotation")){
                String annotation = sideLocation.getCodeElement();
                editLine.removeAnnotation(annotation);
            }
        }
    },

    Modify_Parameter_Annotation {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("method declaration with modified variable annotation").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            for (SideLocation sideLocation : r.leftFilter("original annotation")){
                String annotation = sideLocation.getCodeElement();
                editLine.removeAnnotation(annotation);
            }

            for (SideLocation sideLocation : r.leftFilter("modified annotation")){
                String annotation = sideLocation.getCodeElement();
                editLine.addAnnotation(annotation);
            }
        }
    },

    Add_Parameter_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original variable declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("variable declaration with added modifier").get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.addModifier(modifier);
        }
    },

    Remove_Parameter_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original variable declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("variable declaration with removed modifier").get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.removeModifier(modifier);
        }
    },

    Localize_Parameter{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {

            SideLocation leftLocation = r.leftFilter("original variable declaration").get(0);
            int leftLine = getFirstLine(r, mappings, leftLocation, 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("method declaration with renamed variable").get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            // parameterCodeElement like this : "r : Refactoring"
            String[] parameter = leftLocation.getCodeElement().split(" : ");
            editLine.removeParameter(parameter[0], parameter[1]);
        }
    },

    Reorder_Parameter{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("method declaration with reordered parameters").get(0), 'R');
            DiffFile diffFile = findFile(diffList, r.getRightSideLocations().get(0).getFilePath());
            assert diffFile != null;
            if(diffFile.notContainsAddEditLine(rightLine)){
                diffFile.putEditLine(leftLine, rightLine);
            }
        }
    },

    Add_Parameter {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation rightLocation = r.rightFilter("added parameter").get(0);
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, rightLocation, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            // parameterCodeElement like this : "r : Refactoring"
            String[] parameter = rightLocation.getCodeElement().split(" : ");
            editLine.addParameter(parameter[0], parameter[1]);
        }
    },

    Remove_Parameter {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation leftLocation = r.leftFilter("removed parameter").get(0);
            int leftLine = getFirstLine(r, mappings, leftLocation, 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            // parameterCodeElement like this : "r : Refactoring"
            String[] parameter = leftLocation.getCodeElement().split(" : ");
            editLine.removeParameter(parameter[0], parameter[1]);
        }
    },

    Split_Parameter {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {

            SideLocation originalParameter = r.leftFilter("original variable declaration").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("method declaration with split variable").get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String[] parameter = originalParameter.getCodeElement().split(" : ");
            editLine.removeParameter(parameter[0], parameter[1]);

            for (SideLocation splitParameter : r.rightFilter("split variable declaration")){
                parameter = splitParameter.getCodeElement().split(" : ");
                editLine.addParameter(parameter[0], parameter[1]);
            }
        }
    },

    Merge_Parameter {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            // 将多个合并为一个

            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("method declaration with merged variables").get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String[] parameter = r.rightFilter("new variable declaration").get(0).getCodeElement().split(" : ");
            editLine.addParameter(parameter[0], parameter[1]);

            for (SideLocation mergeParameter : r.leftFilter("merged variable declaration")){
                parameter = mergeParameter.getCodeElement().split(" : ");
                editLine.removeParameter(parameter[0], parameter[1]);
            }
        }
    },

    Parameterize_Variable{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            // 把变量转为函数参数，代码变更中相当于添加

            SideLocation rightLocation = r.rightFilter("renamed variable declaration").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("method declaration with renamed variable").get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            // parameterCodeElement like this : "r : Refactoring"
            String[] parameter = rightLocation.getCodeElement().split(" : ");
            editLine.addParameter(parameter[0], parameter[1]);
        }
    },

    Parameterize_Attribute{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            // 把属性转为函数参数，代码变更中相当于添加

            SideLocation rightLocation = r.rightFilter("renamed variable declaration").get(0);
            int leftLine = getFirstLine(r, mappings, r.leftFilter("original method declaration").get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.rightFilter("method declaration with renamed variable").get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            // parameterCodeElement like this : "r : Refactoring"
            String[] parameter = rightLocation.getCodeElement().split(" : ");
            editLine.addParameter(parameter[0], parameter[1]);
        }
    },

    Add_Attribute_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.addModifier(modifier);
        }
    },

    Remove_Attribute_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.removeModifier(modifier);
        }
    },

    Change_Attribute_Access_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String[] modifiers = getChangedModifier(r.getDescription());
            editLine.changeOneToken(modifiers[0], modifiers[1]);
        }
    },

    Add_Variable_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.addModifier(modifier);
        }
    },

    Remove_Variable_Modifier{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            int leftLine = getFirstLine(r, mappings, r.getLeftSideLocations().get(0), 'L');
            int rightLine = getFirstLine(r, mappings, r.getRightSideLocations().get(0), 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;
            String modifier = getModifier(r.getDescription());
            editLine.removeModifier(modifier);
        }
    },

    Change_Attribute_Type{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String leftType = left.getCodeElement().split(" : ")[1];
            String rightType = right.getCodeElement().split(" : ")[1];

            editLine.changeType(leftType, rightType);
        }
    },

    Change_Parameter_Type {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String leftType = left.getCodeElement().split(" : ")[1];
            String rightType = right.getCodeElement().split(" : ")[1];

            editLine.changeType(leftType, rightType);
        }
    },

    Change_Return_Type {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String leftType = left.getCodeElement();
            String rightType = right.getCodeElement();

            editLine.changeType(leftType, rightType);
        }
    },

    Change_Variable_Type{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String leftType = left.getCodeElement().split(" : ")[1];
            String rightType = right.getCodeElement().split(" : ")[1];

            editLine.changeType(leftType, rightType);
        }
    },

    Rename_Class {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String oldName = toRoot(left.getCodeElement());
            String newName = toRoot(right.getCodeElement());

            editLine.changeOneToken(oldName, newName);
        }
    },

    Rename_Method {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String oldName = getMethodName(left.getCodeElement());
            String newName = getMethodName(right.getCodeElement());

            editLine.changeOneToken(oldName, newName);
        }
    },

    Rename_Attribute {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');
            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String oldName = left.getCodeElement().split(" : ")[0];
            String newName = right.getCodeElement().split(" : ")[0];

            editLine.changeOneToken(oldName, newName);
        }
    },

    Rename_Variable{
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');

            EditLine editLine;
            if (!r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath())){
                editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            } else {
                editLine = putEditLine(diffList, r, leftLine, rightLine);
            }
            if (editLine == null) return;

            String oldName = left.getCodeElement().split(" : ")[0];
            String newName = right.getCodeElement().split(" : ")[0];

            editLine.changeOneToken(oldName, newName);
        }
    },

    Move_And_Rename_Class {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');

            EditLine editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            if (editLine == null) return;

            String oldName = toRoot(left.getCodeElement());
            String newName = toRoot(right.getCodeElement());

            editLine.changeOneToken(oldName, newName);
        }
    },

    Move_And_Rename_Method {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');

            EditLine editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            if (editLine == null) return;

            String oldName = getMethodName(left.getCodeElement());
            String newName = getMethodName(right.getCodeElement());

            editLine.changeOneToken(oldName, newName);
        }
    },

    Move_And_Rename_Attribute {
        @Override
        public void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception {
            SideLocation left = r.getLeftSideLocations().get(0);
            SideLocation right = r.getRightSideLocations().get(0);

            int leftLine = getFirstLine(r, mappings, left, 'L');
            int rightLine = getFirstLine(r, mappings, right, 'R');

            EditLine editLine = putMoveEditLine(diffList, r, leftLine, rightLine);
            if (editLine == null) return;

            String oldName = left.getCodeElement().split(" : ")[0];
            String newName = right.getCodeElement().split(" : ")[0];

            editLine.changeOneToken(oldName, newName);
        }
    },

    ;

    public abstract void apply(HashMap<String, CodeBlock> mappings, Refactoring r, List<DiffFile> diffList) throws Exception;

    public String getModifier(String description){
        String[] tmp = description.split(" ");
        int i;
        for(i=0; i<tmp.length; i++){
            if(tmp[i].equals("Modifier"))
                break;
        }

        if (tmp.length <= i+1){
            return "";
        }

        return tmp[i+1];
    }

    public String[] getChangedModifier(String description){
        String[] tmp = description.split(" ");
        int i;
        for(i=0; i<tmp.length; i++){
            if(tmp[i].equals("Modifier"))
                break;
        }

        if (tmp.length <= i+3){
            return new String[]{"", ""};
        }

        return new String[]{tmp[i+1], tmp[i+3]};
    }

    public String[] getChangedTypeKind(String description){
        String[] tmp = description.split(" ");
        int i;
        for(i=0; i<tmp.length; i++){
            if(tmp[i].equals("Kind"))
                break;
        }

        if (tmp.length <= i+3){
            return new String[]{"", ""};
        }

        return new String[]{tmp[i+1], tmp[i+3]};
    }


    public String toRoot(String name){
        if (!name.contains(".")){
            return name;
        } else {
            return name.substring(name.lastIndexOf(".")+1);
        }
    }

    public String getMethodName(String codeElement){
        return codeElement.substring(codeElement.substring(0, codeElement.indexOf("(")).lastIndexOf(" ")+1, codeElement.indexOf("("));
    }

    public EditLine putMoveEditLine(List<DiffFile> diffList, Refactoring r, int oldLineNum, int newLineNum){
        DiffFile oldDiffFile = findOldFile(diffList, r.getLeftSideLocations().get(0).getFilePath());
        DiffFile newDiffFile = findFile(diffList, r.getRightSideLocations().get(0).getFilePath());

        assert oldDiffFile != null;
        assert newDiffFile != null;
        if(newDiffFile.notContainsAddEditLine(newLineNum)){
            String[] oldLines = oldDiffFile.getOldContent().split("\n");
            String[] newLines = newDiffFile.getContent().split("\n");

            if (oldLineNum-1 < 0 || oldLineNum-1 >= oldLines.length || newLineNum-1 < 0 || newLineNum-1 > newLines.length){
                return null;
            }

            EditLine editLine = new EditLine(oldLineNum, newLineNum, oldLines[oldLineNum-1], newLines[newLineNum-1]);
            oldDiffFile.putMoveFromEditLine(oldLineNum, editLine);
            newDiffFile.putMoveInEditLine(newLineNum, editLine);
        }

        return newDiffFile.getAddEditLine(newLineNum);
    }

    public EditLine putEditLine(List<DiffFile> diffList, Refactoring r, int leftLine, int rightLine){
        DiffFile diffFile = findFile(diffList, r.getRightSideLocations().get(0).getFilePath());
        assert diffFile != null;
        if(diffFile.notContainsAddEditLine(rightLine)){
            diffFile.putEditLine(leftLine, rightLine);
        }
        return diffFile.getAddEditLine(rightLine);
    }

}