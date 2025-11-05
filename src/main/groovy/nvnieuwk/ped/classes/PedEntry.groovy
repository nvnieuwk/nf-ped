package nvnieuwk.ped

class PedEntry {
    private final String family
    private final String individual
    private final String father
    private final String mother
    private final Sex sex
    private final Phenotype phenotype

    PedEntry(List<String> parts) {
        this.family = parts[0]
        this.individual = parts[1]
        this.father = parts[2]
        this.mother = parts[3]
        this.sex = Sex.determine(parts[4])
        this.phenotype = Phenotype.determine(parts[5])
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
}