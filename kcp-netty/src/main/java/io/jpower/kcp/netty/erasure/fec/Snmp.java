/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure.fec;

import java.util.concurrent.atomic.LongAdder;

public class Snmp {
    public LongAdder BytesSent = new LongAdder();
    public LongAdder BytesReceived = new LongAdder();
    public LongAdder MaxConn = new LongAdder();
    public LongAdder ActiveOpens = new LongAdder();
    public LongAdder PassiveOpens = new LongAdder();
    public LongAdder CurrEstab = new LongAdder();
    public LongAdder InErrs = new LongAdder();
    public LongAdder InCsumErrors = new LongAdder();
    public LongAdder KCPInErrors = new LongAdder();
    public LongAdder InPkts = new LongAdder();
    public LongAdder OutPkts = new LongAdder();
    public LongAdder InSegs = new LongAdder();
    public LongAdder OutSegs = new LongAdder();
    public LongAdder InBytes = new LongAdder();
    public LongAdder OutBytes = new LongAdder();
    public LongAdder RetransSegs = new LongAdder();
    public LongAdder FastRetransSegs = new LongAdder();
    public LongAdder EarlyRetransSegs = new LongAdder();
    public LongAdder LostSegs = new LongAdder();
    public LongAdder RepeatSegs = new LongAdder();
    public LongAdder FECRecovered = new LongAdder();
    public LongAdder FECErrs = new LongAdder();
    public LongAdder FECDataShards = new LongAdder();
    public LongAdder FECParityShards = new LongAdder();
    public LongAdder FECShortShards = new LongAdder();
    public LongAdder FECRepeatDataShards = new LongAdder();
    public static volatile Snmp snmp = new Snmp();

    public LongAdder getBytesSent() {
        return this.BytesSent;
    }

    public void setBytesSent(LongAdder bytesSent) {
        this.BytesSent = bytesSent;
    }

    public LongAdder getBytesReceived() {
        return this.BytesReceived;
    }

    public void setBytesReceived(LongAdder bytesReceived) {
        this.BytesReceived = bytesReceived;
    }

    public LongAdder getMaxConn() {
        return this.MaxConn;
    }

    public void setMaxConn(LongAdder maxConn) {
        this.MaxConn = maxConn;
    }

    public LongAdder getActiveOpens() {
        return this.ActiveOpens;
    }

    public void setActiveOpens(LongAdder activeOpens) {
        this.ActiveOpens = activeOpens;
    }

    public LongAdder getPassiveOpens() {
        return this.PassiveOpens;
    }

    public void setPassiveOpens(LongAdder passiveOpens) {
        this.PassiveOpens = passiveOpens;
    }

    public LongAdder getCurrEstab() {
        return this.CurrEstab;
    }

    public void setCurrEstab(LongAdder currEstab) {
        this.CurrEstab = currEstab;
    }

    public LongAdder getInErrs() {
        return this.InErrs;
    }

    public void setInErrs(LongAdder inErrs) {
        this.InErrs = inErrs;
    }

    public LongAdder getInCsumErrors() {
        return this.InCsumErrors;
    }

    public void setInCsumErrors(LongAdder inCsumErrors) {
        this.InCsumErrors = inCsumErrors;
    }

    public LongAdder getKCPInErrors() {
        return this.KCPInErrors;
    }

    public void setKCPInErrors(LongAdder KCPInErrors) {
        this.KCPInErrors = KCPInErrors;
    }

    public LongAdder getInPkts() {
        return this.InPkts;
    }

    public void setInPkts(LongAdder inPkts) {
        this.InPkts = inPkts;
    }

    public LongAdder getOutPkts() {
        return this.OutPkts;
    }

    public void setOutPkts(LongAdder outPkts) {
        this.OutPkts = outPkts;
    }

    public LongAdder getInSegs() {
        return this.InSegs;
    }

    public void setInSegs(LongAdder inSegs) {
        this.InSegs = inSegs;
    }

    public LongAdder getOutSegs() {
        return this.OutSegs;
    }

    public void setOutSegs(LongAdder outSegs) {
        this.OutSegs = outSegs;
    }

    public LongAdder getInBytes() {
        return this.InBytes;
    }

    public void setInBytes(LongAdder inBytes) {
        this.InBytes = inBytes;
    }

    public LongAdder getOutBytes() {
        return this.OutBytes;
    }

    public void setOutBytes(LongAdder outBytes) {
        this.OutBytes = outBytes;
    }

    public LongAdder getRetransSegs() {
        return this.RetransSegs;
    }

    public void setRetransSegs(LongAdder retransSegs) {
        this.RetransSegs = retransSegs;
    }

    public LongAdder getFastRetransSegs() {
        return this.FastRetransSegs;
    }

    public void setFastRetransSegs(LongAdder fastRetransSegs) {
        this.FastRetransSegs = fastRetransSegs;
    }

    public LongAdder getEarlyRetransSegs() {
        return this.EarlyRetransSegs;
    }

    public void setEarlyRetransSegs(LongAdder earlyRetransSegs) {
        this.EarlyRetransSegs = earlyRetransSegs;
    }

    public LongAdder getLostSegs() {
        return this.LostSegs;
    }

    public void setLostSegs(LongAdder lostSegs) {
        this.LostSegs = lostSegs;
    }

    public LongAdder getRepeatSegs() {
        return this.RepeatSegs;
    }

    public void setRepeatSegs(LongAdder repeatSegs) {
        this.RepeatSegs = repeatSegs;
    }

    public LongAdder getFECRecovered() {
        return this.FECRecovered;
    }

    public void setFECRecovered(LongAdder FECRecovered) {
        this.FECRecovered = FECRecovered;
    }

    public LongAdder getFECErrs() {
        return this.FECErrs;
    }

    public void setFECErrs(LongAdder FECErrs) {
        this.FECErrs = FECErrs;
    }

    public LongAdder getFECDataShards() {
        return this.FECDataShards;
    }

    public void setFECDataShards(LongAdder FECDataShards) {
        this.FECDataShards = FECDataShards;
    }

    public LongAdder getFECParityShards() {
        return this.FECParityShards;
    }

    public void setFECParityShards(LongAdder FECParityShards) {
        this.FECParityShards = FECParityShards;
    }

    public LongAdder getFECShortShards() {
        return this.FECShortShards;
    }

    public void setFECShortShards(LongAdder FECShortShards) {
        this.FECShortShards = FECShortShards;
    }

    public LongAdder getFECRepeatDataShards() {
        return this.FECRepeatDataShards;
    }

    public void setFECRepeatDataShards(LongAdder FECRepeatDataShards) {
        this.FECRepeatDataShards = FECRepeatDataShards;
    }

    public static Snmp getSnmp() {
        return snmp;
    }

    public static void setSnmp(Snmp snmp) {
        Snmp.snmp = snmp;
    }

    public String toString() {
        return "Snmp{BytesSent=" + this.BytesSent + ", BytesReceived=" + this.BytesReceived + ", MaxConn=" + this.MaxConn + ", ActiveOpens=" + this.ActiveOpens + ", PassiveOpens=" + this.PassiveOpens + ", CurrEstab=" + this.CurrEstab + ", InErrs=" + this.InErrs + ", InCsumErrors=" + this.InCsumErrors + ", KCPInErrors=" + this.KCPInErrors + ", \u00e6\u201d\u00b6\u00e5\u02c6\u00b0\u00e5\u0152\u2026=" + this.InPkts + ", \u00e5\ufffd\u2018\u00e9\u20ac\ufffd\u00e5\u0152\u2026=" + this.OutPkts + ", InSegs=" + this.InSegs + ", OutSegs=" + this.OutSegs + ", \u00e6\u201d\u00b6\u00e5\u02c6\u00b0\u00e5\u00ad\u2014\u00e8\u0160\u201a=" + this.InBytes + ", \u00e5\ufffd\u2018\u00e9\u20ac\ufffd\u00e5\u00ad\u2014\u00e8\u0160\u201a=" + this.OutBytes + ", \u00e6\u20ac\u00bb\u00e5\u2026\u00b1\u00e9\u2021\ufffd\u00e5\ufffd\u2018\u00e6\u2022\u00b0=" + this.RetransSegs + ", \u00e5\u00bf\u00ab\u00e9\u20ac\u0178\u00e9\u2021\ufffd\u00e5\ufffd\u2018\u00e6\u2022\u00b0=" + this.FastRetransSegs + ", \u00e7\u00a9\u00ba\u00e9\u2014\u00b2\u00e5\u00bf\u00ab\u00e9\u20ac\u0178\u00e9\u2021\ufffd\u00e5\ufffd\u2018\u00e6\u2022\u00b0=" + this.EarlyRetransSegs + ", \u00e8\u00b6\u2026\u00e6\u2014\u00b6\u00e9\u2021\ufffd\u00e5\ufffd\u2018\u00e6\u2022\u00b0=" + this.LostSegs + ", \u00e6\u201d\u00b6\u00e5\u02c6\u00b0\u00e9\u2021\ufffd\u00e5\u00a4\ufffd\u00e5\u0152\u2026\u00e6\u2022\u00b0\u00e9\u2021\ufffd=" + this.RepeatSegs + ", fec\u00e6\ufffd\u00a2\u00e5\u00a4\ufffd\u00e6\u2022\u00b0=" + this.FECRecovered + ", fec\u00e6\ufffd\u00a2\u00e5\u00a4\ufffd\u00e9\u201d\u2122\u00e8\u00af\u00af\u00e6\u2022\u00b0=" + this.FECErrs + ", \u00e6\u201d\u00b6\u00e5\u02c6\u00b0fecData\u00e6\u2022\u00b0=" + this.FECDataShards + ", \u00e6\u201d\u00b6\u00e5\u02c6\u00b0fecParity\u00e6\u2022\u00b0=" + this.FECParityShards + ", fec\u00e7\u00bc\u201c\u00e5\u00ad\u02dc\u00e5\u2020\u2014\u00e4\u00bd\u2122\u00e6\u00b7\u02dc\u00e6\u00b1\u00b0data\u00e5\u0152\u2026\u00e6\u2022\u00b0=" + this.FECShortShards + ", fec\u00e6\u201d\u00b6\u00e5\u02c6\u00b0\u00e9\u2021\ufffd\u00e5\u00a4\ufffd\u00e7\u0161\u201e\u00e6\u2022\u00b0\u00e6\ufffd\u00ae\u00e5\u0152\u2026=" + this.FECRepeatDataShards + "}";
    }
}

