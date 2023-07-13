#!/usr/bin/env python 
from ecmwfapi import ECMWFDataServer 
server = ECMWFDataServer() 
server.retrieve( 
    {
      "dataset" : "interim",
      "date" : "2023-01-01/to/2023-01-31",
      "expver" : "1",
      "grid" : "F128",
      "levellist" : "100",
      "levtype" : "pl",
      "param" : "138.128",
      "step" : "0",
      "stream" : "oper",
      "time" : "00:00/06:00/12:00/18:00",
      "type" : "an",
      "target" : "/home/jorodriguez/Drive/Meritoki/Documents//ECMWF/File/Data/ERA/Interim/Vorticity//138-128_20230101-20230131_100_F128.nc",
      "format" : "netcdf",
      "class" : "ei"
    }
)
