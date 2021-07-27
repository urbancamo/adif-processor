# adif-processor
Processor for Amateur Radio ADIF files.
Written by Mark Wickens [mark@wickensonline.co.uk](mailto:"mark@wickensonline.co.uk").

## What is this?
The post processor was originally designed to enrich ADIF content by 

Currently the post-processor can generate three types of output:
  - Enriched ADIF 
  - Google Earth KML file 
  - Pretty Printed contact listing file.

The post-processor knows activity references for [HEMA](http://hema.org.uk/) [SOTA](https://www.sota.org.uk/), [WOTA](https://wota.org.uk) and [Parks on the Air](https://https://parksontheair.com/).

## Command Line Arguments
Using the argument `-h` or `--help` shows the command line arguments:

```
Transform an ADIF file prior to storing  or mapping it by enriching it with
positional data.

positional arguments:
  path                   Input ADIF files

named arguments:
  -h, --help             show this help message and exit
  -q, --qrz              Enable QRZ.COM lookup (default: false)
  -qu QRZ_USERNAME, --qrz-username QRZ_USERNAME
                         Username for the QRZ XML Service
  -qp QRZ_PASSWORD, --qrz-password QRZ_PASSWORD
                         Password for the QRZ XML Service
  -lat LATITUDE, --latitude LATITUDE
                         Specify  override  latitude   in  decimal  format,
                         enclose in single quotes
  -long LONGITUDE, --longitude LONGITUDE
                         Specify  override  longitude  in  decimal  format,
                         enclose in single quotes
  -g GRID, --grid GRID   Specify override grid in 4/6/10 characters
  -he HEMA, --hema HEMA  Specify override HEMA Id for your location
  -w WOTA, --wota WOTA   Specify override WOTA Id for your location
  -s SOTA, --sota SOTA   Specify override SOTA Id for your location
  -p POTA, --pota POTA   Specify override POTA Id for your location
  -o OUTPUT, --output OUTPUT
                         Write output files to this directory
  -e ENCODING, --encoding ENCODING
                         Specify encoding  of  input  ADIF  file  (default:
                         windows-1251)
  -k, --kml              Generate a KML output  file  for mapping direct to
                         Google Earth (default: false)
  -kcw KML_CONTACT_WIDTH, --kml-contact-width KML_CONTACT_WIDTH
                         Specify the width of contact lines (default: 3)
  -kct KML_CONTACT_TRANSPARENCY, --kml-contact-transparency KML_CONTACT_TRANSPARENCY
                         Specify the transparency of  contact lines between
                         0% and 100%, 0% being solid (default: 20)
  -kcband, --kml-contact-colour-band
                         Colour QSOs  based  on  the  band  used  (default:
                         false)
  -ks2s, --kml-s2s       Highlight Summit to  Summit  Contacts  in KML file
                         (default: false)
  -kcs, --kml-contact-shadow
                         Draw  a  shadow   under   the   KML  contact  line
                         (default: true)
  -ks2sls KML_S2S_LINE_STYLE, --kml-s2s-line-style KML_S2S_LINE_STYLE
                         Set the  colour  of  contact  line  for  Summit to
                         Summit   contacts   of    the   form   html_color:
                         transparency:width,  default   is:  brick_red:50:2
                         (default: brick_red:50:2)
  -kcls KML_CONTACT_LINE_STYLE, --kml-contact-line-style KML_CONTACT_LINE_STYLE
                         Set the colour  of  contact  line  for contacts of
                         the  form  html_color:transparency:width,  default
                         is: baby_blue:50:2 (default: baby_blue:50:2)
  -kfi KML_FIXED_STATION, --kml-fixed-station KML_FIXED_STATION
                         URL of the  icon  to  use  for  fixed/home station
                         locations      (default:       http://maps.google.
                         com/mapfiles/kml/shapes/ranger_station.png)
  -kpi KML_PORTABLE_STATION, --kml-portable-station KML_PORTABLE_STATION
                         URL of the  icon  to  use  for  fixed/home station
                         locations      (default:       http://maps.google.
                         com/mapfiles/kml/shapes/hiker.png)
  -kmi KML_MOBILE_STATION, --kml-mobile-station KML_MOBILE_STATION
                         URL of the  icon  to  use  for  fixed/home station
                         locations      (default:       http://maps.google.
                         com/mapfiles/kml/shapes/ranger_station.png)
  -kmmi KML_MARITIME_STATION, --kml-maritime-station KML_MARITIME_STATION
                         URL  of  the  icon  to  use  for  maritime  mobile
                         station  locations  (default:  http://maps.google.
                         com/mapfiles/kml/shapes/sailing.png)
  -kparki KML_PARK_STATION, --kml-park-station KML_PARK_STATION
                         URL of the  icon  to  use  for  Parks  on  the Air
                         station  locations  (default:  http://maps.google.
                         com/mapfiles/kml/shapes/picnic.png)
  -ksotai KML_SOTA_STATION, --kml-sota-station KML_SOTA_STATION
                         URL of the icon to  use for SOTA station locations
                         (default:                      http://maps.google.
                         com/mapfiles/kml/shapes/mountains.png)
  -khemai KML_HEMA_STATION, --kml-hema-station KML_HEMA_STATION
                         URL of the icon to  use for SOTA station locations
                         (default:                      http://maps.google.
                         com/mapfiles/kml/shapes/hospitals.png)
  -kwotai KML_WOTA_STATION, --kml-wota-station KML_WOTA_STATION
                         URL of the icon to  use for SOTA station locations
                         (default:                      http://maps.google.
                         com/mapfiles/kml/shapes/trail.png)
  -kwwffi KML_WWFF_STATION, --kml-wwff-station KML_WWFF_STATION
                         URL of the icon to  use for WWFF station locations
                         (default:                      http://maps.google.
                         com/mapfiles/kml/shapes/parks.png)
  -md, --markdown        Generate Markdown  file  containing  the  contacts
                         (default: false)

```
## Configuration Files

TODO
## The ADIF format 30 second Primer
[ADIF](http://adif.org/) _Amateur Data Interchange Format_ is a text file representation for Amateur radio contacts. It is a popular
output format for logging programs. The [ADIF specification](https://adif.org/312/ADIF_312.htm) describes the valid content of the header and record fields.

An ADIF file consists of two sections:

- header
- records

###Fields in an ADIF file
Each field in the file is proceeded by a field name separated by the length of the field value with a colon.
For example: `<PROGRAMID:3>FLE` indicates the field is `PROGRAMID` and the text contained in the field
is `3` characters long with a value of `FLE`.

###Header
The header contains information about the program that generate the file and the ADIF version, for example:

```
ADIF Export for Fast Log Entry by DF3CB
<PROGRAMID:3>FLE
<ADIF_VER:5>3.1.0
<EOH>
```

The header is terminated with the `<EOH>` marker.

###Records

Each record captures all the details of a QSO for both the recording station and the contacted station. 
A record is terminated by the `<EOF>` marker.

Here is an example entry in a [Fast Log Entry](https://df3cb.com/fle/) input file:

```
40m ssb 7.090
1258 g7las/p 7.188 <OP: Rob, PWR: 50, GRID: IO81LC, HEMA: G/HWB-026>
```

This is the ADIF record generated by [Fast Log Entry](https://df3cb.com/fle/). These 
are typically stored on one line. In this case I've separated
each field of a record into a single line:

```
<STATION_CALLSIGN:7>M0NOM/P 
<CALL:7>G7LAS/P 
<QSO_DATE:8>20210522 
<TIME_ON:4>1258 
<BAND:3>40m 
<MODE:3>SSB 
<FREQ:5>7.188 
<RST_SENT:2>59 
<RST_RCVD:2>59 
<COMMENT:47>OP: Rob, PWR: 50, GRID: IO81LC, HEMA: G/HWB-026 
<QSLMSG:44>Thx for QSO from Winter Hill io83ro G/SP-010 
<MY_SOTA_REF:8>G/SP-010 
<OPERATOR:5>M0NOM 
<MY_GRIDSQUARE:6>IO83ro 
<EOR>
```

Note that the QSO has a `<STATION_CALLSIGN:7>` (me) and a `<CALL:7>` G7LAS/P who is on the other end, a date and time, frequency, band, mode, signal reports,
my SOTA reference `<MY_SOTA_REF:8>`, the operator (basically my callsign without any modifiers) and my Maidenhead
Locator in `<MY_GRIDSQUARE:6>`.

Of interest is the comment line, which we will examine further, as this is one of the key features of post-processing.
In the comment line:

`<COMMENT:47>OP: Rob, PWR: 50, GRID: IO81LC, HEMA: G/HWB-026
`

You will notice that it consists of a number comma-separated key-value pairs. For example, the first
pair key is `OP` with value `ROB`, then `PWR` value `50` etc.

## History
The first version of the post-processor was written to post-process comments against records
in the output from [Fast Log Entry](https://df3cb.com/fle/) to add additional information into
the ADIF file.
