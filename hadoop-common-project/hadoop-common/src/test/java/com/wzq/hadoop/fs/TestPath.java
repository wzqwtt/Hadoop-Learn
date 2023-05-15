package com.wzq.hadoop.fs;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * @author wzq
 * @create 2023-05-11 21:22
 */
public class TestPath {

    private static final Logger LOG = LoggerFactory.getLogger(TestPath.class);

    // private static final String dir = "//root//conf//setting.xml";
    private static final String dir = "E://root//conf//setting.xml";

    @Test
    public void test() {
        LOG.info("os name: {}", System.getProperty("os.name"));
    }

    @Test
    public void testToURI() {
        Path path = new Path(dir);
        LOG.info("path scheme: {}", path.toUri().getScheme());
    }

    @Test
    public void testIsAbsoulte() {
        LOG.info("dir = {}", dir);
        Path path = new Path(dir);
        LOG.info("isAbsolute() = {}", path.isAbsolute());
    }

    @Test
    public void testGetName() {
        LOG.info("dir = {}", dir);
        Path path = new Path(dir);
        LOG.info("getName() = {}", path.getName());
    }

    @Test
    public void testGetParent() {
        Path path = new Path(dir);
        LOG.info("getParent() = {}", path.getParent().toString());

        String tmp = "E:/a.tar";
        LOG.info("dir = {}", tmp);
        path = new Path(tmp);
        LOG.info("getParent() = {}", path.getParent().toString());
    }

    @Test
    public void testURI() throws Exception {
        String uribase = "https://www.geeksforgeeks.org/";
        String urirelative = "languages/../java";
        String str = "https://www.google.co.in/?gws_rd=ssl#" + ""
                + "q=networking+in+java+geeksforgeeks" + ""
                + "&spf=1496918039682";

        // Construct to create new URI
        // by parsing the string
        URI uriBase = new URI(uribase);

        // create() method
        URI uri = URI.create(str);

        // toString() method
        LOG.info("Base URI = {}", uriBase.toString());

        URI uriRelative = new URI(urirelative);
        LOG.info("Relative URI = {}", uriRelative.toString());

        // resolve() method
        URI uriResolved = uriBase.resolve(uriRelative);
        LOG.info("Resolved URI = {}", uriResolved.toString());

        // relativized() method
        URI uriRelativize = uriBase.relativize(uriResolved);
        LOG.info("Relativize URI = {}", uriRelativize.toString());

        // normalize() method
        LOG.info(uri.normalize().toString());

        // getScheme() method
        LOG.info("Scheme = {}", uri.getScheme());

        // getRawSchemeSpecificPart() method
        LOG.info("Raw Scheme = {}", uri.getRawSchemeSpecificPart());

        // getSchemeSpecificPart() method
        LOG.info("Scheme-specific part = {}", uri.getSchemeSpecificPart());

        // getUserInfo() method
        LOG.info("User Info = {}", uri.getUserInfo());

        // getRawUserInfo() method
        LOG.info("Raw User Info = {}", uri.getRawUserInfo());

        // getAuthority() method
        LOG.info("Authority = {}", uri.getAuthority());

        // getRawAuthority() method
        LOG.info("Raw Authority = {}", uri.getRawAuthority());
    }

}
