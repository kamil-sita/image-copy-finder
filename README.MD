# ImageCopyFinder

This is the repository for my thesis project - image-copy-finder.

## About the project

This is the repository for my thesis project - image-copy-finder. 

image-copy-finder is a project which was created for near-duplicate detection for images.
To do it faster, it is also backed by a database which is able to store "optimizations" 
for each of the images.



## Available modules

The project is modular, with base module being icf-core. All other modules
depend on this module, and might depend on some other modules.


* icf-core - core of the whole project
* comparators:
    * icf-comparator-standard (comparators/standard)
    * icf-comparator-jimagehash (comparators/jimagehash_based)
    * icf-comparator-opencv (comparators/opencv_based)
* preprocessing:
    * icf-standard-preprocessing (preprocessing/standard)
* icf-gui - sample GUI application, using JavaFX
* icf-web - webserver version of the library - heavily work in progress

## Planned

[Problems, future plans - see projects](https://github.com/kamil-sita/image-copy-finder/projects/1)

## Requirements

JDK 14 - will update to JDK 16 once it releases, to make use of Vector API.

OpenCV 4.5 (opencv modules only) - installation depends on your system, but you will probably need to install
linked library in system directory and jar file inside maven. To install jar file you can use following command:

```
mvn install:install-file -Dfile=dir\to\opencv-450.jar -DgroupId=opencv -DartifactId=opencv -Dversion=4.5.0-0 -Dpackaging=jar
```


Hibernate-supported database and hibernate.properties file, for example:

### PostgreSQL:

pom.xml:

```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.2.16</version>
</dependency>
```

properties:
```properties
hibernate.connection.driver_class = org.postgresql.Driver
hibernate.connection.url = jdbc:postgresql://localhost:5432/icf
hibernate.connection.username = postgres
hibernate.connection.password = password
hibernate.dialect = org.hibernate.dialect.PostgreSQL9Dialect

hibernate.show_sql = false
hibernate.format_sql = true
hibernate.use_sql_comments = true
hibernate.current_session_context_class = thread
hibernate.hbm2ddl.auto = update

```


## Comparison engines

### ICF-core-default

Original ICF implementation, quite generic - is able to use any comparator,
even multiple at a time. Supports serialization.

Placed inside icf-core as IcfDefaultEngine.java. 

### ICF-core-perceptual

Planned, not yet started. Probably will be limited to only one comparator at a time (and only some of them),
but should be much faster than ICF-core-default. Will probably be based on ElasticSearch

## Comparators

### ICF-core-default

This engine should support every comparator. Included are the following:

* custom
  * SimpleBlurScaling
  * SimpleDifferentialScaling
  * SimpleScaling
  * YiqMe
  * YiqSe
* j-image-hash (you can use JihBase to convert any comparator from JImageHash)
  * AverageHash
  * DHash
  * PHash
  * RotAverageHash
  * RotPHash
  * WaveletHash
* opencv
  * ORB
  * SIFT
  
Since this engine is able to use multiple comparators at once, the most successful
combined comparator is also added.

### ICF-core-perceptual

This engine will probably support only comparators based on perceptive hash.

# Usage (icf-core)

## Easiest 

The easiest way to use icf-core in your project (after importing it, preferably through maven) would be to use
provided methods in IcfEngineDispatcher. Methods in IcfEngineDispatcher require following elements:

### IcfCollection

Represents a collection of images, that can be compared. Can be created and acquired through static methods in IcfCollection.

### ComplexImageComparator

Defines a way of comparing two images, using one or more comparators. You can create one yourself, obtain one of provided ones,
or create a wrapper over ImageComparator using ComplexImageComparator::toComplexImageComparator.

### IcfSettings

General settings of engines belonging to ICF project.

### LoadableImage

Image that provides a way to be loaded. One example of such an image would a member of IcfCollection or LoadableImageFile.
Only required, if you search for copies of this image.

### Example

```java
IcfCollection icfCollection = IcfCollection.getCollection(chosenDatabase, "");
ComplexImageComparator cic = ComplexImageComparator.toComplexImageComparator(
        new JihDHash(128, DifferenceHash.Precision.Triple)
);
icfCollection.addOrFindFile(file.get().toPath());
icfCollection.addOrFindFile(file2.get().toPath());
ImageComparingResults imageComparingResults 
        = new IcfEngineDispatcher()
        .findDuplicatesInLibrary(
            icfCollection, 
            cic, 
            new IcfSettings()
                .setCutoffStrategy(IcfSettings.ACCEPT_ALL),
            getNullComparisonProgress()
        );

```