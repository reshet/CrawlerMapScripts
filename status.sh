printf "<h1>Status of geocoder service</h1>"
printf "<h2>Geocoder service process status:</h2>"
printf "<p>"
runningGroovyProcs=$(ps aux | grep "OSA-service-scan-geocode-rotate.groovy" | wc -l)
if [ $runningGroovyProcs > 1 ]; then
    printf "<h3 style='color: green;'>Service is running</h3>"
else
    printf "<h3 style='color: red;'>Service is NOT running!</h3>"
fi
printf "</p>"
printf "<h2>Recent log: </h2>"
tail -n 15 /var/www/geocode/geocoding.log $1 | while read x; do printf "<p>$x</p>"; done
printf "<h2>Recent geocodings: </h2>"
cat /var/www/geocode/geocoding.log | egrep "Start | TOTAL" | tail -20 $1 | while read x; do printf "<p>$x</p>"; done
