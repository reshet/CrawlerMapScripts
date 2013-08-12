
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  //File f = new File("/var/www/InteractiveMaps/js/addresses_merged.json")
  File f2 = new File("/home/reshet/cvkkiev/data_units_by_district.csv")
  File f = new File("/home/reshet/cvkkiev/prec_chern.csv")
  File f4 = new File("/var/www/InteractiveMaps/js/addresses_merged_data.json")

File f3 = new File("/var/www/InteractiveMaps/js/mer2008_data_byunits.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()
def units = [:]
def builder = new groovy.json.JsonBuilder()

f2.withReader {
    ff2->
        String curr_district = ""
        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                //println lnarr;
                if(lnarr[0].length()>0) {
                    curr_district = lnarr[0];
                    println curr_district;
                }
                if(lnarr[2]!="Total"){
                   def id = lnarr[2].replaceAll(",",".");
                   int unit_id = Double.parseDouble(id);
                   //println lnarr[2];
                   units[unit_id] = [district:curr_district];
                }
     }
}
f.withReader {
    ff2->
        String curr_district = ""
        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                def perc = lnarr[3];
                def unit = lnarr[0];
                int unit_id = Integer.parseInt(unit);
                if(units.containsKey(unit_id)){
                    println unit_id+" "+perc;
                    units[unit_id]["perc_chern"] = perc;
                }
        }
}

def result = slurper.parseText(f4.getText());
result.each{

    def unit_id = it.unit_id
    String region_ukr = it.tvo_name
    if(units.containsKey(unit_id)){
        def chern_unit = units[unit_id]["district"]
        if((chern_unit == "Голосеевский" && region_ukr.toLowerCase().contains("дарницький"))
        || (chern_unit == "Дарницкий" && region_ukr.toLowerCase().contains("дарницький"))
        || (chern_unit == "Деснянский" && region_ukr.toLowerCase().contains("деснянський"))
        || (chern_unit == "Днепровский" && region_ukr.toLowerCase().contains("дніпровський"))
        || (chern_unit == "Оболонский" && region_ukr.toLowerCase().contains("оболонський"))
        || (chern_unit == "Подольский" && region_ukr.toLowerCase().contains("оболонський"))
        || (chern_unit == "Печерский" && region_ukr.toLowerCase().contains("шевченківський"))
        || (chern_unit == "Шевченковский" && region_ukr.toLowerCase().contains("шевченківський"))
        || (chern_unit == "Святошинский" && region_ukr.toLowerCase().contains("святошинський"))
        || (chern_unit == "Соломенский" && region_ukr.toLowerCase().contains("солом'янський"))
        ){
            units[unit_id]["lat"] = it.lat;
            units[unit_id]["lng"] = it.lng;
            units[unit_id]["address"] = it.address;
            units[unit_id]["tvo_unit_id"] = it.title;
      }
    }
}



f3.withWriter {
    ff3->
        builder(units)
        ff3 << builder.toPrettyString()
}
