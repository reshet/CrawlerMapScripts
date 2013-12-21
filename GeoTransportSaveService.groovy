
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */



@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def url_to_geocode = "http://localhost/MSS/FixTransportListNotGeocoded.php";
//def url_to_geocode = "http://survey-archive.com/MSS/FixTransportListNotGeocoded.php";

def to_geocode_resp = url_to_geocode.toURL().getText()
def result = slurper.parseText(to_geocode_resp)

def url_saver = "http://localhost/MSS/FixGeocodeSaveTransport.php";
//def url_saver = "http://survey-archive.com/MSS/FixGeocodeSaveTransport.php";
final File logfile = new File("/var/www/MSS/geocoding_save.log");
if(!logfile.exists())logfile.createNewFile();
logfile.withWriterAppend {
    lf ->

    lf << new Date().getDateTimeString() +" __ " + "Start saving geocodes from file to DB"+"\n"
    result.eachWithIndex{
        elem,index ->

       // if (index < 2){


            def address = elem["address"]
            address = "Киев "+address
            address = address.replaceAll("\"","")

            //ff3 << "{\"address\":\""+address+"\", \"title\":\""+title+"\""
            //print index +" "
            String geoc = GoogleGeocoderCached.geocode(address,true,true)
            def coor = GoogleGeocoderCached.parseJSON(geoc)
            if(coor!= null && coor.lb!= null && coor.mb!= null){
                def g_addr = coor.address;
                g_addr = g_addr.replaceAll("\"","")

                g_addr = URLEncoder.encode(g_addr)
                //address = URLEncoder.encode(address)

                def loc_type = "no";
                if(coor.loc_type!=null)loc_type = coor.loc_type;
                def var_code = elem["var_code"]
                def task_id = elem["task_id"]
                def int_id = elem["int_id"]
                def lat = coor.lb;
                def lng = coor.mb;
                def params = [
                        base_var_code:var_code,lat:lat,lng:lng,task_id:task_id,int_id:int_id,
                        formatted:g_addr,loc_type:loc_type
                ]

                def url = url_saver +"?"+ params.collect { k,v -> "$k=$v" }.join('&');
                String mss_ans = url.toURL().getText();
                //println index+"  "+mss_ans;
                lf << new Date().getDateTimeString() +" __ " + index+"  "+mss_ans+"\n"
            }





        //}

    }
    lf << new Date().getDateTimeString() +" __ " + "Finish saving geocodes from file to DB"+"\n"
}

