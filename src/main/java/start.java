import Constructor.Constructor;
import Model.CodeBlock;
import Model.CommitCodeChange;
import Project.Project;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class start {

    public static void main(String[] args){
        run(args);
    }

    private static void run(String[] args){
        if (args.length < 1) {
            throw argumentException();
        }

        final String option = args[0];
        if (option.equalsIgnoreCase("-h") || option.equalsIgnoreCase("--h") || option.equalsIgnoreCase("-help")
                || option.equalsIgnoreCase("--help")) {
            printTips();
            return;
        }

        String localPath = args[1];
        String startCommitHash = null;
        String endCommitHash = null;
        String jsonAddress = "";
        String savePath = "";
        boolean jsonUsed = false;

        try (Repository repository = Git.open(new File(localPath)).getRepository()) {

            if (option.equalsIgnoreCase("-a")) {
                startCommitHash = getStartCommitId(repository);

                endCommitHash = getEndCommitId(repository);

            } else if (option.equalsIgnoreCase("-bc")) {
                startCommitHash = args[2];
                endCommitHash = args[3];
            } else if (option.equalsIgnoreCase("-c")) {
                endCommitHash = args[2];
                startCommitHash = getPreCommitId(repository, endCommitHash);
            } else {
                throw argumentException();
            }

            for(int i = 0; i < args.length; i++){
                if(args[i].equalsIgnoreCase("-json")){
                    jsonAddress = args[i+1];
                    jsonUsed = true;
                }

                if (args[i].equalsIgnoreCase("-path")){
                    savePath = args[i+1];
                }
            }

        } catch (GitAPIException | IOException exception){
            exception.printStackTrace();
        }

        String[] info = new String[]{localPath, startCommitHash, endCommitHash};

        Project p = new Project(info, jsonUsed, jsonAddress);
        p.setSavePath(savePath);
        p.run();

        Constructor constructor = new Constructor(p);
        constructor.start();// start code analysis
        List<CodeBlock> codeBlocks = constructor.getCodeBlocks();
        List<CommitCodeChange> commits = constructor.getCodeChange();
        HashMap<String, CodeBlock> mappings = constructor.getMappings();


        int x = 0;
        for(CodeBlock cb: codeBlocks){
            x = x + cb.getHistory().size();
        }

        System.out.println("CodeBlockTimeNum : " + x);
        System.out.println("CodeBlockNum : " + codeBlocks.size());
        System.out.println("CommitNum : " + commits.size());
        System.out.println("MappingNum : " + mappings.size());
    }

    private static String getStartCommitId(Repository repository) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            RevCommit endCommit = null;
            for (RevCommit commit : commits) {
                endCommit = commit;
            }
            return endCommit.getId().getName();
        }
    }

    private static String getEndCommitId(Repository repository) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            RevCommit startCommit = commits.iterator().next();
            return startCommit.getId().getName();
        }
    }

    private static String getPreCommitId(Repository repository, String endHash) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            RevCommit preCommit = null;
            for (RevCommit commit : commits) {
                if(preCommit != null && preCommit.getId().getName().equals(endHash)){
                    preCommit = commit;
                    break;
                }else {
                    preCommit = commit;
                }
            }
            return preCommit.getId().getName();
        }
    }

    private static void printTips() {
        System.out.println("-h\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tShow options");
        System.out.println(
                "-a <git-repo-folder> -s <sqlite-file-path>\t\t\t\t\t\t\t\t\t\tDetect all refactorings for <git-repo-folder>");
        System.out.println(
                "-bc <git-repo-folder> <start-commit-sha1> <end-commit-sha1> -s <sqlite-file-path>\tDetect refactorings between <start-commit-sha1> and <end-commit-sha1> for project <git-repo-folder>");
        System.out.println(
                "-c <git-repo-folder> <commit-sha1> -s <sqlite-file-path>\t\t\t\t\t\t\tDetect refactorings between the previous one of <commit-sha1> and <commit-sha1> for project <git-repo-folder>");

        }

    private static IllegalArgumentException argumentException() {
        return new IllegalArgumentException("Type `Traceability -h` to show usage.");
    }
}
