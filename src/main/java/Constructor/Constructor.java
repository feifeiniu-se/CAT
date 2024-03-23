package Constructor;

import Constructor.Enums.FileType;
import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import Constructor.Visitors.*;
import Model.*;
import Project.ChangedLine.ChangedLabelLine;
import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.Refactorings;
import Project.Utils.CommitHashCode;
import Project.Utils.DiffFile;
import Project.Utils.Metric;
import Project.Utils.RefactoringLine;
import lombok.Data;
import Project.Project;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Constructor {
    private static final Logger logger = Logger.getLogger(Constructor.class);

    Project project;
    List<CodeBlock> codeBlocks = new ArrayList<>();
    List<CommitCodeChange> codeChange = new ArrayList<>();
    HashMap<String, CodeBlock> mappings = new HashMap<>();// mapping between signature and codeBlockID
    HashMap<RefactoringLine, RefactoringLine> refactoringLineHashMap = new HashMap<>();

    public Constructor(Project p) {
        project = p;
    }


    public void start(){
        List<CommitHashCode> commitList = project.getCommitList();
        for(CommitHashCode hashCode: commitList){
//            System.out.println(codeBlocks.size());
//            System.out.println(mappings.size());
            System.out.println("Commit: "+hashCode.getHashCode());
            //add a new commitTime for each commit, for the code change during this commit
            CommitCodeChange commitTime = new CommitCodeChange(hashCode.getHashCode());
            if(codeChange.size()>0){
                commitTime.setPreCommit(codeChange.get(codeChange.size()-1));
                codeChange.get(codeChange.size()-1).setPostCommit(commitTime);
            }else{
                commitTime.setPreCommit(null);
            }
            codeChange.add(commitTime);

            //go through all the files and refactorings
            HashMap<String, DiffFile> originalFileList =  project.getDiffList(hashCode);
            if(originalFileList==null){continue;}//no file changes during this commit
            Map<String, DiffFile> fileList = originalFileList.entrySet().stream()
                    .filter(p -> !FileType.DELETE.equals(p.getValue().getType()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
            List<DiffFile> diffList = originalFileList.values().stream().toList();

            //if refactoring is not null, separate them into three levels: package, class, method&attribute
            Refactorings refact = project.getRefactorings().get(hashCode.getHashCode());

            Map<String, String> fileContents = fileList.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().getContent()));

            RefactoringParser refactoringParser = new RefactoringParser();

            Map<String, String> renameCodeBlockName;
            if (commitTime.getPreCommit() != null && refact != null){
                renameCodeBlockName = refactoringParser.parse(refact.filter("rename"), mappings, hashCode.getHashCode());
            } else {
                renameCodeBlockName = new HashMap<>();
            }

//            MethodCallParser methodCallParser = new MethodCallParser();
//            methodCallParser.visit(mappings, fileContents, project.getProjectAddress(), fileList);


            Visitor visitor = new Visitor();
            visitor.visit(fileContents, codeBlocks, codeChange, mappings, fileList, renameCodeBlockName);


            if (commitTime.getPreCommit() != null){

                Map<String, DiffFile> oldFileList = originalFileList.entrySet().stream()
                        .filter(p -> !FileType.ADD.equals(p.getValue().getType()))
                        .collect(Collectors.toMap(entry -> entry.getValue().getOldPath(), Map.Entry::getValue, (v1, v2) -> v1));

                Map<String, String> oldFileContents = oldFileList.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().getOldContent()));

                DeleteVisitor deleteVisitor = new DeleteVisitor();
                deleteVisitor.visit(oldFileContents, codeChange, mappings, oldFileList);
            }



            RenameInfluenceProcessor renameInfluenceProcessor = new RenameInfluenceProcessor(renameCodeBlockName);

            project.resetCommit(hashCode.getHashCode());
            RenameInfluenceVisitor renameInfluenceVisitor = new RenameInfluenceVisitor();
            renameInfluenceVisitor.setHashCode(hashCode.getHashCode());
            renameInfluenceVisitor.setRenameInfluenceProcessor(renameInfluenceProcessor);
            renameInfluenceVisitor.visit(fileContents, project.getProjectAddress(), fileList);

            Metric metric = new Metric();
            ChangedLabelLine changedLabelLine = new ChangedLabelLine(project.getName(), commitTime.getCommitID());
            if (refact != null && commitTime.getPreCommit() != null) {
                if (!refact.getRefactorings().isEmpty()) {
                    List<Refactoring> refactorings = refact.getRefactorings();
                    if (!refactorings.isEmpty()) {
                        for(Refactoring r: refactorings){
                            metric.refactCount += 1;
                            metric.typeCount.add(r.getType());
                            try {
                                Operator.valueOf(r.getType().replace(" ", "_")).apply(codeBlocks, mappings, r, commitTime, null, diffList);
                            } catch (Exception e){
                                logger.error("CommitHashCode : " + hashCode.getHashCode() + ", " + "RefactoringDescription : " + r.getDescription(), e);
                            }
                        }
                    }
                }
            }


            for(DiffFile dfl: diffList){
                dfl.label(metric, changedLabelLine);
            }
            project.getMetrics().put(hashCode.getHashCode(), metric.count());
            project.getEntropy().put(hashCode.getHashCode(), compute_entropy(diffList));//calculate entropy
            project.getChangedLines().addChangeLine(changedLabelLine);
        }
        project.resetCommit(project.getOriginalHash());
        project.save();
        project.save_entropy();
        project.save_changeLine();
    }

    public RefactoringLine getFirstAddLine(RefactoringLine line){
        return null;
    }

    public String compute_entropy(List<DiffFile> diffList){
        String res = "";

        List<Integer> methods_new = new ArrayList<>();
        List<Integer> class_new = new ArrayList<>();
        List<Integer> methods_old = new ArrayList<>();
        List<Integer> class_old = new ArrayList<>();
        for(DiffFile dfl: diffList){
            if(dfl.getTimes()!=null){
                for(CodeBlockTime c: dfl.getTimes()){
                    int count = 0;
                    if(c instanceof MethodTime||c instanceof ClassTime){
                        for(Map.Entry<Integer, OpeTypeEnum> entry: c.getNewChangeLines().entrySet()){
                            if(dfl.getCodeChangeLineNum().contains(entry.getKey())){
                                count++;
                            }
                        }
                        if(count==0){
                            continue;
                        }
                        if(c instanceof MethodTime){
                            methods_new.add(count);
                        }else {
                            class_new.add(count);
                        }
                    }
                }
            }

            if(dfl.getTimes()!=null){
                for(CodeBlockTime c: dfl.getTimes()){
                    int count = 0;
                    if(c instanceof MethodTime||c instanceof ClassTime){
                        for(Map.Entry<Integer, OpeTypeEnum> entry: c.getOldChangeLines().entrySet()){
                            if(dfl.getOldCodeChangeLineNum().contains(entry.getKey())){
                                count++;
                            }
                        }
                        if(count==0){
                            continue;
                        }
                        if(c instanceof MethodTime){
                            methods_old.add(count);
                        }else{
                            class_old.add(count);
                        }
                    }
                }
            }
        }
        res = res + calculate_entropy(class_new) +";"+ calculate_entropy(class_old) +";"+ calculate_entropy(methods_new) +";"+ calculate_entropy(methods_old);
        return res;
    }

    private double calculate_entropy(List<Integer> counts){
        double entropy  = 0;
        if(counts.size()==0){
            return 0;
        }
        int totalLOCModified = counts.stream().reduce(Integer::sum).orElse(0);
        if(totalLOCModified==0){
            return 0;
        }
        for(Integer c: counts){
            double avg = (double)c/(double)totalLOCModified;
            entropy -= (avg*Math.log(avg)/Math.log(2));
        }
        return entropy;
    }


}
