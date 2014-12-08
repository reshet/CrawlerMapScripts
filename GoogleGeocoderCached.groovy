/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/9/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

class GoogleGeocoderCached {
    public static cache_url_local = "http://localhost/InteractiveMaps/php/geocache.php";
    public static cache_url_remote = "http://survey-archive.com/InteractiveMaps/php/geocache.php";
    private static my_key = ""
    public static def parseJSON(String str){
        def slurper = new JsonSlurper()
        def result = slurper.parseText(str)
        if(!(result instanceof net.sf.json.JSONNull))return result
        else return null
    }
    public static String geocode(String address,boolean local,boolean strict = false, String area_level_bound){
        String ans_cache = ""
        String url = local?cache_url_local:cache_url_remote
        url+="?action=gt&address="+URLEncoder.encode(address);
        String cache_ans = url.toURL().getText()
        //println address +" "+ cache_ans
        if(cache_ans!="Not in cache") return cache_ans
        else
        {
            //println "geocoding..."
            //kiev region bounds:
            def kiev_bounds = "50.193073,29.929461|50.688971,31.114612"
            sleep(300);
            String query = "http://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(address)+"&language=uk&sensor=false"
            if (strict && area_level_bound != null) {
                query+="&components=country:UA|administrative_area_level_1:" + URLEncoder.encode(area_level_bound)
                query+="&bounds=" + kiev_bounds
            }
            String google_ans = (query).toURL().getText()
            def slurper = new JsonSlurper()
            def result = slurper.parseText(google_ans)
            println result
            //println result.status+":"
            if(result.status == "OK"){

                def lat = result.results[0].geometry.location.lat
                def lng = result.results[0].geometry.location.lng
                def loc_type = result.results[0].geometry.location_type

                if(lat != null && lng != null){
                    String url2 = local?cache_url_local:cache_url_remote
                    def formatted = result.results[0].formatted_address
                    //println formatted
                    url2+="?action=pt&address="+URLEncoder.encode(address)+"&long="+lng+"&lat="+lat+"&g_address="+URLEncoder.encode(formatted)+"&type="+loc_type;

                    return url2.toURL().getText()
                }

            } else {
                return "no definite geocode";
            }
            if(result.status == "OVER_QUERY_LIMIT") throw new Exception();
        }
    }
}
