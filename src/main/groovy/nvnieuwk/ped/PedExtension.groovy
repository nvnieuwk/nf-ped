package nvnieuwk.ped

import groovy.transform.CompileStatic
import java.nio.file.Path

import nextflow.Session
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint

/**
 * Implements a custom function which can be imported by
 * Nextflow scripts.
 */
@CompileStatic
class PedExtension extends PluginExtensionPoint {

    Session session

    @Override
    protected void init(Session session) {
        this.session = session
    }

    @Function
    Ped initializePed(Map<String,Object> options = [:], Path pedFile) {
        return initializePed(options, [pedFile])
    }

    @Function
    Ped initializePed(Map<String,Object> options = [:], List<Path> pedFiles) {
        final Ped ped = new Ped(session)
        pedFiles.each { Path pedFile ->
            ped.importPed(pedFile)
        }
        return ped
    }

}
