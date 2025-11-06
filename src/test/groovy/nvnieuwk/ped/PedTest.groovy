package nvnieuwk.ped

import java.nio.file.Path

import nextflow.Session
import nextflow.Nextflow
import spock.lang.Specification

/**
 * Implements tests for the {@link Ped} class.
 *
 */
class PedTest extends Specification {

    def 'should create the Ped instance' () {
        given:
        def ped = new Ped(Mock(Session))
        expect:
        ped instanceof Ped
    }

    def 'should import 1 PED file' () {
        given:
        def ped = new Ped(Mock(Session))
        when:
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        def result = ped.getEntries()
        then:
        result.size() == 3
    }

    def 'should import 2 PED files' () {
        given:
        def ped = new Ped(Mock(Session))
        when:
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test2.ped").getPath()))
        def result = ped.getEntries()
        then:
        result.size() == 6
    }

    def 'getEntries' () {
        given:
        def ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        when:
        def result = ped.getEntries()
        then:
        result instanceof Set<PedEntry>
        result.size() == 3
    }

    def 'getFamilies' () {
        given:
        def ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        when:
        def result = ped.getFamilies()
        then:
        result instanceof Set<String>
        result == ["family2"] as Set<String>
    }

    def 'getIndividuals' () {
        given:
        def ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        when:
        def result = ped.getIndividuals()
        then:
        result instanceof Set<String>
        result == ["sample4", "sample5", "sample6"] as Set<String>
    }

    def 'getEntriesByFamily' () {
        given:
        def ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test2.ped").getPath()))
        when:
        def result = ped.getEntriesByFamily("family1")
        then:
        result instanceof Set<PedEntry>
        result.size() == 3
    }

    def 'writePed default' () {
        given:
        def Session session = Mock(Session)
        def ped = new Ped(session)
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        def Path workDir = session?.getWorkDir()?.toString() ?: Nextflow.file("work")
        Nextflow.file(workDir.resolve("generated_peds")).deleteDir()
        when:
        def result = ped.writePed()
        then:
        result instanceof Path
        result.exists()
        result.text.split("\n").length == 3
        result.toString().endsWith("${workDir.baseName}/generated_peds/ped_50f1dbc063e6d36b2dfdc9146b72d2a5.ped")
    }

    def 'writePed overwrite' () {
        given:
        def ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        when:
        def result = ped.writePed(overwrite: true)
        then:
        result instanceof Path
        result.exists()
        result.text.split("\n").length == 3
        result.toString().endsWith("/generated_peds/ped_50f1dbc063e6d36b2dfdc9146b72d2a5.ped")
    }

    def 'writePed custom file name' () {
        given:
        def Session session = Mock(Session)
        def ped = new Ped(session)
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        def Path workDir = session?.getWorkDir()?.toString() ?: Nextflow.file("work")
        Nextflow.file(workDir.resolve("custom_ped_name_test")).deleteDir()
        when:
        def result = ped.writePed("${workDir}/custom_ped_name_test/this_is_a_test.ped")
        then:
        result instanceof Path
        result.exists()
        result.text.split("\n").length == 3
        result.toString().endsWith("${workDir.baseName}/custom_ped_name_test/this_is_a_test.ped")
    }

    def 'writePed filter families' () {
        given:
        def ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test1.ped").getPath()))
        ped.importPed(Nextflow.file(this.getClass().getResource("/test2.ped").getPath()))
        when:
        def result = ped.writePed(families: ["family2", "family3"], overwrite: true)
        then:
        result instanceof Path
        result.exists()
        result.text.split("\n").length == 3
        result.toString().endsWith("/generated_peds/ped_50f1dbc063e6d36b2dfdc9146b72d2a5.ped")
    }
}
