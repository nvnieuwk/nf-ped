# nf-ped

The nf-ped plugin provides functionality to work with PED files in Nextflow pipelines. PED files are commonly used in genetics to describe the relationships and attributes of individuals in a study.

The current implementation supports PED files as described by the [GATK documentation](https://gatk.broadinstitute.org/hc/en-us/articles/360035531972-PED-Pedigree-format). This is a subset of the full specification available at [PLINK](https://zzz.bwh.harvard.edu/plink/data.shtml#ped). This functionality can be expanded upon request. Here's a list of the unsupported features:

- Custom phenotype values (only 1 (unaffected) and 2 (affected) are supported, all other values will be treated as 'missing')
- Additional columns beyond the standard six (family, individual, father, mother, sex, phenotype).
- Support for less columns than the standard six.

## Building

To build the plugin:
```bash
make assemble
```

## Testing with Nextflow

The plugin can be tested without a local Nextflow installation:

1. Build and install the plugin to your local Nextflow installation: `make install`
2. Run a pipeline with the plugin: `nextflow run hello -plugins nf-ped@0.1.0`

## Publishing

Plugins can be published to a central plugin registry to make them accessible to the Nextflow community. 


Follow these steps to publish the plugin to the Nextflow Plugin Registry:

1. Create a file named `$HOME/.gradle/gradle.properties`, where $HOME is your home directory. Add the following properties:

    * `npr.apiKey`: Your Nextflow Plugin Registry access token.

2. Use the following command to package and create a release for your plugin on GitHub: `make release`.


> [!NOTE]
> The Nextflow Plugin registry is currently available as preview technology. Contact info@nextflow.io to learn how to get access to it.
> 
