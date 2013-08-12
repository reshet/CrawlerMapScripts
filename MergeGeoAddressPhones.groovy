
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */


  File f = new File("/home/reshet/cvkkiev/Kiev_573_test.csv")
  File f3 = new File("/var/www/InteractiveMaps/js/addresses_merged_phones_geo.json")
  f3.createNewFile()



f3.withWriter {
    ff3->
        ff3 << "[\n"

        f.withReader("CP1251", {
            ff2->
                int i = 0

                ff2.eachLine{
                    ln->
                        if(i > 0){
                            def lnarr = ln.split(";")
                            if(lnarr.length > 2){
                                def phone = lnarr[0]
                                def street = lnarr[1]
                                def house = lnarr[3]
                                def name = lnarr[2]
                                def address = "Киев "+street+" "+house
                                def title = phone+"; "+name
                                address = address.replaceAll("\"","")
                                title = title.replaceAll("\"","")

                                ff3 << "{\"address\":\""+address+"\", \"title\":\""+title+"\""
                                String geoc = GoogleGeocoderCached.geocode(address,true)



                                def coor = GoogleGeocoderCached.parseJSON(geoc)
                                    if(coor!= null && coor.lb!= null && coor.mb!= null){
                                        ff3 << ", \"lat\":"+coor.lb+", \"lng\":"+coor.mb
                                        println address+" "+coor.lb+" "+coor.mb
                                    }
                                ff3 << "},\n"
                            }
                        }

                    i++

                }
        })





        ff3 << "]\n"

}
