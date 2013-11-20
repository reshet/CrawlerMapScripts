
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */


  File f = new File("/var/www/MSS/geocode_addresses3.csv")
  File f3 = new File("/var/www/MSS/addresses_geo3.json")
  f3.createNewFile()

def builder = new groovy.json.JsonBuilder()
def codes = []
f3.withWriter {
    ff3->
        //ff3 << "[\n"

        f.withReader("UTF8", {
            ff2->
                int i = 0
                int succes = 0
                ff2.eachLine
                {
                    ln->

                            def lnarr = ln.split(";")
                            if(lnarr.length > 2){
                                def adr = lnarr[0]
                                def var_code = lnarr[1]
                                def task_id = lnarr[2]
                                def int_id = lnarr[3]
                                def address = "Киев "+adr
                                address = address.replaceAll("\"","")

                                //ff3 << "{\"address\":\""+address+"\", \"title\":\""+title+"\""
                                print i +" "
                                String geoc = GoogleGeocoderCached.geocode(address,true,true)
                                if(geoc != "no definite geocode"){
                                    def coor = GoogleGeocoderCached.parseJSON(geoc)
                                    if(coor!= null && coor.lb!= null && coor.mb!= null){
                                        def formatted_addr = coor.address;
                                        def loc_type = "no";
                                        if(coor.loc_type!=null)loc_type = coor.loc_type;
                                        //if(formatted_addr!=null)address = formatted_addr;
                                        //ff3 << ", \"lat\":"+coor.lb+", \"lng\":"+coor.mb
                                        codes.push(["address":address,"geocoded_address":formatted_addr,"var_code":var_code,"task_id":task_id,"int_id":int_id,"lat":coor.lb,"lng":coor.mb,"loc_type":loc_type])
                                        //println address+" "+coor.lb+" "+coor.mb
                                        succes++
                                    }
                                }else{
                                    println address+" "+geoc;
                                }
                                //ff3 << "},\n"
                            }
                           i++
                        }

                println "Total addresses: "+i;
                println "Succesfully geocoded addresses: "+succes;
                println "Coverage ratio: "+succes/i*100+"%";


        })
        builder(codes)
        ff3 << builder.toPrettyString()







        //ff3 << "]\n"

}
