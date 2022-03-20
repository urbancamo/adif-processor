# adif-processor
Processor for Amateur Radio ADIF files.
Written by Mark Wickens [mark@wickensonline.co.uk](mailto:"mark@wickensonline.co.uk").

## What is this?

The [ADIF Processor](http://adifweb-env.eba-saseumwd.eu-west-2.elasticbeanstalk.com/) is an online application
that visualizes amateur radio contacts from an ADIF log file using [Google Earth](https://earth.google.com).
[SOTA](https://www.sotadata.org.uk/en/) CSV log files are also supported.

The processor looks for specially formatted comments in your input file which are transposed into the
correct ADIF fields in your output file.

The post-processor generates three types of output:
  - Enriched ADIF files
  - Google Earth KML files  
  - Customizable Contact listing files

## Documentation

Here is the [documentation for the ADIF Processor](https://urbancamo.github.io/adif-processor/adif-processor).

## JavaApi4Kml

This is unmaintained and now throws runtime exceptions due to the use of the built-in JAXB
implementation in previous JDKs. I'm looking at either forking or depending on a fork
that has been updated.

Possible options are:

 - [KikiManjaro-javaapiforkml](https://github.com/KikiManjaro/javaapiforkml)
 - 