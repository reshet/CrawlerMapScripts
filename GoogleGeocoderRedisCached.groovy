/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/9/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
@Grab('redis.clients:jedis:2.6.2')


import net.sf.json.groovy.JsonSlurper

class GoogleGeocoderRedisCached {
    public static final File logfile = new File("/var/www/geocode/geocoding.log");
    public static final redis = new redis.clients.jedis.Jedis("localhost")
    static {
        if(!logfile.exists())logfile.createNewFile();
        assert "PONG" == redis.ping()
    }
    private static my_key = ""
//    public static def parseJSON(String str){
//        def slurper = new JsonSlurper()
//        def result = slurper.parseText(str)
//        if(!(result instanceof net.sf.json.JSONNull))return result
//        else return null
//    }
//    public static cacheToGeocodingResponse(def cacheAns) {
//        '{"lb":' + cacheAns.lat +
//                ',"mb":' + cacheAns.lng +
//                ',"address":"' + cacheAns.full +
//                '","loc_type":"' + cacheAns.precision +
//                '"}'
//    }
    public static def geocode(String address,boolean local,boolean strict = false, String area_level_bound, String bounds){
        String ans_cache = ""

        if(redis.exists(address)) {
            def cacheAns = redis.hgetAll(address);
            logfile.withWriterAppend {
                lf ->
                    lf << new Date().getDateTimeString() +" Return from cache: " + cacheAns + "\n"
            }
            return cacheAns;
        } else {
            //println "geocoding..."
            //kiev region bounds:
            sleep(250);
            String query = "http://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(address)+"&language=uk&sensor=false"
            if (strict && area_level_bound != null) {
                //println "BOUNDS: " + area_level_bound + "  " + bounds;
                query+="&components=administrative_area:" + URLEncoder.encode(area_level_bound)
                query+="&bounds=" + bounds
            }
            String google_ans = (query).toURL().getText()
            def slurper = new JsonSlurper()
            def result = slurper.parseText(google_ans)
            //println result.results.length
            //println result.status+":"
            //println result.results
            if (result.status == "OK") {

                def lat = result.results[0].geometry.location.lat
                def lng = result.results[0].geometry.location.lng
                def loc_type = result.results[0].geometry.location_type

                if(lat != null && lng != null){
                    def formatted = result.results[0].formatted_address
                    if (formatted == "місто Київ, Україна" || formatted == "Київська область, Україна") {
                        return "no definite geocode";
                    }
                    redis.hset(address, "lng", String.valueOf(lng))
                    redis.hset(address, "lat", String.valueOf(lat))
                    redis.hset(address, "full", formatted)
                    redis.hset(address, "precision", loc_type)

                    def fromRedis = redis.hgetAll(address);
                    return fromRedis;
                } else {
                    return "no definite geocode";
                }
            } else {
                logfile.withWriterAppend {
                    lf ->
                        lf << new Date().getDateTimeString() +" NOT OK Geocode status: " + result.status + "\n"
                }
                if (result.status == "OVER_QUERY_LIMIT") {
                    throw new Exception()
                } else {
                    return "no definite geocode";
                }
            }

        }
    }
}
