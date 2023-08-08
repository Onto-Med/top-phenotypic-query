# TOP Phenotypic Query

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

Add the following Maven dependency to your project's `pom.xml` file. Because the package is hosted on
the [GitHub Maven Package Registry](https://docs.github.com/en/packages), you must authenticate with a personal access
token to access the package.

```xml
<dependency>
    <groupId>care.smith.top</groupId>
    <artifactId>top-phenotypic-query</artifactId>
    <version><!-- the version number --></version>
</dependency>
```

## Authentication

[Create a GitHub personal access token (classic)](https://github.com/settings/tokens/new), see also the [GitHub docs](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic).
Check the `read:packages` box, generate the token and copy it.
You may have to enable `read:packages` again if it fails to activate the first time.
Then create a `~/.m2/settings.xml` if it doesn't exist, add the following and fill in USERNAME and TOKEN:

```xml
<?xml version="1.0"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0                       http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/onto-med/top-api</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>TOKEN</password>
    </server>
  </servers>
</settings>
```

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

## Development

The code in this repository, and in contributions provided via pull requests, should conform to
[Google Java Style](https://google.github.io/styleguide/javaguide.html).

We use the flag `--skip-reflowing-long-strings` for [google-java-format](https://github.com/google/google-java-format),
as it is currently not supported by all IDEs.

## License

The code in this repository and the package `care.smith.top:top-phenotypic-query` are licensed under [GPL-3.0](LICENSE).

## References

> Uciteli A, Beger C, Kirsten T, Meineke FA, Herre H. Ontological representation, classification and data-driven
> computing of phenotypes. J Biomed Semant. 2020 Dec;11(1):15. https://doi.org/10.1186/s13326-020-00230-0.

> Uciteli A, Beger C, Wagner J, Kirsten T, Meineke FA, Stäubert S, et al. Ontological modelling and FHIR Search based
> representation of basic eligibility criteria. GMS Medizinische Informatik. 2021 Apr 26;Biometrie und Epidemiologie;
> 17(
> 2):Doc05. https://doi.org/10.3205/MIBE000219.

> Beger C, Matthies F, Schäfermeier R, Kirsten T, Herre H, Uciteli A. Towards an Ontology-Based Phenotypic Query Model.
> Applied Sciences. 2022 May 21;12(10):5214. https://doi.org/10.3390/app12105214.
