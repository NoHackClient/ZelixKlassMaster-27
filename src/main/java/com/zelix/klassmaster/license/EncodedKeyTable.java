package com.zelix.klassmaster.license;

public abstract class EncodedKeyTable extends EncodedClassNameSource {
    static {
        System.getProperty("java.vm.version", "unknown");
    }

    public String getEncodedKey(int ba) {
        switch (ba) {
            case 0:
                return "_UAQ\u0019UT[Vj~SX#\"D/TO\"iX'-Qly[R\\!5(U0U\u001aUV_";
            case 1:
                return "S'5Y\u001a*$YRl\rQ]&RF^#5Q\u0019[T]\"\u0019xRS$ZM^'3%kTT\\";
            case 2:
                return "^SEW\u0017U$YV\u0016\u000e'RSPA(U4Xj.UW&\u001d\f&,T&DSQ0Sl] X";
            case 3:
                return "_XFR\u001e\\WY^\u0018\fTS\\PF^'DS\u001fY$^P\u0016\nQ[R[AS 5T\u001f-V*";
            default:
                return ",'CV\u0017($\\Wm\u000eS/W\"0,YNU\u001c[ ]Q\u001dxU.U[E_V2P\u0017)X^";
        }
    }
}
