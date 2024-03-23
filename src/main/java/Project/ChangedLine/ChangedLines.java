package Project.ChangedLine;

import java.util.ArrayList;
import java.util.List;

public class ChangedLines {
    List<ChangedLabelLine> changedLabelLines;

    public ChangedLines(){
        changedLabelLines = new ArrayList<>();
    }

    public void addChangeLine(ChangedLabelLine labelLine){
        changedLabelLines.add(labelLine);
    }
}
