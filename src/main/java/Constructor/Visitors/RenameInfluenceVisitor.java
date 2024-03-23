package Constructor.Visitors;

import Project.Utils.DiffFile;
import Constructor.RenameInfluenceProcessor;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class RenameInfluenceVisitor {
    private static final Logger logger = Logger.getLogger(RenameInfluenceVisitor.class);
    String sourceFile;
    private String hashCode = "";
    private DiffFile diffFile;
    private RenameInfluenceProcessor renameInfluenceProcessor;

    public void visit(Map<String, String> javaFileContents, String sourcePath, Map<String, DiffFile> diffMap){
        ASTParser parser = ASTParser.newParser(19);
        Iterator var4 = javaFileContents.keySet().iterator();
        RenameVisitor renameVisitor = new RenameVisitor();
        String[] environments = getEnvironments(sourcePath, javaFileContents);

        while(true) {
            String filePath;
            String javaFileContent;
            char[] charArray;
            do {
                if (!var4.hasNext()) {
                    return;
                }

                filePath = (String)var4.next();
                this.sourceFile = filePath;
                diffFile = diffMap.get(filePath);
                javaFileContent = javaFileContents.get(filePath);
                charArray = javaFileContent.toCharArray();

                Map<String, String> options = JavaCore.getOptions();
                options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                options.put("org.eclipse.jdt.core.compiler.source", "1.8");
                options.put("org.eclipse.jdt.core.compiler.compliance", "1.8");
                parser.setCompilerOptions(options);
                parser.setResolveBindings(true);
                parser.setKind(ASTParser.K_COMPILATION_UNIT);
                parser.setEnvironment(null, environments, null, true);  //setEnvironment（classpath,sourcepath,encoding,true）
                parser.setStatementsRecovery(true);
                parser.setUnitName(filePath.substring(filePath.lastIndexOf('/')+1));
                parser.setSource(charArray);
            } while((javaFileContent.contains("generated using freemarker") || javaFileContent.contains("generated using FreeMarker")) && !javaFileContent.contains("private static final String FREE_MARKER_GENERATED = \"generated using freemarker\";"));

            try {
                CompilationUnit compilationUnit = (CompilationUnit)parser.createAST((IProgressMonitor)null);
                renameVisitor.setCu(compilationUnit);
                compilationUnit.accept(renameVisitor);
            } catch (Exception var13) {
                var13.printStackTrace();
            }
        }
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public void setRenameInfluenceProcessor(RenameInfluenceProcessor renameInfluenceProcessor) {
        this.renameInfluenceProcessor = renameInfluenceProcessor;
    }

    private class RenameVisitor extends ASTVisitor{
        private CompilationUnit cu;

        @Override
        public boolean visit(SimpleName node){
            if (!renameInfluenceProcessor.isEmpty() && !isDeclared(node)){
                int line = cu.getLineNumber(node.getStartPosition());
                if (diffFile.containsChangeLine(line)){
                    IBinding binding = node.resolveBinding();
                    if (binding != null && binding.getKind() == IBinding.VARIABLE){

                        // rename variable
                        IVariableBinding variableBinding = (IVariableBinding) binding;
                        if (variableBinding.isField()) {
                            if (variableBinding.getDeclaringClass() != null && node.resolveTypeBinding() != null){
                                String field = variableBinding.getDeclaringClass().getQualifiedName() + ":" + node.resolveTypeBinding().getName() + "_" + node.getIdentifier();
                                renameInfluenceProcessor.renameInfluence(field, line, diffFile);
                            }
                        } else {
                            IMethodBinding methodBinding = variableBinding.getDeclaringMethod();
                            if (methodBinding != null){
                                String methodName;
                                try {
                                    methodName = getMethodSignatureByBinding(methodBinding);
                                } catch (Exception e){
                                    return super.visit(node);
                                }

                                String variable = methodName + ":" + node.getIdentifier();
                                renameInfluenceProcessor.renameInfluence(variable, line, diffFile);
                            }
                        }
                    } else if (binding != null && binding.getKind() == IBinding.TYPE){
                        // rename type
                        ITypeBinding typeBinding = (ITypeBinding) binding;
                        renameInfluenceProcessor.renameInfluence(typeBinding.getQualifiedName(), line, diffFile);
                    } else if (binding != null && binding.getKind() == IBinding.METHOD){
                        // rename method
                        IMethodBinding methodBinding = (IMethodBinding) binding;
                        try {
                            renameInfluenceProcessor.renameInfluence(getMethodSignatureByBinding(methodBinding), line, diffFile);
                        } catch (Exception e){
                            return super.visit(node);
                        }

                        if (methodBinding.isConstructor()){
                            ITypeBinding declaringClass = methodBinding.getDeclaringClass();
                            if (declaringClass != null){
                                renameInfluenceProcessor.renameInfluence(declaringClass.getQualifiedName(), line, diffFile);
                            }
                        }
                    }
                }
            }

            return super.visit(node);
        }

        public void setCu(CompilationUnit cu){
            this.cu = cu;
        }


        private String getMethodSignatureByBinding(IMethodBinding methodBinding) throws Exception{
            StringBuilder sb = new StringBuilder();

            ITypeBinding declaringClass = methodBinding.getDeclaringClass();
            sb.append(declaringClass.getQualifiedName()).append(":");

            ITypeBinding returnTypeBinding = methodBinding.getReturnType();
            if(returnTypeBinding != null){
//                sb.append(simpleNameOf(returnTypeBinding.getQualifiedName())).append("_");
                sb.append(returnTypeBinding.getName()).append("_");
            }

            sb.append(methodBinding.getName());

            StringJoiner paramJoiner = new StringJoiner(", ");
            ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                ITypeBinding parameterType = parameterTypes[i];
                String typeName = parameterType.getName();
                if(methodBinding.isVarargs() && i == parameterTypes.length - 1){
                    typeName = typeName.substring(0, typeName.lastIndexOf("[]")) + "...";
                }
                paramJoiner.add(typeName);
            }
            sb.append("(").append(paramJoiner).append(")");

            return sb.toString();
        }
    }

    private String getEnvironment(String sourcePath, String filePath, String fileContent){
        String[] lines = fileContent.split("\n");
        String currentPackage = "";

        for(int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.equals("") || line.startsWith("//")) {
                continue;
            } else if (line.startsWith("/*")) {
                while (!line.contains("*/")) {
                    i++;
                    line = lines[i].trim();
                }
            } else if (line.startsWith("package") && line.contains(";")) {
                currentPackage = line.substring(line.indexOf("package") + 7, line.lastIndexOf(";")).trim();
            }
        }

        String currentPath = "";
        if(filePath.contains("/")){
            currentPath = filePath.substring(0, filePath.lastIndexOf("/")).replaceAll("/", "\\\\");
        }
        String currentPackagePath = currentPackage.replaceAll("\\.", "\\\\");

        String basePath = "";
        if(!currentPackagePath.equals("")){
            if (currentPath.contains(currentPackagePath)){
                basePath = currentPath.substring(0, currentPath.lastIndexOf(currentPackagePath)-1);
            }
        }

        return sourcePath + "\\" + basePath;
    }

    private String[] getEnvironments(String sourcePath, Map<String, String> javaFileContents){
        Set<String> environment = new HashSet<>();
        for (String filePath : javaFileContents.keySet()){
            environment.add(getEnvironment(sourcePath, filePath, javaFileContents.get(filePath)));
        }
        return environment.toArray(new String[0]);
    }

    private boolean isDeclared(SimpleName name){
        ASTNode parent = name.getParent();
        return parent instanceof MethodDeclaration ||
                parent instanceof TypeDeclaration ||
                parent instanceof FieldDeclaration ||
                parent instanceof VariableDeclarationFragment;
    }
}
