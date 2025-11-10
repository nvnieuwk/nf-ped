# nf-ped changelog

## 0.2.0

### Added

1. Allowed modification of `PedEntry` fields after creation by adding setter methods for all attributes (family, individual, father, mother, sex, phenotype).
2. Enabled modification of the `entries` set in the `Ped` class by adding setter and adder methods to allow dynamic updates to the PED entries.

### Changes

1. All missing values are now `0` in PED files written with `writePed`