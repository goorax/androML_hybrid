package androML.static_analysis.analyzer;

import androML.AndroMLConfig;
import androML.static_analysis.analyzer.filters.MethodFilter;
import androML.static_analysis.analyzer.filters.URIFilter;
import androML.static_analysis.reports.ByteCodeReport;
import androML.static_analysis.reports.Report;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Format;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction35c;
import org.jf.dexlib2.dexbacked.reference.DexBackedMethodReference;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.reference.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

final public class ByteCodeAnalyzer implements StaticAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(ByteCodeAnalyzer.class);
    private static final String CLASSES_FILTER = "^(Landroid/).*";

    private final AndroMLConfig config;
    private DexBackedDexFile dexFile;
    private ByteCodeRecord bytecodeRecord;
    private ByteCodeReport report;

    public ByteCodeAnalyzer(AndroMLConfig config) {
        this.config = config;
    }

    @Override
    public void analyze(File file) {
        bytecodeRecord = new ByteCodeRecord();
        loadDexFile(file);
        processClasses();
        processMethods();
        processURIs();
        buildReport();
    }

    private void loadDexFile(File file) {
        try {
            dexFile = DexFileFactory.loadDexFile(file, Opcodes.getDefault());
        } catch (IOException e) {
            LOG.error("Failed to load DexFile.", e);
        }
    }

    private void processURIs() {
        bytecodeRecord.setFilteredUsedURIs(URIFilter.filterURIsFrom(dexFile.getStrings()));
    }

    private void buildReport() {
        report = new ByteCodeReport(bytecodeRecord);
    }

    private void processMethods() {
        bytecodeRecord.setFilteredDexMethodNames(MethodFilter.filterMethods(dexFile.getMethods()));
        bytecodeRecord.setTotalMethodAmount(dexFile.getMethodCount());
    }

    private void processClasses() {
        Set<ClassDef> filteredClasses = new HashSet<>();
        Set<String> filteredClassNames = new HashSet<>();
        for (ClassDef classDef : dexFile.getClasses()) {
            if (!classDef.getType().matches(CLASSES_FILTER)) {
                iterateOverContainingMethodsOf(classDef);
                filteredClasses.add(classDef);
                filteredClassNames.add(classDef.getType());
            }
        }
        bytecodeRecord.setTotalClassesAmount(dexFile.getClassCount());
        bytecodeRecord.setTotalSystemClassesAmount(dexFile.getClassCount() - filteredClasses.size());
        bytecodeRecord.setFilteredClasses(filteredClasses);
        bytecodeRecord.setFilteredClassNames(filteredClassNames);
    }

    private void iterateOverContainingMethodsOf(ClassDef cd) {
        Iterator methodIterator = cd.getMethods().iterator();
        while (methodIterator.hasNext()) {
            Method method = (Method) methodIterator.next();
            MethodImplementation methodImpl = method.getImplementation();
            if (methodImpl != null) {
                checkForInvocation(methodImpl);
            }
        }
    }

    private void checkForInvocation(MethodImplementation methodImpl) {
        for (Instruction instruction : methodImpl.getInstructions()) {
            if (instruction.getOpcode().format == Format.Format35c) {
                DexBackedInstruction35c instruction35c = (DexBackedInstruction35c) instruction;
                Reference reference = instruction35c.getReference();
                handleInvokeReference(reference);
            }
        }
    }

    private void handleInvokeReference(Reference reference) {
        if (reference instanceof DexBackedMethodReference) {
            DexBackedMethodReference methodReference = ((DexBackedMethodReference) reference);
            String fqReference = MethodFilter.buildFQMethodName(methodReference);
            bytecodeRecord.getFilteredInvokeReferences().add(fqReference);
        }
    }

    @Override
    public Report getReport() {
        return report;
    }
}
