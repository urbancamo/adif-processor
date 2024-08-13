#!/bin/bash

DATE="`date +%C%y%m%d`"

cd /mediaraid/backups/norad
# Fetch amateur radio satellite file from celestrak
wget http://www.celestrak.com/NORAD/elements/amateur.txt
#wget -O $DATE-amateur.xml http://www.celestrak.com/NORAD/elements/gp.php?GROUP=amateur&FORMAT=xml
mv amateur.txt $DATE-amateur.txt

