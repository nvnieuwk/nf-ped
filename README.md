# nf-ped

The nf-ped plugin provides functionality to work with PED files in Nextflow pipelines. PED files are commonly used in genetics to describe the relationships and attributes of individuals in a study.

The current implementation supports PED files as described by the [GATK documentation](https://gatk.broadinstitute.org/hc/en-us/articles/360035531972-PED-Pedigree-format). This is a subset of the full specification available at [PLINK](https://zzz.bwh.harvard.edu/plink/data.shtml#ped). This functionality can be expanded upon request. Here's a list of the unsupported features:

- Custom phenotype values (only 1 (unaffected) and 2 (affected) are supported, all other values will be treated as 'missing')
- Additional columns beyond the standard six (family, individual, father, mother, sex, phenotype).
- Support for less columns than the standard six.

Add the following to your Nextflow configuration file to enable the plugin:

```groovy
plugins {
    id 'nf-ped@0.3.0'
}
```

## Features

The current implementation of the nf-ped plugin is fairly basic but should allow users to do most common operations with PED files. The main features include:

### Initialization

To start working with PED files, you can initialize a `Ped` object using the `initializePed` function.

```groovy
include { initializePed } from 'plugin/nf-ped'
def ped = initializePed()
```

Optionally, you can provide a list of PED files to import during initialization:

```groovy
def ped = initializePed([file("path/to/first.ped"), file("path/to/second.ped")])
```

Or even a single PED file:

```groovy
def ped = initializePed(file("path/to/file.ped"))
```

### Working with the PED object

The PED object provides methods to fetch and publish PED data:

#### importPed

The `importPed` method allows you to import additional PED files into the existing PED object.

```groovy
ped.importPed(file("path/to/another.ped"))
```

Additional options can be provided to this method:

- `sep`: Specify a custom separator if the PED file uses a different delimiter (default is `\t`).
- `overwrite`: A boolean flag indicating whether to overwrite existing entries with the same family and individual ID. Default is `false`, which means duplicate entries will be skipped.

#### getEntries

The `getEntries` method retrieves entries from the PED object. The returned values are a set of the `PedEntry` class. See the [`PedEntry`](#working-with-the-ped-entries) documentation for more details. 

```groovy
def entries = ped.getEntries()
```

#### setEntries

The `setEntries` method allows you to set the entire set of PED entries in the PED object. You need to provide a set of `PedEntry` objects.

```groovy
def entries = ped.getEntries()
// Modify entries as needed

// Set entries back to the PED object
ped.setEntries(entries)
```

#### addEntry

The `addEntry` method allows you to add a single `PedEntry` object to the PED object.

```groovy
def entry = ped.getEntries().first()
ped2.addEntry(entry)
```

#### addEntries

The `addEntries` method allows you to add multiple `PedEntry` objects to the PED object at once. You need to provide a set of `PedEntry` objects.

```groovy
def entries = ped.getEntries()
ped2.addEntries(entries)
```

#### getFamilies

The `getFamilies` method retrieves a list of unique family IDs present in the PED data.

```groovy
def families = ped.getFamilies()
```

#### getIndividuals

The `getIndividuals` method retrieves a list of unique individual IDs present in the PED data.

```groovy
def individuals = ped.getIndividuals()
```

#### getEntriesByFamily

The `getEntriesByFamily` method retrieves all [entries](#working-with-the-ped-entries) associated with a specific family ID.

```groovy
def familyEntries = ped.getEntriesByFamily("family_id")
```

#### getEntriesByIndividual

The `getEntriesByIndividual` method retrieves the [entries](#working-with-the-ped-entries) associated with a specific individual ID. Only the entries where the individual is listed as the individual (not as a parent) are returned.

```groovy
def individualEntries = ped.getEntriesByIndividual("individual_id")
```

#### getFamiliesFromIndividual

The `getFamiliesFromIndividual` method retrieves the family IDs associated with a specific individual ID.

```groovy
def individualFamilies = ped.getFamiliesFromIndividual("individual_id")
```

#### getIndividualsFromFamily

The `getIndividualsFromFamily` method retrieves the individual IDs associated with a specific family ID.

```groovy
def familyIndividuals = ped.getIndividualsFromFamily("family_id")
```

#### writePed

The `writePed` method allows you to write the PED data to a file. By default the file will be created in the working directory in the following path: `generated_peds/ped_<md5>.ped`, where `<md5>` is a hash based on the contents of the PED data. The method returns the path object to the created PED file (or the already existing PED file).

```groovy
def pedFilePath = ped.writePed()
```

A file path can also be provided to write the PED data to a specific location:

```groovy
def pedFilePath = ped.writePed("path/to/output.ped")
```

Additional options can be provided to this method:
- `families`: A list of family IDs to filter the entries on. Only entries belonging to the specified families will be included in the output PED file. If not provided, all entries will be included.
- `overwrite`: A boolean flag indicating whether to overwrite an existing PED file. Default is `false`.

### Working with the PED entries

The PED entries are represented by the `PedEntry` class. Each entry corresponds to a line in the PED file and contains the following attributes:
- `family`: The family ID.
- `individual`: The individual ID.
- `father`: The father ID.
- `mother`: The mother ID.
- `sex`: The sex of the individual (1 = male, 2 = female, other = unknown).
- `phenotype`: The phenotype of the individual (1 = unaffected, 2 = affected, other = missing).

These attributes can be accessed directly from the `PedEntry` object. For example:

```groovy
def entry = ped.getEntries()[0]
// Using attribute names
def familyId = entry.family
// Using getter methods
def familyId = entry.getFamily()
```

The `PedEntry` class also provides a `toString()` method that returns a string representation of the entry in PED file format.

```groovy
def entry = ped.getEntries()[0]
def entryString = entry.toString()
```

Each attribute of the `PedEntry` class can be modified using setter methods. For example:

```groovy
def entry = ped.getEntries()[0]
entry.setFather("new_father_id")
entry.setPhenotype("2")
```

## Building

To build the plugin:
```bash
make assemble
```

## Testing with Nextflow

The plugin can be tested without a local Nextflow installation:

1. Build and install the plugin to your local Nextflow installation: `make install`
2. Run a pipeline with the plugin: `nextflow run hello -plugins nf-ped@0.3.0`

## Publishing

Plugins can be published to a central plugin registry to make them accessible to the Nextflow community. 


Follow these steps to publish the plugin to the Nextflow Plugin Registry:

1. Create a file named `$HOME/.gradle/gradle.properties`, where $HOME is your home directory. Add the following properties:

    * `npr.apiKey`: Your Nextflow Plugin Registry access token.

2. Use the following command to package and create a release for your plugin on GitHub: `make release`.


> [!NOTE]
> The Nextflow Plugin registry is currently available as preview technology. Contact info@nextflow.io to learn how to get access to it.
> 
