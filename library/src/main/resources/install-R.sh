#!/bin/bash
VERSION=3.6.3
# 3.4.4
# sudo apt-get install -y gfortran
# sudo apt-get install -y build-essential
# sudo apt-get install -y libreadline7
# sudo apt-get install -y xorg-dev
# sudo apt-get install -y libbz2-dev
# sudo apt-get install -y liblzma-dev
# sudo apt-get install -y libcurl4-openssl-dev
# sudo apt-get install -y libpcre++-dev
# sudo apt-get install -y libreadline-dev
# sudo apt-get install -y libcurl4-openssl-dev
# sudo apt-get install -y libssl-dev
# sudo apt-get install -y tcl-dev
# sudo apt-get install -y tk-dev
[ -d "./R-"$VERSION ] || wget http://cran.r-project.org/src/base/R-3/R-$VERSION.tar.gz
tar xf R-$VERSION.tar.gz
cd R-$VERSION
./configure --enable-R-shlib --with-tcltk --with-pcre1
make
sudo make install
# sudo mkdir -p /usr/lib/R/library
# sudo Rscript install.R
