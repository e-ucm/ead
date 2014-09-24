#!/bin/bash

#
# This script reverses colors on all buttons in mockups' raw assets;
# the resulting "reversed" buttons are then available for inclusion in the project's wiki
#

SRCDIR_PREFIX="../../assets-raw"
SRCDIR="$SRCDIR_PREFIX/skins-raw/mockup-hdpi/images"
NAMEPART='80x80'
OUTDIR=.

echo "Cleaning up..."
rm *.png
git rm *.png

echo "Retrieving source images from git..."
git checkout master -- "$SRCDIR/*"

echo "Reversing all images in $SRCDIR with $NAMEPART to $OUTDIR..."
for i in $(find $SRCDIR -iname '*.png' | grep $NAMEPART); do
	j=$(basename $i)
	echo "converting $i to $j..."
	convert $i -negate $j
done
git add *.png

echo "All images converted; cleaning up..."
rm -rf $SRCDIR_PREFIX
git rm -r $SRCDIR_PREFIX

echo "Thanks for using this script!"
