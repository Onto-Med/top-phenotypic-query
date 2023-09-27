# TOP Phenotypic Query

[![JavaDoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://Onto-Med.github.io/top-phenotypic-query/)

## Introduction

TOP Phenotypic Query is a Java Maven package that is responsible for the phenotyping business logic in the TOP
Framework (see [top-deployment](https://github.com/Onto-Med/top-deployment) for a documentation of the whole framework).
There are multiple functionalities covered by this package:

* provide Java interfaces for developers to implement:
    * import and export of phenotype models
    * adapters to generate queries in a specific query language (such as SQL
      or [FHIR Search](https://www.hl7.org/fhir/search.html) servers)
* generate and execute queries to retriev individual data from data sources
* classify individual data into phenotype classes
* export query results to CSV

## Getting Started

Download one of our [JAR releases](https://github.com/Onto-Med/top-phenotypic-query/releases/latest) and use it as
follows:

```sh
# show help message
java -jar top-phenotypic-query-x.x.x.jar query --help

# execute phenotypic queries based on a phenotype model, results are written to ZIP
java -jar top-phenotypic-query-x.x.x.jar query <query config> <phenotype model> \
  <adapter config> <ZIP output path>
```

Input parameters:

* JSON containing a TOP query (i.e., `care.smith.top.model.Query`)
* JSON containing a phenotype model
* YAML adapter configuration
* output destination of the ZIP file that contains the result set

The next sections provide information on how to add `top-phenotypic-query` as Maven dependency to your Java project and
how to call it programmatically.

## Installation

Add the following Maven dependency to your project's `pom.xml` file and see section [Authentication to GitHub Packages](#authentication-to-github-packages)
for authentication with [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).

```xml
<dependency>
    <groupId>care.smith.top</groupId>
    <artifactId>top-phenotypic-query</artifactId>
    <version><!-- the version number --></version>
</dependency>
```

### Authentication to GitHub Packages

Because the Maven package is hosted at [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry),
you need to make some modifications to your Maven installation in order to download and install the package.
Please follow the [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)
instructions.

Authentification is required for `care.smith.top:top-phenotypic-query` and the dependency [care.smith.top:top-api](https://maven.pkg.github.com/onto-med/top-api).

## Usage

### Data Adapter Configuration

Data adapters are used to generate queries in a specific query language to retriev individual data from a source system.
Both default adapters for SQL and FHIR Search need configurations that are provided as [YAML](https://yaml.org) files.

[/src/main/resources/default_adapter_configuration](/src/main/resources/default_adapter_configuration) contains default
configuration files. By providing custom files (see for example [src/test/resources/config](src/test/resources/config)),
you can override these files.

### Query for Individuals Based on a Phenotype Model

```java
Query query = new Query(); // some query
Entity[] phenotypes = new Entity[]; // some phenotype definitions
DataAdapterConfig config = DataAdapterConfig.getInstance("path/to/config.yml");
DataAdapter adapter = DataAdapter.getInstance(config);

PhenotypeFinder finder = new PhenotypeFinder(query, phenotypes, adapter);
ResultSet rs = finder.execute();
adapter.close();
```

### Export a Query Result to CSV

Below code will create a file `export.zip` that contains `data.csv` and `metadata.csv`.

```java
Entity[] phenotypes = new Entity[]; // some phenotype definitions
ResultSet rs; // some resultset

File zipFile = Files.createFile("export.zip").toFile();
ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));

CSV csvConverter = new CSV();
zipStream.putNextEntry(new ZipEntry("data.csv"));
csvConverter.write(rs, zipStream);

zipStream.putNextEntry(new ZipEntry("metadata.csv"));
csvConverter.write(phenotypes, zipStream);

zipStream.close();
```

### Import or Export a Phenotype Model from Various Formats

```java
/* Import */
InputStream inputStream;
PhenotypeImporter importer;
Entity[] phenotypes = importer.read(inputStream);

/* Export */
OutputStream outputStream;
Repository repository; // some repository metadata
String uri = "https://example.com";
PhenotypeExporter exporter;
exporter.write(phenotypes, repository, uri, outputStream);
```

## Contribution and Development

Please see our [Contributing Guide](CONTRIBUTING.md).

## License

The code in this repository and the package `care.smith.top:top-phenotypic-query` are licensed under [MIT](LICENSE).

## References

> Uciteli A, Beger C, Kirsten T, Meineke FA, Herre H. Ontological representation, classification and data-driven
> computing of phenotypes. J Biomed Semant. 2020 Dec;11(1):15. https://doi.org/10.1186/s13326-020-00230-0.

> Uciteli A, Beger C, Wagner J, Kirsten T, Meineke FA, Stäubert S, et al. Ontological modelling and FHIR Search based
> representation of basic eligibility criteria. GMS Medizinische Informatik. 2021 Apr 26;Biometrie und Epidemiologie;
> 17(
> 2):Doc05. https://doi.org/10.3205/MIBE000219.

> Beger C, Matthies F, Schäfermeier R, Kirsten T, Herre H, Uciteli A. Towards an Ontology-Based Phenotypic Query Model.
> Applied Sciences. 2022 May 21;12(10):5214. https://doi.org/10.3390/app12105214.
