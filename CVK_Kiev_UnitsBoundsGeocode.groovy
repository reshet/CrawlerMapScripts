
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  File f = new File("/var/www/InteractiveMaps/js/addresses_merged_polygons.json")
  //File f2 = new File("/var/www/InteractiveMaps/php/geocache.csv")
  File f3 = new File("/var/www/InteractiveMaps/js/addresses_merged_polygons_geocoded.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper


def polygons = [:]

//println "here"

//String ans = GoogleGeocoderCached.geocode("Київ Шовковична 2",true)
def slurper = new JsonSlurper()
//def res = slurper.parseText(ans)
//println result.lb
//println result.mb

def result = slurper.parseText(f.getText())

f3.withWriterAppend {
    ff3->
        ff3 << "[\n"
            int p_count = result.size()
            boolean continuew = false
            result.eachWithIndex{
                elem,index->
                   if(elem.title == "ТВО №219 ВД №147") continuew = true
                   if(continuew){
                       ff3 << "{\"address\":\""+elem.address+"\", \"title\":\""+elem.title+"\", "
                       //println elem.address
                       String geoc = GoogleGeocoderCached.geocode(elem.address,true)
                       def coor = slurper.parseText(geoc)
                       if(!(coor instanceof net.sf.json.JSONNull)&& coor.lb!= null && coor.mb!= null){
                           ff3 << "\"lat\":"+coor.lb+", \"lng\":"+coor.mb+", "
                           //println elem.address+" "+coor.lb+" "+coor.mb
                       }

                       ff3 << "\"bounds\":\n["
                       int b_count = elem.bounds.size()
                       elem.bounds.eachWithIndex{
                           el,ind->
                               ff3 << "{\"address\":\""+el.address+"\""
                               String an = GoogleGeocoderCached.geocode(el.address,true)
                               def coord = slurper.parseText(an)
                               if(coord!=null && !(coord instanceof net.sf.json.JSONNull)&& coord.lb!= null && coord.mb!= null){
                                   ff3 << ", \"lat\":"+coord.lb+", \"lng\":"+coord.mb
                                   println el.address+" "+coord.lb+" "+coord.mb

                               }
                               ff3 << "}"
                               if(ind < b_count -1)ff3 << ","
                               ff3 << "\n"
                       }
                       ff3 <<  "]}"
                       if(index < p_count -1)ff3 << ","
                       ff3 << "\n"
                   }
            }
        ff3 << "]\n"

}
