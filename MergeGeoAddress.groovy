
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  File f = new File("/var/www/InteractiveMaps/js/addresses.json")
  File f2 = new File("/var/www/InteractiveMaps/php/geocache.csv")
  File f3 = new File("/var/www/InteractiveMaps/js/addresses_merged.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def geocache = [:]
def result = slurper.parseText(f.getText())

f2.withReader {
    ff2->
        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                if(lnarr.length > 2){
                    def add = lnarr[0]
                    def lng = lnarr[1]
                    def lat = lnarr[2]
                    if(add!=null){
                        geocache[add] = ["lat": lat,"lng": lng]
                    }
                }


        }
}
println geocache

f3.withWriter {
    ff3->
        ff3 << "[\n"
            result.each{
                elem->
                    //println elem
                    if(geocache.containsKey(elem.address)){
                        println geocache[elem.address]
                        def lat = geocache[elem.address]["lat"]
                        def lng = geocache[elem.address]["lng"]

                        ff3 << "{\"address\":\""+elem.address+"\", \"title\":\""+elem.title+"\", \"lat\":"+lat+", \"lng\":"+lng+"},\n"
                    }

            }
        ff3 << "]\n"

}
