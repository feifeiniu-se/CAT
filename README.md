# Refactoring ≠ Bug-Inducing: Improving Defect Prediction with Code Change Tactics Analysis

# Table of Contents

- [General Info](#general-info)
- [Proposed 66 RAM] (#proposed-66-ram)
- [How to run](#how-to-run)
- [Demonstration of Usage](#demonstration-of-usage)
- [Application Scenarios](#application-scenarios)
- [How to cite this paper](#how-to-cite-this-paper)

## General Info

This is the repository of CAT (Code chAnge Tactics analysis), which is in paper: "Refactoring ≠ Bug-Inducing: Improving Defect Prediction with Code Change Tactics Analysis".

CAT is a tool designed for fine-grained code change analysis, to untangle purely refactoring and refactoring propagated code changes from normal code changes. Given commit of code change, it identifies code changes into 18 types: Add/Del, Add/Del_Refactoring, Add/Del_Move, Add/Del_Propogation, Add/Del_Refactoring_Edit, Add/Del_Propagration_Edit, Add/Del_Refactoring_Propagation, Add/Del_Refactoring_Propagation_Edit, Add/Del_Edit. CAT leverages RefactoringMiner 2.3 to identify code refactoring and then compares code tokens to differentiate purely refactored or propagation of refactoring.

## Proposed 66 RAM
| Line-Level                                                   | Class-Level                                                         | Method-Level                                                       |
|--------------------------------------------------------------|---------------------------------------------------------------------|--------------------------------------------------------------------|
| No. of *Add* lines                                            | No. of *Add* lines per class                                        | No. of pure added lines per method                                 |
| No. of *Add_Move* lines                                       | No. of *Add_Move* lines per class                                   | No. of add_move lines per method                                   |
| No. of *Add_Refactoring* lines                                | No. of *Add_Refactoring* lines per class                            | No. of *Add_Refactoring* lines per method                          |
| No. of *Add_Propagation* lines                                | No. of *Add_Propagation* lines per class                            | No. of *Add_Propagation* lines per method                          |
| No. of *Add_Edit* lines                                       | No. of *Add_Edit* lines per class                                   | No. of *Add_Edit* lines per method                                 |
| No. of *Add_Refactoring_Propagation* lines                    | No. of *Add_Refactoring_Propagation* lines per class                | No. of *Add_Refactoring_Propagation* lines per method              |
| No. of *Add_Refactoring_Edit* lines                           | No. of *Add_Refactoring_Edit* lines per class                       | No. of *Add_Refactoring_Edit* lines per method                     |
| No. of *Add_Propagation_Edit* lines                           | No. of *Add_Propagation_Edit* lines per class                       | No. of *Add_Propagation_Edit* lines per method                     |
| No. of *Add_Refactoring_Propagation_Edit* lines               | No. of *Add_Refactoring_Propagation_Edit* lines per class           | No. of *Add_Refactoring_Propagation_Edit* lines per method         |
| No. of *Delete* lines                                         | No. of *Delete* lines per class                                     | No. of *Delete* lines per method                                   |
| No. of *Delete_Move* lines                                    | No. of *Delete_Move* lines per class                                | No. of *Delete_Move* lines per method                              |
| No. of *Delete_Refactoring* lines                              | No. of *Delete_Refactoring* lines per class                          | No. of *Delete_Refactoring* lines per method                       |
| No. of *Delete_Propagation* lines                              | No. of *Delete_Propagation* lines per class                          | No. of *Delete_Propagation* lines per method                       |
| No. of *Delete_Edit* lines                                    | No. of *Delete_Edit* lines per class                                | No. of *Delete_Edit* lines per method                              |
| No. of *Delete_Refactoring_Propagation* lines                  | No. of *Delete_Refactoring_Propagation* lines per class             | No. of *Delete_Refactoring_Propagation* lines per method           |
| No. of *Delete_Refactoring_Edit* lines                         | No. of *Delete_Refactoring_Edit* lines per class                    | No. of *Delete_Refactoring_Edit* lines per method                  |
| No. of *Delete_Propagation_Edit* lines                         | No. of *Delete_Propagation_Edit* lines per class                    | No. of *Delete_Propagation_Edit* lines per method                  |
| No. of *Delete_Refactoring_Propagation_Edit* lines             | No. of *Delete_Refactoring_Propagation_Edit* lines per class        | No. of *Delete_Refactoring_Propagation_Edit* lines per method      |
|                                                              | No. of purely added classes                                        | No. of purely added methods                                         |
|                                                              | No. of purely deleted classes                                      | No. of purely deleted methods                                       |
|                                                              | No. of purely moved classes                                        | No. of purely moved methods                                        |
|                                                              | No. of purely refactored classes                                   | No. of purely refactored methods                                    |
|                                                              | No. of purely propagated classes                                   | No. of purely propagated methods                                    |
|                                                              | No. of edited classes                                              | No. of edited methods                                               |


## How to run

Requirements: JDK>=17

Download the "CAT-jar-with-dependency.jar" from: https://github.com/feifeiniu-se/CAT/releases/tag/release-v1.0.0

CAT is command line tool so far, it supports three types of commands:

```
> -a <git-repo-folder> -path <result-directory-path> # detects all code change lines

> -bc <git-repo-folder> <start-commit-sha1> <end-commit-sha1> -path <result-directory-path> # detects code change lines between start commit and end commit

> -c <git-repo-folder> <commit-sha1> -path <result-directory-path> # detects code history between last commit and this commit
```

"&lt; git-repo-folder &gt;" defines the path of the local repository, "&lt; result-directory-path &gt;" indicates the path for saving the results.

The calculated results include various data between the specified commits: a matrix CSV file containing the number of rows for each type of change, a CSV file for each entropy value, and a JSON file containing the content of rows corresponding to each type of change for each commit.



Example for how to use in command ：

```
> java -jar CAT-1.0-SNAPSHOT-jar-with-dependencies.jar -bc C:\dataset\jitfine\ant-ivy 6e710a1f78a4a733020a4b2d1f07b6fe4c6684f0 783276d948afe1c69b6309be9d1e8828df0ae9f -path D:\\tool\\database

> java -jar CAT-1.0-SNAPSHOT-jar-with-dependencies.jar -a C:\dataset\jitfine\ant-ivy -path D:\\tool\\database

> java -jar CAT-1.0-SNAPSHOT-jar-with-dependencies.jar -c C:\dataset\jitfine\ant-ivy 783276d948afe1c69b6309be9d1e8828df0ae9fa -path D:\\tool\\database
```

You can replace the path of git repository, start hash code, end hash code and the path  for saving the results.

The following are the github URLs in the example：

https://github.com/apache/ant-ivy

## Demonstration of Usage

For developers: This tool can display more fine-grained types of code changes that occur between two commits, covering types such as refactoring, refactoring modifications, and refactoring impacts. At the same time, the results can be applied to error prediction, making the predictions more accurate.

For researchers: Research relies on code changes, however, ignoring code changes related to refactoring and refactoring propagation may have an impact. Our tool detects more fine-grained code change operations, and researchers can apply our results to obtain more accurate data.


## Application Scenarios
"Code tangling" is a common phenomenon that brings a lot of trouble to code change analysis. Existing refactoring detection tools can only detect existence of refactoring, but can not untangle refactored code and propagation of refactoring from common code changes. This will bring bias to the bug localization dataset, defect prediction dataset, as well as defect prediction models.

## How to cite this paper
To be known...
