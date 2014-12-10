
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */


  def filename = args[0]//"25KMIS1-1"
  def addressVars = ["56", "57", "199","200","342","343","485"
          ,"486"
          ,"628"
          ,"629"
          ,"771"
          ,"772"
          ,"914"
          ,"915"
          ,"1057","1058","1200"
          ,"1201"
          ,"1342"
          ,"1376","1377","1519","1520","1662","1663","1805","1806","1948","1949"
          ,"2091"
          ,"2092"
          ,"2234"
          ,"2235"
          ,"2377"
          ,"2378"
          ,"2520"
          ,"2521"
          ,"2662"
  ]
  //def fileExt = ".txc";
  def defaultPath = "/home/geocode/";
  File f = new File(defaultPath + filename)
  File f3 = new File(defaultPath + "coded_" + filename)
  f3.createNewFile()

final File logfile = new File("/home/geocode/geocoding_cati.log");
if(!logfile.exists())logfile.createNewFile();
//def builder = new groovy.json.JsonBuilder()

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

def geocodeAddress(String str, String region){

    String geoc = GoogleGeocoderCached.geocode(str,true,true, region)
    //println geoc
    if (geoc != "no definite geocode") {

        def coor = GoogleGeocoderCached.parseJSON(geoc)
        if(coor!= null && coor.lb!= null && coor.mb!= null && coor["loc_type"] != "APPROXIMATE"){
            def formatted_addr = coor.address;
            def loc_type = "no";
            if(coor.loc_type!=null)loc_type = coor.loc_type;
            //if(formatted_addr!=null)address = formatted_addr;
            //ff3 << ", \"lat\":"+coor.lb+", \"lng\":"+coor.mb

            //println address+" "+coor.lb+" "+coor.mb
            return [coor.lb,coor.mb, coor.address]
        }
    } else {
        final File logfile = new File("/home/geocode/geocoding_cati.log");
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
        lf << new Date().getDateTimeString() +" __ " + "Start geocoding cati from OSA"+"\n"
}

String osaEncoding = "Cp866"

f3.withWriter(
        osaEncoding,
        {
    ff3->
    f.withReader(
            osaEncoding,
            {
        ff2->
            int i = 0
            ff2.eachLine {
                ln->
                    def line = ln;
                    if (ln.contains(";") && !ln.startsWith(";")) {
                        def lnarr = ln.split(";")
                        if (ln.length() >= 2 ) {
                            def items = lnarr[0].trim().split(" ");
                            def var = items[items.length - 1]
                            if (var in addressVars) {
                                def address = lnarr[lnarr.length-1]
                                if (address.length() > 0) {
                                    //address = new String(address.getBytes(osaEncoding), "UTF-8")
                                    logfile.withWriterAppend {
                                        lf ->
                                            lf << new Date().getDateTimeString() +" __ " + "LINE " + i +" address = " + address + "\n"
                                            println address
                                            home_geo = geocodeAddress(address, "місто Київ");
                                            if (home_geo.size() == 0) {
                                                home_geo = geocodeAddress(address, "Київська область")
                                            };
                                            if (home_geo.size() > 0) {
                                                line += " # " + home_geo.get(0) + " # " + home_geo.get(1) + " # " + new String(home_geo.get(2).getBytes(), osaEncoding)
                                            } else {
                                                line += " # NOT_GEOCODED"
                                            }
                                            lf << new Date().getDateTimeString() +" __ " + "HOME_GEOCODE: "+ home_geo+"\n"
                                    }
                                }
                            }
                        }
                    }
                i++
                ff3 << line +"\n";
            }
    })

    logfile.withWriterAppend {
        lf ->
            lf << new Date().getDateTimeString() +" __ " + "END geocoding cati OSA file"+"\n"
    }
})
