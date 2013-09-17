
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  //File f = new File("/var/www/InteractiveMaps/js/addresses_merged.json")
  File f2 = new File("/home/reshet/cvkkiev/Final_Working_2012.csv")

  File f3 = new File("/var/www/InteractiveMaps/js/ukraine_tigipko_udar.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def candidates = []
def builder = new groovy.json.JsonBuilder()

f2.withReader("CP1251") {
    ff2->
        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                def id = lnarr[0]
                def key = lnarr[1]
                def admin = lnarr[5]

                def street = lnarr[3]
                def res = GoogleGeocoderCached.geocode(street,true);
                def coor = GoogleGeocoderCached.parseJSON(res)
                def district = ["address":street,"id":id,"key":key,"admin":admin]
                if(coor!= null && coor.lb!= null && coor.mb!= null){
                    district["lat"]=coor.lb;
                    district["lng"]=coor.mb;
                }
                def tigipko = lnarr[62]
                def udar = lnarr[64]

                district["tigipko"]=tigipko
                district["udar"]=udar
                candidates.add(district)
       }
}


f3.withWriter {
    ff3->
        builder(candidates)
        ff3 << builder.toPrettyString()
}
