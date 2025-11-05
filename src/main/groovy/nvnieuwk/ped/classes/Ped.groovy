package nvnieuwk.ped

import java.nio.file.Path

import nvnieuwk.ped.exceptions.InvalidPedigreeException

class Ped {
    private final Set<PedEntry> entries = []
    private final Set<String> families = []
    private final Set<String> individuals = []

    void importPed(Map<String, Object> options = [:], Path pedFile) {
        def Integer lineCount = 0
        pedFile.eachLine { String line ->
            lineCount++
            final trimmed = line.trim()
            if(trimmed.startsWith('#')) {
                return // skip comment lines
            }
            final List<String> parts = trimmed.split(options.get("sep", "\t"))
            println(parts)
            if(parts.size() != 6) {
                throw new InvalidPedigreeException("Could not determine PED entry at line $lineCount in '$pedFile': expected 6 columns, found ${parts.size()}")
            }
            final PedEntry entry = new PedEntry(parts)
            entries.add(entry)
            families.add(entry.family)
            individuals.add(entry.individual)
        }
    }

}