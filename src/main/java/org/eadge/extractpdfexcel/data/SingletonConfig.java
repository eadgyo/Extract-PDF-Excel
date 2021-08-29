package org.eadge.extractpdfexcel.data;

public final class SingletonConfig {
    private static SingletonConfig instance;
    public boolean ignoreDirection;

    public SingletonConfig()
    {
        this.ignoreDirection = false;
    }

    public static SingletonConfig getInstance()
    {
        if (instance == null)
        {
            instance = new SingletonConfig();
        }

        return instance;
    }
}
