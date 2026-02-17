/* groovylint-disable MethodName */
package nvnieuwk.ped

import java.nio.file.Path

import nextflow.Session
import nextflow.Nextflow
import spock.lang.Specification
import groovy.transform.CompileDynamic

/**
 * Implements tests for the {@link Ped} class.
 */
@CompileDynamic
class PedTest extends Specification {

    void 'should create the Ped instance'() {
        given:
        Ped ped = new Ped(Mock(Session))
        expect:
        ped instanceof Ped
    }

    void 'should import 1 PED file'() {
        given:
        Ped ped = new Ped(Mock(Session))
        when:
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        Set<PedEntry> result = ped.entries
        then:
        result.size() == 3
    }

    void 'should import 2 PED files'() {
        given:
        Ped ped = new Ped(Mock(Session))
        when:
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test2.ped').path))
        Set<PedEntry> result = ped.entries
        then:
        result.size() == 6
    }

    void 'should not import duplicate entries'() {
        given:
        Ped ped = new Ped(Mock(Session))
        when:
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.changes.ped').path))
        Set<PedEntry> result = ped.entries
        then:
        result.size() == 3
        result*.toString() == [
            'family2\tsample4\t0\t0\t2\t0',
            'family2\tsample5\t0\t0\t1\t0',
            'family2\tsample6\tsample5\tsample4\t1\t0'
        ]
    }

    void 'should overwrite duplicate entries'() {
        given:
        Ped ped = new Ped(Mock(Session))
        when:
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.changes.ped').path), overwrite:true)
        Set<PedEntry> result = ped.entries
        then:
        result.size() == 3
        result*.toString() == [
            'family2\tsample4\t0\t0\t1\t0',
            'family2\tsample5\t0\t0\t2\t0',
            'family2\tsample6\tsample4\tsample5\t2\t0'
        ]
    }

    void 'getEntries'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        when:
        Set<PedEntry> result = ped.entries
        then:
        result instanceof Set<PedEntry>
        result.size() == 3
    }

    void 'getFamilies'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        when:
        Set<String> result = ped.families
        then:
        result instanceof Set<String>
        result == ['family2'] as Set<String>
    }

    void 'getIndividuals'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        when:
        Set<String> result = ped.individuals
        then:
        result instanceof Set<String>
        result == ['sample4', 'sample5', 'sample6'] as Set<String>
    }

    void 'getEntriesByFamily'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test2.ped').path))
        when:
        Set<PedEntry> result = ped.getEntriesByFamily('family1')
        then:
        result instanceof Set<PedEntry>
        result.size() == 3
    }

    void 'getEntriesByIndividual'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test2.ped').path))
        when:
        Set<PedEntry> result = ped.getEntriesByIndividual('sample2')
        then:
        result instanceof Set<PedEntry>
        result.size() == 1
    }

    void 'getFamiliesFromIndividual'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test2.ped').path))
        when:
        Set<String> result = ped.getFamiliesFromIndividual('sample2')
        then:
        result instanceof Set<String>
        result == ['family1'] as Set<String>
    }

    void 'getIndividualsFromFamily'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test2.ped').path))
        when:
        Set<String> result = ped.getIndividualsFromFamily('family1')
        then:
        result instanceof Set<String>
        result == ['sample1', 'sample2', 'sample3'] as Set<String>
    }

    void 'writePed default'() {
        given:
        Session session = Mock(Session)
        Ped ped = new Ped(session)
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        Path workDir = session?.workDir?.toString() ?: Nextflow.file('work')
        Nextflow.file(workDir.resolve('generated_peds')).deleteDir()
        when:
        Path result = ped.writePed()
        then:
        result instanceof Path
        result.exists()
        result.text.split('\n').length == 3
        result.toString().endsWith("${workDir.baseName}/generated_peds/ped_9c3038bfcab545e7530cab4090cd4071.ped")
    }

    void 'writePed overwrite'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        when:
        Path result = ped.writePed(overwrite: true)
        then:
        result instanceof Path
        result.exists()
        result.text.split('\n').length == 3
        result.toString().endsWith('/generated_peds/ped_9c3038bfcab545e7530cab4090cd4071.ped')
    }

    void 'writePed custom file name'() {
        given:
        Session session = Mock(Session)
        Ped ped = new Ped(session)
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        Path workDir = session?.workDir?.toString() ?: Nextflow.file('work')
        Nextflow.file(workDir.resolve('custom_ped_name_test')).deleteDir()
        when:
        Path result = ped.writePed("${workDir}/custom_ped_name_test/this_is_a_test.ped")
        then:
        result instanceof Path
        result.exists()
        result.text.split('\n').length == 3
        result.toString().endsWith("${workDir.baseName}/custom_ped_name_test/this_is_a_test.ped")
    }

    void 'writePed filter families'() {
        given:
        Ped ped = new Ped(Mock(Session))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test1.ped').path))
        ped.importPed(Nextflow.file(this.getClass().getResource('/test2.ped').path))
        when:
        Path result = ped.writePed(families: ['family2', 'family3'], overwrite: true)
        then:
        result instanceof Path
        result.exists()
        result.text.split('\n').length == 3
        result.toString().endsWith('/generated_peds/ped_9c3038bfcab545e7530cab4090cd4071.ped')
    }

    void 'writePed with additional fields'() {
        given:
        Ped ped = new Ped(Mock(Session))
        Path inputPed = Nextflow.file(this.getClass().getResource('/test_additional_fields.ped').path)
        ped.importPed(inputPed)
        when:
        Path result = ped.writePed(overwrite: true)
        then:
        result instanceof Path
        result.exists()
        result.text == inputPed.text
    }

}
