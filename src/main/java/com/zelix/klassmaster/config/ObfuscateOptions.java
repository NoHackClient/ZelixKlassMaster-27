package com.zelix.klassmaster.config;

import com.zelix.annotation.ExceptionObfuscationPolicy;
import com.zelix.annotation.FlowObfuscationPolicy;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.util.SettingsObjectInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.io.ObjectInputStream;

public class ObfuscateOptions implements Serializable {
    public static final Map XC = new HashMap(13);
    public static final Map XP = new HashMap(13);
    public static final Map X3 = new HashMap(13);
    public static final Map XJ = new HashMap(13);
    public static final Map Xr = new HashMap(13);
    public String di;
    public ChangeLogInputFile[] g;
    public String dB;
    public String x;
    public String dE;
    public String dN;
    public String an;
    public Integer at;
    public String q;
    public transient PrintWriter h;
    public String t;
    public String ai;
    public String v;
    public transient boolean a;
    public String D;
    public String dv;
    public int b = 1;
    public int u = 0;
    public boolean c = false;
    public boolean e = false;
    public boolean d = true;
    public String f = "ChangeLog.txt";
    public int i = 0;
    public int B = 0;
    public int am = 0;
    public boolean w = false;
    public boolean E = true;
    public boolean dr = true;
    public boolean j = true;
    public boolean XN = false;
    public boolean ar = false;
    public MixedCaseNamesMode F = MixedCaseNamesMode.d;
    public boolean ay = false;
    public boolean dV = false;
    public int aC = 0;
    public boolean k = false;
    public boolean l = false;
    public int o = 1;
    public int d7 = 0;
    public int as = 4;
    public int dZ = 0;
    public int dC = 0;
    public boolean p = true;
    public int C = 1;
    public boolean s = false;
    public boolean z = false;
    public boolean y = false;
    public boolean A = true;
    public int aj = 0;
    public int m = 1;
    public int r = 1;

    public static String getLevelNameForPolicy(String string) {
        return (String) Xr.get(string);
    }

    public ObfuscateOptions() {
    }

    public boolean isLoaded() {
        return this.a;
    }

    public void saveToFile(final String parent) {
        final File file = new File(parent, "ZKM_SO.ser");
        if (file.exists() && (file.isDirectory() || !file.canWrite())) {
            return;
        }
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(this);
            try {
                objectOutputStream.close();
            } catch (final IOException ex) {
            }
        } catch (final IOException ex2) {
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (final IOException ex3) {
                }
            }
        }
    }

    public static int parseFlowLevel(String string) {
        return (Integer) XC.get(string);
    }

    public static String getFlowLevelName(Integer integer) {
        return (String) XP.get(integer);
    }

    public static int parseExceptionLevel(String string) {
        return (Integer) X3.get(string);
    }

    static {
        XC.put("none", 0);
        XC.put("light", 1);
        XC.put("normal", 2);
        XC.put("aggressive", 3);
        XC.put("extraAggressive", 4);
        XP.put(0, "none");
        XP.put(1, "light");
        XP.put(2, "normal");
        XP.put(3, "aggressive");
        XP.put(4, "extraAggressive");
        X3.put("none", 0);
        X3.put("light", 1);
        X3.put("heavy", 2);
        XJ.put(0, "none");
        XJ.put(1, "light");
        XJ.put(2, "heavy");
        Xr.put(FlowObfuscationPolicy.NONE.name(), "none");
        Xr.put(FlowObfuscationPolicy.LIGHT.name(), "light");
        Xr.put(FlowObfuscationPolicy.NORMAL.name(), "normal");
        Xr.put(FlowObfuscationPolicy.AGGRESSIVE.name(), "aggressive");
        Xr.put(ExceptionObfuscationPolicy.HEAVY.name(), "heavy");
    }

    public ObfuscateOptions(final String parent, final String child) {
        this.b = 1;
        this.u = 0;
        this.c = false;
        this.e = false;
        this.d = true;
        this.f = "ChangeLog.txt";
        this.i = 0;
        this.B = 0;
        this.am = 0;
        this.w = false;
        this.E = true;
        this.dr = true;
        this.j = true;
        this.XN = false;
        this.ar = false;
        this.F = MixedCaseNamesMode.d;
        this.ay = false;
        this.dV = false;
        this.aC = 0;
        this.k = false;
        this.l = false;
        this.o = 1;
        this.d7 = 0;
        this.as = 4;
        this.dZ = 0;
        this.dC = 0;
        this.p = true;
        this.C = 1;
        this.s = false;
        this.z = false;
        this.y = false;
        this.A = true;
        this.aj = 0;
        this.m = 1;
        this.r = 1;
        final File file = new File(parent, child);
        if (file.exists() && !file.isDirectory() && file.canRead()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new SettingsObjectInputStream(new FileInputStream(file));
                final ObfuscateOptions obfuscateOptions = (ObfuscateOptions) objectInputStream.readObject();
                this.b = obfuscateOptions.b;
                this.F = obfuscateOptions.F;
                this.ay = obfuscateOptions.ay;
                this.dV = obfuscateOptions.dV;
                this.aC = obfuscateOptions.aC;
                this.u = obfuscateOptions.u;
                this.c = obfuscateOptions.c;
                this.e = obfuscateOptions.e;
                this.d = obfuscateOptions.d;
                this.g = obfuscateOptions.g;
                this.f = obfuscateOptions.f;
                this.i = obfuscateOptions.i;
                this.B = obfuscateOptions.B;
                this.am = obfuscateOptions.am;
                this.w = obfuscateOptions.w;
                this.E = obfuscateOptions.E;
                this.dr = obfuscateOptions.dr;
                this.j = obfuscateOptions.j;
                this.XN = obfuscateOptions.XN;
                this.ar = obfuscateOptions.ar;
                this.s = obfuscateOptions.s;
                this.t = obfuscateOptions.t;
                this.k = obfuscateOptions.k;
                this.l = obfuscateOptions.l;
                this.o = obfuscateOptions.o;
                this.d7 = obfuscateOptions.d7;
                this.as = obfuscateOptions.as;
                this.dZ = obfuscateOptions.dZ;
                this.dC = obfuscateOptions.dC;
                this.p = obfuscateOptions.p;
                this.x = obfuscateOptions.x;
                this.z = obfuscateOptions.z;
                this.y = obfuscateOptions.y;
                this.A = obfuscateOptions.A;
                this.r = obfuscateOptions.r;
                this.q = obfuscateOptions.q;
                this.D = obfuscateOptions.D;
                this.C = obfuscateOptions.C;
                this.aj = obfuscateOptions.aj;
                this.m = obfuscateOptions.m;
                this.ai = obfuscateOptions.ai;
                this.an = obfuscateOptions.an;
                this.at = obfuscateOptions.at;
                this.dv = obfuscateOptions.dv;
                this.dE = obfuscateOptions.dE;
                this.di = obfuscateOptions.di;
                this.dB = obfuscateOptions.dB;
                this.a = true;
                try {
                    objectInputStream.close();
                } catch (final IOException ex) {
                }
            } catch (final IOException ex2) {
            } catch (final ClassNotFoundException ex3) {
            } catch (final ClassCastException ex4) {
            } catch (final AssertionFailedException ex5) {
                throw ex5;
            } catch (final Throwable t) {
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (final IOException ex6) {
                    }
                }
            }
        }
    }

    public static String getExceptionLevelName(Integer integer) {
        return (String) XJ.get(integer);
    }
}
