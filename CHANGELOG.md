# nf-ped changelog

## 0.4.0

## Added

1. Add support for additional fields in the PED input file. Any additional columns will also be included in the output PED file and can be accessed in the `PedEntry` class with the `additionalFields` attribute, which is a list of strings containing the values of the additional columns for each entry.

## 0.3.1

### Fixed

1. Corrected the file handling to resolve issues on cloud systems

## 0.3.0

### Added

1. Allow integer inputs in the `setSex` and `setPhenotype` methods of the `PedEntry` class for easier modification of these attributes.

### Fixed

1. Resolved issue with duplicate family and individual IDs in `Ped` class by skipping addition of entries with a family and individual ID that already exist in the class object. Entries can also be overwritten with the `overwrite` option of the `importPed` method.

## 0.2.0

### Added

1. Allowed modification of `PedEntry` fields after creation by adding setter methods for all attributes (family, individual, father, mother, sex, phenotype).
2. Enabled modification of the `entries` set in the `Ped` class by adding setter and adder methods to allow dynamic updates to the PED entries.

### Changes

1. All missing values are now `0` in PED files written with `writePed`
