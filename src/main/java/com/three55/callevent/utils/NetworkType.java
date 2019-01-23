package com.three55.callevent.utils;

public enum NetworkType {
    NT_2G,
    NT_3G,
    NT_LTE,
    UNKNOWN;

    public static NetworkType fromString(String networkTypeStr) {
        switch (networkTypeStr) {
            case "LTE":
                return NT_LTE;
            case "2G":
                return NT_2G;
            case "3G":
                return NT_3G;
            default:
                return UNKNOWN;
        }
    }

    public static String toString(NetworkType networkType) {
        switch (networkType) {
            case NT_LTE:
                return "LTE";
            case NT_2G:
                return "2G";
            case NT_3G:
                return "3G";
            default:
                return "UNKNOWN";
        }
    }

}
