#!/bin/bash
#
# eAdventure is a research project of the
#    e-UCM research group.
#
#    Copyright 2005-2014 e-UCM research group.
#
#    You can access a list of all the contributors to eAdventure at:
#          http://e-adventure.e-ucm.es/contributors
#
#    e-UCM is a research group of the Department of Software Engineering
#          and Artificial Intelligence at the Complutense University of Madrid
#          (School of Computer Science).
#
#          CL Profesor Jose Garcia Santesmases 9,
#          28040 Madrid (Madrid), Spain.
#
#          For more info please visit:  <http://e-adventure.e-ucm.es> or
#          <http://www.e-ucm.es>
#
# ****************************************************************************
#
#  This file is part of eAdventure
#
#      eAdventure is free software: you can redistribute it and/or modify
#      it under the terms of the GNU Lesser General Public License as published by
#      the Free Software Foundation, either version 3 of the License, or
#      (at your option) any later version.
#
#      eAdventure is distributed in the hope that it will be useful,
#      but WITHOUT ANY WARRANTY; without even the implied warranty of
#      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#      GNU Lesser General Public License for more details.
#
#      You should have received a copy of the GNU Lesser General Public License
#      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
#

# 
# JREs at
#   https://github.com/alexkasko/openjdk-unofficial-builds
#   https://bitbucket.org/alexkasko/openjdk-unofficial-builds/
#       downloads/openjdk-1.7.0-u40-unofficial-windows-i586-image.zip
#
# JarWrapper in
#   https://github.com/stbachmann/JarWrapper
#   http://robotality.com/jarwrapper/download/JarWrapper-latest.zip
#
# Launch4j in
#   http://launch4j.sourceforge.net/
#   http://sourceforge.net/projects/launch4j/files/
#       launch4j-3/3.1.0-beta2/
#

version="openjdk-1.7.0-u45-unofficial-icedtea-2.4.3-"
dl_url="https://bitbucket.org/alexkasko/"
dl_path="openjdk-unofficial-builds/downloads/${version}"
jre_path="jres/"
prefix="${jre_path}${version}"
suffix="-image.zip"
output_path="output"

function download_jre {
    src=$1
    tgt="${version}${src}${suffix}"
    echo "retrieving ${dl_url}${dl_path}${src}${suffix} ..."
    wget "${dl_url}${dl_path}${src}${suffix}" -v -o ${jre_path}${tgt} && \
    echo " ... retrieved" && return
    
    echo " ... ERROR in last step"
}

function prepare_jre {
    tmp=$(mktemp -d)
    src=$1
    tgt=$2
    echo "using tmpdir ${tmp} to install ${src} into ${tgt}..."
    echo " - removing old files..." && rm -rf ${tgt} && \
    echo " - recreating target dir... " && mkdir ${tgt} && \
    echo " - unzipping... " && unzip "${prefix}${src}${suffix}" -d ${tmp} && \
    echo " - moving to target... " && mv ${tmp}/*/jre/* ${tgt} && \
    echo " - removing tmpdir... " && rm -rf ${tmp} && \
    echo "... installation of ${src} into ${tgt}: OK" && return

    echo "... installation of ${src} into ${tgt}: ERROR in last step"
}

function wrap {
    echo "cleaning old wrappers..."
    rm -rf ${output_path}/*
    echo "wrapping up..."
    java -jar JarWrapper.jar
    echo "... wrap-up complete"
}

# download_jre "windows-amd64"
prepare_jre "windows-amd64" "win/jre/"
wrap 

# prepare_jre "linux-i586" "linux/jre/32"
# prepare_jre "linux-amd64" "linux/jre/64"

