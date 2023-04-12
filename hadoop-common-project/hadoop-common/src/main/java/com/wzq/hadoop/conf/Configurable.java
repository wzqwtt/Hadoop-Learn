package com.wzq.hadoop.conf;

/**
 * 为需要配置的类提供可配置的接口
 *
 * @author wzq
 * @create 2023-04-12 13:07
 */
public interface Configurable {

    /**
     * Set the configuration to be used by this object
     *
     * @param conf Configuration
     */
    void setConf(Configuration conf);

    /**
     * @return configuration uses by this object
     */
    Configuration getConf();
}
