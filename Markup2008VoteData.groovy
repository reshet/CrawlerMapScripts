
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  //File f = new File("/var/www/InteractiveMaps/js/addresses_merged.json")
  File f2 = new File("/home/reshet/cvkkiev/mer2008.csv")
  File f3 = new File("/var/www/InteractiveMaps/js/mer2008_data.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def regions = [:]
def builder = new groovy.json.JsonBuilder()

f2.withReader {
    ff2->
        String curr_district = ""
        def curr_vote_data = [:]
        def curr_vote_data_arr = []

        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                //println lnarr;
                if(lnarr[0].length()>0) {
                    if(curr_district != ""){
                        curr_vote_data["data"]= curr_vote_data_arr;
                        //println curr_vote_data.toString()
                        curr_vote_data_arr = []
                        regions[curr_district] = curr_vote_data;
                        curr_vote_data = [:]

                    }
                    curr_district = lnarr[0]
                }
                if(lnarr.length > 5){
                  def candidate = lnarr[2]
                  def freq = lnarr[3]
                  def perc = lnarr[4]
                  def v_perc = lnarr[5]
                  def cum_perc = lnarr[5]
                  curr_vote_data_arr.add([candidate:candidate,freq:freq,perc:perc,v_perc:v_perc,cum_perc:cum_perc])
                }else{
                   if(lnarr[2] == "Total"){
                       curr_vote_data["Total_Valid"] = lnarr[3]
                       curr_vote_data["Total_Valid_Perc"] = lnarr[4]

                   }else
                   if(lnarr[1] == "Missing"){
                       curr_vote_data["Missing"] = lnarr[3]
                       curr_vote_data["Missing_Perc"] = lnarr[4]
                   }else
                   if(lnarr[1] == "Total"){
                       curr_vote_data["Total"] = lnarr[3]
                   }
                }
                /*if(lnarr.length > 3){
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

                }*/
        }
}

f3.withWriter {
    ff3->
        builder(regions)
        ff3 << builder.toPrettyString()

}
