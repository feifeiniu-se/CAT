package Project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import Constructor.Enums.FileType;
import Project.ChangedLine.ChangedLines;
import Project.RefactoringMiner.Commits;
import Project.RefactoringMiner.Refactorings;
import Project.Utils.CommitHashCode;
import Project.Utils.DiffFile;
import Project.Utils.GenerateJsonFiles;
import com.google.gson.*;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import static Project.Utils.ReadJsonFiles.readFile;

@Data
public class Project {
    String name;
    String startHash;
    String endHash;
    String originalHash;
    String projectAddress;
    String refactoringMinerAddress;
    HashMap<String, Refactorings> refactorings;
    List<CommitHashCode> commitList;
    private static GitService gitService = new GitServiceImpl();

    HashMap<String, String> metrics;
    HashMap<String, String> entropy;
    String savePath;
    String filePathSplitWord;

    ChangedLines changedLines;


    boolean jsonUsed = false;


    public Project(String[] info, boolean jsonUsed, String jsonAddress) {
        projectAddress = info[0];

        if (projectAddress.contains("/")){
            filePathSplitWord = "/";
        } else {
            filePathSplitWord = "\\";
        }

        name = projectAddress.substring(projectAddress.lastIndexOf(filePathSplitWord)+1);
        startHash = info[1];
        endHash = info[2];
        refactoringMinerAddress = ".\\" + name + ".json";
        if(jsonUsed) {
            this.jsonUsed = true;
            refactoringMinerAddress = jsonAddress;
        }
        metrics = new HashMap<>();
        entropy = new HashMap<>();
        savePath = "";
        changedLines = new ChangedLines();
    }

    public void run(){
        commitList = getList();
        refactorings = readRefactoring();
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    private String existFileRename(String filePath){
        int tag = 2;
        File file = new File(filePath);
        while (file.exists()){
            filePath = filePath.replace(name + (tag - 1), name+tag);
            file = new File(filePath);
            tag++;
        }
        return filePath;
    }

    public void save(){
        if (savePath.isEmpty()) return;
        String filePath;
        filePath = savePath + filePathSplitWord + name+ ".csv";

        filePath = existFileRename(filePath);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
            String[] types = {"project","commit_hash","R_I", "R_T",
                    "A_C", "A_M_C", "A_R_C", "A_I_C", "A_E_C", "A_R_I_C", "A_R_E_C", "A_I_E_C", "A_R_I_E_C",
                    "D_C", "D_M_C", "D_R_C", "D_I_C", "D_E_C", "D_R_I_C", "D_R_E_C", "D_I_E_C", "D_R_I_E_C",
                    "A_C_PC", "A_M_C_PC", "A_R_C_PC", "A_I_C_PC", "A_E_C_PC", "A_R_I_C_PC", "A_R_E_C_PC", "A_I_E_C_PC", "A_R_I_E_C_PC",
                    "D_C_PC", "D_M_C_PC", "D_R_C_PC", "D_I_C_PC", "D_E_C_PC", "D_R_I_C_PC", "D_R_E_C_PC", "D_I_E_C_PC", "D_R_I_E_C_PC",
                    "A_C_PM", "A_M_C_PM", "A_R_C_PM", "A_I_C_PM", "A_E_C_PM", "A_R_I_C_PM", "A_R_E_C_PM", "A_I_E_C_PM", "A_R_I_E_C_PM",
                    "D_C_PM", "D_M_C_PM", "D_R_C_PM", "D_I_C_PM", "D_E_C_PM", "D_R_I_C_PM", "D_R_E_C_PM", "D_I_E_C_PM", "D_R_I_E_C_PM",
                    "A_A", "A_D", "A_R", "A_M", "A_E", "A_I",
                    "A_R_I", "A_R_E", "A_I_E", "A_R_I_E",
                    "C_A", "C_D", "C_R", "C_M", "C_E", "C_I",
                    "M_A", "M_D", "M_R", "M_M", "M_E", "M_I"
            };

            for (String type : types){
                writer.write(type);
                writer.write(",");
            }
            writer.newLine();

            for(Map.Entry<String, String> entry: metrics.entrySet()){
                writer.write(name);
                writer.write(",");
                writer.write(entry.getKey());
                writer.write(",");
                String[] values = entry.getValue().split("\\s|\\;");
                for(String i: values){
                    if(!i.isEmpty()){
                        writer.write(i);
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void save_entropy(){
        if (savePath.isEmpty()) return;
        String filePath;
        filePath = savePath + filePathSplitWord + "entropy_" + name+ ".csv";

        filePath = existFileRename(filePath);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));

            String[] types = {"commit_hash","Entropy_CA", "Entropy_CD", "Entropy_MA", "Entropy_MD"};

            for (String type : types){
                writer.write(type);
                writer.write(",");
            }
            writer.newLine();

            for(Map.Entry<String, String> entry: entropy.entrySet()){
                writer.write(entry.getKey());
                writer.write(",");
                String[] values = entry.getValue().split("\\;");
                for(String i: values){
                    if(!i.isEmpty()){
                        writer.write(i);
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save_changeLine(){
        if (savePath.isEmpty()) return;

        String filePath;
        filePath = savePath + filePathSplitWord + "change_line_" + name+ ".json";
        filePath = existFileRename(filePath);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();;
        String json = gson.toJson(changedLines);

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //return the list of commit hashcode between startHash and endHash
    public List<CommitHashCode> getList() {
        //done
        List<CommitHashCode> commits = new ArrayList<>();
        commits.add(new CommitHashCode(null, startHash));
        String last = startHash;
        try (Repository repo = gitService.openRepository(projectAddress); Git git = Git.open(new File(projectAddress))) {
            ObjectId head = repo.resolve("HEAD");
            RevWalk revWalk = new RevWalk(repo);
            RevCommit commit = revWalk.parseCommit(head);
            originalHash = commit.getName();

            Iterable<RevCommit> walk = gitService.createRevsWalkBetweenCommits(repo, startHash, endHash);
            for (RevCommit currentCommit : walk) {
                RevCommit[] parents = currentCommit.getParents();
                if (parents.length > 0) {
                    commits.add(new CommitHashCode(parents[0].getName(), currentCommit.getName()));
                } else {
                    commits.add(new CommitHashCode(null, currentCommit.getName()));
                }
            }
//
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commits;
    }

    // return the source code of project after one commit: hashCode
    public Map<String, String> getSourceCode(String hashCode) {
        //done return all the java file, notice: return list is null
        Map<String, String> res = new HashMap<>();

        try (Repository repository = gitService.openRepository(projectAddress); Git git = Git.open(new File(projectAddress))) {
            RevWalk walk = new RevWalk(repository);
            RevCommit commit = walk.parseCommit(repository.resolve(hashCode));
            res = fileIter(repository, commit);
//            System.out.println(fileContents.size());
            return res;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> fileIter(Repository repository, RevCommit commit) {

        Set<String> repositoryDirectories = new LinkedHashSet<>();  // file path
        Map<String, String> fileContents = new LinkedHashMap<>();  // 所有的文件内容，{ key: filePath, value: fileContent }

        // A reference to a tree of subtrees/files.
        RevTree parentTree = commit.getTree();
        // Walks one or more AbstractTreeIterators in parallel.
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            // Add an already created tree iterator for walking.
            treeWalk.addTree(parentTree);
            // Set the walker to enter (or not enter) subtrees automatically.
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {

                String pathString = treeWalk.getPathString();

                if (pathString.endsWith(".java")) {

                    // Obtain the ObjectId for the current entry.
                    ObjectId objectId = treeWalk.getObjectId(0);
                    // Base class for a set of loaders for different representations of Git objects.
                    ObjectLoader loader = repository.open(objectId);
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(loader.openStream(), writer);
                    if (!writer.toString().contains("<<<<<<< .working")) {
                        fileContents.put(pathString, writer.toString());
                    }
                }
                if (pathString.endsWith(".java") && pathString.contains("/")) {

                    String directory = pathString.substring(0, pathString.lastIndexOf("/"));
                    repositoryDirectories.add(directory);

                    String subDirectory = directory;
                    while (subDirectory.contains("/")) {
                        subDirectory = subDirectory.substring(0, subDirectory.lastIndexOf("/"));
                        repositoryDirectories.add(subDirectory);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContents;
    }

    //return the refactorings
    public HashMap<String, Refactorings> readRefactoring() {

        if (!jsonUsed){
            GenerateJsonFiles generateJsonFiles = new GenerateJsonFiles();
            generateJsonFiles.generateJson(projectAddress, refactoringMinerAddress, startHash, endHash);
        }

        //done
        String fileContent = readFile(refactoringMinerAddress);


        Gson gson = new Gson();
        Commits refactors = gson.fromJson(fileContent, Commits.class);
        refactors.filter();//filter out unused refactoring types
        HashMap<String, Refactorings> res = transferRefactorings(refactors.getCommits());// transfer refactorings into hashmap<sha, refactorings> format
        return res;
    }

    private static HashMap<String, Refactorings> transferRefactorings(List<Refactorings> listOfRefactors) {
        HashMap<String, Refactorings> res = new HashMap<>();
        for (Refactorings r : listOfRefactors) {
            if (r.getRefactorings().size() > 0) {
                res.put(r.getSha1(), r);
            }
        }
        return res;
    }

    //return the list of <oldFile, newFile>, which stands for the parent file and current file
    public HashMap<String, DiffFile> getDiffList(CommitHashCode commitHash) {
        //done
        HashMap<String, DiffFile> res = new HashMap<>();
        String parent = commitHash.getParent();
        String hashCode = commitHash.getHashCode();
        if (parent == null) {
            //this is the first commit
            Map<String, String> sourceCode = getSourceCode(hashCode);//all the java code and it's name during this commit
            // set all the files as add
            if (sourceCode.size() < 1) {
                return null;
            }// no new java file
            for (Map.Entry<String, String> entry : sourceCode.entrySet()) {
                DiffFile f = new DiffFile(FileType.ADD, entry.getKey(), entry.getValue());
                f.patchNew();
                res.put(entry.getKey(), f);
            }
        } else {
            //get the list of diff between two commits
            Map<String, String> oldCode = getSourceCode(parent);
            Map<String, String> newCode = getSourceCode(hashCode);

            try (Git git = Git.open(new File(projectAddress)); Repository repository = git.getRepository();) {
                List<DiffEntry> diffs = git.diff()
                        .setOldTree(prepareTreeParser(repository, parent))
                        .setNewTree(prepareTreeParser(repository, hashCode))
                        .setPathFilter(PathSuffixFilter.create(".java"))
                        .call();
                if (diffs.size() < 1) {
                    return null;
                }
                // for detecting rename
                Config config = new Config();
                config.setBoolean("diff", null, "renames", true);
                DiffConfig diffConfig = config.get(DiffConfig.KEY); // for rename detection
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    RenameDetector rd = new RenameDetector(treeWalk.getObjectReader(), diffConfig);
                    rd.addAll(diffs);
                    diffs = rd.compute();
                }
//                RenameDetector  renameDetector = new RenameDetector(repository);
//                renameDetector.addAll(diffs);
//                List<DiffEntry> x = renameDetector.compute();
//                for(DiffEntry dt: x){
//                    if(dt.getChangeType().equals("RENAME")){
//                        System.out.println("OK");
//                    }
//                }


//                System.out.println("Found: " + diffs.size() + " differences");
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                for (DiffEntry diff : diffs) {
                    //newCode.containsKey()
                    DiffFile f = null;
                    if (diff.getChangeType().name().equals("DELETE")) {
                        f = new DiffFile(FileType.valueOf(diff.getChangeType().name()), diff.getNewPath(), newCode.get(diff.getNewPath()), diff.getOldPath(), oldCode.get(diff.getOldPath()));
                        res.put(diff.getOldPath(), f);
                    } else {
                        if(newCode.get(diff.getNewPath())!=null){
                            f = new DiffFile(FileType.valueOf(diff.getChangeType().name()), diff.getNewPath(), newCode.get(diff.getNewPath()), diff.getOldPath(), oldCode.get(diff.getOldPath()));
                            res.put(diff.getNewPath(), f);
                        }

                    }
                    if(f != null){
                        DiffFormatter formatter = new DiffFormatter(byteStream) ;
                        formatter.setRepository(repository);
                        formatter.format(diff);

                        String mid=byteStream.toString();
                        byteStream.reset();
                        f.setPatch(mid);
                        f.patchParser();

                        EditList editList = formatter.toFileHeader(diff).toEditList();
                        f.setEditList(editList);
                    }
                }

            } catch (IOException | GitAPIException e) {
                e.printStackTrace();
            }

        }

        return res;

    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    public void resetCommit(String hashCode){
        try (Git git = Git.open(new File(projectAddress))) {
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef(hashCode).call();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
