package nvnieuwk.ped

import java.nio.file.Path
import java.security.MessageDigest
import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic

import nextflow.Session
import nextflow.Nextflow

import nvnieuwk.ped.exceptions.InvalidPedigreeException

/**
 * Implements the main class for handling PED files and their contents.
 */

@Slf4j
@CompileStatic
class Ped {

    private Set<PedEntry> entries = []

    private final Path workDir

    Ped(Session session) {
        this.workDir = (session?.getWorkDir() ?: Nextflow.file('work')) as Path
    }

    void importPed(Map<String, Object> options = [:], Path pedFile) {
        Integer lineCount = 0
        pedFile.eachLine { String line ->
            lineCount++
            if (line.startsWith('#')) {
                return // skip comment lines
            }
            List<String> parts = line.split(options.get('sep', '\t') as String) as List<String>
            if (parts.size() < 6) {
                throw new InvalidPedigreeException(
                    "Could not determine PED entry at line $lineCount in '${pedFile.toUri()}':" +
                    " expected at least6 columns, found ${parts.size()}"
                )
            }
            final PedEntry entry = new PedEntry(parts, lineCount, pedFile)
            if (options.get('overwrite', false)) {
                this.entries.removeAll { PedEntry e -> e.family == entry.family && e.individual == entry.individual }
            } else if (
                this.entries.any { PedEntry e -> e.family == entry.family && e.individual == entry.individual }
            ) {
                return null // skip duplicate entries
            }
            this.entries.add(entry)
        }
    }

    Set<PedEntry> getEntries() {
        return entries
    }

    void setEntries(Set<PedEntry> entries) {
        this.entries = entries
    }

    void setEntries(List<PedEntry> entries) {
        this.entries = entries as Set<PedEntry>
    }

    void addEntry(PedEntry entry) {
        this.entries.add(entry)
    }

    void addEntries(Set<PedEntry> entries) {
        this.entries.addAll(entries)
    }

    Set<String> getFamilies() {
        return entries*.family as Set<String>
    }

    Set<String> getIndividuals() {
        return entries*.individual as Set<String>
    }

    Set<PedEntry> getEntriesByFamily(String familyId) {
        return this.entries.findAll { PedEntry entry -> entry.family == familyId }
    }

    Set<PedEntry> getEntriesByIndividual(String individualId) {
        return this.entries.findAll { PedEntry entry -> entry.individual == individualId }
    }

    Set<String> getFamiliesFromIndividual(String individualId) {
        return this.entries.findAll { PedEntry entry -> entry.individual == individualId }*.family as Set<String>
    }

    Set<String> getIndividualsFromFamily(String familyId) {
        return this.entries.findAll { PedEntry entry -> entry.family == familyId }*.individual as Set<String>
    }

    Path writePed(String outputPath) {
        return writePed([:], outputPath)
    }

    Path writePed(Map<String,Object> options = [:], String outputPath = null) {
        Set<PedEntry> publishEntries = []
        Set<String> publishFamilies = options.get('families', []) as Set<String>
        if (publishFamilies) {
            publishFamilies.each { String familyId ->
                if (!families.contains(familyId)) {
                    log.warn("Family ID '$familyId' not found in pedigree, skipping...")
                }
                getEntriesByFamily(familyId).each { PedEntry entry ->
                    publishEntries.add(entry)
                }
            }
        } else {
            publishEntries = this.entries
        }

        // Generate unique output path name for PED contents
        Path outputFile
        if (outputPath) {
            outputFile = Nextflow.file(outputPath) as Path
        } else {
            MessageDigest md = MessageDigest.getInstance('MD5')
            publishEntries.each { PedEntry entry ->
                md.update(entry.toString().getBytes('UTF-8'))
            }
            String md5 = new BigInteger(1, md.digest()).toString(16)
            outputFile = workDir.resolve("generated_peds/ped_${md5}.ped")
        }

        // Write the PED file
        if (!publishEntries) {
            log.warn('No PED entries to publish, skipping writing PED file')
            return null
        }
        if (!outputFile.exists() || options.get('overwrite', false)) {
            if (!outputFile.parent.exists()) {
                outputFile.parent.mkdirs()
            }
            outputFile.withWriter { target ->
                publishEntries.each { PedEntry entry ->
                    target.writeLine(entry.toString())
                }
            }
        }
        return outputFile
    }

}
