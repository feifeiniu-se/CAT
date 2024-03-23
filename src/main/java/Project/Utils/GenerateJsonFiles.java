package Project.Utils;

import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class GenerateJsonFiles {
    private static final Logger logger = LoggerFactory.getLogger(GenerateJsonFiles.class);

    public void generateJson(String projectAddress, String refactoringMinerAddress, String startHash, String endHash){
        Path path = Paths.get(refactoringMinerAddress);
        try {
            if(Files.exists(path)) {
                Files.delete(path);
            }
            if(Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        GitService gitService = new GitServiceImpl();
        try (Repository repo = gitService.openRepository(projectAddress)) {
            String gitURL = repo.getConfig().getString("remote", "origin", "url");
            GitHistoryRefactoringMiner detector = new GitHistoryRefactoringMinerImpl();
            startJSON(path);
            detector.detectBetweenCommits(repo, startHash, endHash, new RefactoringHandler() {
                private int commitCount = 0;
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    if(commitCount > 0) {
                        betweenCommitsJSON(path);
                    }
                    commitJSON(path, gitURL, commitId, refactorings);
                    commitCount++;
                }

                @Override
                public void onFinish(int refactoringsCount, int commitsCount, int errorCommitsCount) {
                    System.out.println(String.format("Total count: [Commits: %d, Errors: %d, Refactorings: %d]",
                            commitsCount, errorCommitsCount, refactoringsCount));
                }

                @Override
                public void handleException(String commit, Exception e) {
                    System.err.println("Error processing commit " + commit);
                    logger.error(e.getMessage());
                }
            });
            endJSON(path);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void startJSON(Path path) {
        if(path != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("{").append("\n");
            sb.append("\"").append("commits").append("\"").append(": ");
            sb.append("[").append("\n");
            try {
                Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void endJSON(Path path) {
        if(path != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("]").append("\n");
            sb.append("}");
            try {
                Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void betweenCommitsJSON(Path path) {
        if(path != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(",").append("\n");
            try {
                Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void commitJSON(Path path, String cloneURL, String currentCommitId, List<Refactoring> refactoringsAtRevision) {
        if(path != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("{").append("\n");
            sb.append("\t").append("\"").append("repository").append("\"").append(": ").append("\"").append(cloneURL).append("\"").append(",").append("\n");
            sb.append("\t").append("\"").append("sha1").append("\"").append(": ").append("\"").append(currentCommitId).append("\"").append(",").append("\n");
            String url = GitHistoryRefactoringMinerImpl.extractCommitURL(cloneURL, currentCommitId);
            sb.append("\t").append("\"").append("url").append("\"").append(": ").append("\"").append(url).append("\"").append(",").append("\n");
            sb.append("\t").append("\"").append("refactorings").append("\"").append(": ");
            sb.append("[");
            int counter = 0;
            for(Refactoring refactoring : refactoringsAtRevision) {
                sb.append(refactoring.toJSON());
                if(counter < refactoringsAtRevision.size()-1) {
                    sb.append(",");
                }
                sb.append("\n");
                counter++;
            }
            sb.append("]").append("\n");
            sb.append("}");
            try {
                Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
