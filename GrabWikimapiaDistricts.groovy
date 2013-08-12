
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

@Grab(
        group = 'net.sourceforge.jexcelapi',
        module = 'jxl',
        version = '2.6.12'
) @Grab(group = 'org.codehaus.gpars', module = 'gpars', version = '1.1.0') @Grab(
        group = 'net.sourceforge.jexcelapi',
        module = 'jxl',
        version = '2.6.12'
) @Grab(group = 'org.codehaus.gpars', module = 'gpars', version = '1.1.0')
import java.lang.Object

class GrabberWikimapiaKiev {
    public static final File logfile = new File("/home/reshet/cvkkiev/grabbing_wiki_data.log")
    //public static File f_data = new File("/home/reshet/cvkkiev/cvk_data_2007_kiev.csv")
    public static File f_addresses_data_json = new File("/var/www/InteractiveMaps/js/districts_wiki_detailed.json")

    public static int total_read = 0
    public static int total_found = 0


    public static void main(def args){
        //println "Start grabbing routes"

        long startTime = System.currentTimeMillis()
        if(!logfile.exists())logfile.createNewFile();
        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Start wikimapia grabbing"+"\n"}
        //http://www.cvk.gov.ua/pls/vnd2007/W6P029?PT001F01=600&PID100=80&pf7331=218
        def key = "3707829A-C5E5D2D5-CD78B9DE-54D8825C-E8EAB78D-ADE727FD-8469BA93-2942B466"
        def base_url = "http://api.wikimapia.org/?function=place.search&category=45057&key="+key+"&lat=50.426019&lon=30.514526&z=10&format=&pack=&language=ru&m=h&count=100"
        def text = (base_url).toURL().getText();
        //println text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)
        def xml = slurper.parseText(text)

        //f_data.createNewFile();
        f_addresses_data_json.createNewFile();
        f_addresses_data_json.withWriterAppend {
            it << "[\n"
        }
        def page = ""
        def count = ""
        xml."**".find{it.name() == "page"}.each{
            page = it.text()
        }
        xml."**".find{it.name() == "count"}.each{
            count =  it.text()
        }
        xml."**".find{it.name() == "found"}.each{
           total_found =  Integer.parseInt(it.text())
        }
        println "page "+page+" count "+count+" found "+total_found
        while(total_read < total_found){
            districtGrab(base_url+"&page="+page++)
            //sleep(2000)
        }
        f_addresses_data_json.withWriterAppend {
            it << "]\n"
        }



        long endTime = System.currentTimeMillis()

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"+"\n"}
        //println "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " +"End grabbing votes data"+"\n"}
        println "End grabbing wikimapia"

    }

    public static void districtGrab(String page_link){

        def text = (page_link).toURL().getText();
        //println text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)

        def xml = slurper.parseText(text)

        f_addresses_data_json.withWriterAppend {
            ff3->
                xml."**".findAll{it.name().toString().startsWith("places_")}.each{
                       elem ->
                            println elem.name()
                           def title = elem.title.text();
                           title = title.replaceAll("\""," ")
                            ff3 << "{\"title\":\""+title+"\", "
                            ff3 << "\"bounds\":\n["
                            int b_count = elem."**".findAll{it.name().toString().startsWith("polygon_")}.size()
                            elem."**".findAll{it.name().toString().startsWith("polygon_")}.eachWithIndex{
                                el,ind->
                                    ff3 << "{\"lat\":"+el.y.text()+", \"lng\":"+el.x.text()+"}"
                                    //println el.x.text()+" "+el.y.text()
                                    if(ind < b_count -1)ff3 << ","
                                    ff3 << "\n"
                            }
                            ff3 <<  "]}"
                            if(total_read < total_found -1)ff3 << ","
                            ff3 << "\n"
                            total_read++

                }

        }

    }

}

