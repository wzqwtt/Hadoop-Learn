package com.wzq.hadoop.conf;

import com.wzq.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Configuration {

    // ##################################################################################
    // 属性
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    /**
     * 设置加载配置的模式。如果quietmode为true（默认），则在加载解析配置文件的过程中，不输出日志信息。quietmode只是一个方便开发人员调试的变量
     */
    private boolean quietmode = true;

    /**
     * 一个Object的ArrayList数组。保存了所有通过addResource()方法添加Configuration对象的资源
     */
    private ArrayList<Object> resources = new ArrayList<Object>();

    /**
     * 保存在配置文件中声明为final的键值对的键
     */
    private Set<String> finalParameters = new HashSet<String>();

    /**
     * 用于确定是否加载默认资源，这些默认资源将保存在defaultResources中
     */
    private boolean loadDefaults = true;

    /**
     * 记录系统中所有Configuration对象
     */
    private static final WeakHashMap<Configuration, Object> REGISTRY =
            new WeakHashMap<Configuration, Object>();

    /**
     * 默认资源列表，资源按照列表条目的顺序加载
     */
    private static final CopyOnWriteArrayList<String> defaultResources =
            new CopyOnWriteArrayList<String>();

    /**
     *
     */
    private boolean storeResource;

    private HashMap<String, String> updatingResource;

    static {
        ClassLoader cL = Thread.currentThread().getContextClassLoader();
        if (cL == null) {
            cL = Configuration.class.getClassLoader();
        }
        if (cL.getResource("hadoop-site.xml") != null) {
            LOG.warn("DEPRECATED: hadoop-site.xml found in the classpath. " +
                    "Usage of hadoop-site.xml is deprecated. Instead use core-site.xml, "
                    + "mapred-site.xml and hdfs-site.xml to override properties of " +
                    "core-default.xml, mapred-default.xml and hdfs-default.xml " +
                    "respectively");
        }
        // 用于默认加载核心配置文件
        addDefaultResource("core-default.xml");
        addDefaultResource("core-site.xml");
    }

    /**
     * Hadoop配置文件解析后的键值对，都存放在properties中
     */
    private Properties properties;

    /**
     * 用于记录通过set()方式改变的配置项。也就是说，出现在overlay中的键值对是应用设置的，而不是通过对配置资源解析得到的
     */
    private Properties overlay;
    private ClassLoader classLoader;

    {
        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = Configuration.class.getClassLoader();
        }
    }

    // ##################################################################################
    // 构造函数

    /**
     * 创建一个新的配置类
     */
    public Configuration() {
        this(true);
    }

    /**
     * 加载默认资源可以被关闭的配置类构造函数。如果{@code loadDefaults}是fasle，新的实例不会从默认文件中加载资源
     *
     * @param loadDefaults 是否加载默认资源
     */
    public Configuration(boolean loadDefaults) {
        this.loadDefaults = loadDefaults;
        if (LOG.isDebugEnabled()) {
            // TODO 此处使用了一个Hadoop自己的StringUtils工具类
            LOG.debug("构造函数");
        }
        synchronized (Configuration.class) {
            REGISTRY.put(this, null);
        }
        this.storeResource = false;
    }

    public Configuration(Configuration other, boolean storeResource) {
        // TODO public Configuration(Configuration other, boolean storeResource)
    }

    public Configuration(Configuration other) {
        // TODO public Configuration(Configuration other)
    }

    // ##################################################################################
    // 添加资源

    /**
     * 添加一个默认resource，
     *
     * @param name file name，文件应该在classpath下
     */
    public static synchronized void addDefaultResource(String name) {
        if (!defaultResources.contains(name)) {
            defaultResources.add(name);
            for (Configuration conf : REGISTRY.keySet()) {
                if (conf.loadDefaults) {
                    conf.reloadConfiguration();
                }
            }
        }
    }

    /**
     * 添加一个配置资源
     * <p>
     * 这个resource的properties将会覆盖以前添加的资源的properties，除非他们被标记为<a href="#Final">final</a>
     *
     * @param name
     */
    public void addResource(String name) {
        addResourceObject(name);
    }

    public void addResource(URL url) {
        addResourceObject(url);
    }

    public void addResource(Path file) {
        addResourceObject(file);
    }

    public void addResource(InputStream in) {
        addResourceObject(in);
    }


    /**
     * 从以前添加的资源重新加载配置。此方法将清除添加的资源和最终参数中读取的所有配置。
     * 这将使资源在访问值之前再次被读取。通过set方法添加的值将覆盖从资源中读取的值。
     */
    public synchronized void reloadConfiguration() {
        properties = null;
        finalParameters.clear();
    }

    private synchronized void addResourceObject(Object resource) {
        resources.add(resource);
        reloadConfiguration();
    }

    // ##################################################################################
    // 加载资源

    /**
     * 加载所有资源
     *
     * @param properties 属性
     * @param resources  资源
     * @param quiet      调试
     */
    private void loadResources(Properties properties, ArrayList resources, boolean quiet) {
        if (loadDefaults) {
            for (String resource : defaultResources) {
                loadResource(properties, resource, quiet);
            }

            // support the hadoop-site.xml as a deprecated case
            if (getResource("hadoop-site.xml") != null) {
                loadResource(properties, "hadoop-site.xml", quiet);
            }
        }
        for (Object resource : resources) {
            LOG.debug("loadResources resource : [{}]", resource);
            loadResource(properties, resource, quiet);
        }
    }

    private void loadResource(Properties properties, Object name, boolean quiet) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            // 忽略xml文件中所有的注释
            docBuilderFactory.setIgnoringComments(true);
            docBuilderFactory.setNamespaceAware(true);

            try {
                docBuilderFactory.setXIncludeAware(true);
            } catch (UnsupportedOperationException e) {
                LOG.error("Failed to set setXIncludeAware(true) for parser "
                                + docBuilderFactory
                                + ":" + e,
                        e);
            }

            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            Document doc = null;
            Element root = null;

            LOG.debug("name class: [{}]", name.getClass().getName());

            if (name instanceof URL) {
                // TODO an URL resource
            } else if (name instanceof String) {
                URL url = getResource((String) name);
                LOG.debug("Resource name : [{}]; url : [{}]", name, url);
                if (url != null) {
                    if (!quiet) {
                        LOG.info("parsing " + url);
                    }
                    doc = builder.parse(url.toString());
                }
            } else if (name instanceof Path) {
                // TODO a file resource
            } else if (name instanceof InputStream) {
                // TODO an InputStream resource
            } else if (name instanceof Element) {
                root = (Element) name;
            }

            // 判断是否获取到doc
            if (doc == null && root == null) {
                if (quiet) {
                    return;
                }
                throw new RuntimeException(name + " not found");
            }

            // 为root赋值
            if (root == null) {
                root = doc.getDocumentElement();
            }

            // 判断顶级元素是否为configuration
            if (!"configuration".equals(root.getTagName())) {
                LOG.error("bad conf file : top-level element not <configuration>");
            }

            // 获取所有的属性
            NodeList props = root.getChildNodes();
            LOG.debug("props : [{}]", props);
            for (int i = 0; i < props.getLength(); i++) {
                Node propNode = props.item(i);

                if (!(propNode instanceof Element)) {
                    continue;
                }

                Element prop = (Element) propNode;

                if ("configuration".equals(prop.getTagName())) {
                    loadResource(properties, prop, quiet);
                    continue;
                }

                if (!"property".equals(prop.getTagName())) {
                    LOG.warn("bad conf file: element not <property>");
                }

                NodeList fields = prop.getChildNodes();
                String attr = null;
                String value = null;
                boolean finalParameter = false;

                for (int j = 0; j < fields.getLength(); j++) {
                    Node filedNode = fields.item(j);
                    if (!(filedNode instanceof Element)) {
                        continue;
                    }
                    Element field = (Element) filedNode;

                    if ("name".equals(field.getTagName()) && field.hasChildNodes()) {
                        attr = ((Text) field.getFirstChild()).getData().trim();
                    }
                    if ("value".equals(field.getTagName()) && field.hasChildNodes()) {
                        value = ((Text) field.getFirstChild()).getData();
                    }
                    if ("final".equals(field.getTagName()) && field.hasChildNodes()) {
                        finalParameter = "true".equals(((Text) field.getFirstChild()).getData());
                    }
                }

                if (attr != null) {
                    if (value != null) {
                        if (!finalParameters.contains(attr)) {
                            properties.setProperty(attr, value);
                            if (storeResource) {
                                updatingResource.put(attr, name.toString());
                            }
                        } else if (!value.equals(properties.getProperty(attr))) {
                            LOG.warn(name + ": a attempt to override final parameter: " + attr + "; Ignoring");
                        }
                    }
                    if (finalParameter) {
                        finalParameters.add(attr);
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("error parsing conf file: " + e);
            throw new RuntimeException(e);
        } catch (DOMException e) {
            LOG.error("error parsing conf file: " + e);
            throw new RuntimeException(e);
        } catch (SAXException e) {
            LOG.error("error parsing conf file: " + e);
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            LOG.error("error parsing conf file: " + e);
            throw new RuntimeException(e);
        }
    }

    // ##################################################################################
    // Get

    /**
     * 成员变量properties中的数据，直到需要的时候才会加载进来。延迟加载
     *
     * @return @{link properties}
     */
    private synchronized Properties getProps() {
        if (properties == null) {
            properties = new Properties();

            // 触发loadResources加载配置资源
            loadResources(properties, resources, quietmode);
            if (overlay != null) {
                properties.putAll(overlay);
                if (storeResource) {
                    for (Map.Entry<Object, Object> item : overlay.entrySet()) {
                        updatingResource.put((String) item.getKey(), "Unknown");
                    }
                }
            }
        }

        return properties;
    }

    public URL getResource(String name) {
        // 从这里获取文件
        URL url = classLoader.getResource(name);
        LOG.debug("file name => [{}]; url => [{}]", name, url);
        return url;
    }

    public Properties getProperties() {
        if (properties == null) {
            properties = getProps();
        }
        return properties;
    }

    private static Pattern varPat = Pattern.compile("\\$\\{[^\\}\\$\u0020]+\\}");
    private static int MAX_SUBST = 20;

    /**
     * 属性扩展
     *
     * @param expr
     * @return
     */
    private String substituteVars(String expr) {
        if (expr == null) {
            return null;
        }

        Matcher match = varPat.matcher(expr);
        String eval = expr;

        for (int s = 0; s < MAX_SUBST; s++) {
            match.reset(eval);
            // 如果没有匹配到，返回
            if (!match.find()) {
                return eval;
            }

            String var = match.group();
            var = var.substring(2, var.length() - 1); // remove ${...}
            String val = null;

            try {
                // 尝试获取系统属性
                LOG.debug("准备获取系统属性, expr: [{}], val: [{}]", expr, var);
                val = System.getProperty(var);
                LOG.debug("获取系统属性后, expr: [{}], val: [{}]", expr, val);
            } catch (SecurityException se) {
                LOG.warn("Unexpected SecurityException in Configuration", se);
            }
            if (val == null) {
                val = getRaw(val);
            }
            if (val == null) {
                return eval;
            }
            // subsitute
            eval = eval.substring(0, match.start()) + val + eval.substring(match.end());
        }
        throw new IllegalStateException("Variable substitution depth too large: " + MAX_SUBST + " " + expr);
    }

    public String getRaw(String name) {
        return getProps().getProperty(name);
    }

    public String get(String name) {
        return substituteVars(getProps().getProperty(name));
    }

    public String get(String name, String defaultValue) {
        return substituteVars(getProps().getProperty(name, defaultValue));
    }

    /**
     * 获取十六进制数字
     *
     * @param value 值
     * @return 字符串
     */
    public String getHexDigits(String value) {
        boolean negative = false;

        String str = value;
        String hexString = null;

        // 判断是否为负数
        if (value.startsWith("-")) {
            negative = true;
            str = value.substring(1);
        }

        // 0x或0X是16进制的前缀，如果是16进制，就返回16进制的String，否则返回null
        if (str.startsWith("0x") || str.startsWith("0X")) {
            hexString = str.substring(2);
            if (negative) {
                hexString = "-" + hexString;
            }
            return hexString;
        }

        return null;
    }

    public int getInt(String name, int defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            String hexString = getHexDigits(valueString);
            LOG.debug("getInt method, hexString : [{}]", hexString);
            if (hexString != null) {
                return Integer.parseInt(hexString, 16);
            }
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getLong(String name, long defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            String hexString = getHexDigits(valueString);
            if (hexString != null) {
                return Long.parseLong(hexString, 16);
            }
            return Long.parseLong(valueString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float getFloat(String name, float defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(valueString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        }
        if ("true".equals(valueString)) {
            return true;
        } else if ("false".equals(valueString)) {
            return false;
        } else {
            return defaultValue;
        }
    }

    /**
     * overlay：所有修改过的变量都存储在overlay变量中
     *
     * @return overlay properties
     */
    private synchronized Properties getOverlay() {
        if (overlay == null) {
            overlay = new Properties();
        }
        return overlay;
    }

    public Collection<String> getStringCollection(String name) {
        String valueString = get(name);
        return StringUtils.getStringCollection(valueString);
    }

    public String[] getStrings(String name) {
        String valueString = get(name);
        return StringUtils.getStrings(valueString);
    }

    public String[] getStrings(String name, String... defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        } else {
            return StringUtils.getStrings(valueString);
        }
    }

    /**
     * Load a class by name
     *
     * @param name the class name
     * @return the class Object
     */
    public Class<?> getClassByName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, classLoader);
    }

    /**
     * Get the value of the name property as a Class. if no such property is specified, then
     * defaultValue is returned.
     *
     * @param name         the classname
     * @param defaultValue default value
     * @return property value as Class, or defaultValue
     */
    public Class<?> getClass(String name, Class<?> defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            return getClassByName(valueString);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the value of the name property as an array of Class. The value of the property specifies
     * a list of comma separated class names. if no such property is specified, the defaultValue is
     * returned.
     *
     * @param name         the property name
     * @param defaultValue defalut value
     * @return property value as a Class[], or defaultValue
     */
    public Class<?>[] getClasses(String name, Class<?>... defaultValue) {
        String[] classnames = getStrings(name);
        if (classnames == null) {
            return defaultValue;
        }
        try {
            Class<?>[] classes = new Class<?>[classnames.length];
            for (int i = 0; i < classnames.length; i++) {
                classes[i] = getClassByName(classnames[i]);
            }
            return classes;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // ##################################################################################
    // Set
    public void set(String name, String value) {
        getOverlay().setProperty(name, value);
        getProps().setProperty(name, value);
    }

    public void setIfUnset(String name, String value) {
        if (get(name) == null) {
            set(name, value);
        }
    }

    public void setBoolean(String name, boolean value) {
        set(name, Boolean.toString(value));
    }

    public void setBooleanIfUnset(String name, boolean value) {
        setIfUnset(name, Boolean.toString(value));
    }

    public void setInt(String name, int value) {
        set(name, Integer.toString(value));
    }

    public void setLong(String name, long value) {
        set(name, Long.toString(value));
    }

    public void setFloat(String name, float value) {
        set(name, Float.toString(value));
    }

    public void setQuietmode(boolean quietmode) {
        this.quietmode = quietmode;
    }

    public void setStrings(String name, String... values) {
        set(name, StringUtils.arrayToString(values));
    }

    // ##################################################################################
    public int size() {
        return getProps().size();
    }

    public void clear() {
        getProps().clear();
        getOverlay().clear();
    }
}
