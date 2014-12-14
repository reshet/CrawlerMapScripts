import groovy.io.FileType
import groovy.transform.Field


@Field addressVars = ["56", "57", "199","200","342","343","485"
        ,"486","628","629","771","772","914","915","1057","1058","1200","1201","1342"
        ,"1376","1377","1519","1520","1662","1663","1805","1806","1948","1949"
        ,"2091","2092","2234","2235","2377","2378","2520","2521","2662"]

@Field uploadsPath = "/var/www/geocode/uploads"
@Field codedPath = "/var/www/geocode/coded"
@Field downloadsPath = "/var/www/geocode/downloads"



def rotate(filename) {

    def Matrix = [:]

    File f = new File(codedPath + "/coded_" + filename)
    File f3 = new File(downloadsPath + "/rotated_coded_" + filename + ".csv" )
    f3.createNewFile()

    String osaEncoding = "Cp866"

    f.withReader(osaEncoding, {
        ff2->
            ff2.eachLine {
                ln->
                    def line = ln;
                    if (ln.contains(";") && !ln.startsWith(";") && ln.contains(" # ")) {
                        def lnarr = ln.split(";")
                        if (ln.length() >= 2 ) {
                            def items = lnarr[0].trim().split(" ");
                            def var = items[items.length - 1]
                            def Case = items[1]
                            if (var in addressVars) {
                                def address_coded = lnarr[lnarr.length-1]
                                def address_items = address_coded.split(" # ")
                                if (address_items.length == 4) {
                                    if (!Matrix.containsKey(Case)) {
                                        Matrix[Case] = [:]
                                    }
                                    caseMap = Matrix[Case]
                                    caseMap["case"] = Case
                                    caseMap[var] = address_items[0]
                                    caseMap[var + "_lat"] = address_items[1]
                                    caseMap[var + "_lng"] = address_items[2]
                                    caseMap[var + "_g_adr"] = address_items[3]
                                    Matrix[Case] = caseMap;
                                }
                            }
                        }
                    }
            }
    })

    f3.withWriter(osaEncoding, {
        ff3 ->
            def header = "Case;";
            addressVars.each { elem ->
                header += elem + ";" + elem + "_lat;" + elem + "_lng;" + elem + "_g_adr;"
            }
            ff3 << header +"\n";

            Matrix.each { Case, Vars ->
                def line = new StringBuilder(Case + ";")
                addressVars.each { elem ->
                    if (Vars.containsKey(elem)) {
                        line.append(Vars[elem])
                    }
                    line.append(";")
                    if (Vars.containsKey(elem + "_lat")) {
                        line.append(Vars[elem + "_lat"])
                    }
                    line.append(";")
                    if (Vars.containsKey(elem + "_lng")) {
                        line.append(Vars[elem + "_lng"])
                    }
                    line.append(";")
                    if (Vars.containsKey(elem + "_g_adr")) {
                        line.append(Vars[elem + "_g_adr"])
                    }
                    line.append(";")
                }
                ff3 << line.toString() + "\n";
            }
    })
}

def geocodeAddress(String str, String region, String bounds){

    String geoc = GoogleGeocoderCached.geocode(str,true,true, region, bounds)
    println geoc
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
        final File logfile = new File("/var/www/geocode/geocoding.log");
        if(!logfile.exists())logfile.createNewFile();
        logfile.withWriterAppend {
            lf ->
                lf << new Date().getDateTimeString() +" __ " + "geocoding: "+geoc+"\n"
        }
    }
    return []
}

def geocode(filename) {
    File f = new File(uploadsPath + "/" + filename)
    File f3 = new File(codedPath + "/coded_" + filename)
    f3.createNewFile()

    final File logfile = new File("/var/www/geocode/geocoding.log");
    if(!logfile.exists())logfile.createNewFile();
    logfile.withWriterAppend {
        lf ->
            lf << new Date().getDateTimeString() +" __ " + "Start geocoding cati from OSA"+"\n"
    }

    String osaEncoding = "Cp866"
    def addressCount = 0;
    def geocodedAddressCount = 0;
    f3.withWriter(osaEncoding, {
        ff3->
            f.withReader(osaEncoding,{
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
                                            addressCount++
                                            logfile.withWriterAppend {
                                                lf ->
                                                    lf << new Date().getDateTimeString() +" __ " + "LINE " + i +" address = " + address + "\n"
                                                    println address
                                                    def kiev_bounds = "50.193073,29.929461|50.688971,31.114612"
                                                    home_geo = geocodeAddress(address, "місто Київ", kiev_bounds);
                                                    if (home_geo.size() == 0) {
                                                        home_geo = geocodeAddress(address, "Київська область", kiev_bounds)
                                                    };
                                                    if (home_geo.size() > 0) {
                                                        line += " # " + home_geo.get(0) + " # " + home_geo.get(1) + " # " + home_geo.get(2)
                                                        geocodedAddressCount++
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
                    lf << new Date().getDateTimeString() +" __ " + "TOTAL ADDRESSES = " + addressCount + ", GEOCODED ADDRESSES = " + geocodedAddressCount+ "\n"
                    lf << new Date().getDateTimeString() +" __ " + "SUCCESS RATIO: " + (geocodedAddressCount * 1.0 / addressCount) * 100 + "%.\n"
            }
    })
}



    def uploadDir = new File(uploadsPath)
    def codedDir = new File(codedPath)

    def uploadFiles = []
    def codedFiles = []

    while(true) {
        uploadDir.eachFileRecurse (FileType.FILES) { file ->
            if (!uploadFiles.contains(file.name)) {
                uploadFiles << file.name
            }
        }
        codedDir.eachFileRecurse (FileType.FILES) { file ->
            if (!codedFiles.contains(file.name)) {
                codedFiles << file.name
            }
        }
        uploadFiles.each { filename ->
            if (!codedFiles.contains("coded_"+ filename)) {
               println "Start to geocode " + filename
               geocode(filename)
               rotate(filename)
            }
        }
        sleep(10000)
    }