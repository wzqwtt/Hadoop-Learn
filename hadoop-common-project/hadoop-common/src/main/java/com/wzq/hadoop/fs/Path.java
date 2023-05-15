package com.wzq.hadoop.fs;

import com.wzq.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * 在FileSystem上命名一个文件或目录。Path字符串使用斜线作为目录的分隔符。
 * 一个path字符串的开始必定是斜线。
 * <p>
 * Names a file or directory in a FileSystem. Path strings use slash as the directory
 * separator. A path string is absolute if it begins with a slash.
 */
public class Path implements Comparable {

    private static final Logger LOG = LoggerFactory.getLogger(Path.class);

    /**
     * 目录分隔符，一个斜线
     * The directory separator, a slash.
     */
    public static final String SEPARATOR = "/";
    public static final char SEPARATOR_CHAR = '/';

    public static final String CUR_DIR = ".";

    // 如果当前操作系统为Windows，该值为true
    static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private URI uri;    // a hierarchical uri

    // ######################################################################################
    // 构造函数

    /**
     * 用字符串构造一个path。字符串是URI
     * <p>
     * Construct a path from a String. Path strings are URIs, but with unescaped elements
     * and some additional normalization.
     */
    public Path(String pathString) {
        checkPathArg(pathString);

        // we can't use 'new URI(String)' directly, since it assumes things are
        // escaped, which we don't require of Paths.

        // add a slash in front of paths with Windows drive letters
        if (hasWindowsDrive(pathString, false)) {
            pathString = "/" + pathString;
        }

        // parse uri components
        String scheme = null;
        String authority = null;

        int start = 0;

        // parse uri scheme, if any
        int colon = pathString.indexOf(':');
        int slash = pathString.indexOf('/');
        // has a scheme
        if ((colon != -1) && ((slash == -1) || (colon < slash))) {
            scheme = pathString.substring(0, colon);
            start = colon + 1;
        }

        // parse uri authority, if any
        if (pathString.startsWith("//", start) && pathString.length() - start > 2) {
            // has authority
            // i.e. file
            int nextSlash = pathString.indexOf('/', start + 2);
            int authEnd = nextSlash > 0 ? nextSlash : pathString.length();
            authority = pathString.substring(start + 2, authEnd);
            start = authEnd;
        }

        // uri path is the rest of the string -- query & fragment not supported
        String path = pathString.substring(start, pathString.length());

        initialize(scheme, authority, path, null);
    }

    public Path(String scheme, String authority, String path) {
        checkPathArg(path);
        initialize(scheme, authority, path, null);
    }

    public Path(URI aUri) {
        uri = aUri;
    }

    public Path(String parent, String child) {
        this(new Path(parent), new Path(child));
    }

    public Path(Path parent, String child) {
        this(parent, new Path(child));
    }

    public Path(String parent, Path child) {
        this(new Path(parent), child);
    }

    /**
     * Resolve a child path against a parent path.
     */
    public Path(Path parent, Path child) {
        // Add a slash to parent's path so resolution is compatible with URI's
        URI parentUri = parent.uri;
        String parentPath = parentUri.getPath();

        if (!(parentPath.equals("/") || parentPath.equals(""))) {
            try {
                parentUri = new URI(parentUri.getScheme(), parentUri.getAuthority(),
                        parentUri.getPath() + "/", null, parentUri.getFragment());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        URI resolved = parentUri.resolve(child.uri);
        initialize(resolved.getScheme(), resolved.getAuthority(), resolved.getPath(), resolved.getFragment());
    }

    // ######################################################################################
    // 辅助构造函数

    private void checkPathArg(String path) {
        // disallow construction of a Path from an null or empty string
        if (path == null) {
            throw new IllegalArgumentException("Can not create a Path from a null string");
        }
        if (path.length() == 0) {
            throw new IllegalArgumentException("Can not create a Path from an empty string");
        }
    }

    /**
     * 判断字符串中是否有 C:、D:等驱动器盘符
     */
    private boolean hasWindowsDrive(String path, boolean slashed) {
        if (!WINDOWS) return false;
        int start = slashed ? 1 : 0;
        return path.length() >= start + 2 &&
                (slashed ? path.charAt(0) == '/' : true) &&
                path.charAt(start + 1) == ':' &&
                ((path.charAt(start) >= 'A' && path.charAt(start) <= 'Z') ||
                        (path.charAt(start) >= 'a' && path.charAt(start) <= 'z'));
    }

    private void initialize(String scheme, String authority, String path, String fragment) {
        try {
            this.uri = new URI(scheme, authority, normalizePath(path), null, fragment)
                    .normalize();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String normalizePath(String path) {
        // remove double slashes & backslashes
        if (path.indexOf("//") != -1) {
            path = path.replace("//", "/");
        }
        if (path.indexOf("\\") != -1) {
            path = path.replace("\\", "/");
        }

        // trim trailing slash from non-root path (ignoring windows drive)
        int minLength = hasWindowsDrive(path, true) ? 4 : 1;
        if (path.length() > minLength && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public URI toUri() {
        return uri;
    }

    /**
     * Return the {@link FileSystem} that owns this Path.
     */
    public FileSystem getFileSystem(Configuration conf) {
        // TODO FileSystem
        return null;
    }

    /**
     * 如果这个路径是绝对路径返回true
     *
     * @return True if the directory of this path is absolute.
     */
    public boolean isAbsolute() {
        int start = hasWindowsDrive(uri.getPath(), true) ? 3 : 0;
        LOG.info("start = {}", start);
        return uri.getPath().startsWith(SEPARATOR, start);
    }

    /**
     * 返回Path的最后一个组件
     *
     * @return the final component of this Path
     */
    public String getName() {
        String path = uri.getPath();
        LOG.info("path = {}", path);
        int slash = path.lastIndexOf(SEPARATOR);
        return path.substring(slash + 1);
    }

    /**
     * 返回Path的父目录，如果在root目录下返回null
     *
     * @return the parent of a path or null if at root
     */
    public Path getParent() {
        String path = uri.getPath();
        int lastSlash = path.lastIndexOf('/');
        int start = hasWindowsDrive(path, true) ? 3 : 0;

        // empty path
        if ((path.length() == start) ||
                (lastSlash == start && path.length() == start + 1)) {   // at root
            return null;
        }

        String parent;
        if (lastSlash == -1) {
            // parent等于当前路径
            parent = CUR_DIR;
        } else {
            int end = hasWindowsDrive(path, true) ? 3 : 0;
            parent = path.substring(0, lastSlash == end ? end + 1 : lastSlash);
        }
        return new Path(uri.getScheme(), uri.getAuthority(), parent);
    }

    @Override
    public String toString() {
        // we can't use uri.toString(), which escapes everything, because we want
        // illegal characters unescaped in the string, for glob processing, etc.
        StringBuffer buffer = new StringBuffer();
        if (uri.getScheme() != null) {
            buffer.append(uri.getScheme());
            buffer.append(":");
        }
        if (uri.getAuthority() != null) {
            buffer.append("//");
            buffer.append(uri.getAuthority());
        }
        if (uri.getPath() != null) {
            String path = uri.getPath();
            if (path.indexOf('/') == 0 &&
                    hasWindowsDrive(path, true) &&  // has windows driver
                    uri.getScheme() == null &&  // but no scheme
                    uri.getAuthority() == null) {   // or authority
                path = path.substring(1);
            }
            buffer.append(path);
        }
        if (uri.getFragment() != null) {
            buffer.append("#");
            buffer.append(uri.getFragment());
        }

        return buffer.toString();
    }

    /**
     * Adds a suffix to the final name in the path.
     *
     * @param suffix
     * @return
     */
    public Path suffix(String suffix) {
        return new Path(getParent(), getName() + suffix);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Path)) {
            return false;
        }
        Path that = (Path) o;
        return this.uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        Path that = (Path) o;
        return this.uri.compareTo(that.uri);
    }

    /**
     * @return the number of elements in this path.
     */
    public int depth() {
        String path = uri.getPath();
        int depth = 0;
        int slash = path.length() == 1 && path.charAt(0) == '/' ? -1 : 0;
        while (slash != -1) {
            depth++;
            slash = path.indexOf(SEPARATOR, slash + 1);
        }
        return depth;
    }

    // TODO(feat) makeQualified()
}
