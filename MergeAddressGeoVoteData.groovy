
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  File f = new File("/var/www/InteractiveMaps/js/addresses_merged.json")
  File f2 = new File("/home/reshet/cvkkiev/cvk_data_2007_kiev.csv")
  File f3 = new File("/var/www/InteractiveMaps/js/addresses_merged_data.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def vote_data = [:]
def result = slurper.parseText(f.getText())

f2.withReader {
    ff2->
        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                if(lnarr.length > 10){
                    def tvo_id = lnarr[0]
                    def tvo_name = lnarr[1]
                    def unit_id = lnarr[2]
                    unit_id = unit_id.replaceAll(" ","")
                    def live = lnarr[3]
                    def took_part = lnarr[4]
                    def votes_pr = lnarr[9]
                    def votes_cpu = lnarr[6]
                    def votes_but = lnarr[13]
                    def id = "ТВО №"+tvo_id+" ВД №"+unit_id;
                    println id
                    vote_data[id] = ["tvo_id": tvo_id,"unit_id": unit_id,"tvo_name":tvo_name,
                                     "live":live,"took_part":took_part,
                                     "votes_pr":votes_pr,"votes_cpu":votes_cpu,"votes_but":votes_but]

                }


        }
}

f3.withWriter {
    ff3->
        ff3 << "[\n"
            result.each{
                elem->
                    println elem.title
                    if(vote_data.containsKey(elem.title)){
                        println vote_data[elem.title]
                        def tvo_id = vote_data[elem.title]["tvo_id"]
                        def tvo_name = vote_data[elem.title]["tvo_name"]
                        def unit_id = vote_data[elem.title]["unit_id"]
                        def live = vote_data[elem.title]["live"]
                        def took_part = vote_data[elem.title]["took_part"]
                        def votes_pr = vote_data[elem.title]["votes_pr"]
                        def votes_cpu = vote_data[elem.title]["votes_cpu"]
                        def votes_but = vote_data[elem.title]["votes_but"]

                        ff3 << "{\"address\":\""+elem.address+"\", \"title\":\""+elem.title+"\", \"lat\":"+elem.lat+", \"lng\":"+elem.lng+
                                ", \"tvo_id\":"+tvo_id+", \"tvo_name\":\""+tvo_name+"\", \"unit_id\":"+unit_id+
                                ", \"live\":"+live+", \"took_part\":"+took_part+
                                ", \"votes_pr\":"+votes_pr+", \"votes_cpu\":"+votes_cpu+", \"votes_but\":"+votes_but+
                               "},\n"
                    }

            }
        ff3 << "]\n"

}
