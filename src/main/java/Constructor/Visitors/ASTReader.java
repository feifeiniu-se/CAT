package Constructor.Visitors;

import gr.uom.java.xmi.*;
import gr.uom.java.xmi.LocationInfo.CodeElementType;
import gr.uom.java.xmi.decomposition.OperationBody;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.decomposition.Visitor;

import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTReader {
    protected static final String FREE_MARKER_GENERATED = "generated using freemarker";
    protected static final String FREE_MARKER_GENERATED_2 = "generated using FreeMarker";

    public ASTReader(Map<String, String> javaFileContents) {
        processJavaFileContents(javaFileContents);
    }

    public static ASTNode processBlock(String methodBody) {
        ASTParser parser = ASTParser.newParser(19);
        Map<String, String> options = JavaCore.getOptions();
        options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
        options.put("org.eclipse.jdt.core.compiler.source", "1.8");
        options.put("org.eclipse.jdt.core.compiler.compliance", "1.8");
        parser.setCompilerOptions(options);
        parser.setResolveBindings(false);
        parser.setKind(2);
        parser.setStatementsRecovery(true);
        char[] charArray = methodBody.toCharArray();
        parser.setSource(charArray);
        ASTNode node = parser.createAST((IProgressMonitor)null);
        ASTNode methodBodyBlock = null;
        if (node instanceof Block) {
            Block extraBlockAddedByParser = (Block)node;
            if (extraBlockAddedByParser.statements().size() > 0) {
                methodBodyBlock = (ASTNode)extraBlockAddedByParser.statements().get(0);
            }
        }

        return methodBodyBlock;
    }

    protected void processJavaFileContents(Map<String, String> javaFileContents) {
        ASTParser parser = ASTParser.newParser(19);
        Iterator var4 = javaFileContents.keySet().iterator();

        while(true) {
            String filePath;
            String javaFileContent;
            char[] charArray;
            do {
                if (!var4.hasNext()) {
                    return;
                }

                filePath = (String)var4.next();
                Map<String, String> options = JavaCore.getOptions();
                options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                options.put("org.eclipse.jdt.core.compiler.source", "1.8");
                options.put("org.eclipse.jdt.core.compiler.compliance", "1.8");
                parser.setCompilerOptions(options);
                parser.setResolveBindings(false);
                parser.setKind(8);
                parser.setStatementsRecovery(true);
                javaFileContent = (String)javaFileContents.get(filePath);
                charArray = javaFileContent.toCharArray();
                parser.setSource(charArray);
            } while((javaFileContent.contains("generated using freemarker") || javaFileContent.contains("generated using FreeMarker")) && !javaFileContent.contains("private static final String FREE_MARKER_GENERATED = \"generated using freemarker\";"));

            try {
                CompilationUnit compilationUnit = (CompilationUnit)parser.createAST((IProgressMonitor)null);
                processCompilationUnit(filePath, compilationUnit, javaFileContent);
            } catch (Exception var13) {
                var13.printStackTrace();
            }
        }
    }


    protected void processCompilationUnit(String sourceFilePath, CompilationUnit compilationUnit, String javaFileContent) {
        List<UMLComment> comments = extractInternalComments(compilationUnit, sourceFilePath, javaFileContent);
        PackageDeclaration packageDeclaration = compilationUnit.getPackage();
        String packageName = null;
        UMLJavadoc packageDoc = null;
        if (packageDeclaration != null) {
            packageName = packageDeclaration.getName().getFullyQualifiedName();
            packageDoc = generateJavadoc(compilationUnit, sourceFilePath, packageDeclaration.getJavadoc());
        } else {
            packageName = "";
        }

        List<ImportDeclaration> imports = compilationUnit.imports();
        List<UMLImport> importedTypes = new ArrayList();
        Iterator var10 = imports.iterator();

        while(var10.hasNext()) {
            ImportDeclaration importDeclaration = (ImportDeclaration)var10.next();
            String elementName = importDeclaration.getName().getFullyQualifiedName();
            LocationInfo locationInfo = generateLocationInfo(compilationUnit, sourceFilePath, importDeclaration, CodeElementType.IMPORT_DECLARATION);
            UMLImport imported = new UMLImport(elementName, importDeclaration.isOnDemand(), importDeclaration.isStatic(), locationInfo);
            importedTypes.add(imported);
        }

        List<AbstractTypeDeclaration> topLevelTypeDeclarations = compilationUnit.types();
        Iterator var16 = topLevelTypeDeclarations.iterator();

        while(var16.hasNext()) {
            AbstractTypeDeclaration abstractTypeDeclaration = (AbstractTypeDeclaration)var16.next();
            if (abstractTypeDeclaration instanceof TypeDeclaration) {
                TypeDeclaration topLevelTypeDeclaration = (TypeDeclaration)abstractTypeDeclaration;
                processTypeDeclaration(compilationUnit, topLevelTypeDeclaration, packageName, sourceFilePath, importedTypes, packageDoc, comments);
            } else if (abstractTypeDeclaration instanceof EnumDeclaration) {
                EnumDeclaration enumDeclaration = (EnumDeclaration)abstractTypeDeclaration;
                processEnumDeclaration(compilationUnit, enumDeclaration, packageName, sourceFilePath, importedTypes, packageDoc, comments);
            }
        }

    }

    protected List<UMLComment> extractInternalComments(CompilationUnit cu, String sourceFile, String javaFileContent) {
        List<Comment> astComments = cu.getCommentList();
        List<UMLComment> comments = new ArrayList();
        Iterator var6 = astComments.iterator();

        while(var6.hasNext()) {
            Comment comment = (Comment)var6.next();
            LocationInfo locationInfo = null;
            if (comment.isLineComment()) {
                locationInfo = generateLocationInfo(cu, sourceFile, comment, CodeElementType.LINE_COMMENT);
            } else if (comment.isBlockComment()) {
                locationInfo = generateLocationInfo(cu, sourceFile, comment, CodeElementType.BLOCK_COMMENT);
            }

            if (locationInfo != null) {
                int start = comment.getStartPosition();
                int end = start + comment.getLength();
                String text = javaFileContent.substring(start, end);
                UMLComment umlComment = new UMLComment(text, locationInfo);
                comments.add(umlComment);
            }
        }

        return comments;
    }

    protected void distributeComments(List<UMLComment> compilationUnitComments, LocationInfo codeElementLocationInfo, List<UMLComment> codeElementComments) {
        ListIterator listIterator = compilationUnitComments.listIterator(compilationUnitComments.size());

        while(true) {
            UMLComment comment;
            LocationInfo commentLocationInfo;
            do {
                if (!listIterator.hasPrevious()) {
                    compilationUnitComments.removeAll(codeElementComments);
                    return;
                }

                comment = (UMLComment)listIterator.previous();
                commentLocationInfo = comment.getLocationInfo();
            } while(!codeElementLocationInfo.subsumes(commentLocationInfo) && !codeElementLocationInfo.sameLine(commentLocationInfo) && (!codeElementLocationInfo.nextLine(commentLocationInfo) || codeElementLocationInfo.getCodeElementType().equals(CodeElementType.ANONYMOUS_CLASS_DECLARATION)) && (codeElementComments.size() <= 0 || !((UMLComment)codeElementComments.get(0)).getLocationInfo().nextLine(commentLocationInfo)));

            codeElementComments.add(0, comment);
        }
    }

    protected UMLJavadoc generateJavadoc(CompilationUnit cu, BodyDeclaration bodyDeclaration, String sourceFile) {
        Javadoc javaDoc = bodyDeclaration.getJavadoc();
        return generateJavadoc(cu, sourceFile, javaDoc);
    }

    protected UMLJavadoc generateJavadoc(CompilationUnit cu, String sourceFile, Javadoc javaDoc) {
        UMLJavadoc doc = null;
        if (javaDoc != null) {
            LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, javaDoc, CodeElementType.JAVADOC);
            doc = new UMLJavadoc(locationInfo);
            List<TagElement> tags = javaDoc.tags();
            Iterator var7 = tags.iterator();

            while(var7.hasNext()) {
                TagElement tag = (TagElement)var7.next();
                UMLTagElement tagElement = new UMLTagElement(tag.getTagName());
                List fragments = tag.fragments();
                Iterator var11 = fragments.iterator();

                while(var11.hasNext()) {
                    Object docElement = var11.next();
                    tagElement.addFragment(docElement.toString());
                }

                doc.addTag(tagElement);
            }
        }

        return doc;
    }

    protected void processEnumDeclaration(CompilationUnit cu, EnumDeclaration enumDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments) {
        UMLJavadoc javadoc = generateJavadoc(cu, (BodyDeclaration)enumDeclaration, (String)sourceFile);
        if (javadoc == null || !javadoc.containsIgnoreCase("generated using freemarker")) {
            String className = enumDeclaration.getName().getFullyQualifiedName();
            LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, enumDeclaration, CodeElementType.TYPE_DECLARATION);
            UMLClass umlClass = new UMLClass(packageName, className, locationInfo, enumDeclaration.isPackageMemberTypeDeclaration(), importedTypes);
            umlClass.setJavadoc(javadoc);
            if (enumDeclaration.isPackageMemberTypeDeclaration()) {
                umlClass.setPackageDeclarationJavadoc(packageDoc);
                Iterator var12 = comments.iterator();

                while(var12.hasNext()) {
                    UMLComment comment = (UMLComment)var12.next();
                    if (comment.getLocationInfo().getStartLine() == 1) {
                        umlClass.getPackageDeclarationComments().add(comment);
                    }
                }
            }

            umlClass.setEnum(true);
            List<Type> superInterfaceTypes = enumDeclaration.superInterfaceTypes();
            Iterator var19 = superInterfaceTypes.iterator();

            while(var19.hasNext()) {
                Type interfaceType = (Type)var19.next();
                UMLType umlType = UMLType.extractTypeObject(cu, sourceFile, interfaceType, 0);
                UMLRealization umlRealization = new UMLRealization(umlClass, umlType.getClassType());
                umlClass.addImplementedInterface(umlType);
            }

            List<EnumConstantDeclaration> enumConstantDeclarations = enumDeclaration.enumConstants();
            Iterator var21 = enumConstantDeclarations.iterator();

            while(var21.hasNext()) {
                EnumConstantDeclaration enumConstantDeclaration = (EnumConstantDeclaration)var21.next();
                processEnumConstantDeclaration(cu, enumConstantDeclaration, sourceFile, umlClass, comments);
            }

            processModifiers(cu, sourceFile, enumDeclaration, umlClass);
            Map<BodyDeclaration, VariableDeclarationContainer> map = processBodyDeclarations(cu, enumDeclaration, packageName, sourceFile, importedTypes, umlClass, packageDoc, comments);
            processAnonymousClassDeclarations(cu, enumDeclaration, packageName, sourceFile, className, importedTypes, packageDoc, comments, umlClass);
            Iterator var24 = map.keySet().iterator();

            while(var24.hasNext()) {
                BodyDeclaration declaration = (BodyDeclaration)var24.next();
                if (declaration instanceof MethodDeclaration) {
                    UMLOperation operation = (UMLOperation)map.get(declaration);
                    processMethodBody(cu, sourceFile, (MethodDeclaration)declaration, operation, umlClass.getAttributes());
                } else if (declaration instanceof Initializer) {
                    UMLInitializer initializer = (UMLInitializer)map.get(declaration);
                    processInitializerBody(cu, sourceFile, (Initializer)declaration, initializer, umlClass.getAttributes());
                }
            }

            distributeComments(comments, locationInfo, umlClass.getComments());
        }
    }

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

                    while(var15.hasNext()) {
                        UMLAttribute attribute = (UMLAttribute)var15.next();
                        attribute.setClassName(umlClass.getName());
                        umlClass.addAttribute(attribute);
                    }
                } else if (bodyDeclaration instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                    UMLOperation operation = processMethodDeclaration(cu, methodDeclaration, packageName, umlClass.isInterface(), sourceFile, comments);
                    operation.setClassName(umlClass.getName());
                    umlClass.addOperation(operation);
                    map.put(methodDeclaration, operation);
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

    protected void processTypeDeclaration(CompilationUnit cu, TypeDeclaration typeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments) {
        UMLJavadoc javadoc = generateJavadoc(cu, (BodyDeclaration)typeDeclaration, (String)sourceFile);
        if (javadoc == null || !javadoc.containsIgnoreCase("generated using freemarker")) {
            String className = typeDeclaration.getName().getFullyQualifiedName();
            LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, typeDeclaration, CodeElementType.TYPE_DECLARATION);
            UMLClass umlClass = new UMLClass(packageName, className, locationInfo, typeDeclaration.isPackageMemberTypeDeclaration(), importedTypes);
            umlClass.setJavadoc(javadoc);
            if (typeDeclaration.isPackageMemberTypeDeclaration()) {
                umlClass.setPackageDeclarationJavadoc(packageDoc);
                Iterator var12 = comments.iterator();

                while(var12.hasNext()) {
                    UMLComment comment = (UMLComment)var12.next();
                    if (comment.getLocationInfo().getStartLine() == 1) {
                        umlClass.getPackageDeclarationComments().add(comment);
                    }
                }
            }

            if (typeDeclaration.isInterface()) {
                umlClass.setInterface(true);
            }

            processModifiers(cu, sourceFile, typeDeclaration, umlClass);
            List<TypeParameter> typeParameters = typeDeclaration.typeParameters();
            Iterator var22 = typeParameters.iterator();

            while(var22.hasNext()) {
                TypeParameter typeParameter = (TypeParameter)var22.next();
                UMLTypeParameter umlTypeParameter = new UMLTypeParameter(typeParameter.getName().getFullyQualifiedName(), generateLocationInfo(cu, sourceFile, typeParameter, CodeElementType.TYPE_PARAMETER));
                List<Type> typeBounds = typeParameter.typeBounds();
                Iterator var17 = typeBounds.iterator();

                while(var17.hasNext()) {
                    Type type = (Type)var17.next();
                    umlTypeParameter.addTypeBound(UMLType.extractTypeObject(cu, sourceFile, type, 0));
                }

                List<IExtendedModifier> typeParameterExtendedModifiers = typeParameter.modifiers();
                Iterator var34 = typeParameterExtendedModifiers.iterator();

                while(var34.hasNext()) {
                    IExtendedModifier extendedModifier = (IExtendedModifier)var34.next();
                    if (extendedModifier.isAnnotation()) {
                        Annotation annotation = (Annotation)extendedModifier;
                        umlTypeParameter.addAnnotation(new UMLAnnotation(cu, sourceFile, annotation));
                    }
                }

                umlClass.addTypeParameter(umlTypeParameter);
            }

            Type superclassType = typeDeclaration.getSuperclassType();
            if (superclassType != null) {
                UMLType umlType = UMLType.extractTypeObject(cu, sourceFile, superclassType, 0);
                UMLGeneralization umlGeneralization = new UMLGeneralization(umlClass, umlType.getClassType());
                umlClass.setSuperclass(umlType);
            }

            List<Type> superInterfaceTypes = typeDeclaration.superInterfaceTypes();
            Iterator var27 = superInterfaceTypes.iterator();

            while(var27.hasNext()) {
                Type interfaceType = (Type)var27.next();
                UMLType umlType = UMLType.extractTypeObject(cu, sourceFile, interfaceType, 0);
                UMLRealization umlRealization = new UMLRealization(umlClass, umlType.getClassType());
                umlClass.addImplementedInterface(umlType);
            }

            Map<BodyDeclaration, VariableDeclarationContainer> map = processBodyDeclarations(cu, typeDeclaration, packageName, sourceFile, importedTypes, umlClass, packageDoc, comments);
            processAnonymousClassDeclarations(cu, typeDeclaration, packageName, sourceFile, className, importedTypes, packageDoc, comments, umlClass);
            Iterator var30 = map.keySet().iterator();

            while(var30.hasNext()) {
                BodyDeclaration declaration = (BodyDeclaration)var30.next();
                if (declaration instanceof MethodDeclaration) {
                    UMLOperation operation = (UMLOperation)map.get(declaration);
                    processMethodBody(cu, sourceFile, (MethodDeclaration)declaration, operation, umlClass.getAttributes());
                } else if (declaration instanceof Initializer) {
                    UMLInitializer initializer = (UMLInitializer)map.get(declaration);
                    processInitializerBody(cu, sourceFile, (Initializer)declaration, initializer, umlClass.getAttributes());
                }
            }

            distributeComments(comments, locationInfo, umlClass.getComments());
        }
    }

    protected void processAnonymousClassDeclarations(CompilationUnit cu, AbstractTypeDeclaration typeDeclaration, String packageName, String sourceFile, String className, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> allComments, UMLClass umlClass) {
        AnonymousClassDeclarationVisitor visitor = new AnonymousClassDeclarationVisitor();
        typeDeclaration.accept(visitor);
        Set<AnonymousClassDeclaration> anonymousClassDeclarations = visitor.getAnonymousClassDeclarations();
        Set<TypeDeclarationStatement> typeDeclarationStatements = visitor.getTypeDeclarationStatements();
        Iterator var13 = typeDeclarationStatements.iterator();

        while(var13.hasNext()) {
            TypeDeclarationStatement statement = (TypeDeclarationStatement)var13.next();
            String methodNamePath = getMethodNamePath(statement);
            String fullName = packageName + "." + className + "." + methodNamePath;
            AbstractTypeDeclaration localTypeDeclaration = statement.getDeclaration();
            if (localTypeDeclaration instanceof TypeDeclaration) {
                TypeDeclaration typeDeclaration2 = (TypeDeclaration)localTypeDeclaration;
                processTypeDeclaration(cu, typeDeclaration2, fullName, sourceFile, importedTypes, packageDoc, allComments);
            } else if (localTypeDeclaration instanceof EnumDeclaration) {
                EnumDeclaration enumDeclaration = (EnumDeclaration)localTypeDeclaration;
                processEnumDeclaration(cu, enumDeclaration, fullName, sourceFile, importedTypes, packageDoc, allComments);
            }
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        Iterator var37 = anonymousClassDeclarations.iterator();

        while(var37.hasNext()) {
            AnonymousClassDeclaration anonymous = (AnonymousClassDeclaration)var37.next();
            insertNode(anonymous, root);
        }

        List<UMLAnonymousClass> createdAnonymousClasses = new ArrayList();
        Enumeration enumeration = root.postorderEnumeration();

        while(true) {
            UMLOperation matchingOperation;
            UMLAttribute matchingAttribute;
            UMLInitializer matchingInitializer;
            UMLEnumConstant matchingEnumConstant;
            List comments;
            DefaultMutableTreeNode node;
            AnonymousClassDeclaration anonymous;
            do {
                do {
                    if (!enumeration.hasMoreElements()) {
                        return;
                    }

                    node = (DefaultMutableTreeNode)enumeration.nextElement();
                } while(node.getUserObject() == null);

                anonymous = (AnonymousClassDeclaration)node.getUserObject();
                boolean operationFound = false;
                boolean attributeFound = false;
                boolean initializerFound = false;
                matchingOperation = null;
                matchingAttribute = null;
                matchingInitializer = null;
                matchingEnumConstant = null;
                comments = null;
                Iterator var26 = umlClass.getOperations().iterator();

                while(var26.hasNext()) {
                    UMLOperation operation = (UMLOperation)var26.next();
                    if (operation.getLocationInfo().getStartOffset() <= anonymous.getStartPosition() && operation.getLocationInfo().getEndOffset() >= anonymous.getStartPosition() + anonymous.getLength()) {
                        comments = operation.getComments();
                        operationFound = true;
                        matchingOperation = operation;
                        break;
                    }
                }

                if (!operationFound) {
                    var26 = umlClass.getAttributes().iterator();

                    while(var26.hasNext()) {
                        UMLAttribute attribute = (UMLAttribute)var26.next();
                        if (attribute.getLocationInfo().getStartOffset() <= anonymous.getStartPosition() && attribute.getLocationInfo().getEndOffset() >= anonymous.getStartPosition() + anonymous.getLength()) {
                            comments = attribute.getComments();
                            attributeFound = true;
                            matchingAttribute = attribute;
                            break;
                        }
                    }
                }

                if (!operationFound && !attributeFound) {
                    var26 = umlClass.getInitializers().iterator();

                    while(var26.hasNext()) {
                        UMLInitializer initializer = (UMLInitializer)var26.next();
                        if (initializer.getLocationInfo().getStartOffset() <= anonymous.getStartPosition() && initializer.getLocationInfo().getEndOffset() >= anonymous.getStartPosition() + anonymous.getLength()) {
                            comments = initializer.getComments();
                            initializerFound = true;
                            matchingInitializer = initializer;
                            break;
                        }
                    }
                }

                if (!operationFound && !attributeFound && !initializerFound) {
                    var26 = umlClass.getEnumConstants().iterator();

                    while(var26.hasNext()) {
                        UMLEnumConstant enumConstant = (UMLEnumConstant)var26.next();
                        if (enumConstant.getLocationInfo().getStartOffset() <= anonymous.getStartPosition() && enumConstant.getLocationInfo().getEndOffset() >= anonymous.getStartPosition() + anonymous.getLength()) {
                            comments = enumConstant.getComments();
                            matchingEnumConstant = enumConstant;
                            break;
                        }
                    }
                }
            } while(matchingOperation == null && matchingAttribute == null && matchingInitializer == null && matchingEnumConstant == null);

            String anonymousBinaryName = getAnonymousBinaryName(node);
            String anonymousCodePath = getAnonymousCodePath(node);
            UMLAnonymousClass anonymousClass = processAnonymousClassDeclaration(cu, anonymous, packageName + "." + className, anonymousBinaryName, anonymousCodePath, sourceFile, comments, umlClass.getImportedTypes());
            umlClass.addAnonymousClass(anonymousClass);
            if (matchingOperation != null) {
                matchingOperation.addAnonymousClass(anonymousClass);
                anonymousClass.addParentContainer(matchingOperation);
            }

            if (matchingAttribute != null) {
                matchingAttribute.addAnonymousClass(anonymousClass);
                anonymousClass.addParentContainer(matchingAttribute);
            }

            if (matchingInitializer != null) {
                matchingInitializer.addAnonymousClass(anonymousClass);
                anonymousClass.addParentContainer(matchingInitializer);
            }

            if (matchingEnumConstant != null) {
                matchingEnumConstant.addAnonymousClass(anonymousClass);
                anonymousClass.addParentContainer(matchingEnumConstant);
            }

            Iterator var29 = anonymousClass.getOperations().iterator();

            Iterator var31;
            UMLAnonymousClass createdAnonymousClass;
            while(var29.hasNext()) {
                UMLOperation operation = (UMLOperation)var29.next();
                var31 = createdAnonymousClasses.iterator();

                while(var31.hasNext()) {
                    createdAnonymousClass = (UMLAnonymousClass)var31.next();
                    if (operation.getLocationInfo().subsumes(createdAnonymousClass.getLocationInfo())) {
                        operation.addAnonymousClass(createdAnonymousClass);
                        createdAnonymousClass.addParentContainer(operation);
                    }
                }
            }

            var29 = anonymousClass.getAttributes().iterator();

            while(var29.hasNext()) {
                UMLAttribute attribute = (UMLAttribute)var29.next();
                var31 = createdAnonymousClasses.iterator();

                while(var31.hasNext()) {
                    createdAnonymousClass = (UMLAnonymousClass)var31.next();
                    if (attribute.getLocationInfo().subsumes(createdAnonymousClass.getLocationInfo())) {
                        attribute.addAnonymousClass(createdAnonymousClass);
                        createdAnonymousClass.addParentContainer(attribute);
                    }
                }
            }

            var29 = anonymousClass.getInitializers().iterator();

            while(var29.hasNext()) {
                UMLInitializer initializer = (UMLInitializer)var29.next();
                var31 = createdAnonymousClasses.iterator();

                while(var31.hasNext()) {
                    createdAnonymousClass = (UMLAnonymousClass)var31.next();
                    if (initializer.getLocationInfo().subsumes(createdAnonymousClass.getLocationInfo())) {
                        initializer.addAnonymousClass(createdAnonymousClass);
                        createdAnonymousClass.addParentContainer(initializer);
                    }
                }
            }

            var29 = anonymousClass.getEnumConstants().iterator();

            while(var29.hasNext()) {
                UMLEnumConstant enumConstant = (UMLEnumConstant)var29.next();
                var31 = createdAnonymousClasses.iterator();

                while(var31.hasNext()) {
                    createdAnonymousClass = (UMLAnonymousClass)var31.next();
                    if (enumConstant.getLocationInfo().subsumes(createdAnonymousClass.getLocationInfo())) {
                        enumConstant.addAnonymousClass(createdAnonymousClass);
                        createdAnonymousClass.addParentContainer(enumConstant);
                    }
                }
            }

            createdAnonymousClasses.add(anonymousClass);
            List<BodyDeclaration> bodyDeclarations = anonymous.bodyDeclarations();
            int i = 0;
            int j = 0;
            Iterator var56 = bodyDeclarations.iterator();

            while(var56.hasNext()) {
                BodyDeclaration bodyDeclaration = (BodyDeclaration)var56.next();
                if (bodyDeclaration instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                    UMLOperation operation = (UMLOperation)anonymousClass.getOperations().get(i);
                    processMethodBody(cu, sourceFile, methodDeclaration, operation, umlClass.getAttributes());
                    ++i;
                } else if (bodyDeclaration instanceof Initializer) {
                    Initializer initializer = (Initializer)bodyDeclaration;
                    UMLInitializer umlInitializer = (UMLInitializer)anonymousClass.getInitializers().get(j);
                    processInitializerBody(cu, sourceFile, initializer, umlInitializer, umlClass.getAttributes());
                    ++j;
                }
            }
        }
    }

    protected void processMethodBody(CompilationUnit cu, String sourceFile, MethodDeclaration methodDeclaration, UMLOperation operation, List<UMLAttribute> attributes) {
        Block block = methodDeclaration.getBody();
        if (block != null) {
            OperationBody body = new OperationBody(cu, sourceFile, block, operation, attributes);
            operation.setBody(body);
        } else {
            operation.setBody((OperationBody)null);
        }

    }

    protected void processInitializerBody(CompilationUnit cu, String sourceFile, Initializer initializer, UMLInitializer umlInitializer, List<UMLAttribute> attributes) {
        Block block = initializer.getBody();
        if (block != null) {
            OperationBody body = new OperationBody(cu, sourceFile, block, umlInitializer, attributes);
            umlInitializer.setBody(body);
        } else {
            umlInitializer.setBody((OperationBody)null);
        }

    }

    protected void processModifiers(CompilationUnit cu, String sourceFile, AbstractTypeDeclaration typeDeclaration, UMLClass umlClass) {
        int modifiers = typeDeclaration.getModifiers();
        if ((modifiers & 1024) != 0) {
            umlClass.setAbstract(true);
        }

        if ((modifiers & 8) != 0) {
            umlClass.setStatic(true);
        }

        if ((modifiers & 16) != 0) {
            umlClass.setFinal(true);
        }

        if ((modifiers & 1) != 0) {
            umlClass.setVisibility(Visibility.PUBLIC);
        } else if ((modifiers & 4) != 0) {
            umlClass.setVisibility(Visibility.PROTECTED);
        } else if ((modifiers & 2) != 0) {
            umlClass.setVisibility(Visibility.PRIVATE);
        } else {
            umlClass.setVisibility(Visibility.PACKAGE);
        }

        List<IExtendedModifier> extendedModifiers = typeDeclaration.modifiers();
        Iterator var7 = extendedModifiers.iterator();

        while(var7.hasNext()) {
            IExtendedModifier extendedModifier = (IExtendedModifier)var7.next();
            if (extendedModifier.isAnnotation()) {
                Annotation annotation = (Annotation)extendedModifier;
                umlClass.addAnnotation(new UMLAnnotation(cu, sourceFile, annotation));
            } else if (extendedModifier.isModifier()) {
                Modifier modifier = (Modifier)extendedModifier;
                umlClass.addModifier(new UMLModifier(cu, sourceFile, modifier));
            }
        }

    }

    protected UMLInitializer processInitializer(CompilationUnit cu, Initializer initializer, String packageName, boolean isInterfaceMethod, String sourceFile, List<UMLComment> comments) {
        UMLJavadoc javadoc = generateJavadoc(cu, (BodyDeclaration)initializer, (String)sourceFile);
        String name = "";
        if (initializer.getParent() instanceof AnonymousClassDeclaration && initializer.getParent().getParent() instanceof ClassInstanceCreation) {
            ClassInstanceCreation creation = (ClassInstanceCreation)initializer.getParent().getParent();
            name = Visitor.stringify(creation.getType());
        } else if (initializer.getParent() instanceof AbstractTypeDeclaration) {
            AbstractTypeDeclaration typeDeclaration = (AbstractTypeDeclaration)initializer.getParent();
            name = typeDeclaration.getName().getIdentifier();
        }

        LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, initializer, CodeElementType.INITIALIZER);
        UMLInitializer umlInitializer = new UMLInitializer(name, locationInfo);
        umlInitializer.setJavadoc(javadoc);
        distributeComments(comments, locationInfo, umlInitializer.getComments());
        int methodModifiers = initializer.getModifiers();
        if ((methodModifiers & 8) != 0) {
            umlInitializer.setStatic(true);
        }

        return umlInitializer;
    }

    protected UMLOperation processMethodDeclaration(CompilationUnit cu, MethodDeclaration methodDeclaration, String packageName, boolean isInterfaceMethod, String sourceFile, List<UMLComment> comments) {
        UMLJavadoc javadoc = generateJavadoc(cu, (BodyDeclaration)methodDeclaration, (String)sourceFile);
        String methodName = methodDeclaration.getName().getFullyQualifiedName();
        LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, methodDeclaration, CodeElementType.METHOD_DECLARATION);
        UMLOperation umlOperation = new UMLOperation(methodName, locationInfo);
        umlOperation.setJavadoc(javadoc);
        distributeComments(comments, locationInfo, umlOperation.getComments());
        if (methodDeclaration.isConstructor()) {
            umlOperation.setConstructor(true);
        }

        int methodModifiers = methodDeclaration.getModifiers();
        if ((methodModifiers & 1) != 0) {
            umlOperation.setVisibility(Visibility.PUBLIC);
        } else if ((methodModifiers & 4) != 0) {
            umlOperation.setVisibility(Visibility.PROTECTED);
        } else if ((methodModifiers & 2) != 0) {
            umlOperation.setVisibility(Visibility.PRIVATE);
        } else if (isInterfaceMethod) {
            umlOperation.setVisibility(Visibility.PUBLIC);
        } else {
            umlOperation.setVisibility(Visibility.PACKAGE);
        }

        if ((methodModifiers & 1024) != 0) {
            umlOperation.setAbstract(true);
        }

        if ((methodModifiers & 16) != 0) {
            umlOperation.setFinal(true);
        }

        if ((methodModifiers & 8) != 0) {
            umlOperation.setStatic(true);
        }

        if ((methodModifiers & 32) != 0) {
            umlOperation.setSynchronized(true);
        }

        if ((methodModifiers & 256) != 0) {
            umlOperation.setNative(true);
        }

        List<IExtendedModifier> extendedModifiers = methodDeclaration.modifiers();
        Iterator var13 = extendedModifiers.iterator();

        while(var13.hasNext()) {
            IExtendedModifier extendedModifier = (IExtendedModifier)var13.next();
            if (extendedModifier.isAnnotation()) {
                Annotation annotation = (Annotation)extendedModifier;
                umlOperation.addAnnotation(new UMLAnnotation(cu, sourceFile, annotation));
            } else if (extendedModifier.isModifier()) {
                Modifier modifier = (Modifier)extendedModifier;
                umlOperation.addModifier(new UMLModifier(cu, sourceFile, modifier));
            }
        }

        List<TypeParameter> typeParameters = methodDeclaration.typeParameters();
        Iterator var24 = typeParameters.iterator();

        while(var24.hasNext()) {
            TypeParameter typeParameter = (TypeParameter)var24.next();
            UMLTypeParameter umlTypeParameter = new UMLTypeParameter(typeParameter.getName().getFullyQualifiedName(), generateLocationInfo(cu, sourceFile, typeParameter, CodeElementType.TYPE_PARAMETER));
            List<Type> typeBounds = typeParameter.typeBounds();
            Iterator var18 = typeBounds.iterator();

            while(var18.hasNext()) {
                Type type = (Type)var18.next();
                umlTypeParameter.addTypeBound(UMLType.extractTypeObject(cu, sourceFile, type, 0));
            }

            List<IExtendedModifier> typeParameterExtendedModifiers = typeParameter.modifiers();
            Iterator var37 = typeParameterExtendedModifiers.iterator();

            while(var37.hasNext()) {
                IExtendedModifier extendedModifier = (IExtendedModifier)var37.next();
                if (extendedModifier.isAnnotation()) {
                    Annotation annotation = (Annotation)extendedModifier;
                    umlTypeParameter.addAnnotation(new UMLAnnotation(cu, sourceFile, annotation));
                }
            }

            umlOperation.addTypeParameter(umlTypeParameter);
        }

        Type returnType = methodDeclaration.getReturnType2();
        if (returnType != null) {
            UMLType type = UMLType.extractTypeObject(cu, sourceFile, returnType, methodDeclaration.getExtraDimensions());
            UMLParameter returnParameter = new UMLParameter("return", type, "return", false);
            umlOperation.addParameter(returnParameter);
        }

        List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
        Iterator var31 = parameters.iterator();

        Type thrownExceptionType;
        while(var31.hasNext()) {
            SingleVariableDeclaration parameter = (SingleVariableDeclaration)var31.next();
            thrownExceptionType = parameter.getType();
            String parameterName = parameter.getName().getFullyQualifiedName();
            UMLType type = UMLType.extractTypeObject(cu, sourceFile, thrownExceptionType, parameter.getExtraDimensions());
            if (parameter.isVarargs()) {
                type.setVarargs();
            }

            UMLParameter umlParameter = new UMLParameter(parameterName, type, "in", parameter.isVarargs());
            VariableDeclaration variableDeclaration = new VariableDeclaration(cu, sourceFile, parameter, umlOperation, parameter.isVarargs());
            variableDeclaration.setParameter(true);
            umlParameter.setVariableDeclaration(variableDeclaration);
            umlOperation.addParameter(umlParameter);
        }

        List<Type> thrownExceptionTypes = methodDeclaration.thrownExceptionTypes();
        Iterator var34 = thrownExceptionTypes.iterator();

        while(var34.hasNext()) {
            thrownExceptionType = (Type)var34.next();
            UMLType type = UMLType.extractTypeObject(cu, sourceFile, thrownExceptionType, 0);
            umlOperation.addThrownExceptionType(type);
        }

        return umlOperation;
    }

    protected void processEnumConstantDeclaration(CompilationUnit cu, EnumConstantDeclaration enumConstantDeclaration, String sourceFile, UMLClass umlClass, List<UMLComment> comments) {
        UMLJavadoc javadoc = generateJavadoc(cu, (BodyDeclaration)enumConstantDeclaration, (String)sourceFile);
        LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, enumConstantDeclaration, CodeElementType.ENUM_CONSTANT_DECLARATION);
        UMLEnumConstant enumConstant = new UMLEnumConstant(enumConstantDeclaration.getName().getIdentifier(), UMLType.extractTypeObject(umlClass.getName()), locationInfo);
        VariableDeclaration variableDeclaration = new VariableDeclaration(cu, sourceFile, enumConstantDeclaration);
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
            enumConstant.addArgument(Visitor.stringify(argument));
        }

        enumConstant.setClassName(umlClass.getName());
        umlClass.addEnumConstant(enumConstant);
    }

    protected List<UMLAttribute> processFieldDeclaration(CompilationUnit cu, FieldDeclaration fieldDeclaration, boolean isInterfaceField, String sourceFile, List<UMLComment> comments) {
        UMLJavadoc javadoc = generateJavadoc(cu, (BodyDeclaration)fieldDeclaration, (String)sourceFile);
        List<UMLAttribute> attributes = new ArrayList();
        Type fieldType = fieldDeclaration.getType();
        List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();

        UMLAttribute umlAttribute;
        for(Iterator var10 = fragments.iterator(); var10.hasNext(); attributes.add(umlAttribute)) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment)var10.next();
            UMLType type = UMLType.extractTypeObject(cu, sourceFile, fieldType, fragment.getExtraDimensions());
            String fieldName = fragment.getName().getFullyQualifiedName();
            LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, fragment, CodeElementType.FIELD_DECLARATION);
            umlAttribute = new UMLAttribute(fieldName, type, locationInfo);
            VariableDeclaration variableDeclaration = new VariableDeclaration(cu, sourceFile, fragment, umlAttribute);
            variableDeclaration.setAttribute(true);
            umlAttribute.setVariableDeclaration(variableDeclaration);
            umlAttribute.setJavadoc(javadoc);
            distributeComments(comments, locationInfo, umlAttribute.getComments());
            int fieldModifiers = fieldDeclaration.getModifiers();
            if ((fieldModifiers & 1) != 0) {
                umlAttribute.setVisibility(Visibility.PUBLIC);
            } else if ((fieldModifiers & 4) != 0) {
                umlAttribute.setVisibility(Visibility.PROTECTED);
            } else if ((fieldModifiers & 2) != 0) {
                umlAttribute.setVisibility(Visibility.PRIVATE);
            } else if (isInterfaceField) {
                umlAttribute.setVisibility(Visibility.PUBLIC);
            } else {
                umlAttribute.setVisibility(Visibility.PACKAGE);
            }

            if ((fieldModifiers & 16) != 0) {
                umlAttribute.setFinal(true);
            }

            if ((fieldModifiers & 8) != 0) {
                umlAttribute.setStatic(true);
            }

            if ((fieldModifiers & 64) != 0) {
                umlAttribute.setVolatile(true);
            }

            if ((fieldModifiers & 128) != 0) {
                umlAttribute.setTransient(true);
            }
        }

        return attributes;
    }

    protected UMLAnonymousClass processAnonymousClassDeclaration(CompilationUnit cu, AnonymousClassDeclaration anonymous, String packageName, String binaryName, String codePath, String sourceFile, List<UMLComment> comments, List<UMLImport> importedTypes) {
        List<BodyDeclaration> bodyDeclarations = anonymous.bodyDeclarations();
        LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, anonymous, CodeElementType.ANONYMOUS_CLASS_DECLARATION);
        UMLAnonymousClass anonymousClass = new UMLAnonymousClass(packageName, binaryName, codePath, locationInfo, importedTypes);
        Iterator var12 = bodyDeclarations.iterator();

        while(true) {
            while(var12.hasNext()) {
                BodyDeclaration bodyDeclaration = (BodyDeclaration)var12.next();
                if (bodyDeclaration instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration)bodyDeclaration;
                    List<UMLAttribute> attributes = processFieldDeclaration(cu, fieldDeclaration, false, sourceFile, comments);
                    Iterator var16 = attributes.iterator();

                    while(var16.hasNext()) {
                        UMLAttribute attribute = (UMLAttribute)var16.next();
                        attribute.setClassName(anonymousClass.getCodePath());
                        attribute.setAnonymousClassContainer(anonymousClass);
                        anonymousClass.addAttribute(attribute);
                    }
                } else if (bodyDeclaration instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                    UMLOperation operation = processMethodDeclaration(cu, methodDeclaration, packageName, false, sourceFile, comments);
                    operation.setClassName(anonymousClass.getCodePath());
                    operation.setAnonymousClassContainer(anonymousClass);
                    anonymousClass.addOperation(operation);
                } else if (bodyDeclaration instanceof Initializer) {
                    Initializer initializer = (Initializer)bodyDeclaration;
                    UMLInitializer umlInitializer = processInitializer(cu, initializer, packageName, false, sourceFile, comments);
                    umlInitializer.setClassName(anonymousClass.getCodePath());
                    umlInitializer.setAnonymousClassContainer(anonymousClass);
                    anonymousClass.addInitializer(umlInitializer);
                }
            }

            distributeComments(comments, locationInfo, anonymousClass.getComments());
            return anonymousClass;
        }
    }

    protected void insertNode(AnonymousClassDeclaration childAnonymous, DefaultMutableTreeNode root) {
        Enumeration enumeration = root.postorderEnumeration();
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childAnonymous);
        DefaultMutableTreeNode parentNode = root;

        while(enumeration.hasMoreElements()) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)enumeration.nextElement();
            AnonymousClassDeclaration currentAnonymous = (AnonymousClassDeclaration)currentNode.getUserObject();
            if (currentAnonymous != null && isParent(childAnonymous, currentAnonymous)) {
                parentNode = currentNode;
                break;
            }
        }

        parentNode.add(childNode);
    }

    protected String getMethodNamePath(TypeDeclarationStatement statement) {
        String name = "";

        for(ASTNode parent = statement.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof MethodDeclaration) {
                String methodName = ((MethodDeclaration)parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = methodName;
                } else {
                    name = methodName + "." + name;
                }
            }
        }

        return name;
    }

    protected String getAnonymousCodePath(DefaultMutableTreeNode node) {
        AnonymousClassDeclaration anonymous = (AnonymousClassDeclaration)node.getUserObject();
        String name = "";

        for(ASTNode parent = anonymous.getParent(); parent != null; parent = parent.getParent()) {
            String invocationName;
            if (parent instanceof MethodDeclaration) {
                invocationName = ((MethodDeclaration)parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = invocationName;
                } else {
                    name = invocationName + "." + name;
                }
            } else if (parent instanceof VariableDeclarationFragment && (parent.getParent() instanceof FieldDeclaration || parent.getParent() instanceof VariableDeclarationStatement)) {
                invocationName = ((VariableDeclarationFragment)parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = invocationName;
                } else {
                    name = invocationName + "." + name;
                }
            } else if (parent instanceof MethodInvocation) {
                invocationName = ((MethodInvocation)parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = invocationName;
                } else {
                    name = invocationName + "." + name;
                }
            } else if (parent instanceof SuperMethodInvocation) {
                invocationName = ((SuperMethodInvocation)parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = invocationName;
                } else {
                    name = invocationName + "." + name;
                }
            } else if (parent instanceof ClassInstanceCreation) {
                invocationName = Visitor.stringify(((ClassInstanceCreation)parent).getType());
                if (name.isEmpty()) {
                    name = "new " + invocationName;
                } else {
                    name = "new " + invocationName + "." + name;
                }
            }
        }

        return name.toString();
    }

    protected String getAnonymousBinaryName(DefaultMutableTreeNode node) {
        StringBuilder name = new StringBuilder();
        TreeNode[] path = node.getPath();

        for(int i = 0; i < path.length; ++i) {
            DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)path[i];
            if (tmp.getUserObject() != null) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode)tmp.getParent();
                int index = parent.getIndex(tmp);
                name.append(index + 1);
                if (i < path.length - 1) {
                    name.append(".");
                }
            }
        }

        return name.toString();
    }

    protected boolean isParent(ASTNode child, ASTNode parent) {
        for(ASTNode current = child; current.getParent() != null; current = current.getParent()) {
            if (current.getParent().equals(parent)) {
                return true;
            }
        }

        return false;
    }

    protected LocationInfo generateLocationInfo(CompilationUnit cu, String sourceFile, ASTNode node, CodeElementType codeElementType) {
        return new LocationInfo(cu, sourceFile, node, codeElementType);
    }
}

