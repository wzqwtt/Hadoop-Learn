package com.wzq.hadoop.io.compress;


import com.wzq.hadoop.conf.Configuration;
import com.wzq.hadoop.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A factory that will find the correct codec for a given filename
 */
public class CompressionCodecFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CompressionCodecFactory.class);

    /**
     * A map from the reversed filenames suffixes to the codecs. This is probably overkill, because
     * the maps should be small, but it automatically supports finding the longest matching suffix.
     */
    private SortedMap<String, CompressionCodec> codecs = null;

    private void addCodec(CompressionCodec codec) {
        String suffix = codec.getDefaultExtension();
        codecs.put(new StringBuffer(suffix).reverse().toString(), codec);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator<Map.Entry<String, CompressionCodec>> itr = codecs.entrySet().iterator();

        buf.append("{ ");
        if (itr.hasNext()) {
            Map.Entry<String, CompressionCodec> entry = itr.next();
            buf.append(entry.getKey());
            buf.append(": ");
            buf.append(entry.getValue().getClass().getName());

            while (itr.hasNext()) {
                entry = itr.next();
                buf.append(", ");
                buf.append(entry.getKey());
                buf.append(": ");
                buf.append(entry.getValue().getClass().getName());
            }
        }
        buf.append(" }");

        return buf.toString();
    }

    /**
     * Get the list of codecs listed in the configuration
     *
     * @param conf the configuration to look in
     * @return a list of the Configuration classes or null if the attribute was not set
     */
    public static List<Class<? extends CompressionCodec>> getCodecClasses(Configuration conf) {
        String codecsString = conf.get("io.compression.codecs");
        if (codecsString != null) {
            List<Class<? extends CompressionCodec>> result = new ArrayList<>();

            // split by comma
            StringTokenizer codecSplit = new StringTokenizer(codecsString, ",");
            while (codecSplit.hasMoreElements()) {
                String codecSubstring = codecSplit.nextToken();
                if (codecSubstring.length() != 0) {
                    try {
                        Class<?> cls = conf.getClassByName(codecSubstring);
                        // cls 可以转换为 CompressionCodec
                        if (!CompressionCodec.class.isAssignableFrom(cls)) {
                            throw new IllegalArgumentException("Class " + codecSubstring +
                                    " is not a CompressionCodec");
                        }
                        result.add(cls.asSubclass(CompressionCodec.class));
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("Compression codec " + codecSubstring +
                                " not found.", e);
                    }
                }
            }

            return result;
        } else {
            return null;
        }
    }

    /**
     * Sets a list of codec classes in the configuration
     *
     * @param conf    the configuration to modify
     * @param classes the list of classes to set
     */
    public static void setCodecClasses(Configuration conf, List<Class> classes) {
        StringBuffer buf = new StringBuffer();

        Iterator<Class> itr = classes.iterator();
        if (itr.hasNext()) {
            Class cls = itr.next();
            buf.append(cls.getName());
            while (itr.hasNext()) {
                buf.append(",");
                buf.append(itr.next().getName());
            }
        }
        conf.set("io.compression.codecs", buf.toString());
    }

    /**
     * Find the codecs specified in the config value {@code io.compression.codecs} and register them.
     * Defaults to gzip and zip
     *
     * @param conf the configuration
     */
    public CompressionCodecFactory(Configuration conf) {
        codecs = new TreeMap<String, CompressionCodec>();
        List<Class<? extends CompressionCodec>> codecClasses = getCodecClasses(conf);

        if (codecClasses == null) {
            // TODO 添加默认的编解码压缩器
//            addCodec(new GzipCodec());
//            addCodec(new DefaultCodec());
        } else {
            Iterator<Class<? extends CompressionCodec>> itr = codecClasses.iterator();
            while (itr.hasNext()) {
                CompressionCodec codec = ReflectionUtils.newInstance(itr.next(), conf);
                addCodec(codec);
            }
        }
    }

    /**
     * TODO Get {@link CompressionCodec} using fs.Path
     *
     * @param file
     * @return
     */
    public CompressionCodec getCodec(Object file) {
        return null;
    }

    /**
     * @param name 编解码器全类名
     * @return
     */
    public CompressionCodec getCodec(String name) {
        CompressionCodec result = null;

        String reversedFilename = new StringBuffer(name).reverse().toString();
        SortedMap<String, CompressionCodec> subMap = codecs.headMap(reversedFilename);

        if (!subMap.isEmpty()) {
            String potentialSuffix = subMap.lastKey();
            if (reversedFilename.startsWith(potentialSuffix)) {
                result = codecs.get(potentialSuffix);
            }
        }
        return result;
    }
}
