package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import Model.*;
import Project.Utils.DiffFile;
import gr.uom.java.xmi.*;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class Visitor {
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;
    private HashMap<String, CodeBlock> residualMethodMap;
    private Map<String, DiffFile> diffMap;
    private Map<String, String> renameCodeBlockName;
    private HashSet<String> packageNames;

    public void visit(Map<String, String> javaFileContents, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings, Map<String, DiffFile> fileList, Map<String, String> renameCodeBlockName) {
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1);
        this.residualMethodMap = new HashMap<>();
        this.diffMap = fileList;
        this.renameCodeBlockName = renameCodeBlockName;
        packageNames = new HashSet<>();

        Reader reader= new Reader(javaFileContents);
    }

    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents) {
            super(javaFileContents);
        }

        protected void processCompilationUnit(String sourceFilePath, CompilationUnit compilationUnit, String javaFileContent) {
            PackageDeclaration packageDeclaration = compilationUnit.getPackage();
            String packageName;
            if (packageDeclaration != null) {
                packageName = packageDeclaration.getName().getFullyQualifiedName();
            } else {
                packageName = "";
            }
            CodeBlock codeBlock;

            if (!packageNames.contains(packageName)){
                PackageTime packageTime;
                if(!mappings.containsKey(packageName)){
                    if(renameCodeBlockName.containsKey(packageName) && mappings.containsKey(renameCodeBlockName.get(packageName))){
                        codeBlock = mappings.get(renameCodeBlockName.get(packageName));
                        if (codeBlock.getLastHistory() instanceof PackageTime){
                            packageTime = (PackageTime) codeBlock.getLastHistory().clone();
                            commitCodeChange.addCodeChange(packageTime);
                            codeBlock.addHistory(packageTime);
                            mappings.put(packageName, codeBlock);
                        }
                    }else {
                        codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Package);
                        mappings.put(packageName, codeBlock);
                        codeBlocks.add(codeBlock);
//                        packageTime = new PackageTime(packageName, commitCodeChange, Operator.Add_Package, codeBlock);
                    }
                } else {
                    codeBlock = mappings.get(packageName);
                    CodeBlockTime oldTime = codeBlock.getLastHistory();
                    if (oldTime instanceof PackageTime && !oldTime.getTime().equals(commitCodeChange)){
                        packageTime = (PackageTime) oldTime.clone();
                        commitCodeChange.addCodeChange(packageTime);
                        codeBlock.addHistory(packageTime);
                    }
                }
                packageNames.add(packageName);
            }

            super.processCompilationUnit(sourceFilePath, compilationUnit, javaFileContent);
        }

        @Override
        protected void processEnumDeclaration(CompilationUnit cu, EnumDeclaration enumDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = enumDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;


            CodeBlock pkgBlock = mappings.get(packageName);
            String anotherName = packageName;
            if(anotherName.startsWith(".")){
                anotherName = anotherName.substring(1);
            }
            if(pkgBlock == null && residualMethodMap.containsKey(anotherName)){
                mappings.put(packageName, residualMethodMap.get(anotherName));
                pkgBlock = mappings.get(packageName);
            }

            CodeBlock codeBlock;
            ClassTime classTime = null;
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(enumDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(enumDeclaration.getStartPosition() + enumDeclaration.getLength() - 1);
            int declarationLine = cu.getLineNumber((enumDeclaration.getName().getStartPosition()));

            if (!mappings.containsKey(signature)) {
                if(renameCodeBlockName.containsKey(signature) && mappings.containsKey(renameCodeBlockName.get(signature))){
                    codeBlock = mappings.get(renameCodeBlockName.get(signature));
                    oldTime = codeBlock.getLastHistory();
                    classTime = (ClassTime) codeBlock.getLastHistory().clone();
                    commitCodeChange.addCodeChange(classTime);
                    codeBlock.addHistory(classTime);
                    mappings.put(signature, codeBlock);
                }else {
                    codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                    mappings.put(signature, codeBlock);
                    codeBlocks.add(codeBlock);
                    oldTime = codeBlock.getLastHistory();
                    classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, codeBlock, pkgBlock);
                }
            } else {
                for(int i = startLine; i <= endLine; i++){
                    codeBlock = mappings.get(signature);
                    oldTime = codeBlock.getLastHistory();
                    if (oldTime.getTime().equals(commitCodeChange)){
                        break;
                    }
                    if(isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())){
                        classTime = (ClassTime) codeBlock.getLastHistory().clone();
                        commitCodeChange.addCodeChange(classTime);
                        codeBlock.addHistory(classTime);
                        break;
                    }
                }
            }

            if(classTime != null){
                if(diffMap.containsKey(sourceFile)){
                    diffMap.get(sourceFile).getTimes().add(classTime);
                }

                classTime.setNewStartLineNum(startLine);
                classTime.setNewEndLineNum(endLine);
                classTime.setNewDeclarationLineNum(declarationLine);

                if(oldTime != null){
                    classTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                    classTime.setOldEndLineNum(oldTime.getNewEndLineNum());
                    classTime.setOldDeclarationLineNum(oldTime.getNewDeclarationLineNum());
                }
            }


            super.processEnumDeclaration(cu, enumDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);

        }

        @Override
        protected void processTypeDeclaration(CompilationUnit cu, TypeDeclaration typeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = typeDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;

            addNewRenameClass(signature, packageName, className);

            CodeBlock pkgBlock = mappings.get(packageName);
            String anotherName = packageName;
            if(anotherName.startsWith(".")){
                anotherName = anotherName.substring(1);
            }

            if(pkgBlock == null && residualMethodMap.containsKey(anotherName)){
                mappings.put(packageName, residualMethodMap.get(anotherName));
                pkgBlock = mappings.get(packageName);
            }

            CodeBlock codeBlock;
            ClassTime classTime = null;
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(typeDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(typeDeclaration.getStartPosition() + typeDeclaration.getLength() - 1);
            int declarationLine = cu.getLineNumber(typeDeclaration.getName().getStartPosition());

            if (!mappings.containsKey(signature)) {
                if(renameCodeBlockName.containsKey(signature) && mappings.containsKey(renameCodeBlockName.get(signature))){
                    codeBlock = mappings.get(renameCodeBlockName.get(signature));
                    oldTime = codeBlock.getLastHistory();
                    classTime = (ClassTime) codeBlock.getLastHistory().clone();
                    commitCodeChange.addCodeChange(classTime);
                    codeBlock.addHistory(classTime);
                    mappings.put(signature, codeBlock);
                }else {
                    codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                    mappings.put(signature, codeBlock);
                    codeBlocks.add(codeBlock);
                    oldTime = codeBlock.getLastHistory();
                    classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, codeBlock, pkgBlock);
                }
            } else {
                for(int i = startLine; i <= endLine; i++){
                    codeBlock = mappings.get(signature);
                    oldTime = codeBlock.getLastHistory();
                    if (oldTime.getTime().equals(commitCodeChange)){
                        break;
                    }
                    if(isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())){
                        classTime = (ClassTime) codeBlock.getLastHistory().clone();
                        commitCodeChange.addCodeChange(classTime);
                        codeBlock.addHistory(classTime);
                        break;
                    }
                }

            }

            if(classTime != null){
                if(diffMap.containsKey(sourceFile)){
                    diffMap.get(sourceFile).getTimes().add(classTime);
                }

                classTime.setNewStartLineNum(startLine);
                classTime.setNewEndLineNum(endLine);
                classTime.setNewDeclarationLineNum(declarationLine);

                if(oldTime != null){
                    classTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                    classTime.setOldEndLineNum(oldTime.getNewEndLineNum());
                    classTime.setOldDeclarationLineNum(oldTime.getNewDeclarationLineNum());
                }
            }


            super.processTypeDeclaration(cu, typeDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);

        }

        @Override
        protected Map<BodyDeclaration, VariableDeclarationContainer> processBodyDeclarations(CompilationUnit cu, AbstractTypeDeclaration abstractTypeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLClass umlClass, UMLJavadoc packageDoc, List<UMLComment> comments) {
            Map<BodyDeclaration, VariableDeclarationContainer> map = new LinkedHashMap();
            List<BodyDeclaration> bodyDeclarations = abstractTypeDeclaration.bodyDeclarations();
            Iterator var11 = bodyDeclarations.iterator();

            while(true) {
                while(var11.hasNext()) {
                    BodyDeclaration bodyDeclaration = (BodyDeclaration)var11.next();
                    if (bodyDeclaration instanceof FieldDeclaration) {
                        FieldDeclaration fieldDeclaration = (FieldDeclaration)bodyDeclaration;
                        List<UMLAttribute> attributes = processFieldDeclaration(cu, fieldDeclaration, umlClass.isInterface(), sourceFile, comments);
                        Iterator var15 = attributes.iterator();

                        int index = 0;
                        while(var15.hasNext()) {
                            UMLAttribute attribute = (UMLAttribute)var15.next();
                            attribute.setClassName(umlClass.getName());
                            umlClass.addAttribute(attribute);
                            attributeVisitor(cu, fieldDeclaration, attribute, index, sourceFile);
                            index++;
                        }
                    } else if (bodyDeclaration instanceof MethodDeclaration) {
                        MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                        UMLOperation operation = processMethodDeclaration(cu, methodDeclaration, packageName, umlClass.isInterface(), sourceFile, comments);
                        operation.setClassName(umlClass.getName());
                        umlClass.addOperation(operation);
                        map.put(methodDeclaration, operation);
                        methodVisitor(cu, methodDeclaration, operation, sourceFile);
                    } else if (bodyDeclaration instanceof Initializer) {
                        Initializer initializer = (Initializer)bodyDeclaration;
                        UMLInitializer umlInitializer = processInitializer(cu, initializer, packageName, false, sourceFile, comments);
                        umlInitializer.setClassName(umlClass.getName());
                        umlClass.addInitializer(umlInitializer);
                        map.put(initializer, umlInitializer);
                    } else if (bodyDeclaration instanceof TypeDeclaration) {
                        TypeDeclaration typeDeclaration = (TypeDeclaration)bodyDeclaration;
                        processTypeDeclaration(cu, typeDeclaration, umlClass.getName(), sourceFile, importedTypes, packageDoc, comments);
                    } else if (bodyDeclaration instanceof EnumDeclaration) {
                        EnumDeclaration enumDeclaration = (EnumDeclaration)bodyDeclaration;
                        processEnumDeclaration(cu, enumDeclaration, umlClass.getName(), sourceFile, importedTypes, packageDoc, comments);
                    }
                }

                return map;
            }
        }

        @Override
        protected void processEnumConstantDeclaration(CompilationUnit cu, EnumConstantDeclaration enumConstantDeclaration, String sourceFile, UMLClass umlClass, List<UMLComment> comments) {
            UMLJavadoc javadoc = generateJavadoc(cu, (BodyDeclaration)enumConstantDeclaration, (String)sourceFile);
            LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, enumConstantDeclaration, LocationInfo.CodeElementType.ENUM_CONSTANT_DECLARATION);
            UMLEnumConstant enumConstant = new UMLEnumConstant(enumConstantDeclaration.getName().getIdentifier(), UMLType.extractTypeObject(umlClass.getName()), locationInfo);
            gr.uom.java.xmi.decomposition.VariableDeclaration variableDeclaration = new VariableDeclaration(cu, sourceFile, enumConstantDeclaration);
            enumConstant.setVariableDeclaration(variableDeclaration);
            enumConstant.setJavadoc(javadoc);
            distributeComments(comments, locationInfo, enumConstant.getComments());
            enumConstant.setFinal(true);
            enumConstant.setStatic(true);
            enumConstant.setVisibility(Visibility.PUBLIC);
            List<Expression> arguments = enumConstantDeclaration.arguments();
            Iterator var11 = arguments.iterator();

            while(var11.hasNext()) {
                Expression argument = (Expression)var11.next();
                enumConstant.addArgument(gr.uom.java.xmi.decomposition.Visitor.stringify(argument));
            }

            enumConstant.setClassName(umlClass.getName());
            umlClass.addEnumConstant(enumConstant);

            String attributeName = umlClass.getNonQualifiedName() + "_" + enumConstant.getName();
            String signature = umlClass.getName();
            String signatureAttribute = signature + ":" + attributeName;

            addNewRenameMethodOrAttribute(signatureAttribute, signature, attributeName);

            CodeBlock codeBlock;
            AttributeTime attriTime = null;
            CodeBlock classBlock = mappings.get(signature);
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(enumConstantDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(enumConstantDeclaration.getStartPosition() + enumConstantDeclaration.getLength() - 1);
            int declarationLine = cu.getLineNumber(enumConstantDeclaration.getName().getStartPosition());

            if (!mappings.containsKey(signatureAttribute)) {
                if(renameCodeBlockName.containsKey(signatureAttribute) && mappings.containsKey(renameCodeBlockName.get(signatureAttribute))){
                    codeBlock = mappings.get(renameCodeBlockName.get(signatureAttribute));
                    oldTime = codeBlock.getLastHistory();
                    attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
                    commitCodeChange.addCodeChange(attriTime);
                    codeBlock.addHistory(attriTime);
                    mappings.put(signatureAttribute, codeBlock);
                }else {
                    codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Attribute);
                    mappings.put(signatureAttribute, codeBlock);
                    codeBlocks.add(codeBlock);
                    oldTime = codeBlock.getLastHistory();
                    attriTime = new AttributeTime(attributeName, commitCodeChange, Operator.Add_Attribute, codeBlock, classBlock);
                }
            } else {
                for(int i = startLine; i <= endLine; i++) {
                    codeBlock = mappings.get(signatureAttribute);
                    oldTime = codeBlock.getLastHistory();
                    if (oldTime.getTime().equals(commitCodeChange)){
                        break;
                    }
                    if (isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())) {
                        attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
                        commitCodeChange.addCodeChange(attriTime);
                        codeBlock.addHistory(attriTime);
                        break;
                    }
                }
            }


            if(attriTime != null){
                if(diffMap.containsKey(sourceFile)){
                    diffMap.get(sourceFile).getTimes().add(attriTime);
                }

                attriTime.setNewStartLineNum(startLine);
                attriTime.setNewEndLineNum(endLine);
                attriTime.setNewDeclarationLineNum(declarationLine);

                if(oldTime != null){
                    attriTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                    attriTime.setOldEndLineNum(oldTime.getNewEndLineNum());
                    attriTime.setOldDeclarationLineNum(oldTime.getNewDeclarationLineNum());
                }
            }
        }
    }

    private void methodVisitor(CompilationUnit cu, MethodDeclaration md, UMLOperation umlOperation, String sourceFile) {
        String signature = umlOperation.getClassName();

        StringBuilder sb = new StringBuilder();
        StringBuilder parameterTypes = new StringBuilder();

        UMLParameter returnParameter = umlOperation.getReturnParameter();
        if (returnParameter != null) {
            sb.append(returnParameter).append("_");
        }

        sb.append(umlOperation.getName());

        List<UMLParameter> parameters = new ArrayList(umlOperation.getParameters());
        parameters.remove(returnParameter);
        sb.append("(");

        for(int i = 0; i < parameters.size(); ++i) {
            UMLParameter parameter = parameters.get(i);
            if (parameter.getKind().equals("in")) {
                String parameterStr = parameter.toString();
                parameterTypes.append(parameterStr.substring(parameterStr.indexOf(" ")+1));
                if (i < parameters.size() - 1) {
                    parameterTypes.append(", ");
                }
            }
        }

        sb.append(parameterTypes);
        sb.append(")");

        String methodName = sb.toString();
        String signatureMethod = signature + ":" + methodName;

        addNewRenameMethodOrAttribute(signatureMethod, signature, methodName);


        CodeBlock codeBlock = null;
        MethodTime methodTime = null;
        CodeBlock classBlock = mappings.get(signature);
        CodeBlockTime oldTime = null;
        int startLine = cu.getLineNumber(md.getStartPosition());
        int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength() - 1);
        int declarationLine = cu.getLineNumber(md.getName().getStartPosition());

        if (!mappings.containsKey(signatureMethod)) {
            if(renameCodeBlockName.containsKey(signatureMethod) && mappings.containsKey(renameCodeBlockName.get(signatureMethod))){
                String oldSignatureMethod = renameCodeBlockName.get(signatureMethod);
                codeBlock = mappings.get(oldSignatureMethod);
                oldTime = codeBlock.getLastHistory();
                methodTime = (MethodTime) codeBlock.getLastHistory().clone();
                commitCodeChange.addCodeChange(methodTime);
                codeBlock.addHistory(methodTime);
                mappings.put(signatureMethod, codeBlock);


                String oldClass = "";
                String newClass = "";
                String oldMethod = "";
                String newMethod = "";
                if(oldSignatureMethod.contains(":") ){
                    oldClass = oldSignatureMethod.split(":")[0];
                    oldMethod = oldSignatureMethod.split(":")[1];
                }
                if (signatureMethod.contains(":")){
                    newClass = signatureMethod.split(":")[0];
                    newMethod = signatureMethod.split(":")[1];
                }

                mappings.put(oldClass + ":" + newMethod, codeBlock);
                mappings.put(newClass + ":" + oldMethod, codeBlock);
            }else {
                codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Method);
                mappings.put(signatureMethod, codeBlock);
                codeBlocks.add(codeBlock);
                oldTime = codeBlock.getLastHistory();
                methodTime = new MethodTime(methodName, commitCodeChange, Operator.Add_Method, codeBlock, classBlock, parameterTypes.toString());
            }
        } else {
            for(int i = startLine; i <= endLine; i++) {
                codeBlock = mappings.get(signatureMethod);
                oldTime = codeBlock.getLastHistory();

                if (oldTime.getTime().equals(commitCodeChange)){
                    break;
                }

                if (isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())) {
                    methodTime = (MethodTime) codeBlock.getLastHistory().clone();
                    commitCodeChange.addCodeChange(methodTime);
                    codeBlock.addHistory(methodTime);
                    break;
                }
            }
        }
        if(!residualMethodMap.containsKey(signature + "." + umlOperation.getName())){
            residualMethodMap.put(signature + "." + umlOperation.getName(), codeBlock);
        }

        if (methodTime != null){
            if(diffMap.containsKey(sourceFile)){
                diffMap.get(sourceFile).getTimes().add(methodTime);
            }

            methodTime.setNewStartLineNum(startLine);
            methodTime.setNewEndLineNum(endLine);
            methodTime.setNewDeclarationLineNum(declarationLine);

            if(oldTime != null){
                methodTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                methodTime.setOldEndLineNum(oldTime.getNewEndLineNum());
                methodTime.setOldDeclarationLineNum(oldTime.getNewDeclarationLineNum());
            }
        }



    }

    private void attributeVisitor(CompilationUnit cu, FieldDeclaration fd, UMLAttribute umlAttribute, int index, String sourceFile){
        String attributeName = umlAttribute.getType() + "_" + umlAttribute.getName();
        String signature = umlAttribute.getClassName();
        String signatureAttribute = signature + ":" + attributeName;

        addNewRenameMethodOrAttribute(signatureAttribute, signature, attributeName);

        CodeBlock codeBlock;
        AttributeTime attriTime = null;
        CodeBlock classBlock = mappings.get(signature);
        CodeBlockTime oldTime = null;
        int startLine = cu.getLineNumber(fd.getStartPosition());
        int endLine = cu.getLineNumber(fd.getStartPosition() + fd.getLength() - 1);
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment)fd.fragments().get(index);
        int declarationLine = cu.getLineNumber(variableDeclarationFragment.getStartPosition());

        if (!mappings.containsKey(signatureAttribute)) {
            if(renameCodeBlockName.containsKey(signatureAttribute) && mappings.containsKey(renameCodeBlockName.get(signatureAttribute))){
                String oldSignatureAttribute = renameCodeBlockName.get(signatureAttribute);
                codeBlock = mappings.get(renameCodeBlockName.get(signatureAttribute));
                oldTime = codeBlock.getLastHistory();
                attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
                commitCodeChange.addCodeChange(attriTime);
                codeBlock.addHistory(attriTime);
                mappings.put(signatureAttribute, codeBlock);

                //refactoringMiner部分refactoring有时无法正确匹配新方法与新类，需要额外添加mapping以正确匹配
                String oldClass = "";
                String newClass = "";
                String oldAttribute = "";
                String newMethod = "";
                if(oldSignatureAttribute.contains(":") ){
                    oldClass = oldSignatureAttribute.split(":")[0];
                    oldAttribute = oldSignatureAttribute.split(":")[1];
                }
                if (signatureAttribute.contains(":")){
                    newClass = signatureAttribute.split(":")[0];
                    newMethod = signatureAttribute.split(":")[1];
                }

                mappings.put(oldClass + ":" + newMethod, codeBlock);
                mappings.put(newClass + ":" + oldAttribute, codeBlock);
            }else {
                codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Attribute);
                mappings.put(signatureAttribute, codeBlock);
                codeBlocks.add(codeBlock);
                oldTime = codeBlock.getLastHistory();
                attriTime = new AttributeTime(attributeName, commitCodeChange, Operator.Add_Attribute, codeBlock, classBlock);
            }
        } else {
            for(int i = startLine; i <= endLine; i++) {
                codeBlock = mappings.get(signatureAttribute);
                oldTime = codeBlock.getLastHistory();
                if (oldTime.getTime().equals(commitCodeChange)){
                    break;
                }
                if (isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())) {
                    attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
                    commitCodeChange.addCodeChange(attriTime);
                    codeBlock.addHistory(attriTime);
                    break;
                }
            }
        }


        if(attriTime != null){
            if(diffMap.containsKey(sourceFile)){
                diffMap.get(sourceFile).getTimes().add(attriTime);
            }

            attriTime.setNewStartLineNum(startLine);
            attriTime.setNewEndLineNum(endLine);
            attriTime.setNewDeclarationLineNum(declarationLine);

            if(oldTime != null){
                attriTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                attriTime.setOldEndLineNum(oldTime.getNewEndLineNum());
                attriTime.setOldDeclarationLineNum(oldTime.getNewDeclarationLineNum());
            }
        }
    }

    boolean isChanged(String sourceFile, int changeLine, int startLine, int oldStartLine, int endLine, int oldEndLine){
        return diffMap.containsKey(sourceFile)
                && (diffMap.get(sourceFile).containsChangeLine(changeLine)
                || startLine != oldStartLine
                || endLine != oldEndLine);
    }


    void addNewRenameClass(String signatureName, String fatherName, String name){
        if (renameCodeBlockName.containsKey(fatherName) && !renameCodeBlockName.containsKey(signatureName)){
            String oldFatherName = renameCodeBlockName.get(fatherName);
            renameCodeBlockName.put(signatureName, oldFatherName+"."+name);
        }
    }


    void addNewRenameMethodOrAttribute(String signatureName, String fatherName, String name){
        if (renameCodeBlockName.containsKey(fatherName)){
            if (!signatureName.contains(":")){
                return;
            }
            String oldName = "";
            if (renameCodeBlockName.containsKey(signatureName)){

                String oldSignatureName = renameCodeBlockName.get(signatureName);
                if (!oldSignatureName.contains(":")){
                    return;
                }
                oldName = oldSignatureName.split(":")[1];
                String newName = signatureName.split(":")[1];
                if (!oldName.equals(newName)){
                    String oldFatherName = renameCodeBlockName.get(fatherName);
                    renameCodeBlockName.put(signatureName, oldFatherName+":"+oldName);
                }
            } else {
                String oldFatherName = renameCodeBlockName.get(fatherName);
                renameCodeBlockName.put(signatureName, oldFatherName+"."+name);
            }
        }
    }

}
