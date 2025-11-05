package nvnieuwk.ped

import groovy.transform.CompileStatic
import nextflow.plugin.BasePlugin
import org.pf4j.PluginWrapper

/**
 * The plugin entry point
 */
@CompileStatic
class PedPlugin extends BasePlugin {

    PedPlugin(PluginWrapper wrapper) {
        super(wrapper)
    }
}
