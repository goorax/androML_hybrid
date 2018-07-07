package androML.static_analysis.analyzer;

import org.apache.commons.collections.set.UnmodifiableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

final public class Manifest {
    private static final Logger LOG = LoggerFactory.getLogger(Manifest.class);

    private String manifest;
    private ManifestHandler manifestHandler;

    public Manifest(String manifest) {
        this.manifest = manifest;
        manifestHandler = new ManifestHandler();
        analyzeManifest();
    }

    private void analyzeManifest() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            tryToParseManifest(factory);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("Analyze Manifest with SAXParser failed.", e);
        }
    }

    private void tryToParseManifest(SAXParserFactory factory) throws ParserConfigurationException, SAXException, IOException {
        SAXParser saxParser;
        saxParser = factory.newSAXParser();
        saxParser.parse(new InputSource(new StringReader(manifest)), manifestHandler);
    }

    public ManifestHandler getManifestHandler() {
        return manifestHandler;
    }

    public final class ManifestHandler extends DefaultHandler {
        private static final String UNKOWN_TAG = "unkown";
        private static final String EMPTY_TAG = "";

        private static final String MANIFEST = "manifest";
        private static final String ACTIVITY = "activity";
        private static final String RECEIVER = "receiver";
        private static final String SERVICE = "service";
        private static final String PROVIDER = "provider";
        private static final String ACTION = "action";

        private static final String ANDROID_PERMISSION = "permission";
        private static final String ANDROID_NAME = ".*(android:)?name.*";
        private static final String PACKAGE = "package";

        private String packageName;
        private Set<String> activities;
        private Set<String> services;
        private Set<String> receivers;
        private Set<String> providers;
        private Set<String> intents;
        private Set<String> permissions;

        public ManifestHandler() {
            packageName = EMPTY_TAG;
            activities = new HashSet<>();
            services = new HashSet<>();
            receivers = new HashSet<>();
            providers = new HashSet<>();
            intents = new HashSet<>();
            permissions = new HashSet<>();
        }

        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes atts) {
            switch (qName) {
                case MANIFEST:
                    packageName = seekAttributes(atts, PACKAGE);
                    break;
                case ACTIVITY:
                    activities.add(seekAttributes(atts, ANDROID_NAME));
                    permissions.add(seekAttributes(atts, ANDROID_PERMISSION));
                    break;
                case SERVICE:
                    services.add(seekAttributes(atts, ANDROID_NAME));
                    permissions.add(seekAttributes(atts, ANDROID_PERMISSION));
                    break;
                case RECEIVER:
                    receivers.add(seekAttributes(atts, ANDROID_NAME));
                    permissions.add(seekAttributes(atts, ANDROID_PERMISSION));
                    break;
                case PROVIDER:
                    providers.add(seekAttributes(atts, ANDROID_NAME));
                    permissions.add(seekAttributes(atts, ANDROID_PERMISSION));
                    break;
                case ACTION:
                    intents.add(seekAttributes(atts, ANDROID_NAME));
                    break;
                default:
                    break;
            }
        }

        @Override
        public void endDocument() {
            cleanEmptyTags();
        }

        private void cleanEmptyTags() {
            if (packageName.isEmpty()) {
                packageName = UNKOWN_TAG;
            }
            activities.remove(EMPTY_TAG);
            services.remove(EMPTY_TAG);
            receivers.remove(EMPTY_TAG);
            providers.remove(EMPTY_TAG);
            intents.remove(EMPTY_TAG);
            permissions.remove(EMPTY_TAG);
        }

        private String seekAttributes(Attributes atts, String pattern) {
            for (int i = 0; i < atts.getLength(); i++) {
                String key = atts.getQName(i);
                String value = atts.getValue(i);
                if (key.matches(pattern)) {
                    return value;
                }
            }
            return EMPTY_TAG;
        }

        public String getPackageName() {
            return packageName;
        }

        public Set<String> getActivities() {
            return UnmodifiableSet.decorate(activities);
        }

        public Set<String> getServices() {
            return UnmodifiableSet.decorate(services);
        }

        public Set<String> getReceivers() {
            return UnmodifiableSet.decorate(receivers);
        }

        public Set<String> getProviders() { return UnmodifiableSet.decorate(providers); }

        public Set<String> getPermissions() {
            return UnmodifiableSet.decorate(permissions);
        }

        public Set<String> getIntents() { return UnmodifiableSet.decorate(intents); }
    }
}
