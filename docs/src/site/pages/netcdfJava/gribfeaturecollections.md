---
title: GRIB Feature Collections
last_updated: 2025-08-15
sidebar: netcdfJavaTutorial_sidebar 
permalink: grib_feature_collections_ref.html
toc: false
---

GRIB Feature Collection Datasets are collections of GRIB records, which contain gridded data, typically from numeric model output. Because of the complexity of how GRIB data is written and stored, the TDS has developed specialized handling of GRIB datasets, as of version 4.3, called GRIB Feature Collections.

The user need only specify a collection of GRIB-1 or GRIB-2 files, and the software turns them into a dataset.
New indexing scheme allows fast access and scalability to very large datasets.
Multiple horizontal domains are supported and placed into separate groups.
Interval time coordinates are fully supported.

## Version 4.5

The GRIB Collections framework has been rewritten in CDM version 4.5, in order to handle large collections efficiently. Version 4.5 requires Java 7. Some of the new capabilities in version 4.5 are:

GRIB Collections now keep track of both the reference time and valid time. The collection is partitioned by reference time.
A collection with a single reference time will have a single partition with a single time coordinate.
A collection with multiple reference times will have partitions for each reference time, plus a PartitionCollection that represents the entire collection. Very large collections should be partitioned by directory and/or file, creating a tree of partitions.
A PartitionCollection has two datasets (kept in separate groups), the TwoD and the Best dataset.
The TwoD dataset has two time coordinates - reference time (aka run time) and forecast time (aka valid time), corresponding to FMRC TwoD datasets. The forecast time is two-dimensional, corresponding to all the times available for each reference time.
The Best dataset has a single forecast time coordinate, the same as 4.3 GRIB Collections and FMRC Best datasets. If there are multiple GRIB records corresponding to the same forecast time, the record with the smallest offset from its reference time is used.
Implementation notes:

The featureType attribute is now GRIB1 or GRIB2.
For each GRIB file, a grib index is written, named <grib filename>.gbx9. Once written, this never has to be rewritten.
For each reference time, a cdm index is written, named <collection.referenceTime>.ncx2. This occasionally has to be rewritten when new CDM versions are released, or if you modify your GRIB configuration.
For each PartitionCollection, a cdm index is written named <collection name>.ncx2. This must be rewritten if any of the collection files change.
The cdm indexing uses extension .ncx2, in order to coexist with the .ncx indexes of previous versions. If you are upgrading to 4.5, and no longer running earlier versions, remove the ncx files (save the gbx9 files).
For large collections, especially if they change, the THREDDS Data Manager (TDM) must be run as a separate process to update the index files. Generally it is strongly recommended to run the TDM, and configure the TDS to only read and never write the indexes.
Collections in the millions of records are now feasible. Java 7 NIO2 package is used to efficiently scan directories.

## Version 4.6

The GRIB Collections framework has been rewritten in CDM version 4.6, in order to handle very large collections efficiently. Oh, wait, we already did that in 4.5. Sorry, it wasn't good enough.

Collection index files now use the suffix ncx3. These will be rewritten first time you access the files. The gbx9 files do NOT need to be rewritten, which is good because those are the slow ones.
TimePartition can now be set to directory (default), file, a time period, or none. Details here.
Multiple reference times are handled more efficiently, e.g. only one index file typically needs to be written.
Global attributes promoted to dataset properties in the catalog
Internal changes:
Internal memory use has been reduced.
Runtime objects are now immutable, which makes caching possible.
RandomAccessFiles are kept in a separate pool, so they can be cached independent of the Collection objects.
(IN PROGRESS FOR VERSION 5) DefaultServices. One can use the service name "DefaultServices" to use the default services for that Feature Type.
If you don't specify the service name, DefaultServices is used as the default.
DefaultServices use all enabled services appropriate to that Feature Type.

## Example 1 (timePartition="none")

{% highlight_with_annotations xml %}
<featureCollection featureType="GRIB1" name="rdavm partition none" path="gribCollection/none">{% raw %}{% annotation 1 %}{% endraw %}
  <metadata inherited="true">{% raw %}{% annotation 2 %}{% endraw %}
    <dataFormat>GRIB-2</dataFormat><!--not used -->{% raw %}{% annotation 3 %}{% endraw %}
    <serviceName>all</serviceName>
    <dataType>Grid</dataType>
  </metadata>

  <collection name="ds083.2-none"
    spec="Q:/cdmUnitTest/gribCollections/rdavm/ds083.2/PofP/**/.*grib1"
    timePartition="none"/>{% raw %}{% annotation 4 %}{% annotation 5 %}{% annotation 6 %}{% endraw %}

  <update startup="never" trigger="allow"/>{% raw %}{% annotation 7 %}{% endraw %}  
  <tdm rewrite="test" rescan="0 0/15 * * * ? *" trigger="allow"/>{% raw %}{% annotation 8 %}{% endraw %}  
  <gribConfig datasetTypes="TwoD Latest Best" />{% raw %}{% annotation 9 %}{% endraw %}  
</featureCollection>
{% endhighlight_with_annotations %}

* {% annotation 1 %} A featureCollection must have a name, a featureType and a path (do not set an ID attribute). Note that the featureType attribute must now equal GRIB1 or GRIB2, not plain GRIB.
* {% annotation 2 %} A featureCollection is an InvDataset, so it can contain any elements an InvDataset can contain. It must have or inherit a default service.
* {% annotation 3 %} The collection must consist of either GRIB-1 or GRIB-2 files (not both). You no longer should set the dataFormat element to indicate which, as it is specified in the featureType, and will be added automatically.
* {% annotation 4 %} The collection name should be short but descriptive, it must be unique across all collections on your TDS, and should not change.
* {% annotation 5 %} The collection specification defines the collection of files that are in this dataset.
* {% annotation 6 %} The partitionType is none.
* {% annotation 7 %} This update element tells the TDS to use the existing indices, and to read them only when an external trigger is sent. This is the default behavior as of 4.5.4.
* {% annotation 8 %} This tdm element tells the TDM to test every 15 minutes if the collection has changed, and to rewrite the indices and send a trigger to the TDS when it has changed.
* {% annotation 9 %} GRIB specific configuration.

### Resulting Datasets

The above example generates a TwoD and Best dataset for the entire collection, a reference to the latest dataset, as well as one dataset for each reference time in the collection, which become nested datasets in the catalog. These datasets are named by their index files, in the form <collection-name>.<referenceTime>.ncx3, e.g. GFS-Puerto_Rico-20141110-000000.ncx3
The simplified catalog is:

~~~xml
<dataset name="NCEP GFS Puerto_Rico (191km)">
  <metadata inherited="true">
    <serviceName>VirtualServices</serviceName>
    <dataType>GRID</dataType>
    <dataFormat>GRIB-2</dataFormat>
  </metadata>
  <dataset name="Full Collection (Reference / Forecast Time) Dataset" ID="fmrc/NCEP/GFS/Puerto_Rico/TwoD" urlPath="fmrc/NCEP/GFS/Puerto_Rico/TwoD">
    <documentation type="summary">Two time dimensions: reference and forecast; full access to all GRIB records</documentation>
  </dataset>
  <dataset name="Best NCEP GFS Puerto_Rico (191km) Time Series" ID="fmrc/NCEP/GFS/Puerto_Rico/Best" urlPath="fmrc/NCEP/GFS/Puerto_Rico/Best">
    <documentation type="summary">Single time dimension: for each forecast time, use GRIB record with the smallest offset from reference time</documentation>
  </dataset>
  <dataset name="Latest Collection for NCEP GFS Puerto_Rico (191km)" urlPath="latest.xml">
    <serviceName>latest</serviceName>
  </dataset>
  <catalogRef xlink:href="/thredds/catalog/fmrc/NCEP/GFS/Puerto_Rico/GFS-Puerto_Rico-20141110-000000.ncx3/catalog.xml" />
  <catalogRef xlink:href="/thredds/catalog/fmrc/NCEP/GFS/Puerto_Rico/GFS-Puerto_Rico-20141110-060000.ncx3/catalog.xml" />
  <catalogRef xlink:href="/thredds/catalog/fmrc/NCEP/GFS/Puerto_Rico/GFS-Puerto_Rico-20141110-120000.ncx3/catalog.xml" />
</dataset>
~~~

The catalogRefs are links to virtual datasets, formed from the collection of records for the specified reference time, and independent of which file stores them.

## Example 2 (timePartition="directory")

Now suppose that we modify the above example and use timePartition="directory":

{% highlight_with_annotations xml %}
<featureCollection featureType="GRIB1" name="rdavm partition directory" path="gribCollection/pofp">
  <metadata inherited="true">
    <serviceName>all</serviceName>
    <dataType>Grid</dataType>
  </metadata>
  <collection name="ds083.2-directory" spec="Q:/cdmUnitTest/gribCollections/rdavm/ds083.2/PofP/**/.*grib1" timePartition="directory"/>
    <update startup="test" />
    <gribConfig datasetTypes="TwoD Latest Best" />
  </featureCollection>
  <featureCollection name="NAM-Polar90" featureType="GRIB" path="grib/NCEP/NAM/Polar90">
    <metadata inherited="true">
      <dataFormat>GRIB-2</dataFormat>
    </metadata>
    <collection spec="G:/mlode/polar90/.*grib2$"
      timePartition="file"
      dateFormatMark="#NAM_Polar_90km_#yyyyMMdd_HHmm" />{% raw %}{% annotation 1 %}{% annotation 2 %}{% endraw %}
  <update startup="true" trigger="allow"/>{% raw %}{% annotation 3 %}{% endraw %}
</featureCollection>
{% endhighlight_with_annotations %}


* {% annotation 1 %} The collection is divided into partitions. In this case, each file becomes a separate partition. In order to use this, each file must contain GRIB records from a single runtime.
* {% annotation 3 %} The starting time of the partition must be encoded into the filename. One must define a date extractor in the collection specification, or by using a dateFormatMark, as in this example.
* {% annotation 3 %} In this example, the collection is readied when the server starts up. Manual triggers for updating are enabled.

### Resulting Datasets

A time partition generates one collection dataset, one dataset for each partition, and one dataset for each individual file in the collection:

~~~xml
<dataset name="NAM-Polar90" ID="grib/NCEP/NAM/Polar90">
  <catalogRef xlink:href="/thredds/catalog/grib/NCEP/NAM/Polar90/collection/catalog.xml" xlink:title="collection"/>
  <catalogRef xlink:href="/thredds/catalog/grib/NCEP/NAM/Polar90/NAM-Polar90_20110301/catalog.xml" xlink:title="NAM-Polar90_20110301">
    <catalogRef xlink:href="/thredds/catalog/grib/NCEP/NAM/Polar90/NAM-Polar90_20110301/files/catalog.xml" xlink:title="files" />
  </catalogRef>
  <catalogRef xlink:href="/thredds/catalog/grib/NCEP/NAM/Polar90/NAM-Polar90_20110302/catalog.xml" xlink:title="NAM-Polar90_20110302">
    <catalogRef xlink:href="/thredds/catalog/grib/NCEP/NAM/Polar90/NAM-Polar90_20110302/files/catalog.xml" xlink:title="files" name="" />
  </catalogRef>
  ...
</dataset>
~~~

De-referencing the catalogRefs, and simplifying:

{% highlight_with_annotations xml %}
<dataset name="NAM-Polar90" ID="grib/NCEP/NAM/Polar90">
  <dataset name="NAM-Polar90-collection" urlPath="grib/NCEP/NAM/Polar90/collection">{% raw %}{% annotation 1 %}{% endraw %}
    <dataset name="NAM-Polar90_20110301" urlPath="grib/NCEP/NAM/Polar90/NAM-Polar90_20110301/collection">{% raw %}{% annotation 2 %}{% endraw %}
    <dataset name="NAM_Polar_90km_20110301_0000.grib2" urlPath="grib/NCEP/NAM/Polar90/files/NAM_Polar_90km_20110301_0000.grib2"/>{% raw %}{% annotation 3 %}{% endraw %}
    <dataset name="NAM_Polar_90km_20110301_0600.grib2" urlPath="grib/NCEP/NAM/Polar90/files/NAM_Polar_90km_20110301_0600.grib2"/>
    ...
  </dataset>
  <dataset name="NAM-Polar90_20110302-collection" urlPath="grib/NCEP/NAM/Polar90/NAM-Polar90_20110302/collection">{% raw %}{% annotation 4 %}{% endraw %}
    <dataset name="NAM_Polar_90km_20110302_0000.grib2" urlPath="grib/NCEP/NAM/Polar90/files/NAM_Polar_90km_20110302_0000.grib2"/>
    <dataset name="NAM_Polar_90km_20110302_0600.grib2" urlPath="grib/NCEP/NAM/Polar90/files/NAM_Polar_90km_20110302_0600.grib2"/>
    ...
  </dataset>
</dataset>
{% endhighlight_with_annotations %}  

* {% annotation 1 %} The overall collection dataset
* {% annotation 2 %} The first partition collection, with a partitionName = `name_startingTime`
* {% annotation 3 %} The files in the first partition
* {% annotation 4 %} The second partition collection, etc

So the datasets that are generated from a `Time Partition` with `name`, `path`, and `partitionName`:

| dataset type     | catalogRef                           | name          | path                          |
|:-----------------|:-------------------------------------|:--------------|:------------------------------|
| collection       | path/collection/catalog.xml          | name          | path/name/collection          |
| partitions       | path/partitionName/catalog.xml       | partitionName | path/partitionName/collection |
| individual files | path/partitionName/files/catalog.xml | filename      | path/files/filename           |

## Example 3 (Multiple Groups)

When a Grib Collection contains multiple horizontal domains (i.e. distinct Grid Definition Sections (GDS)), each domain gets placed into a separate group. As a rule, one can't tell if there are separate domains without reading the files. If you open this collection through the CDM (e.g. using ToolsUI) you would see a dataset that contains groups. The TDS, however, separates groups into different datasets, so that each dataset has only a single (unnamed, aka root) group.

~~~
 
~~~

{% highlight_with_annotations xml %}
<featureCollection name="RFC" featureType="GRIB" path="grib/NPVU/RFC">
  <metadata inherited="true">
    <serviceName>all</serviceName>
  </metadata>
  <collection spec="/tds2012data/grib/rfc/ZETA.*grib1$"
    dateFormatMark="yyyyMMdd#.grib1#"/>
  <gribConfig>{% raw %}{% annotation 1 %}{% endraw %}
    <gdsHash from="-752078894" to="1193085709"/>
    <gdsName hash='-1960629519' groupName='KTUA:Arkansas-Red River RFC'/>
    <gdsName hash='-1819879011' groupName='KFWR:West Gulf RFC'/>
    <gdsName hash='-1571856555' groupName='KORN:Lower Mississippi RFC'/>
    <gdsName hash='-1491065322' groupName='KKRF:Missouri Basin RFC'/>
    <gdsName hash='-1017807718' groupName='TSJU:San Juan PR WFO'/>
    <gdsName hash='-1003775954' groupName='NCEP-QPE National Mosaic'/>
    <gdsName hash='-529497359' groupName='KRHA:Middle Atlantic RFC'/>
    <gdsName hash='289752153' groupName='KRSA:California-Nevada RFC-6hr'/>
    <gdsName hash='424971237' groupName='KRSA:California-Nevada RFC-1hr'/>
    <gdsName hash='511861653' groupName='KTIR:Ohio Basin RFC'/>
    <gdsName hash='880498701' groupName='KPTR:Northwest RFC'/>
    <gdsName hash='1123818409' groupName='KTAR:Northeast RFC'/>
    <gdsName hash='1174418106' groupName='KNES-National Satellite Analysis'/>
    <gdsName hash='1193085709' groupName='KMSR:North Central RFC'/>
    <gdsName hash='1464276934' groupName='KSTR:Colorado Basin RFC'/>
    <gdsName hash='1815048381' groupName='KALR:Southeast RFC'/>
  </gribConfig>
</featureCollection>
{% endhighlight_with_annotations %}

* {% annotation 1 %} This dataset has many different groups, and we are using a <gribConfig> element to name them.

### Resulting Datasets:

For each group, this generates one collection dataset, and one dataset for each individual file in the group:

~~~xml
<catalog>
  <dataset name="KALR:Southeast RFC" urlPath="grib/NPVU/RFC/KALR-Southeast-RFC/collection">
    <catalogRef xlink:href="/thredds/catalog/grib/NPVU/RFC/KALR-Southeast-RFC/files/catalog.xml" xlink:title="files" name="" />
  </dataset>
  <dataset name="KFWR:West Gulf RFC" urlPath="grib/NPVU/RFC/KFWR-West-Gulf-RFC/collection">
    <catalogRef xlink:href="/thredds/catalog/grib/NPVU/RFC/KFWR-West-Gulf-RFC/files/catalog.xml" xlink:title="files" name="" />
  </dataset>
  ...
</catalog> 
~~~

Note that the groups are sorted by name, and that there is no overall collection for the dataset.
Simplifying:

{% highlight_with_annotations xml %}
<catalog>
  <dataset name="KALR:Southeast RFC" urlPath="grib/NPVU/RFC/KALR-Southeast-RFC/collection">{% raw %}{% annotation 1 %}{% endraw %}
    <dataset name="ZETA_KALR_NWS_152_20120111.grib1" urlPath="grib/NPVU/RFC/files/ZETA_KALR_NWS_152_20120111.grib1"/>{% raw %}{% annotation 2 %}{% endraw %}
    <dataset name="ZETA_KALR_NWS_160_20120111.grib1" urlPath="grib/NPVU/RFC/files/ZETA_KALR_NWS_160_20120111.grib1"/>
      ...
  </dataset>
  <dataset name="KFWR:West Gulf RFC" urlPath="grib/NPVU/RFC/KFWR-West-Gulf-RFC/collection">{% raw %}{% annotation 3 %}{% endraw %}
    <dataset name="ZETA_KFWR_NWS_152_20120111.grib1" urlPath="grib/NPVU/RFC/files/ZETA_KFWR_NWS_152_20120111.grib1"/>
    <dataset name="ZETA_KFWR_NWS_161_20120110.grib1" urlPath="grib/NPVU/RFC/files/ZETA_KFWR_NWS_161_20120110.grib1"/>
    ...
  </dataset>
  ...
</catalog>
{% endhighlight_with_annotations %}


* {% annotation 1 %} The first group collection dataset
* {% annotation 2 %} The files in the first group
* {% annotation 3 %} The second group collection dataset, etc

So the datasets that are generated from a `Grib Collection` with `groupName` and `path` :

| dataset          | catalogRef                       | name                      | path                |
|:-----------------|:---------------------------------|:--------------------------|:--------------------| 
| group collection | groupName                        | path/groupName/collection |
| individual files | path/groupName/files/catalog.xml | filename                  | path/files/filename |

## Example 4 (Time Partition with Multiple Groups):

Here is a time partitioned dataset with multiple groups:

{% highlight_with_annotations xml %}
<featureCollection name="NCDC-CFSR" featureType="GRIB" path="grib/NCDC/CFSR">
  <metadata inherited="true">
    <dataFormat>GRIB-2</dataFormat>
  </metadata>
  <collection spec="G:/nomads/cfsr/timeseries/**/.*grb2$"
    timePartition="directory"
    dateFormatMark="#timeseries/#yyyyMM"/>{% raw %}{% annotation 1 %}{% annotation 2 %}{% endraw %}
  <update startup="true" trigger="allow"/>
  <gribConfig>
    <gdsHash from="1450218978" to="1450192070"/>{% raw %}{% annotation 3 %}{% endraw %}
    <gdsName hash='1450192070' groupName='FLX GaussianT382'/>{% raw %}{% annotation 4 %}{% endraw %}
    <gdsName hash='2079260842' groupName='FLX GaussianT62'/>
     ...
    <intvFilter excludeZero="true"/>{% raw %}{% annotation 5 %}{% endraw %}
  </gribConfig>
</featureCollection>
{% endhighlight_with_annotations %}

* {% annotation 1 %} Partition the files by which directory they are in (the files must be time partitioned by the directories)
* {% annotation 2 %} One still needs a date extractor from the filename, even when using a directory partition.
* {% annotation 3 %} Minor errors in GRIB coding can create spurious differences in the GDS. Here we correct one such problem (see below for details).
* {% annotation 4 %} Group renaming as in example 2
* {% annotation 5 %} Exclude GRIB records that have a time coordinate interval of (0,0) (see below for details).

### Resulting Datasets:

A time partition with multiple groups generates an overall collection dataset for each group, a collection dataset for each group in each partition, and a dataset for each individual file:

{% highlight_with_annotations xml %}
<dataset name="NCDC-CFSR" ID="grib/NCDC/CFSR">
  <catalogRef xlink:href="/thredds/catalog/grib/NCDC/CFSR/collection/catalog.xml" xlink:title="collection" name="" />{% raw %}{% annotation 1 %}{% endraw %}
  <catalogRef xlink:href="/thredds/catalog/grib/NCDC/CFSR/200808/catalog.xml" xlink:title="200808" name="" />{% raw %}{% annotation 4 %}{% endraw %}
   <catalogRef xlink:href="/thredds/catalog/grib/NCDC/CFSR/200809/catalog.xml" xlink:title="200809" name="" />{% raw %}{% annotation 8 %}{% endraw %}
   ...
</dataset>
{% endhighlight_with_annotations %}

De-referencing the catalogRefs, and simplifying:

{% highlight_with_annotations xml %}
<dataset name="NCDC-CFSR" ID="grib/NCDC/CFSR">
  <dataset name="NCDC-CFSR">{% raw %}{% annotation 1 %}{% endraw %}
    <dataset name="FLX GaussianT382" urlPath="grib/NCDC/CFSR/NCDC-CFSR/FLX-GaussianT382" />{% raw %}{% annotation 2 %}{% endraw %}
    <dataset name="FLX GaussianT62" urlPath="grib/NCDC/CFSR/NCDC-CFSR/FLX-GaussianT62" />{% raw %}{% annotation 3 %}{% endraw %}
    ...
  </dataset> 

  <dataset name="200808" >{% raw %}{% annotation 4 %}{% endraw %}
    <dataset name="FLX GaussianT382" urlPath="grib/NCDC/CFSR/200808/FLX-GaussianT382">{% raw %}{% annotation 5 %}{% endraw %}
      <catalogRef xlink:href="/thredds/catalog/grib/NCDC/CFSR/200808/FLX-GaussianT382/files/catalog.xml" xlink:title="files" name="" />{% raw %}{% annotation 6 %}{% endraw %}
    </dataset>
    <dataset name="FLX GaussianT62" urlPath="grib/NCDC/CFSR/200808/FLX-GaussianT62">{% raw %}{% annotation 7 %}{% endraw %}
      <catalogRef xlink:href="/thredds/catalog/grib/NCDC/CFSR/200808/FLX-GaussianT62/files/catalog.xml" xlink:title="files" name="" /> 
    </dataset>
    ... 
  </dataset>
  <dataset name="200809" >{% raw %}{% annotation 8 %}{% endraw %}
    ...
  </dataset>
</dataset>
{% endhighlight_with_annotations %}

* {% annotation 1 %} Container for the overall collection datasets
* {% annotation 2 %} The overall collection for the first group
* {% annotation 3 %} The overall collection for the second group, etc
* {% annotation 4 %} Container for the first partition
* {% annotation 5 %} The collection dataset for the first group of the first partition
* {% annotation 6 %} The individual files for the first group of the first partition, etc
* {% annotation 7 %} The collection dataset for the second group of the first partition, etc.
* {% annotation 8 %} Container for the second partition, etc

So the datasets that are generated from a `Time Partition` with `name`, `path`, `groupName`, and `partitionName`:

| dataset                            | catalogRef                                     | name                   | path                         |
|:-----------------------------------|:-----------------------------------------------|:-----------------------|:-----------------------------| 
| overall collection for group       | path/groupName/collection/catalog.xml          | groupName              | path/name/groupName          |
| collection for partition and group | path/partitionName/catalog.xml                 | groupName              | path/partitionName/groupName |
| individual files                   | path/partitionName/groupName/files/catalog.xml | partitionName/filename | path/files/filename          |
