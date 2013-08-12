
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/1/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */

@Grab(group='org.codehaus.gpars', module='gpars', version='1.1.0')
import groovyx.gpars.GParsPool
import java.util.concurrent.atomic.AtomicInteger

class Grabber{
    public static final File logfile = new File("/home/reshet/phonescrawling/crawling.log")

    public static void main(def args){
        GParsPool.withPool{
            //def animals = ['dog', 'ant', 'cat', 'whale']
            //animals.eachParallel {println it}


            long startTime = System.currentTimeMillis()


            if(!logfile.exists())logfile.createNewFile();
            logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Start phones crawling"+"\n"}

            def text = ("http://spravkaru.net/streets/ukraine/").toURL().getText();
            //print text
            @Grab(group='org.ccil.cowan.tagsoup',
                    module='tagsoup', version='1.2' )
            def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
            def slurper = new XmlSlurper(tagsoupParser)

            def xml = slurper.parseText(text)
            //int i = 0
            xml."**".findAll { it.@href.toString().contains("/streets/380/")}.eachWithIndexParallel {
                elem, index ->
                    //if(index == 0) {
                    //  println 'KIEV ONLY'
                    if(index > 0)townPageGrab(elem.@href.toString(),elem,index)
                    //return;
                    //};
                    //println index+ " "+elem+" "+elem.@href.toString();
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + index+ " "+elem+" "+elem.@href.toString()+"\n"}
                    // doSomething with each entry
            }
            long endTime = System.currentTimeMillis()

            println "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"
            logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"+"\n"}

            //println(animals.everyParallel {it.contains('a')} ? 'All animals contain a' : 'Some animals can live without an a')
        }
    }


    private static File fileManagePrepare(def town_name, def seq){
        File current_city_file = new File("/home/reshet/phonescrawling/"+town_name+"_"+seq+".csv");
        if(!current_city_file.exists())current_city_file.createNewFile();
        return current_city_file;
    }
    public static void townPageGrab(def town_link,def town_name, def seq){
        GParsPool.withPool {
            def text = ("http://spravkaru.net"+town_link).toURL().getText();
            @Grab(group='org.ccil.cowan.tagsoup',
                    module='tagsoup', version='1.2' )
            def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
            def slurper = new XmlSlurper(tagsoupParser)

            def xml = slurper.parseText(text)
            final File fl = fileManagePrepare(town_name,seq)
            xml."**".findAll { it.@href.toString().startsWith(town_link)}.eachWithIndexParallel {
                elem, index ->
                        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + fl.getName()+ " "+ index+ " "+elem+" "+elem.@href.toString()+"\n"}
                        //println index+ " "+elem+" "+elem.@href.toString()
                        final AtomicInteger phones_total = new AtomicInteger();
                        phones_total.set(0);
                        streetPageGrab("http://spravkaru.net"+elem.@href.toString(),elem,index,fl,phones_total)

                    // doSomething with each entry
            }
        }
    }

    public static void parsePersonsPage(def xml,File file,AtomicInteger phones_total){
        int i = 0
        //println "LIST OF PERSONS"
        file.withWriterAppend("UTF-8") {
            fl->
                xml."**".findAll {it.@id.toString() == "data"}.each {
                    it.table.tr.each{
                        itt->
                            // print i++;
                            if(i!=0){
                                def phone = itt.td[0].a[0].text().toString()
                                def name = itt.td[1].a[0].text().toString()
                                def address = itt.td[2].a[0].text().toString()
                                def full_address = ""
                                int j = 0;
                                itt.td[2].a.each{
                                    ttt->
                                        if(j>0)full_address+=ttt.text().toString()+", "
                                        j++
                                }
                                full_address+=itt.td[2].text().toString();
                                //println " Phone: "+phone+"; Street: "+address+"; Name: "+name+"; Full address: "+full_address;

                                fl << phone +"; "+address+"; "+name+"; "+ full_address+";\n"

                                phones_total.addAndGet(1)
                                if(phones_total.get()%200 == 0) logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ "+file.getName()+" NUMBERS FOUND: "+phones_total.get()+"\n"}
                                //fl.get
                                //current_total_found_numbers++;
                                //if(current_total_found_numbers%200 == 0)println "NUMBERS FOUND: "+current_total_found_numbers;
                                //if(current_total_found_numbers%40000 == 0) fileManageEnsureSplit()

                            }
                            i++;
                    }
                }
                // doSomething with each entry
        }
    }
    public static void parseHousePage(def xml,File file,AtomicInteger phones_total){
        int i = 0
        //println "LIST OF HOUSES"
        xml."**".findAll {it.@id.toString() == "data"}.each {
            it.table.tr.each{
                itt->
                    // print i++;
                    def link = itt.td[0].a[0].@href.toString()
                    def house_name = itt.td[0].a[0].text().toString()
                    streetPageGrab(link,house_name, i,file,phones_total)
                    i++
            }

            // doSomething with each entry
        }
    }
    public static boolean hasMultiplePages(def xml){
        if(xml."**".find{
            it.@class.equals("tab") &&
                    it.text().toString().startsWith("Страница")&&
                    it.ul.@class == "tabcontainer" &&
                    it.ul.li!=null &&
                    it.ul.li.size()> 1}!=null)
        {
            // println "           MULTIPLE PAGES";
            return true;
        }else return false;
    }
    private static void visitMultipleHousePages(def xml,File file,AtomicInteger phones_total){
        def found = xml."**".find{
            it.@class.equals("tab") &&
                    it.text().toString().startsWith("Страница")&&
                    it.ul.@class == "tabcontainer" &&
                    it.ul.li!=null &&
                    it.ul.li.size()> 1}
        if(found!=null)
        {
            found.ul.li.each{   elem->
                if(elem.a.@href!=null) {
                    def link =  elem.a.@href.toString()
                    if(link.length() > 1){
                        // println elem.a.text().toString()+" "+ link
                        housesPageGrab(link,file,phones_total)
                    }

                }
            };
        }
    }
    private static void visitMultiplePersonsPages(def xml,File file,AtomicInteger phones_total){
        def found = xml."**".find{
            it.@class.equals("tab") &&
                    it.text().toString().startsWith("Страница")&&
                    it.ul.@class == "tabcontainer" &&
                    it.ul.li!=null &&
                    it.ul.li.size()> 1}
        if(found!=null)
        {
            found.ul.li.each{   elem->
                if(elem.a.@href!=null) {
                    def link =  elem.a.@href.toString()
                    if(link.length() > 1){
                        // println elem.a.text().toString()+" "+ link
                        personsPageGrab(link,file,phones_total)
                    }

                }
            };
        }
    }

    public static void streetPageGrab(def street_link,def street_name, def seq, File fl,AtomicInteger phones_total){
        // println "------";
        //  println street_link;
        //  println "------";
        def text = (street_link).toURL().getText();
        // print text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)
        def xml = slurper.parseText(text)

        if (xml."**".find{it.text().toString().contains("Список жителей по данному адресу")}!=null){
            parsePersonsPage(xml,fl,phones_total)
            if(hasMultiplePages(xml)){
                visitMultiplePersonsPages(xml,fl,phones_total)
            }
        }else
        if(xml."**".find{it.text().toString().contains("Список домов по данному адресу:")}!=null){
            parseHousePage(xml,fl,phones_total)
            if(hasMultiplePages(xml)){
                visitMultipleHousePages(xml,fl,phones_total)
            }
        }
    }
    public static void personsPageGrab(def link,File file,AtomicInteger phones_total){
        // println "------";
        // println link;
        // println "------";
        def text = link.toURL().getText();
        // print text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)
        def xml = slurper.parseText(text)
        if (xml."**".find{it.text().toString().contains("Список жителей по данному адресу")}!=null){
            parsePersonsPage(xml,file,phones_total)
        }

    }
    public static void housesPageGrab(def link,File file,AtomicInteger phones_total){
        // println "------";
        //  println link;
        //  println "------";
        def text = link.toURL().getText();
        // print text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)

        def xml = slurper.parseText(text)
        if(xml."**".find{it.text().toString().contains("Список домов по данному адресу:")}!=null){
            parseHousePage(xml,file,phones_total)
        }
    }
}
