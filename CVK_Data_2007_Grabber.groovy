
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

class CVK_Data_2007_Grabber {
    public static final File logfile = new File("/home/reshet/cvkkiev/grabbing_data.log")
    public static File f_data = new File("/home/reshet/cvkkiev/cvk_data_2007_kiev.csv")
    public static File f_addresses_data_json = new File("/home/reshet/cvkkiev/addresses_withdata.json")


    public static void main(def args){
        //println "Start grabbing routes"

        long startTime = System.currentTimeMillis()
        if(!logfile.exists())logfile.createNewFile();
        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Start votes data 2007 grabbing"+"\n"}
        //http://www.cvk.gov.ua/pls/vnd2007/W6P029?PT001F01=600&PID100=80&pf7331=218
        def text = ("http://www.cvk.gov.ua/pls/vnd2007/W6P335?PT001F01=600").toURL().getText("CP1251");
        //println text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)
        def xml = slurper.parseText(text)

        f_data.createNewFile();
        f_addresses_data_json.createNewFile();
        f_addresses_data_json.withWriterAppend {
            it << "[\n"
        }
                xml."**".findAll{it.td[3].text().toString().contains("місто  Київ,")}.eachWithIndex {
                    elem,ind ->
                        //println elem
                        String name = elem.td[3].text()
                        name  = name.replaceAll("\n", "")
                        def link = elem.td[0].a.@href.toString()
                        def distr_id = elem.td[0].a.text()
                        int units_count = Integer.valueOf(elem.td[1].a.text())
                        println name+" "+link+" "+distr_id+" "+units_count
                        if(ind == 0){
                            f_data.withWriterAppend {
                                fl-> fl << "ТВО №; Имя района; "
                            }
                        }

                        districtGrab(link,distr_id,name,units_count,ind == 0)
                }
        f_addresses_data_json.withWriterAppend {
            it << "]\n"
        }



        long endTime = System.currentTimeMillis()

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"+"\n"}
        //println "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " +"End grabbing votes data"+"\n"}
        println "End grabbing votes data"

    }

    public static void districtGrab(String district_link,def distr_id,String district_name,int units_count,boolean head){

        def text = ("http://www.cvk.gov.ua/pls/vnd2007/"+district_link).toURL().getText("CP1251");
        //println text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)

        def xml = slurper.parseText(text)
        //println text
        xml."**".find{ it.tr.td.text().toString().startsWith("Дільн.")}.each{
            //println it.tr.td.text()
            f_data.withWriterAppend {
                fd->
                it.tr.eachWithIndex{
                    row,ind->
                        if(ind > 0 || head){
                            StringBuilder str_row = new StringBuilder()
                            if(ind > 0){
                                str_row.append(distr_id+"; "+district_name+"; ")
                            }
                            row.td.eachWithIndex{
                                cell,ind2->
                                    String celltext = cell.text()
                                    celltext = celltext.replaceAll("\n","")
                                    str_row.append(celltext)
                                    str_row.append(";")
                            }
                            str_row.append("\n")
                            fd<<str_row.toString()
                        }
                }
            }

        }
    }

}

