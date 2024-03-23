package Constructor.Visitors;

import Model.*;
import Project.Utils.DiffFile;
import gr.uom.java.xmi.*;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class DeleteVisitor {
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;
    private Map<String, DiffFile> diffMap;

    public void visit(Map<String, String> javaFileContents, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings, Map<String, DiffFile> fileList) {
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1);
        this.diffMap = fileList;

        Reader reader= new Reader(javaFileContents);
    }

    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents) {
            super(javaFileContents);
        }

        @Override
        protected void processEnumDeclaration(CompilationUnit cu, EnumDeclaration enumDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = enumDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;

            CodeBlock codeBlock;
            ClassTime classTime = null;
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(enumDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(enumDeclaration.getStartPosition() + enumDeclaration.getLength() - 1);

            if (mappings.containsKey(signature)) {
                codeBlock = mappings.get(signature);
                oldTime = codeBlock.getLastHistory();

                if (oldTime.getTime().equals(commitCodeChange) && !diffMap.get(sourceFile).getTimes().contains(oldTime)){
                    if(diffMap.containsKey(sourceFile)){
                        diffMap.get(sourceFile).getTimes().add(oldTime);
                    }
                } else if (!oldTime.getTime().equals(commitCodeChange)){
                    for(int i = startLine; i <= endLine; i++){
                        if(isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())){
                            classTime = (ClassTime) codeBlock.getLastHistory().clone();
                            commitCodeChange.addCodeChange(classTime);
                            codeBlock.addHistory(classTime);
                            if(diffMap.containsKey(sourceFile)){
                                diffMap.get(sourceFile).getTimes().add(classTime);
                            }
                            break;
                        }
                    }
                }
            }

            super.processEnumDeclaration(cu, enumDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);

        }

        @Override
        protected void processTypeDeclaration(CompilationUnit cu, TypeDeclaration typeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = typeDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;

            CodeBlock codeBlock;
            ClassTime classTime = null;
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(typeDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(typeDeclaration.getStartPosition() + typeDeclaration.getLength() - 1);

            if (mappings.containsKey(signature)) {
                codeBlock = mappings.get(signature);
                oldTime = codeBlock.getLastHistory();


                if (oldTime.getTime().equals(commitCodeChange) && !diffMap.get(sourceFile).getTimes().contains(oldTime)){
                    if(diffMap.containsKey(sourceFile)){
                        diffMap.get(sourceFile).getTimes().add(oldTime);
                        diffMap.get(sourceFile).getDeleteMoveTimes().add(oldTime);
                        oldTime.setMoved(true);
                    }
                } else if (!oldTime.getTime().equals(commitCodeChange)){
                    for(int i = startLine; i <= endLine; i++){
                        if(isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())){
                            classTime = (ClassTime) codeBlock.getLastHistory().clone();
                            commitCodeChange.addCodeChange(classTime);
                            codeBlock.addHistory(classTime);
                            if(diffMap.containsKey(sourceFile)){
                                diffMap.get(sourceFile).getTimes().add(classTime);
                            }
                            break;
                        }
                    }
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
            String signature_attribute = signature + ":" + attributeName;

            CodeBlock codeBlock;
            AttributeTime attriTime = null;
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(enumConstantDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(enumConstantDeclaration.getStartPosition() + enumConstantDeclaration.getLength() - 1);

            if (mappings.containsKey(signature_attribute)) {
                codeBlock = mappings.get(signature_attribute);
                oldTime = codeBlock.getLastHistory();

                if (oldTime.getTime().equals(commitCodeChange) && !diffMap.get(sourceFile).getTimes().contains(oldTime)){
                    if(diffMap.containsKey(sourceFile)){
                        diffMap.get(sourceFile).getTimes().add(oldTime);
                    }
                } else if (!oldTime.getTime().equals(commitCodeChange)){
                    for(int i = startLine; i <= endLine; i++){
                        if(isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())){
                            attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
                            commitCodeChange.addCodeChange(attriTime);
                            codeBlock.addHistory(attriTime);
                            if(diffMap.containsKey(sourceFile)){
                                diffMap.get(sourceFile).getTimes().add(attriTime);
                            }
                            break;
                        }
                    }
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

        CodeBlock codeBlock = null;
        MethodTime methodTime = null;
        CodeBlockTime oldTime = null;
        int startLine = cu.getLineNumber(md.getStartPosition());
        int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength() - 1);

        if (mappings.containsKey(signatureMethod)) {
            codeBlock = mappings.get(signatureMethod);
            oldTime = codeBlock.getLastHistory();


            if (oldTime.getTime().equals(commitCodeChange) && !diffMap.get(sourceFile).getTimes().contains(oldTime)){
                if(diffMap.containsKey(sourceFile)){
                    diffMap.get(sourceFile).getTimes().add(oldTime);
                }
            } else if (!oldTime.getTime().equals(commitCodeChange)){
                for(int i = startLine; i <= endLine; i++){
                    if(isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())){
                        methodTime = (MethodTime) codeBlock.getLastHistory().clone();
                        commitCodeChange.addCodeChange(methodTime);
                        codeBlock.addHistory(methodTime);
                        if(diffMap.containsKey(sourceFile)){
                            diffMap.get(sourceFile).getTimes().add(methodTime);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void attributeVisitor(CompilationUnit cu, FieldDeclaration fd, UMLAttribute umlAttribute, int index, String sourceFile){
        String attributeName = umlAttribute.getType() + "_" + umlAttribute.getName();
        String signature = umlAttribute.getClassName();
        String signatureAttribute = signature + ":" + attributeName;

        CodeBlock codeBlock;
        AttributeTime attriTime = null;
        CodeBlockTime oldTime = null;
        int startLine = cu.getLineNumber(fd.getStartPosition());
        int endLine = cu.getLineNumber(fd.getStartPosition() + fd.getLength() - 1);
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment)fd.fragments().get(index);

        if (mappings.containsKey(signatureAttribute)) {
            codeBlock = mappings.get(signatureAttribute);
            oldTime = codeBlock.getLastHistory();


            if (oldTime.getTime().equals(commitCodeChange) && !diffMap.get(sourceFile).getTimes().contains(oldTime)){
                if(diffMap.containsKey(sourceFile)){
                    diffMap.get(sourceFile).getTimes().add(oldTime);
                }
            } else if (!oldTime.getTime().equals(commitCodeChange)){
                for(int i = startLine; i <= endLine; i++){
                    if(isChanged(sourceFile, i, startLine, oldTime.getNewStartLineNum(), endLine, oldTime.getNewEndLineNum())){
                        attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
                        commitCodeChange.addCodeChange(attriTime);
                        codeBlock.addHistory(attriTime);
                        if(diffMap.containsKey(sourceFile)){
                            diffMap.get(sourceFile).getTimes().add(attriTime);
                        }
                        break;
                    }
                }
            }
        }
    }

    boolean isChanged(String sourceFile, int changeLine, int startLine, int oldStartLine, int endLine, int oldEndLine){
        return diffMap.containsKey(sourceFile)
                && (diffMap.get(sourceFile).containsOldChangeLine(changeLine));
    }

}
