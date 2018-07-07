package androML.static_analysis.analyzer.filters;

import org.jf.dexlib2.dexbacked.reference.DexBackedStringReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class URIFilter {
    private static final Logger LOG = LoggerFactory.getLogger(URIFilter.class);
    private static final String URL_REGEX = "(^(https?|s?ftp|ssh|news|rtmp|rtsp|ldap|data|telnet|crid|rsync|smb|" +
            "wss?|scp|rcp|git)://[a-zA-Z0-9]+[a-zA-Z0-9-\\+&@#/%\\?=~_|!:,\\.;]*[a-zA-Z0-9-\\+&@#/%=~_|]$)";
    private static final String MAIL_REGEX = "|(^(mailto|pop|imap|smtps?|smtpTLS):(//)?[a-zA-Z0-9]+[_A-Za-z0-9-\\+&%=@\\?\\.]+$)";
    private static final String FILE_REGEX = "|(^file:///?[a-zA-Z0-9]+[_A-Za-z0-9-\\+\\./%]+$)";
    private static final String SIP_REGEX = "|(^sip:[a-zA-Z0-9]+[_A-Za-z0-9-\\+\\./]*@[_A-Za-z0-9-\\+\\./]+:[0-9]+$)";
    private static final String GIT_REGEX = "|(^git:[a-zA-Z0-9]+[_A-Za-z0-9-\\+\\./]+\\.git$)";
    private static final String XMPP_REGEX = "|(^xmpp:(//)?[a-zA-Z0-9]+[_A-Za-z0-9-\\+\\./]+@[_A-Za-z0-9-\\+\\./]+$)";
    private static final String VAR_SIGN = "%.";
    private static final String ASTERISK = "*";
    private static final Pattern URI_PATTERN = Pattern.compile(URL_REGEX + MAIL_REGEX + FILE_REGEX +
            SIP_REGEX + GIT_REGEX + XMPP_REGEX);
    private static final Matcher uriMatcher = URI_PATTERN.matcher("");
    public static final String EMPTY_STRING = "";

    public synchronized static Set<String> filterURIsFrom(List<DexBackedStringReference> stringReferences) {
        Set<String> uris = new HashSet<>();
        for (DexBackedStringReference stringRef : stringReferences) {
            String string = stringRef.getString();
            checkStringForURI(uris, string);
        }
        return uris;
    }

    private static void checkStringForURI(Set<String> uris, String string) {
        uriMatcher.reset(string);
        if (uriMatcher.matches()) {
            string = string.replaceAll(VAR_SIGN, ASTERISK);
            String host = getURIHost(string);
            if (!host.equals(EMPTY_STRING)) {
                uris.add(host);
            }
        }
    }

    private static String getURIHost(String string) {
        URI uri = null;
        String uriHost = EMPTY_STRING;
        try {
            uri = new URI(string);
            if (uri.getHost() != null) {
                uriHost = uri.getHost();
            }
        } catch (URISyntaxException e) {
            LOG.error("URI generation failed.", e);
        }
        return uriHost;
    }
}
