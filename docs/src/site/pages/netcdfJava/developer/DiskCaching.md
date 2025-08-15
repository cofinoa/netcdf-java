---
title: Disk Caching
last_updated: 2025-08-15
sidebar: netcdfJavaTutorial_sidebar
toc: false
permalink: disk_caching.html
---

## CDM Caching

### Disk Caching

#### Writing temporary files using DiskCache

There are a number of places where the CDM library needs to write temporary files to disk. If you end up using the file more than once, its useful to save these files. The CDM uses static methods in `ucar.nc2.util.DiskCache` to manage how the temporary files are managed.

Before the CDM writes the temporary file, it looks to see if it already exists.

1. If a filename ends with `.Z`, `.zip`, `.gzip`, `.gz`, or `.bz2`, `NetcdfFile.open` will write an uncompressed file of the same name, but without the suffix.

2. `Nexrad2`, `Cinrad2` files that are compressed will be uncompressed to a file with an `.uncompress` prefix.
By default, DiskCache prefers to place the temporary file in the same directory as the original file. If it does not have write permission in that directory, by default it will use the directory `${user_home}/.unidata/cache/`. You can change the directory by calling

`ucar.nc2.util.DiskCache.setRootDirectory(rootDirectory).`

You might want to always write temporary files to the cache directory, in order to manage them in a central place. To do so, call

`ucar.nc2.util.DiskCache.setCachePolicy( boolean alwaysInCache)` with parameter `alwaysInCache = true`.

You may want to limit the amount of space the disk cache uses (unless you always have data in writeable directories, so that the disk cache is never used). To scour the cache, call `DiskCache.cleanCache()`. There are several variations of the cleanup:

* `DiskCache.cleanCache(Date cutoff, StringBuilder sbuff)` will delete files older than the cutoff date.
* `DiskCache.cleanCache(long maxBytes, StringBuilder sbuff)` will retain maxBytes bytes, deleting oldest files first.
* `DiskCache.cleanCache(long maxBytes, Comparator<File> fileComparator, StringBuilder sbuff)` will retain maxBytes bytes, deleting files in the order defined by your Comparator.

For a long-running application, you might want to do this periodically in a background timer thread, as in the following example.


~~~java
// Get the current time and add 30 minutes to it
Calendar c = Calendar.getInstance();
c.add(Calendar.MINUTE, 30);

// your class must extend TimerTask, the run method is called by the Timer
private class CacheScourTask extends java.util.TimerTask {
  public void run() {
    StringBuffer sbuff = new StringBuffer();
    //Scour the cache, allowing 100 Mbytes of space to be retained
    DiskCache.cleanCache(100 * 1000 * 1000, sbuff);
    sbuff.append("----------------------\n");
    // Optionally log a message with the results of the scour
    log.info(sbuff.toString());
  }
}

// Start up a timer that executes every 60 minutes, starting in 30 minutes
java.util.Timer timer = new Timer();
timer.scheduleAtFixedRate(new CacheScourTask(), c.getTime(), (long) 1000 * 60 * 60);

// make sure you cancel the timer before your application exits, or else
// the process will not terminate
timer.cancel();
~~~

#### Writing temporary files using DiskCache2

In a number of places, the `ucar.nc2.util.DiskCache2` class is used to control caching. This does not use static methods, so can be configured for each individual use.

The default constructor mimics DiskCache, using `${user_home}/.unidata/cache/` as the root directory:

`DiskCache2 dc2 = new DiskCache2();`

You can change the root directory by calling

`dc2.setRootDirectory(rootDirectory).`

You can tell the class to scour itself in a background timer by using the constructor:

`DiskCache2 dc2 = new DiskCache2(rootDirectory, false, 24 * 60, 60);`

~~~java
/**
 * Create a cache on disk.
 * @param root the root directory of the cache. Must be writeable.
 * @param relativeToHome if the root directory is relative to the cache home directory.
 * @param persistMinutes  a file is deleted if its last modified time is greater than persistMinutes
 * @param scourEveryMinutes how often to run the scour process. If <= 0, dont scour.
 */
public DiskCache2(String root, boolean relativeToHome, int persistMinutes, int scourEveryMinutes);
~~~
       
You can change the cache policy from the default CachePathPolicy.OneDirectory by (eg):

~~~java
dc2.setCachePathPolicy(CachePathPolicy.NestedTruncate, null).

/**
* Set the cache path policy
* @param cachePathPolicy one of:
*   OneDirectory (default) : replace "/" with "-", so all files are in one directory.
*   NestedDirectory: cache files are in nested directories under the root.
*   NestedTruncate: eliminate leading directories
*
* @param cachePathPolicyParam for NestedTruncate, eliminate this string
*/
public void setCachePathPolicy(CachePathPolicy cachePathPolicy, String cachePathPolicyParam);
~~~
  
You can ensure that the cache is always used with:

`dc2.setCacheAlwaysUsed(true);`

Otherwise, the cache will try to write the temporary file in the same directory as the data file, and only use the cache if that directory is not writeable.

### GRIB Indexing and Caching

In 4.3 and above, for each GRIB file the CDM writes a _grib index file_ using the filename plus suffix `.gbx9`. So a file named `filename.grib1` will have an index file `filename.grib1.gbx9` created for it the first time that its read. Usually a _cdm index file_ is also created, using the filename plus suffix `.ncx`. So a file named filename.grib1 will have an index file filename.grib1.ncx created for it the first time. When a GRIB file is only part of a collection of GRIB files, then the ncx file may be created only for the collection.

The location of these index files is controlled by a caching strategy. The default strategy is to try to place the index files in the same directory as the data file. If that directory is not writeable, then the default strategy is to write the index files in the default caching directory. In a client application using the CDM, that default will be

`${user_home}/.unidata/cache/.`

On the TDS it will be
`${tomcat_home}/content/thredds/cache/cdm`

Clients of the CDM can change the GRIB caching behavior by configuring a DiskCache2 and calling:

`ucar.nc2.grib.GribCollection.setDiskCache2(DiskCache2 dc);`

### Object Caching

#### NetcdfFileCache
NetcdfFile objects are cached in memory for performance. When acquired, the object is locked so another thread cannot use. When closed, the lock is removed. When the cache is full, older objects are removed from the cache, and all resources released.

Note that typically a `java.io.RandomAccessFile` object, holding an OS file handle, is open while its in the cache. You must make sure that your cache size is not so large such that you run out of file handles due to NetcdfFile object caching. Most aggregations do not hold more than one file handle open, no matter how many files are in the aggregation. The exception to that is a Union aggregation, which holds each of the files in the union open for the duration of the NetcdfFile object.

Holding a file handle open also creates a read lock on some operating systems, which will prevent the file from being opened in write mode.

To enable caching, you must first call

~~~java
NetcdfDataset.initNetcdfFileCache(int minElementsInMemory, int maxElementsInMemory, int period);
~~~

where `minElementsInMemory` is the number of objects to keep in the cache when cleaning up, `maxElementsInMemory` triggers a cleanup if the cache size goes over it, and `period` specifies the time in seconds to do periodic cleanups.

After enabling, you can disable with:

~~~java
NetcdfDataset.disableNetcdfFileCache();
~~~

However, you cant reenable after disabling.

Setting `minElementsInMemory` to zero will remove all files not currently in use every `period` seconds.

Normally the cleanup is done is a background thread to not interferre with your application, and the maximum elements is approximate. When resources such as file handles must be carefully managed, you can set a hard limit with this call:

~~~java
NetcdfDataset.initNetcdfFileCache(int minElementsInMemory, int maxElementsInMemory, int hardLimit, int period);
~~~

so that as soon as the number of NetcdfFile objects exceeds hardLimit , a cleanup is done immediately in the calling thread.