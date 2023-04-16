package com.wzq.hadoop.conf;

/**
 * Base class for things that may be configured with a {@link Configuration}
 */
public class Configured implements Configurable{

    private Configuration conf;

    public Configured() {
        this(null);
    }

    public Configured(Configuration conf) {
        setConf(conf);
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
