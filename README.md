# Refactoring ≠ Bug-Inducing: Improving Defect Prediction with Code Change Tactics Analysis

# Table of Contents

- [General Info](#general-info)
- [How to run](#how-to-run)
- [Demonstration of Usage](#demonstration-of-usage)
- [Application Scenarios](#application-scenarios)
- [How to cite this paper](#how-to-cite-this-paper)

## General Info

This is the repository of CAT (Code chAnge Tactics analysis), which is in paper: "Refactoring ≠ Bug-Inducing: Improving Defect Prediction with Code Change Tactics Analysis" submitted to ICSE 2025.

CAT is a tool designed for fine-grained code change analysis, to untangle purely refactoring and refactoring propagated code changes from normal code changes. Given commit of code change, it identifies code changes into 18 types: Add/Del, Add/Del_Refactoring, Add/Del_Move, Add/Del_Propogation, Add/Del_Refactoring_Edit, Add/Del_Propagration_Edit, Add/Del_Refactoring_Propagation, Add/Del_Refactoring_Propagation_Edit, Add/Del_Edit. CAT leverages RefactoringMiner 2.3 to identify code refactoring and then compares code tokens to differentiate purely refactored or propagation of refactoring.

## How to run

Requirements: 



Example for how to use in command ：



## Demonstration of Usage


## Application Scenarios
"Code tangling" is a common phenomenon that brings a lot of trouble to code change analysis. Existing refactoring detection tools can only detect existence of refactoring, but can not untangle refactored code and propagation of refactoring from common code changes. This will bring bias to the bug localization dataset, defect prediction dataset, as well as defect prediction models.

## How to cite this paper
To be known...
