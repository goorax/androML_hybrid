package androML.static_analysis.analyzer.filters;

import org.jf.dexlib2.dexbacked.reference.DexBackedMethodReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class MethodFilter {
    private static final Logger LOG = LoggerFactory.getLogger(MethodFilter.class);
    private static final String BINARY_EXEC = "(Ljava/lang/Runtime;->exec.*)";
    private static final String DEVICE_ID = "|(.*TelephonyManager;->getDeviceId.*)";

    private static final String LOAD_FILE = "|(Landroid/net/Uri;->fromFile.*)";
    private static final String CLASS_LOADER_1 = "|(Ljava/net/URLClassLoader;->newInstance.*)";
    private static final String CLASS_LOADER_2 = "|(Ljava/lang/ClassLoader;->loadClass.*)";
    private static final String CLASS_LOADER_3 = "|(Landroid/net/Uri;->fromFile\\(Ljava/io/File;\\)Landroid/net/Uri.*)";

    private static final String DEVICE_ADMIN1 = "|(.*android\\.app\\.action\\.ADD_DEVICE_ADMIN.*)";
    private static final String DEVICE_ADMIN2 = "|(.*android\\.app\\.extra\\.DEVICE_ADMIN.*)";

    private static final Pattern METHOD_PATTERN = Pattern.compile(BINARY_EXEC + DEVICE_ID + LOAD_FILE +
            CLASS_LOADER_1 + CLASS_LOADER_2 + CLASS_LOADER_3 + DEVICE_ADMIN1 + DEVICE_ADMIN2);
    private static final Matcher MATCHER = METHOD_PATTERN.matcher("");
    private static final String ARROW = "->";
    private static final String BRACKET_OPEN = "(";
    private static final String BRACKET_CLOSED = ")";

    public synchronized static Set<String> filterMethods(List<DexBackedMethodReference> methods) {
        Set<String> filteredMethods = new HashSet<>();
        for (DexBackedMethodReference dm : methods) {
            matchMethod(filteredMethods, dm);
        }
        return filteredMethods;
    }

    private static Set<String> matchMethod(Set<String> filteredMethods, DexBackedMethodReference dm) {
        String fqMethodName = buildFQMethodName(dm);
        // Currently no filtering in place!
        //MATCHER.reset(fqMethodName);
        //if (MATCHER.matches()) {
            filteredMethods.add(fqMethodName);
        //}
        return filteredMethods;
    }

    public synchronized static String buildFQMethodName(DexBackedMethodReference dm) {
        String parameters = buildParameterString(dm);
        StringBuilder method = buildFQMethodString(dm, parameters);
        return method.toString();
    }

    private static StringBuilder buildFQMethodString(DexBackedMethodReference dm, String parameters) {
        StringBuilder method = new StringBuilder();
        method.append(dm.getDefiningClass());
        method.append(ARROW);
        method.append(dm.getName());
        method.append(parameters);
        method.append(dm.getReturnType());
        return method;
    }

    private static String buildParameterString(DexBackedMethodReference dm) {
        StringBuilder parameters = new StringBuilder();
        parameters.append(BRACKET_OPEN);
        if (!dm.getParameterTypes().isEmpty()) {
            for (String parameterType : dm.getParameterTypes()) {
                parameters.append(parameterType);
            }
        }
        parameters.append(BRACKET_CLOSED);
        return parameters.toString();
    }

}
