
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  //File f = new File("/var/www/InteractiveMaps/js/addresses_merged.json")
  File f2 = new File("/home/reshet/cvkkiev/exitPollPoroshPopov.csv")

  File f3 = new File("/var/www/InteractiveMaps/js/mer2008_data_exitpoll.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def candidates = ["ПОПОВ":["votes":[]],"ПОРОШЕНКО":["votes":[]]]
def builder = new groovy.json.JsonBuilder()

f2.withReader("CP1251") {
    ff2->
        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                def street = lnarr[0]
                street = "Киев, "+street;
                def res = GoogleGeocoderCached.geocode(street,true);
                def coor = GoogleGeocoderCached.parseJSON(res)
                def vote = ["address":street]
                if(coor!= null && coor.lb!= null && coor.mb!= null){
                    vote["lat"]=coor.lb;
                    vote["lng"]=coor.mb;
                }
                def hasVote1 = lnarr[1];
                def hasVote2 = lnarr[2];
                if(hasVote1 == "1") candidates["ПОПОВ"]["votes"].add(vote);
                if(hasVote2 == "1") candidates["ПОРОШЕНКО"]["votes"].add(vote);
     }
}


f3.withWriter {
    ff3->
        builder(candidates)
        ff3 << builder.toPrettyString()
}
