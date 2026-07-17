package com.medo.api.tenant;

public class TenantContext {
    private static final ThreadLocal<String> CURRENT = new InheritableThreadLocal<>();
    private TenantContext() {}
    public static void setCurrentTenant(String t) { CURRENT.set(t); }
    public static String getCurrentTenant()        { return CURRENT.get(); }
    public static void clear()                     { CURRENT.remove(); }
    public static boolean hasTenant()              { return CURRENT.get() != null; }
}
