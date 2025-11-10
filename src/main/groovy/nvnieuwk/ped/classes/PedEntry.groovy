package nvnieuwk.ped

import java.nio.file.Path

import nvnieuwk.ped.exceptions.InvalidPedigreeException

class PedEntry {
    private String family
    private String individual
    private String father
    private String mother
    private Sex sex
    private Phenotype phenotype

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

    public void setFamily(String family) {
        this.family = parseID(family)
    }

    public String getIndividual() {
        return individual
    }

    public void setIndividual(String individual) {
        this.individual = parseID(individual)
    }

    public String getFather() {
        return father
    }

    public void setFather(String father) {
        this.father = parseID(father)
    }

    public String getMother() {
        return mother
    }

    public void setMother(String mother) {
        this.mother = parseID(mother)
    }

    public String getSex() {
        return sex.toString()
    }

    public void setSex(String sex) {
        this.sex = Sex.determine(sex)
    }

    public String getPhenotype() {
        return phenotype.toString()
    }

    public void setPhenotype(String phenotype) {
        this.phenotype = Phenotype.determine(phenotype)
    }

    public String toString() {
        return "${family}\t${individual}\t${father ?: '0'}\t${mother ?: '0'}\t${sex}\t${phenotype}"
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