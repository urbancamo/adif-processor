# adif-processor
Processor for Amateur Radio ADIF files.
Written by Mark Wickens [mark@wickensonline.co.uk](mailto:"mark@wickensonline.co.uk").

## What is this?

The [ADIF Processor](https://www.adif.uk) is an online application
that visualizes amateur radio contacts from an ADIF log file using [Google Earth](https://earth.google.com).
[SOTA](https://www.sotadata.org.uk/en/) CSV log files are also supported.

The processor looks for specially formatted comments in your input file which are transposed into the
correct ADIF fields in your output file.

The post-processor generates three types of output:
  - Enriched ADIF files
  - Google Earth KML files  
  - Customizable Contact listing files

The application also contains a very flexible [coordinate conversion tool](https://www.adif.uk/coord) 
which can be used to convert between all sorts of different coordinate systems.

## Documentation

Here is the [documentation for the ADIF Processor](https://urbancamo.github.io/adif-processor/adif-processor).

## JavaApi4Kml

This has been forked in [urbancamo/javaapiforkml](https://github.com/urbancamo/javaapiforkml) and updated to
work with Java 11+.