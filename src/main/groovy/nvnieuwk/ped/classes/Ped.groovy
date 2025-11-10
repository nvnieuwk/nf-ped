package nvnieuwk.ped

import java.nio.file.Path
import java.security.MessageDigest
import groovy.util.logging.Slf4j

import nextflow.Session
import nextflow.Nextflow

import nvnieuwk.ped.exceptions.InvalidPedigreeException

@Slf4j
class Ped {
    private Set<PedEntry> entries = []

    private final String workDir

    Ped(Session session) {
        this.workDir = session?.getWorkDir()?.toString() ?: "work"
    }

    void importPed(Map<String, Object> options = [:], Path pedFile) {
        def Integer lineCount = 0
        pedFile.eachLine { String line ->
            lineCount++
            if(line.startsWith('#')) {
                return // skip comment lines
            }
            final List<String> parts = line.split(options.get("sep", "\t"))
            if(parts.size() != 6) {
                throw new InvalidPedigreeException("Could not determine PED entry at line $lineCount in '${pedFile.toUri()}': expected 6 columns, found ${parts.size()}")
            }
            final PedEntry entry = new PedEntry(parts, lineCount, pedFile)
            this.entries.add(entry)
            this.families.add(entry.family)
            this.individuals.add(entry.individual)
        }
    }

    public Set<PedEntry> getEntries() {
        return entries
    }

    public void setEntries(Set<PedEntry> entries) {
        this.entries = entries
    }

    public void setEntries(List<PedEntry> entries) {
        this.entries = entries as Set<PedEntry>
    }

    public void addEntry(PedEntry entry) {
        this.entries.add(entry)
    }

    public void addEntries(Set<PedEntry> entries) {
        this.entries.addAll(entries)
    }

    public Set<String> getFamilies() {
        return entries.collect { PedEntry entry -> entry.family } as Set<String>
    }

    public Set<String> getIndividuals() {
        return entries.collect { PedEntry entry -> entry.individual } as Set<String>
    }

    public Set<PedEntry> getEntriesByFamily(String familyId) {
        return this.entries.findAll { PedEntry entry -> entry.family == familyId }
    }

    public Set<PedEntry> getEntriesByIndividual(String individualId) {
        return this.entries.findAll { PedEntry entry -> entry.individual == individualId }
    }

    public Set<String> getFamiliesFromIndividual(String individualId) {
        return this.entries.findAll { PedEntry entry -> entry.individual == individualId }.collect { PedEntry entry -> entry.family } as Set<String>
    }

    public Set<String> getIndividualsFromFamily(String familyId) {
        return this.entries.findAll { PedEntry entry -> entry.family == familyId }.collect { PedEntry entry -> entry.individual } as Set<String>
    }

    public Path writePed(String outputPath) {
        return writePed([:], outputPath)
    }

    public Path writePed(Map<String,Object> options = [:], String outputPath = null) {
        def Set<PedEntry> publishEntries = []
        final Set<String> publishFamilies = options.get("families", []) as Set<String>
        if(publishFamilies) {
            publishFamilies.each { String familyId ->
                if(!getFamilies().contains(familyId)) {
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
        if(!outputPath) {
            def MessageDigest md = MessageDigest.getInstance("MD5")
            publishEntries.each { PedEntry entry ->
                md.update(entry.toString().getBytes("UTF-8"))
            }
            def String md5 = new BigInteger(1, md.digest()).toString(16)
            outputPath = "${workDir}/generated_peds/ped_${md5}.ped" as String
        }

        // Write the PED file
        if(!publishEntries) {
            log.warn("No PED entries to publish, skipping writing PED file")
            return null
        }
        final Path outputFile = Nextflow.file(outputPath)
        if(!outputFile.exists() || options.get("overwrite", false)) {
            if(!outputFile.parent.exists()) {
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