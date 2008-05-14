/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004-2008], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package org.hyperic.hq.plugin.vim;

import org.hyperic.util.collection.IntHashMap;

import com.vmware.vim.ManagedObjectReference;
import com.vmware.vim.PerfCounterInfo;
import com.vmware.vim.PerfEntityMetric;
import com.vmware.vim.PerfEntityMetricBase;
import com.vmware.vim.PerfMetricId;
import com.vmware.vim.PerfMetricIntSeries;
import com.vmware.vim.PerfMetricSeries;
import com.vmware.vim.PerfQuerySpec;

public class VimHostCollector extends VimCollector {
 
    protected ManagedObjectReference getRoot() {
        return null;
    }

    protected String getType() {
        return "HostSystem";
    }

    protected String getName() {
        return getHostname();
    }

    protected void printXml(PerfCounterInfo info, String key) {
        String name = info.getNameInfo().getLabel();
        System.out.println("    <metric name=\"" + name + "\"");
        System.out.println("                  alias=\"" + key + "\"");
        System.out.println("     />");
    }

    protected void collect(VimUtil vim)
        throws Exception {

        ManagedObjectReference mor = getManagedObjectReference(vim);
        ManagedObjectReference perfManager = vim.getPerfManager();
        IntHashMap counters = getCounterInfo(vim, perfManager);
        PerfMetricId[] ids = getPerfMetricIds(vim, perfManager, mor);

        PerfQuerySpec spec = new PerfQuerySpec();
        spec.setEntity(mor);
        spec.setMetricId(ids);
        spec.setMaxSample(new Integer(1));
        spec.setIntervalId(new Integer(20));
       
        PerfQuerySpec[] query = new PerfQuerySpec[] { spec };      
        PerfEntityMetricBase[] values =
            vim.getConn().getService().queryPerf(perfManager, query);
        
        PerfEntityMetric metric = (PerfEntityMetric)values[0];
        PerfMetricSeries[] vals = metric.getValue();

        for (int i=0; i<vals.length; i++) {
            PerfCounterInfo info =
                (PerfCounterInfo)counters.get(vals[i].getId().getCounterId());

            if (info == null) {
                continue;
            }

            if (!(vals[i] instanceof PerfMetricIntSeries)) {
                continue;
            }
            PerfMetricIntSeries series = (PerfMetricIntSeries)vals[i];
            String key = getCounterKey(info);
            String instance = series.getId().getInstance();
            if (instance.length() != 0) {
                continue;
            }
            long val = series.getValue()[0];
            if (info.getStatsType().getValue().equals("absolute")) {
                setValue(key, val);
            }
        }
    }
}
