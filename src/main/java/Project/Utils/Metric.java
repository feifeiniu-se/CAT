package Project.Utils;

import Model.CodeBlockTime;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
// our defined metric
public class Metric {
    public Set<String> typeCount = new HashSet<>();//number of types
    public int refactCount = 0;//number of refactorings
    public int aCount = 0;
    public int amCount = 0;
    public int arCount = 0;
    public int aiCount = 0;
    public int aeCount = 0;
    public int ariCount = 0;
    public int areCount = 0;
    public int aieCount = 0;
    public int arieCount = 0;

    public int dCount = 0;
    public int dmCount = 0;
    public int drCount = 0;
    public int diCount = 0;
    public int deCount = 0;
    public int driCount = 0;
    public int dreCount = 0;
    public int dieCount = 0;
    public int drieCount = 0;

//    Code structure
//    C Class
//    M Method
//    A Attribute
//
//    Operation
//    M Move
//    R Rename
//    A Add
//    D Delete
//    E Edit


    //todo define types of metrics
    public Set<CodeBlockTime> C_M = new HashSet<>();
    public Set<CodeBlockTime> C_R = new HashSet<>();
    public Set<CodeBlockTime> C_A = new HashSet<>();
    public Set<CodeBlockTime> C_D = new HashSet<>();
    public Set<CodeBlockTime> C_E = new HashSet<>();
    public Set<CodeBlockTime> C_I = new HashSet<>();

    public Set<CodeBlockTime> M_M = new HashSet<>();
    public Set<CodeBlockTime> M_R = new HashSet<>();
    public Set<CodeBlockTime> M_A = new HashSet<>();
    public Set<CodeBlockTime> M_D = new HashSet<>();
    public Set<CodeBlockTime> M_E = new HashSet<>();
    public Set<CodeBlockTime> M_I = new HashSet<>();

    public Set<CodeBlockTime> A_M = new HashSet<>();
    public Set<CodeBlockTime> A_R = new HashSet<>();
    public Set<CodeBlockTime> A_A = new HashSet<>();
    public Set<CodeBlockTime> A_D = new HashSet<>();
    public Set<CodeBlockTime> A_E = new HashSet<>();
    public Set<CodeBlockTime> A_I = new HashSet<>();
    public Set<CodeBlockTime> A_R_I = new HashSet<>();
    public Set<CodeBlockTime> A_R_E = new HashSet<>();
    public Set<CodeBlockTime> A_I_E = new HashSet<>();
    public Set<CodeBlockTime> A_R_I_E = new HashSet<>();


    public HashMap<CodeBlockTime, CodeBlockTime> renamePairs = new HashMap<>();
    public String count(){
        int classAddNum = this.C_A.size() + this.C_R.size() + this.C_M.size() + this.C_E.size() + this.C_I.size();
        int classDeleteNum = this.C_D.size() + this.C_R.size() + this.C_M.size() + this.C_E.size() + this.C_I.size();
        int methodAddNum = this.M_A.size() + this.M_R.size() + this.M_M.size() + this.M_E.size() + this.M_I.size();
        int methodDeleteNum = this.M_D.size() + this.M_R.size() + this.M_M.size() + this.M_E.size() + this.M_I.size();

        double aLinePerClass = classAddNum == 0 ? 0 : (double) aCount / classAddNum;
        double amLinePerClass = classAddNum == 0 ? 0 : (double) amCount / classAddNum;
        double arLinePerClass = classAddNum == 0 ? 0 : (double) arCount / classAddNum;
        double aiLinePerClass = classAddNum == 0 ? 0 : (double) aiCount / classAddNum;
        double aeLinePerClass = classAddNum == 0 ? 0 : (double) aeCount / classAddNum;
        double ariLinePerClass = classAddNum == 0 ? 0 : (double) ariCount / classAddNum;
        double areLinePerClass = classAddNum == 0 ? 0 : (double) areCount / classAddNum;
        double aieLinePerClass = classAddNum == 0 ? 0 : (double) aieCount / classAddNum;
        double arieLinePerClass = classAddNum == 0 ? 0 : (double) arieCount / classAddNum;

        double dLinePerClass = classDeleteNum == 0 ? 0 : (double) dCount / classDeleteNum;
        double dmLinePerClass = classDeleteNum == 0 ? 0 : (double) dmCount / classDeleteNum;
        double drLinePerClass = classDeleteNum == 0 ? 0 : (double) drCount / classDeleteNum;
        double diLinePerClass = classDeleteNum == 0 ? 0 : (double) diCount / classDeleteNum;
        double deLinePerClass = classDeleteNum == 0 ? 0 : (double) deCount / classDeleteNum;
        double driLinePerClass = classDeleteNum == 0 ? 0 : (double) driCount / classDeleteNum;
        double dreLinePerClass = classDeleteNum == 0 ? 0 : (double) dreCount / classDeleteNum;
        double dieLinePerClass = classDeleteNum == 0 ? 0 : (double) dieCount / classDeleteNum;
        double drieLinePerClass = classDeleteNum == 0 ? 0 : (double) drieCount / classDeleteNum;

        double aLinePerMethod = methodAddNum == 0 ? 0 : (double) aCount / methodAddNum;
        double amLinePerMethod = methodAddNum == 0 ? 0 : (double) amCount / methodAddNum;
        double arLinePerMethod = methodAddNum == 0 ? 0 : (double) arCount / methodAddNum;
        double aiLinePerMethod = methodAddNum == 0 ? 0 : (double) aiCount / methodAddNum;
        double aeLinePerMethod = methodAddNum == 0 ? 0 : (double) aeCount / methodAddNum;
        double ariLinePerMethod = methodAddNum == 0 ? 0 : (double) ariCount / methodAddNum;
        double areLinePerMethod = methodAddNum == 0 ? 0 : (double) areCount / methodAddNum;
        double aieLinePerMethod = methodAddNum == 0 ? 0 : (double) aieCount / methodAddNum;
        double arieLinePerMethod = methodAddNum == 0 ? 0 : (double) arieCount / methodAddNum;

        double dLinePerMethod = methodDeleteNum == 0 ? 0 : (double) dCount / methodDeleteNum;
        double dmLinePerMethod = methodDeleteNum == 0 ? 0 : (double) dmCount / methodDeleteNum;
        double drLinePerMethod = methodDeleteNum == 0 ? 0 : (double) drCount / methodDeleteNum;
        double diLinePerMethod = methodDeleteNum == 0 ? 0 : (double) diCount / methodDeleteNum;
        double deLinePerMethod = methodDeleteNum == 0 ? 0 : (double) deCount / methodDeleteNum;
        double driLinePerMethod = methodDeleteNum == 0 ? 0 : (double) driCount / methodDeleteNum;
        double dreLinePerMethod = methodDeleteNum == 0 ? 0 : (double) dreCount / methodDeleteNum;
        double dieLinePerMethod = methodDeleteNum == 0 ? 0 : (double) dieCount / methodDeleteNum;
        double drieLinePerMethod = methodDeleteNum == 0 ? 0 : (double) drieCount / methodDeleteNum;

        String res = "";

        res = res + this.refactCount + " " + this.typeCount.size() + " ; " +
                this.aCount + " " + this.amCount + " " + this.arCount + " " + this.aiCount + " " + this.aeCount + " ; " +
                this.ariCount + " " + this.areCount + " " + this.aieCount + " " + this.arieCount + " ; " +
                this.dCount + " " + this.dmCount + " " + this.drCount + " " + this.diCount + " " + this.deCount + " ; " +
                this.driCount + " " + this.dreCount + " " + this.dieCount + " " + this.drieCount + " ; " +
                aLinePerClass + " " + amLinePerClass + " " + arLinePerClass + " " + aiLinePerClass + " " + aeLinePerClass + " ; " +
                ariLinePerClass + " " + areLinePerClass + " " + aieLinePerClass + " " + arieLinePerClass + " ; " +
                dLinePerClass + " " + dmLinePerClass + " " + drLinePerClass + " " + diLinePerClass + " " + deLinePerClass + " ; " +
                driLinePerClass + " " + dreLinePerClass + " " + dieLinePerClass + " " + drieLinePerClass + " ; " +
                aLinePerMethod + " " + amLinePerMethod + " " + arLinePerMethod + " " + aiLinePerMethod + " " + aeLinePerMethod + " ; " +
                ariLinePerMethod + " " + areLinePerMethod + " " + aieLinePerMethod + " " + arieLinePerMethod + " ; " +
                dLinePerMethod + " " + dmLinePerMethod + " " + drLinePerMethod + " " + diLinePerMethod + " " + deLinePerMethod + " ; " +
                driLinePerMethod + " " + dreLinePerMethod + " " + dieLinePerMethod + " " + drieLinePerMethod + " ; " +
                this.A_A.size() + " " + this.A_D.size() + " " + this.A_R.size() + " " + this.A_M.size() + " " + this.A_E.size() + " " + this.A_I.size() + " ; " +
                this.A_R_I.size() + " " + this.A_R_E.size() + " " + this.A_I_E.size() + " " + this.A_R_I_E.size() + " ; " +
                this.C_A.size() + " " + this.C_D.size() + " " + this.C_R.size() + " " + this.C_M.size() + " " + this.C_E.size() + " " + this.C_I.size() + " ; " +
                this.M_A.size() + " " + this.M_D.size() + " " + this.M_R.size() + " " + this.M_M.size() + " " + this.M_E.size() + " " + this.M_I.size();
        return res;
    }

}
