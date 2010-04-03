/*
 * Generated by XDoclet - Do not edit!
 */
package org.hyperic.hq.measurement.shared;

import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.authz.server.session.AuthzSubject;
import org.hyperic.hq.authz.shared.PermissionException;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

/**
 * Local interface for TrackerManager.
 */
public interface TrackerManager {
    /**
     * Enable log and config tracking for a resource if it has been enabled.
     */
    public void enableTrackers(AuthzSubject subject, AppdefEntityID id, ConfigResponse config)
        throws PermissionException, PluginException;

    /**
     * Disable log and config tracking for a resource.
     */
    public void disableTrackers(AuthzSubject subject, AppdefEntityID id, ConfigResponse config)
        throws PermissionException, PluginException;

    /**
     * Toggle log and config tracking for the resource.
     */
    public void toggleTrackers(AuthzSubject subject, AppdefEntityID id, ConfigResponse config)
        throws PermissionException, PluginException;

}