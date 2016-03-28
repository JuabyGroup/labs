package com.juaby.labs.raft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by juaby on 16-3-22.
 */
public class Util {

    private static final NumberFormat format;

    static {
        format = NumberFormat.getNumberInstance();
        format.setGroupingUsed(false);
        // format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
    }

    public static List<String> parseStringList(String l, String separator) {
        List<String> tmp = new LinkedList<String>();
        StringTokenizer tok = new StringTokenizer(l, separator);
        String t;

        while (tok.hasMoreTokens()) {
            t = tok.nextToken();
            tmp.add(t.trim());
        }

        return tmp;
    }

    /**
     * Tries to load the class from the current thread's context class loader. If
     * not successful, tries to load the class from the current instance.
     *
     * @param classname Desired class.
     * @param clazz     Class object used to obtain a class loader
     *                  if no context class loader is available.
     * @return Class, or null on failure.
     */
    public static Class loadClass(String classname, Class clazz) throws ClassNotFoundException {
        return loadClass(classname, clazz.getClassLoader());
    }

    /**
     * Tries to load the class from the preferred loader.  If not successful, tries to
     * load the class from the current thread's context class loader or system class loader.
     *
     * @param classname       Desired class name.
     * @param preferredLoader The preferred class loader
     * @return the loaded class.
     * @throws ClassNotFoundException if the class could not be loaded by any loader
     */
    public static Class<?> loadClass(String classname, ClassLoader preferredLoader) throws ClassNotFoundException {
        ClassNotFoundException exception = null;
        for (ClassLoader loader : Arrays.asList(preferredLoader, Thread.currentThread().getContextClassLoader(), ClassLoader.getSystemClassLoader())) {
            try {
                return loader.loadClass(classname);
            } catch (ClassNotFoundException e) {
                if (exception == null) {
                    exception = e;
                }
            }
        }
        throw exception;
    }

    public static byte[] raftIdKey(String raftId) {
        if (raftId == null || raftId.length() == 0) {
            return null;
        }
        try {
            return raftId.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();    //TODO
            return null;
        }
    }

    public static Map<String,String> parseCommaDelimitedProps(String s) {
        if (s == null)
            return null;
        Map<String,String> props=new HashMap<String,String>();
        Pattern p=Pattern.compile("\\s*([^=\\s]+)\\s*=\\s*([^=\\s,]+)\\s*,?"); //Pattern.compile("\\s*([^=\\s]+)\\s*=\\s([^=\\s]+)\\s*,?");
        Matcher matcher=p.matcher(s);
        while(matcher.find()) {
            props.put(matcher.group(1),matcher.group(2));
        }
        return props;
    }

    /**
     * MByte nowadays doesn't mean 1024 * 1024 bytes, but 1 million bytes, see http://en.wikipedia.org/wiki/Megabyte
     *
     * @param bytes
     * @return
     */
    public static String printBytes(long bytes) {
        double tmp;

        if (bytes < 1000)
            return bytes + "b";
        if (bytes < 1000000) {
            tmp = bytes / 1000.0;
            return format.format(tmp) + "KB";
        }
        if (bytes < 1000000000) {
            tmp = bytes / 1000000.0;
            return format.format(tmp) + "MB";
        } else {
            tmp = bytes / 1000000000.0;
            return format.format(tmp) + "GB";
        }
    }

    public static byte[] stringToBytes(String str) {
        if (str == null) {
            return null;
        }
        byte[] retval = new byte[str.length()];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = (byte) str.charAt(i);
        }
        return retval;
    }

    public static String readStringFromStdin(String message) throws Exception {
        System.out.print(message);
        System.out.flush();
        System.in.skip(System.in.available());
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine().trim();
    }

    public static int keyPress(String msg) {
        System.out.println(msg);

        try {
            int ret=System.in.read();
            System.in.skip(System.in.available());
            return ret;
        }
        catch(IOException e) {
            return 0;
        }
    }

    public static long readLongFromStdin(String message) throws Exception {
        String tmp=readStringFromStdin(message);
        return Long.parseLong(tmp);
    }

    public static int readIntFromStdin(String message) throws Exception {
        String tmp=readStringFromStdin(message);
        return Integer.parseInt(tmp);
    }

    /**
     * OS平台
     *
     * @author isea533
     */
    public enum OSPlatform {
        Any("any"),
        Linux("Linux"),
        Mac_OS("Mac OS"),
        Mac_OS_X("Mac OS X"),
        Windows("Windows"),
        OS2("OS/2"),
        Solaris("Solaris"),
        SunOS("SunOS"),
        MPEiX("MPE/iX"),
        HP_UX("HP-UX"),
        AIX("AIX"),
        OS390("OS/390"),
        FreeBSD("FreeBSD"),
        Irix("Irix"),
        Digital_Unix("Digital Unix"),
        NetWare_411("NetWare"),
        OSF1("OSF1"),
        OpenVMS("OpenVMS"),
        Others("Others");

        private OSPlatform(String desc) {
            this.description = desc;
        }

        public String toString() {
            return description;
        }

        private String description;

    }

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public static boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public static boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public static boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public static boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public static boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public static boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public static boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public static boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public static boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public static boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public static boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public static OSPlatform getOSName() {
        OSPlatform platform;
        if (isAix()) {
            platform = OSPlatform.AIX;
        } else if (isDigitalUnix()) {
            platform = OSPlatform.Digital_Unix;
        } else if (isFreeBSD()) {
            platform = OSPlatform.FreeBSD;
        } else if (isHPUX()) {
            platform = OSPlatform.HP_UX;
        } else if (isIrix()) {
            platform = OSPlatform.Irix;
        } else if (isLinux()) {
            platform = OSPlatform.Linux;
        } else if (isMacOS()) {
            platform = OSPlatform.Mac_OS;
        } else if (isMacOSX()) {
            platform = OSPlatform.Mac_OS_X;
        } else if (isMPEiX()) {
            platform = OSPlatform.MPEiX;
        } else if (isNetWare()) {
            platform = OSPlatform.NetWare_411;
        } else if (isOpenVMS()) {
            platform = OSPlatform.OpenVMS;
        } else if (isOS2()) {
            platform = OSPlatform.OS2;
        } else if (isOS390()) {
            platform = OSPlatform.OS390;
        } else if (isOSF1()) {
            platform = OSPlatform.OSF1;
        } else if (isSolaris()) {
            platform = OSPlatform.Solaris;
        } else if (isSunOS()) {
            platform = OSPlatform.SunOS;
        } else if (isWindows()) {
            platform = OSPlatform.Windows;
        } else {
            platform = OSPlatform.Others;
        }
        return platform;
    }

}