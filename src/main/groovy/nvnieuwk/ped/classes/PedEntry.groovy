package nvnieuwk.ped

import java.nio.file.Path

import nvnieuwk.ped.exceptions.InvalidPedigreeException

class PedEntry {
    private final String family
    private final String individual
    private final String father
    private final String mother
    private final Sex sex
    private final Phenotype phenotype

    PedEntry(List<String> parts, Integer lineNumber, Path pedFile) {
        this.family = parseID(parts[0])
        if(this.family == null) {
            throw new InvalidPedigreeException("Expected a family ID at line $lineNumber in PED file '${pedFile.toUri()}'")
        }
        this.individual = parseID(parts[1])
        if(this.individual == null) {
            throw new InvalidPedigreeException("Expected an individual ID at line $lineNumber in PED file '${pedFile.toUri()}'")
        }
        this.father = parseID(parts[2])
        this.mother = parseID(parts[3])
        this.sex = Sex.determine(parts[4])
        this.phenotype = Phenotype.determine(parts[5])
    }

    private final List<String> ignoreValues = ['0', 'NA', '']

    private String parseID(String id) {
        return ignoreValues.contains(id) ? null : id
    }

    public String getFamily() {
        return family
    }

    public String getIndividual() {
        return individual
    }

    public String getFather() {
        return father
    }

    public String getMother() {
        return mother
    }

    public String getSex() {
        return sex.toString()
    }

    public String getPhenotype() {
        return phenotype.toString()
    }

    public String toString() {
        return "${family}\t${individual}\t${father ?: ''}\t${mother ?: ''}\t${sex}\t${phenotype}"
    }

}

enum Sex {
    MALE,
    FEMALE,
    UNKNOWN

    static determine(String value) {
        if(value == '1') {
            return MALE
        } else if(value == '2') {
            return FEMALE
        } else {
            return UNKNOWN
        }
    }

    public String toString() {
        switch(this) {
            case MALE: return "1"
            case FEMALE: return "2"
            case UNKNOWN: return "0"
        }
    }
}

enum Phenotype {
    AFFECTED,
    UNAFFECTED,
    MISSING

    static determine(String value) {
        if(value == '1') {
            return UNAFFECTED
        } else if(value == '2') {
            return AFFECTED
        } else {
            return MISSING
        }
    }

    public String toString() {
        switch(this) {
            case UNAFFECTED: return "1"
            case AFFECTED: return "2"
            case MISSING: return "0"
        }
    }
}