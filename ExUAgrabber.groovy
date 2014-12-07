
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 11/19/13
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */



@Grab(group='org.codehaus.gpars', module='gpars', version='1.1.0')
import groovyx.gpars.GParsPool

//def text = ("http://www.eway.in.ua/js/routes.js.php?lang=ru&city=kyiv&id=-1&lat=0&lng=0&zoom=0").toURL().getText();
//println text
//@Grab(group='org.ccil.cowan.tagsoup',
//        module='tagsoup', version='1.2' )
//def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
//def slurper = new XmlSlurper(tagsoupParser)
  "".toURL().get

public static void screenGrab(String file_name){
    String screen_name = "/home/reshet/grab/photos/" + file_name;

    if(!new File(screen_name).exists()){
        def proc = command.execute()
        proc.waitFor()
    }
}

//def xml = slurper.parseText(text)
//
////getting screens code fast
//GParsPool.withPool{
//    xml."**".findAll { it.@id.toString().contains("showRoute")}.eachWithIndexParallel {
//        elem, index ->
//            if(index >= 262 && index < 283) {
//                def str = index+ " "+elem+" "+elem.@id.toString();
//                //logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ making screen " + str+"\n"}
//                println new Date().getDateTimeString() +" __ making screen " + str;
//                String route_name = elem.text().toString()
//                String route_link = elem.@id.toString()
//                route_link = route_link.substring(9,route_link.length())
//                screenGrab(route_link,route_name)
//            }
//
//    }
//}

def http = new HTTPBuilder()

http.request( 'http://ajax.googleapis.com', GET, TEXT ) { req ->
    uri.path = '/ajax/services/search/web'
    uri.query = [ v:'1.0', q: 'Calvin and Hobbes' ]
    headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
    headers.Accept = 'application/json'

    response.success = { resp, reader ->
        assert resp.statusLine.statusCode == 200
        println "Got response: ${resp.statusLine}"
        println "Content-Type: ${resp.headers.'Content-Type'}"
        println reader.text
    }

    response.'404' = {
        println 'Not found'
    }
}