
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */


  File f = new File("/var/www/InteractiveMaps/js/addresses_geo3.json")

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def result = slurper.parseText(f.getText())

//def url_fixer = "http://localhost/MSS/FixGeocodeSaveTransport.php";
def url_fixer = "http://survey-archive.com/MSS/FixGeocodeSaveTransport.php";
final File logfile = new File("/var/www/MSS/geocoding_save.log");
if(!logfile.exists())logfile.createNewFile();
logfile.withWriterAppend {
    lf ->

    lf << new Date().getDateTimeString() +" __ " + "Start saving geocodes from file to DB"+"\n"
    result.eachWithIndex{
        elem,index ->

       // if (index < 2){

            def g_addr = elem["geocoded_address"]
            g_addr = URLEncoder.encode(g_addr);
            def address = elem["address"]
            address = URLEncoder.encode(address)
            def lat = elem["lat"]
            def lng = elem["lng"]
            def var_code = elem["var_code"]
            def task_id = elem["task_id"]
            def int_id = elem["int_id"]
            def loc_type = elem["loc_type"]
            //println index+"  "+g_addr;
            def params = [
                base_var_code:var_code,lat:lat,lng:lng,task_id:task_id,int_id:int_id,
                formatted:g_addr,loc_type:loc_type
            ]
            def url = url_fixer +"?"+ params.collect { k,v -> "$k=$v" }.join('&');
            String mss_ans = url.toURL().getText();
            //println index+"  "+mss_ans;
            lf << new Date().getDateTimeString() +" __ " + index+"  "+mss_ans+"\n"

        //}

    }
    lf << new Date().getDateTimeString() +" __ " + "Finish saving geocodes from file to DB"+"\n"
}

