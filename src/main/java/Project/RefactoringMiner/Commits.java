package Project.RefactoringMiner;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Commits {
    private List<Refactorings> commits;

    /*
     * 1. 将 refactorings 字段为空的 Commit 从 List<Commit> 中过滤出去
     * 2. 只保留指定的 refactoring type
     */
    public void filter() {
        for (Refactorings refactor : commits) {
            refactor.filter();
        }
        commits = commits.stream()
                // done: filter
                .filter(commit -> !commit.getRefactorings().isEmpty())
                .collect(Collectors.toList());
    }

}
