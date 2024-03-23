import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Result{
    public String currentClass;
    public Map<String, List<String>> methods;
}



class Visitor extends VoidVisitorAdapter<Result> {
    public void visit(MethodDeclaration md, Result result) {
        result.methods.get(result.currentClass).add(md.getName().toString());
    }
    public void visit(ClassOrInterfaceDeclaration md, Result arg) {
        String className = md.getFullyQualifiedName().toString();
        arg.methods.put(className, new ArrayList<String>());
        arg.currentClass = className;
    }
}
