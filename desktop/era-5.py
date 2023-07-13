import cdsapi 
c = cdsapi.Client() 
c.retrieve( 
    'reanalysis-era5-pressure-levels',
    {
      "product_type" : "reanalysis",
      "format" : "netcdf",
      "variable" : [ "geopotential" ],
      "pressure_level" : [ "100" ],
      "year" : [ "2015" ],
      "month" : [ "01" ],
      "day" : [ "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" ],
      "time" : [ "00:00", "06:00", "12:00", "18:00" ],
      "grid" : "F128"
    },
    '/home/jorodriguez/Drive/Meritoki/Documents//ECMWF/File/Data/ERA/5/Geopotential//geopotential_20150101-20150131_100_F128.nc')
