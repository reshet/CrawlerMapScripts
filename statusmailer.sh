#!/bin/bash

function sendNotification {
    subject="Geocoder node $(hostname) status update"
    from="geocoder@mresearch.com"
    recipients="reshet.ukr@gmail.com,fshodan@gmail.com"
    emailtext="$1"

/usr/sbin/sendmail "$recipients" <<EOF
subject:$subject
from:$from
Content-Type: text/html
MIME-Version: 1.0
$emailtext
EOF
}

txt=""
while :
do
	txt2=$(curl http://localhost/geocode/status.php)
     if [ "$txt" != "$txt2" ];
     then
       txt=$txt2
       echo "Sending mail...\n"
       sendNotification "$txt"
     fi
	sleep 600
done