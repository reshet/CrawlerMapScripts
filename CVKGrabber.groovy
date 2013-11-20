
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/5/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/5/13
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */

@Grab(
        group='net.sourceforge.jexcelapi',
        module='jxl',
        version='2.6.12'
)
@Grab(group='org.codehaus.gpars', module='gpars', version='1.1.0')
import groovyx.gpars.GParsPool
import jxl.Workbook
import jxl.write.*

import javax.imageio.ImageIO
@Grab(
        group = 'net.sourceforge.jexcelapi',
        module = 'jxl',
        version = '2.6.12'
) @Grab(group = 'org.codehaus.gpars', module = 'gpars', version = '1.1.0')
import java.awt.image.BufferedImage

class CVKGrabber {
    public static final File logfile = new File("/home/reshet/cvkkiev/grabbing.log")
    public static File f_addresses = new File("/home/reshet/cvkkiev/addresses.csv")
    public static File f_addresses_json = new File("/home/reshet/cvkkiev/addresses.json")

    public static void writeStops(File f, List stops){
        TreeSet unique_stops = stops.unique().sort()
        f.withWriter {
            fl->
                unique_stops.eachWithIndex {
                    elem,index ->
                    fl << index+"; "+elem+"\n"
                }
        }
    }
    public static void main(def args){
        //println "Start grabbing routes"

        long startTime = System.currentTimeMillis()
        if(!logfile.exists())logfile.createNewFile();
        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Start votes grabbing"+"\n"}
        //http://www.cvk.gov.ua/pls/vnd2007/W6P029?PT001F01=600&PID100=80&pf7331=218
        def text = ("http://www.cvk.gov.ua/pls/vnd2007/W6P023?PT001F01=600&PID100=80&rdat=28.03.2013").toURL().getText("CP1251");
        //println text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)
        def xml = slurper.parseText(text)

        f_addresses.createNewFile();
        f_addresses_json.createNewFile();
        f_addresses_json.withWriterAppend {
            it << "[\n"
        }
                xml."**".findAll { it.@class.toString().contains("a1") && it.text().toString().contains("ТВО")}.eachWithIndex {
                    elem, index ->
                        def str = index+ " "+elem+" "+elem.@href.toString();
                        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + str+"\n"}
                        String district_name = elem.text().toString()
                        //route_name = route_name.replaceAll(" ","")
                        String district_link = elem.@href.toString()
                        districtGrab(district_link,district_name)
                }
        f_addresses_json.withWriterAppend {
            it << "]\n"
        }



        long endTime = System.currentTimeMillis()

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"+"\n"}
        //println "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " +"End grabbing votes"+"\n"}
        println "End grabbing votes"

    }

    public static void districtGrab(String district_link,String district_name){

        def text = ("http://www.cvk.gov.ua/pls/vnd2007/"+district_link).toURL().getText("CP1251");
        //println text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)

        def xml = slurper.parseText(text)
        //println text
        xml."**".findAll{ it.text().toString().equals("Виборчі дільниці") && it.@class.toString().contains("a1small")}.each{
            def link = it.@href.toString()
            println "here "+it +" "+link
            unitsGrab(link,district_name)
        }
    }

    public static void unitsGrab(String units_link,String district_name){

        def text = ("http://www.cvk.gov.ua/pls/vnd2007/"+units_link).toURL().getText("CP1251");
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)

        def xml = slurper.parseText(text)

        xml."**".find{ it.@class.toString().startsWith("t2") && it.tbody.tr[0].td[0].text().toString().contains("№  дільниці")}.each{

            f_addresses.withWriterAppend {
                fl->
                    f_addresses_json.withWriterAppend {
                        fl2->
                            it.tbody.tr.eachWithIndex{
                                elem, index ->
                                    if(index > 0){
                                        def name = ""
                                        def address = ""
                                        def bounds = ""
                                        if(elem.td[0].text().toString().length()>0)name = elem.td[0].text()
                                        if(elem.td[1].text().toString().length()>0)address = elem.td[1].text()
                                        if(elem.td[2].text().toString().length()>0)bounds = elem.td[2].text()

                                        println district_name+" "+name+" "+address
                                        if(name.length()>0 && address.length()>0){
                                            address = address.replaceAll("\"","'");
                                            address = address.replaceAll(";","");
                                            bounds = bounds.replaceAll(";","");
                                            bounds = bounds.replaceAll("\"","'");
                                            bounds = bounds.replaceAll("\n",". ");
                                            bounds = bounds.replaceAll("\r",". ");

                                            fl << district_name+"; "+name+"; "+address+"; "+bounds+"\n"
                                            address = address.replaceAll("\"","'");
                                            address = address.substring(address.lastIndexOf("Київ"),address.length());
                                            fl2 << "{\"address\":\""+address+"\", \"title\":\""+district_name+" "+name+"\"}"
                                            if(index < it.tbody.tr.size()-1) fl2 << ","
                                            fl2 << "\n"
                                        }

                                    }
                            }
                    }
            }

        }
    }
}

