package androML.static_analysis.analyzer;

import org.jf.dexlib2.iface.ClassDef;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ByteCodeRecord {

    private int totalPackageAmount;
    private int totalClassesAmount;
    private int totalSystemClassesAmount;
    private int totalMethodAmount;

    private Map<String, Integer> instructionAmountPerFilteredMethod;
    private Set<ClassDef> filteredClasses;
    private Set<String> filteredClassNames;
    private Set<String> filteredDexMethodNames;
    private Set<String> filteredInvokeReferences;
    private Set<String> filteredUsedURIs;

    public ByteCodeRecord() {
        instructionAmountPerFilteredMethod = new HashMap<>();
        filteredClasses = new HashSet<>();
        filteredClassNames = new HashSet<>();
        filteredDexMethodNames = new HashSet<>();
        filteredInvokeReferences = new HashSet<>();
        filteredUsedURIs = new HashSet<>();
    }

    public int getTotalPackageAmount() {
        return totalPackageAmount;
    }

    public void increasePackageAmount() {
        totalPackageAmount++;
    }

    public void setTotalPackageAmount(int totalPackageAmount) {
        this.totalPackageAmount = totalPackageAmount;
    }

    public int getTotalSystemClassesAmount() {
        return totalSystemClassesAmount;
    }

    public void setTotalSystemClassesAmount(int totalSystemClassesAmount) {
        this.totalSystemClassesAmount = totalSystemClassesAmount;
    }

    public int getTotalClassesAmount() {
        return totalClassesAmount;
    }

    public void setTotalClassesAmount(int totalClassesAmount) {
        this.totalClassesAmount = totalClassesAmount;
    }

    public int getTotalMethodAmount() {
        return totalMethodAmount;
    }

    public void setTotalMethodAmount(int totalMethodAmount) {
        this.totalMethodAmount = totalMethodAmount;
    }

    public Map<String, Integer> getInstructionAmountPerFilteredMethod() {
        return instructionAmountPerFilteredMethod;
    }

    public void setInstructionAmountPerFilteredMethod(Map<String, Integer> instructionAmountPerFilteredMethod) {
        this.instructionAmountPerFilteredMethod = instructionAmountPerFilteredMethod;
    }

    public Set<ClassDef> getFilteredClasses() {
        return filteredClasses;
    }

    public void setFilteredClasses(Set<ClassDef> filteredClasses) {
        this.filteredClasses = filteredClasses;
    }

    public Set<String> getFilteredClassNames() {
        return filteredClassNames;
    }

    public void setFilteredClassNames(Set<String> filteredClassNames) {
        this.filteredClassNames = filteredClassNames;
    }

    public Set<String> getFilteredDexMethodNames() {
        return filteredDexMethodNames;
    }

    public void setFilteredDexMethodNames(Set<String> dexMethodNames) {
        this.filteredDexMethodNames = dexMethodNames;
    }

    public Set<String> getFilteredInvokeReferences() {
        return filteredInvokeReferences;
    }

    public void setFilteredInvokeReferences(Set<String> filteredInvokeReferences) {
        this.filteredInvokeReferences = filteredInvokeReferences;
    }

    public Set<String> getFilteredUsedURIs() {
        return filteredUsedURIs;
    }

    public void setFilteredUsedURIs(Set<String> filteredUsedURIs) {
        this.filteredUsedURIs = filteredUsedURIs;
    }
}
