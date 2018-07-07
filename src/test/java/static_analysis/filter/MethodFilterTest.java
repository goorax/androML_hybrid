package static_analysis.filter;

import androML.static_analysis.analyzer.filters.MethodFilter;
import org.jf.dexlib2.dexbacked.reference.DexBackedMethodReference;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MethodFilterTest {
    private static final String CLASS_NAME = "Ljava/lang/Runtime;";
    private static final String METHOD_NAME = "exec";
    private static final String PARAMETER_1 = "Ljava/lang/String;";
    private static final String PARAMETER_2 = "Z";
    private static final String RETURN_TYPE = "Ljava/lang/String;";
    private static final String RESULT = "Ljava/lang/Runtime;->exec(Ljava/lang/String;Z)Ljava/lang/String;";

    @Test
    public void testBuildFQMethodString() {
        DexBackedMethodReference methodReference = builtMockedDexBackedMethodReference();

        String fqMethod = MethodFilter.buildFQMethodName(methodReference);

        Assert.assertEquals(fqMethod, RESULT);
    }

    private DexBackedMethodReference builtMockedDexBackedMethodReference() {
        DexBackedMethodReference methodReference = mock(DexBackedMethodReference.class);
        given(methodReference.getDefiningClass()).willReturn(CLASS_NAME);
        given(methodReference.getName()).willReturn(METHOD_NAME);
        given(methodReference.getParameterTypes()).willReturn(buildTestParameters());
        given(methodReference.getReturnType()).willReturn(RETURN_TYPE);
        return methodReference;
    }

    @Test
    public void testFilterMethods() {
        List<DexBackedMethodReference> methods = new ArrayList<>();
        methods.add(builtMockedDexBackedMethodReference());

        Set<String> results = MethodFilter.filterMethods(methods);

        Assert.assertEquals(results.size(),1);
        Assert.assertTrue(results.contains(RESULT));
    }

    private List<String> buildTestParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add(PARAMETER_1);
        parameters.add(PARAMETER_2);
        return parameters;
    }
}
