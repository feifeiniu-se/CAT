package Util;

public class OsInfoUtil {
    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static final OsInfoUtil _instance = new OsInfoUtil();

    private OsType platform;

    private OsInfoUtil() {
    }

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOS() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x");
    }

    public static boolean isMacOSX() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public static OsType getOSname() {
        if (isLinux()) {
            _instance.platform = OsType.Linux;
        } else if (isMacOS()) {
            _instance.platform = OsType.Mac_OS;
        } else if (isMacOSX()) {
            _instance.platform = OsType.Mac_OS_X;
        } else if (isWindows()) {
            _instance.platform = OsType.Windows;
        }
        return _instance.platform;
    }
}
