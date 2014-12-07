
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */


  File f = new File("/home/reshet/Downloads/CATI_2013.10.31_FINAL.csv")
  File f2 = new File("/home/reshet/Downloads/Kyiv_codes_streets.csv")
  File f3 = new File("/home/reshet/Downloads/CATI_2013.10.31_FINAL_geocoded.csv")
  f3.createNewFile()

final File logfile = new File("/var/www/MSS/geocoding_cati.log");
if(!logfile.exists())logfile.createNewFile();
//def builder = new groovy.json.JsonBuilder()
def kapi_codes = [:]
def kapi_names = [:]
f2.withReader {
    ff->
        int i = 0;
        ff.eachLine {
            ln ->
                if(i > 0){
                    def arr = ln.split(";");
                    //println "kati "+arr[0]+" kapi "+arr[1]
                    kapi_codes[arr[0]] = arr[1]
                    kapi_names[arr[1]] = arr[4]

                }
                i++
        }
}
//println kapi_names;
//return

boolean hasGeocode(String str){
    //println "hasgeocode: "+str;
    if(str == null) return false;
    if(str.size()==0) return false;
    //println "hasgeocode-checked";

    final int count = 6;
    int count_my = 0;

    //println "hasgeocode-start";
    for(int i = 0; i < str.length();i++)
    {
      //  println "hasgeocode-cycle "+i;

        Character c = str.charAt(i);
        if (c.isDigit())count_my++;
        if(count_my>= count)return true;
    }
    //println "hasgeocode-end";

    if(count_my>= count)return true;
    return false;
}
def parseGeocode(String str){
   def ans = []
    if(str.contains(",")){
        def arr = str.split(",");
        if(arr.size()==2){
            ans[0] = arr[0].trim();
            ans[1] = arr[1].trim();
        }

    }
   else if(str.contains(" ")){
       def arr = str.split(" ");
        if(arr.size() == 2){
            ans[0] = arr[0];
            ans[1] = arr[1];
        }

   }

   return ans;
}

def geocodeAddress(String str){

    String geoc = GoogleGeocoderCached.geocode(str,true,true)
    if(geoc != "no definite geocode"){
        def coor = GoogleGeocoderCached.parseJSON(geoc)
        if(coor!= null && coor.lb!= null && coor.mb!= null){
            def formatted_addr = coor.address;
            def loc_type = "no";
            if(coor.loc_type!=null)loc_type = coor.loc_type;
            //if(formatted_addr!=null)address = formatted_addr;
            //ff3 << ", \"lat\":"+coor.lb+", \"lng\":"+coor.mb

            //println address+" "+coor.lb+" "+coor.mb
            return [coor.lb,coor.mb]
        }
    }else{
        final File logfile = new File("/var/www/MSS/geocoding_cati.log");
        if(!logfile.exists())logfile.createNewFile();
        logfile.withWriterAppend {
            lf ->

                lf << new Date().getDateTimeString() +" __ " + "geocoding: "+geoc+"\n"
        }
    }
    return []
}


logfile.withWriterAppend {
    lf ->

        lf << new Date().getDateTimeString() +" __ " + "Start geocoding cati"+"\n"
}
    f3.withWriter {
        ff3->
        //ff3 << "[\n"

        f.withReader("UTF8", {
            ff2->
                int i = 0
                int succes = 0
                int address_live_comm_column = 0;
                int address_build_comm_live_column = 0;
                int address_live_column = 0;
                int address_build_live_column = 0;

                int address_start_column = 0;
                int address_start_street_column = 0;
                int address_start_build_column = 0;
                int address_start_lat_column = 0;
                int address_start_lng_column = 0;

                int address_start_comm_column = 0;
                int address_start_street_comm_column = 0;
                int address_start_build_comm_column = 0;
                int address_start_lat_comm_column = 0;
                int address_start_lng_comm_column = 0;

                int address_end_column = 0;
                int address_end_street_column = 0;
                int address_end_build_column = 0;
                int address_end_lat_column = 0;
                int address_end_lng_column = 0;

                int address_end_comm_column = 0;
                int address_end_street_comm_column = 0;
                int address_end_build_comm_column = 0;
                int address_end_lat_comm_column = 0;
                int address_end_lng_comm_column = 0;



                ff2.eachLine
                {
                    ln->

                            def lnarr = ln.split(";")
                            if(lnarr.length > 2){
                                if(i == 0){
                                   for(int j = 0; j < lnarr.size();j++){
                                       if(lnarr[j] == "V26"){
                                           address_live_column= j;
                                       }
                                       if(lnarr[j] == "V27"){
                                           address_build_live_column= j;
                                       }
                                       if(lnarr[j] == "V33"){
                                           address_start_column= j;
                                       }
                                       if(lnarr[j] == "V34"){
                                           address_start_street_column= j;
                                       }
                                       if(lnarr[j] == "V35"){
                                           address_start_build_column= j;
                                       }
                                       if(lnarr[j] == "V36"){
                                           address_start_lat_column= j;
                                       }
                                       if(lnarr[j] == "V37"){
                                           address_start_lng_column= j;
                                       }

                                       if(lnarr[j] == "v10"){
                                           address_live_comm_column= j;
                                       }
                                       if(lnarr[j] == "v11"){
                                           address_build_comm_live_column = j;
                                       }
                                       if(lnarr[j] == "v14"){
                                           address_start_comm_column= j;
                                       }
                                       if(lnarr[j] == "v15"){
                                           address_start_street_comm_column= j;
                                       }
                                       if(lnarr[j] == "v16"){
                                           address_start_build_comm_column= j;
                                       }
                                       if(lnarr[j] == "v17"){
                                           address_start_lat_comm_column= j;
                                       }
                                       if(lnarr[j] == "v18"){
                                           address_start_lng_comm_column= j;
                                       }

                                       if(lnarr[j] == "V40"){
                                           address_end_column= j;
                                       }
                                       if(lnarr[j] == "V41"){
                                           address_end_street_column= j;
                                       }
                                       if(lnarr[j] == "V42"){
                                           address_end_build_column= j;
                                       }
                                       if(lnarr[j] == "V43"){
                                           address_end_lat_column= j;
                                       }
                                       if(lnarr[j] == "V44"){
                                           address_end_lng_column= j;
                                       }

                                       if(lnarr[j] == "v28"){
                                           address_end_comm_column= j;
                                       }
                                       if(lnarr[j] == "v29"){
                                           address_end_street_comm_column= j;
                                       }
                                       if(lnarr[j] == "v80"){
                                           address_end_build_comm_column= j;
                                       }
                                       if(lnarr[j] == "v81"){
                                           address_end_lat_comm_column= j;
                                       }
                                       if(lnarr[j] == "v82"){
                                           address_end_lng_comm_column= j;
                                       }
                                   }
                                }
                                else
                                {
                                    //println "LINE "+i;
                                    logfile.withWriterAppend {
                                        lf ->
                                            lf << new Date().getDateTimeString() +" __ " + "LINE "+i+"\n"


                                        def adr_l = lnarr[address_live_column]
                                        def home_geo = [];
                                        if(adr_l == "0") {
                                            def street_co = lnarr[address_live_comm_column]
                                            lf << new Date().getDateTimeString() +" __ " + "ZERO LIVE STREET:"+  street_co+"\n"
                                            //println "ZERO LIVE STREET:"+  street_co;
                                            if(hasGeocode(street_co)){
                                                //println "FOUND GEOCODE!";
                                                home_geo = parseGeocode(street_co);
                                            }else{
                                                home_geo = geocodeAddress(street_co);
                                            }


                                        }else{
                                            def street_code = kapi_codes[adr_l];
                                            lnarr[address_live_column] = street_code;
                                            def street_name = kapi_names[street_code];
                                            def live_home = lnarr[address_build_comm_live_column]
                                            //println "HOME: "+street_name+ " "+live_home;
                                            home_geo = geocodeAddress("Київ "+street_name+ " "+live_home);
                                            lf << new Date().getDateTimeString() +" __ " + "HOME_GEOCODE: "+ home_geo+"\n"

                                            //println "HOME_GEOCODE: "+ home_geo;
                                        }
                                        //println adr_l;
                                        //println "here"



                                        def adr_s =  lnarr[address_start_column]
                                        switch(adr_s){
                                            case "-99":
                                                break;
                                            case "1":
                                                //buliding, suppose on living street
                                               /* def has_start_home = lnarr[address_start_build_column]
                                                //println "START HOME: "+has_start_home;
                                                if(has_start_home == 1){

                                                }*/
                                                //def start_home = lnarr[address_start_build_comm_column]
                                                //println "START FROM HOME: ";
                                                if(home_geo[0]!=null && home_geo[1]!=null){
                                                    lnarr[address_start_lat_column] = 1;
                                                    lnarr[address_start_lat_column] = 1;
                                                    lnarr[address_start_lat_comm_column] = home_geo[0];
                                                    lnarr[address_start_lng_comm_column] = home_geo[1];
                                                }

                                                break;
                                            case "2":
                                                //address
                                                def geo2 = [];
                                                def start_street = lnarr[address_start_street_column];
                                                if(start_street == "0"){
                                                    def street_comm = lnarr[address_start_street_comm_column]

                                                    //println "ZERO STREET START:"+  street_comm;
                                                    lf << new Date().getDateTimeString() +" __ " + "ZERO STREET START:"+  street_comm+"\n"
                                                    if(hasGeocode(street_comm)){
                                                        //println "FOUND GEOCODE!"
                                                        geo2 = parseGeocode(street_comm);
                                                    }else{
                                                        geo2 = geocodeAddress(street_comm);

                                                    }
                                                }else{
                                                    def street_code2 = kapi_codes[start_street];
                                                    lnarr[address_start_street_column] = street_code2;
                                                    def street_name2 = kapi_names[street_code2];

                                                    def has_start_home = lnarr[address_start_build_column]
                                                    //println "START HOME: "+has_start_home;
                                                    if(has_start_home == "1"){
                                                        def start_home = lnarr[address_start_build_comm_column]
                                                        //println "START ADDRESS: " + street_name2 +" "+ start_home;
                                                        lf << new Date().getDateTimeString() +" __ " + "START_ADDRESS: " + street_name2 +" "+ start_home+"\n"

                                                        geo2 = geocodeAddress("Київ "+street_name2+ " "+start_home);
                                                        lf << new Date().getDateTimeString() +" __ " + "START_GEOCODE: "+ geo2+"\n"

                                                        //println "START HOME: "+start_home;
                                                    }

                                                }
                                                if(geo2[0]!=null && geo2[1]!=null){
                                                    lnarr[address_start_lat_column] = 1;
                                                    lnarr[address_start_lat_column] = 1;
                                                    lnarr[address_start_lat_comm_column] = geo2[0];
                                                    lnarr[address_start_lng_comm_column] = geo2[1];
                                                }
                                                break;
                                            /*case "3":
                                                //coordinates
                                                def hasCoordLat = lnarr[address_start_lat_column]
                                                def hasCoordLng = lnarr[address_start_lng_column]
                                                if(hasCoordLat == "1" && hasCoordLng == "1"){
                                                    def coordLat = lnarr[address_start_lat_comm_column]
                                                    def coordLng = lnarr[address_start_lng_comm_column]
                                                    println "COORDS: "+coordLat+";"+coordLng;
                                                }
                                                break;*/

                                        }

                                        def adr_e =  lnarr[address_end_column]
                                        switch(adr_e){
                                            case "-99":
                                                break;
                                            case "1":
                                                //buliding, suppose on living street
                                                /* def has_start_home = lnarr[address_start_build_column]
                                                 //println "START HOME: "+has_start_home;
                                                 if(has_start_home == 1){

                                                 }*/
                                                //def start_home = lnarr[address_start_build_comm_column]
                                                //println "END TO HOME: ";
                                                if(home_geo[0]!=null && home_geo[1]!=null){
                                                    lnarr[address_end_lat_column] = 1;
                                                    lnarr[address_end_lat_column] = 1;
                                                    lnarr[address_end_lat_comm_column] = home_geo[0];
                                                    lnarr[address_end_lng_comm_column] = home_geo[1];
                                                }

                                                break;
                                            case "2":
                                                //address
                                                def geo3 = [];
                                                def end_street = lnarr[address_end_street_column];
                                                if(end_street == "0"){
                                                    def street_comm = lnarr[address_end_street_comm_column]

                                                    //println "ZERO STREET END:"+  street_comm;
                                                    lf << new Date().getDateTimeString() +" __ " + "ZERO STREET END:"+  street_comm+"\n"
                                                    if(hasGeocode(street_comm)){
                                                        //println "FOUND GEOCODE!"
                                                        geo3 = parseGeocode(street_comm);
                                                    }else{
                                                        geo3 = geocodeAddress(street_comm);
                                                    }
                                                } else{
                                                    def street_code3 = kapi_codes[end_street];
                                                    lnarr[address_end_street_column] = street_code3;
                                                    def street_name3 = kapi_names[street_code3];

                                                    def has_end_home = lnarr[address_end_build_column]
                                                    //println "START HOME: "+has_start_home;
                                                    if(has_end_home == "1"){
                                                        def end_home = lnarr[address_end_build_comm_column]
                                                        //println "END ADDRESS: " + street_name3 +" "+ end_home;
                                                        geo3 = geocodeAddress("Київ "+street_name3+ " "+end_home);
                                                        lf << new Date().getDateTimeString() +" __ " + "END_GEOCODE: "+ geo3+"\n"
                                                        //println "START HOME: "+start_home;
                                                    }
                                                }
                                                if(geo3[0]!=null && geo3[1]!=null){
                                                    lnarr[address_end_lat_column] = 1;
                                                    lnarr[address_end_lat_column] = 1;
                                                    lnarr[address_end_lat_comm_column] = geo3[0];
                                                    lnarr[address_end_lng_comm_column] = geo3[1];
                                                }
                                                break;
                                            /*case "3":
                                                //coordinates
                                                def hasCoordLat = lnarr[address_end_lat_column]
                                                def hasCoordLng = lnarr[address_end_lng_column]
                                                if(hasCoordLat == "1" && hasCoordLng == "1"){
                                                    def coordLat = lnarr[address_end_lat_comm_column]
                                                    def coordLng = lnarr[address_end_lng_comm_column]
                                                    println "COORDS: "+coordLat+";"+coordLng;
                                                }
                                                break;
    */
                                        }
                                    }
                                    /* def var_code = lnarr[1]
                                     def task_id = lnarr[2]
                                     def int_id = lnarr[3]
                                     def address = "Киев "+adr
                                     address = address.replaceAll("\"","")*/

                                    //ff3 << "{\"address\":\""+address+"\", \"title\":\""+title+"\""
                                }

                                //String geoc = GoogleGeocoderCached.geocode(address,true,true)
                               /* if(geoc != "no definite geocode"){
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
                                }*/
                                //ff3 << "},\n"
                            }
                           i++
                            ff3 << lnarr.join(";")+"\n";
                        }

               /* println "Total addresses: "+i;
                println "Succesfully geocoded addresses: "+succes;
                println "Coverage ratio: "+succes/i*100+"%";*/


        })
       /* builder(codes)*/
       // ff3 << builder.toPrettyString()



            logfile.withWriterAppend {
                lf ->
                    lf << new Date().getDateTimeString() +" __ " + "END geocoding cati"+"\n"
            }

        //ff3 << "]\n"

}
