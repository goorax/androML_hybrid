package learning_tests;

import helper.TestConstants;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.analysis.DexClassProvider;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.ZipDexContainer;
import org.jf.dexlib2.dexbacked.raw.RawDexFile;
import org.jf.dexlib2.dexbacked.reference.DexBackedMethodReference;
import org.jf.dexlib2.dexbacked.reference.DexBackedTypeReference;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;

public class DexLib2Test {
    public static final String ACTIVITY = "MainActivity";
    public static final String ENTRYPOINT = "onCreate";
    private String testClassesDexPath = getClass().getClassLoader().getResource(TestConstants.TEST_CLASSES_DEX).getPath();

    @Test
    public void testDexFileLoading() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);
        Assert.assertThat(dexFile, instanceOf(DexBackedDexFile.class));
    }

    @Test
    public void testClassLoading() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);

        Set<? extends DexBackedClassDef> classes = dexFile.getClasses();
        List<DexBackedTypeReference> types = dexFile.getTypes();

        Assert.assertThat(classes, instanceOf(Set.class));
    }

    @Test
    public void testDexMethodLoading() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);

        List<DexBackedMethodReference> dexMethods = dexFile.getMethods();

        Assert.assertThat(dexMethods, instanceOf(List.class));
    }

    @Test
    public void testInstructionLoading() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);

        List<DexBackedMethodReference> dexMethods = dexFile.getMethods();
        DexBackedMethodReference method = dexMethods.get(0);

        Assert.assertThat(method, instanceOf(DexBackedMethodReference.class));
    }

    @Test
    public void testMethodLoadingWithIterator() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);

        Method m = getMethod(dexFile);
        Assert.assertTrue(m != null);
    }

    @Test
    public void testMethodImplementation() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);
        Method m = getMethod(dexFile);
        MethodImplementation mI = m.getImplementation();
        Instruction instruction = null;
        for (Instruction i : mI.getInstructions()) {
            instruction = i;
            break;
        }
        Assert.assertFalse(instruction == null);
    }

    private Method getMethod(DexBackedDexFile dexFile) {
        Method m = null;
        for (ClassDef cd : dexFile.getClasses()) {
            Iterator it = cd.getMethods().iterator();
            while (it.hasNext()) {
                m = (Method) it.next();
                break;
            }
        }
        return m;
    }


    public static DexBackedDexFile loadDexFile(String path) {
        DexBackedDexFile dexFile = null;
        try {
            dexFile = DexFileFactory.loadDexFile(path, Opcodes.getDefault());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dexFile;
    }

    @Test
    public void testIfMainInstructionIsAvailable() {
        int instructionCounter = 0;
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);

        instructionCounter = getInstructionAmountOfMainActivity(instructionCounter, dexFile);

        Assert.assertNotSame(instructionCounter, 0);
    }

    private int getInstructionAmountOfMainActivity(int instructionCounter, DexBackedDexFile dexFile) {
        for (ClassDef classDef : dexFile.getClasses()) {
            if (classDef.getType().contains(ACTIVITY)) {
                for (Method method : classDef.getMethods()) {
                    if (method.getName().equals(ENTRYPOINT)) {
                        for (Instruction ins : method.getImplementation().getInstructions()) {
                            instructionCounter++;
                        }
                    }
                }
            }
        }
        return instructionCounter;
    }

    @Test
    public void testSecondaryDexFileLoading() {
        DexBackedDexFile dexFile = loadDexFileWithInputStream();

        Assert.assertThat(dexFile, instanceOf(DexBackedDexFile.class));
    }

    private DexBackedDexFile loadDexFileWithInputStream() {
        DexBackedDexFile dexFile = null;
        try {
            InputStream is = new FileInputStream(testClassesDexPath);
            BufferedInputStream bis = new BufferedInputStream(is);
            dexFile = RawDexFile.fromInputStream(Opcodes.getDefault(), bis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dexFile;
    }

    @Test
    public void testDexClassProvider() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);
        DexClassProvider dexClassProvider = new DexClassProvider(dexFile);
        Assert.assertThat(dexClassProvider, instanceOf(DexClassProvider.class));
    }

    @Test
    public void testLoadingApk() {
        DexBackedDexFile dexFile = loadDexFile(testClassesDexPath);
        Assert.assertThat(dexFile, instanceOf(DexBackedDexFile.class));
    }

    @Test
    public void testZipContainer() {
        ZipDexContainer zipDexContainer = new ZipDexContainer(new File(testClassesDexPath), Opcodes.getDefault());
        Assert.assertThat(zipDexContainer, instanceOf(ZipDexContainer.class));
    }


}
