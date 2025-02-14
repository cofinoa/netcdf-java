
12/09/2014 caron - download from http://www.wmo.int/pages/prog/www/WMOCodes/WMO306_vI2/LatestVERSION/LatestVERSION.html
  The latest versions 14(.0.0) (GRIB edition 2) and 23(.0.0) (BUFR and CREX) are effective as from 5 November 2014.
  - put BUFRCREX_22_0_1.zip zip file into src/main/sources/wmo/
  - unzip and put BUFR_22_0_1_Table(A|C|D)_en.xml, BUFRCREX_22_0_1_(CodeFlag|TableB)_en.xml  into resources/bufrTables/wmo
  - modify resources/bufrTables/local/tablelookup.csv
  - modify ucar.nc2.iosp.bufr.tables.CodeFlagTables, ucar.nc2.iosp.bufr.tables.WmoXmlReader, ucar.nc2.iosp.bufr.tables.TableA


09/05/2019 caron
  download from http://www.wmo.int/pages/prog/www/WMOCodes/WMO306_vI2/LatestVERSION/BUFRCREX_32_0_0.zip
   - put BUFRCREX_32_0_0.zip file into src/main/sources/wmo/
   - unzip and put BUFR_32_0_0_Table(A|C|D)_en.xml, BUFRCREX_32_0_0_(CodeFlag|TableB)_en.xml into resources/bufrTables/wmo
   - modify resources/bufrTables/local/tablelookup.csv
   - modify ucar.nc2.iosp.bufr.tables.CodeFlagTables, ucar.nc2.iosp.bufr.tables.WmoXmlReader, ucar.nc2.iosp.bufr.tables.TableA

2025-02-14 sarms
 download from https://community.wmo.int/en/activity-areas/wis/2023-11-30
   - put BUFR4-v41.zip file into src/main/sources/wmo/
   - unzip and put xml/BUFR_Table(A|C|D)_en.xml, xml/BUFRCREX_(CodeFlag|TableB)_en.xml into resources/bufrTables/wmo (rename to include version in name)
   - modify resources/bufrTables/local/tablelookup.csv
   - modify ucar.nc2.iosp.bufr.tables.CodeFlagTables, ucar.nc2.iosp.bufr.tables.WmoXmlReader, ucar.nc2.iosp.bufr.tables.TableA
