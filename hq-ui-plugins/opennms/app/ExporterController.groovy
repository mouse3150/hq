import java.text.SimpleDateFormat
import org.hyperic.hq.context.Bootstrap;
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.PlatformManager;
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.authz.server.session.ResourceSortField


class ExporterController 
	extends BaseController
{
    def ExporterController() {
        setXMLMethods(['list'])
    }
    
    def list(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def platforms = resourceHelper.findPlatforms(new PageInfo(ResourceSortField.NAME, true));
        def man = Bootstrap.getBean(PlatformManager.class)
        xml.'model-import'('foreign-source':'HQ', 'date-stamp':formatter.format(new Date())) {
            platforms.each { res ->   
                def p = man.findPlatformById(res.instanceId)
                node('node-label':p.fqdn, 'foreign-id':p.id) {
                     'interface'('ip-addr': p.agent.address, descr: 'agent-address',
                                 status: 1, 'snmp-primary': 'N') {
                        'monitored-service'('service-name': 'ICMP')
                        'monitored-service'('service-name': 'HypericAgent')
                    }
                }
            }
        }

       xml
    }
}