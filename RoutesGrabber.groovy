
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

import jxl.*
import jxl.write.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

class EwayGrabber {
    public static final File logfile = new File("/home/reshet/transportua/grabbing.log")
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
        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Start routes grabbing"+"\n"}
        def text = ("http://www.eway.in.ua/js/routes.js.php?lang=ru&city=kyiv&id=-1&lat=0&lng=0&zoom=0").toURL().getText();
        //println text
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)

        WritableWorkbook workbook = null
        File f_stops = new File("/home/reshet/transportua/autobus_stops.csv")
        f_stops.createNewFile();
        def stops = []
        def xml = slurper.parseText(text)

        //getting screens code fast
        GParsPool.withPool{
            xml."**".findAll { it.@id.toString().contains("showRoute")}.eachWithIndexParallel {
                elem, index ->
                    def str = index+ " "+elem+" "+elem.@id.toString();
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ making screen " + str+"\n"}

                    String route_name = elem.text().toString()
                    String route_link = elem.@id.toString()
                    route_link = route_link.substring(9,route_link.length())
                    screenGrab(route_link,route_name)
            }
        }
        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ all screens done \n"}
        xml."**".findAll { it.@id.toString().contains("showRoute")}.eachWithIndex {
            elem, index ->
                int local_index = 0
                if(index == 0) {
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Автобусы"+"\n"}
                    //println  "Автобусы"
                    File f = new File("/home/reshet/transportua/autobus.xls")
                    f.createNewFile()
                    workbook = Workbook.createWorkbook(f);
                }
                if(index == 262) {
                    local_index = 0
                    if(workbook.getSheets().size()>0)workbook.write()
                    workbook.close()
                    //println  "Трамвай"
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Трамвай"+"\n"}
                    File f = new File("/home/reshet/transportua/tram.xls")
                    f.createNewFile()
                    workbook = Workbook.createWorkbook(f);
                    writeStops(f_stops,stops)
                    stops = []
                    f_stops = new File("/home/reshet/transportua/tram_stops.csv")
                    f_stops.createNewFile();

                }
                if(index == 283){
                    local_index = 0
                    if(workbook.getSheets().size()>0)workbook.write()
                    workbook.close()
                    //println  "Троллейбус"
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Троллейбус"+"\n"}
                    File f = new File("/home/reshet/transportua/trolley.xls")
                    f.createNewFile()
                    workbook = Workbook.createWorkbook(f);
                    writeStops(f_stops,stops)
                    stops = []
                    f_stops = new File("/home/reshet/transportua/trolley_stops.csv")
                    f_stops.createNewFile();
                }
                if(index == 320) {
                    local_index = 0
                    if(workbook.getSheets().size()>0)workbook.write()
                    workbook.close()
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Metro"+"\n"}
                    //println  "Метро"
                    File f = new File("/home/reshet/transportua/metro.xls")
                    f.createNewFile()
                    workbook = Workbook.createWorkbook(f);
                    writeStops(f_stops,stops)
                    stops = []
                    f_stops = new File("/home/reshet/transportua/metro_stops.csv")
                    f_stops.createNewFile();
                }
                if(index == 323) {
                    local_index = 0
                    if(workbook.getSheets().size()>0)workbook.write()
                    workbook.close()
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Electron"+"\n"}

                    //println  "Электричка"
                    File f = new File("/home/reshet/transportua/electron.xls")
                    f.createNewFile()
                    workbook = Workbook.createWorkbook(f);
                    writeStops(f_stops,stops)
                    stops = []
                    f_stops = new File("/home/reshet/transportua/electron_stops.csv")
                    f_stops.createNewFile();
                }
                if(index == 325) {
                    local_index = 0
                    if(workbook.getSheets().size()>0)workbook.write()
                    workbook.close()

                    //println  "Пригородные маршруты"
                    logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "Пригородные маршруты"+"\n"}

                    File f = new File("/home/reshet/transportua/prigorod.xls")
                    f.createNewFile()
                    workbook = Workbook.createWorkbook(f);
                    writeStops(f_stops,stops)
                    stops = []
                    f_stops = new File("/home/reshet/transportua/prigorod_stops.csv")
                    f_stops.createNewFile();
                }
                def str = index+ " "+elem+" "+elem.@id.toString();
                logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + str+"\n"}
                //if(index > 325){
                    String route_name = elem.text().toString()
                    route_name = route_name.replaceAll(" ","")
                    String route_link = elem.@id.toString()
                    route_link = route_link.substring(9,route_link.length())
                    //println "link: "+route_link;
                    routeGrab(route_link,route_name,local_index,workbook,stops)

                //}
                local_index++;
        }
        if(workbook.getSheets().size()>0)workbook.write()
        workbook.close()
        writeStops(f_stops,stops)


        /*  xml."**".find{ it.@class.toString().contains("result_num_span") && it.text().toString().contains("Автобус")}
                  ."**".findAll{
                       it.@id.toString().contains("showRoute")}.eachWithIndex {
                          elem, index ->
                          println index+ " "+elem+" "+elem.@id.toString();
                       }*/



        long endTime = System.currentTimeMillis()

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"+"\n"}
        //println "TIME SPENT: "+ (endTime-startTime)/1000.0 + " seconds"

        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " +"End grabbing routes"+"\n"}
        //println "End grabbing routes"

    }

    public static void screenGrab(def route_link,String route_name){
        String screen_name = "/home/reshet/transportua/"+route_name+"_"+route_link+"_screen.png";
        String command = "/home/reshet/phantomjs/bin/phantomjs " +
                "/home/reshet/phantomjs/bin/rasterize.js " +
                "http://www.eway.in.ua/ua/cities/kyiv/routes/"+route_link+
                " "+screen_name
        if(!new File(screen_name).exists()){
            def proc = command.execute()
            proc.waitFor()
        }
    }
        public static void routeGrab(def route_link,String route_name, int seq, WritableWorkbook workbook,List stops){

        def text = ("http://www.eway.in.ua/ua/cities/kyiv/routes/"+route_link).toURL().getText();
        @Grab(group='org.ccil.cowan.tagsoup',
                module='tagsoup', version='1.2' )
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
        def slurper = new XmlSlurper(tagsoupParser)

        def xml = slurper.parseText(text)
        //final File fl = fileManagePrepare(town_name,seq)
        def forward = []
        def backward = []

        xml."**".find { it.@class.toString().startsWith("route-block route-stops")}.each{

            it.table.tr.eachWithIndex{
                elem, index ->
                    if(index > 0){
                        if(elem.td[0].a.text().toString().length()>0)forward.add(elem.td[0].a.text())
                        if(elem.td[1].a.text().toString().length()>0)backward.add(elem.td[1].a.text())
                    }

            }
        }
        //println forward
        //println backward

        //Make a screen

        // WritableSheet sheet = workbook.createSheet(route_name,0);
        WritableSheet sheet = workbook.createSheet(route_name, seq);
        logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + "sheet "+sheet.getName()+" created"+"\n"}
        //println "sheet "+sheet.getName()+" created"
        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 14)
        cellFont.setBoldStyle(WritableFont.BOLD)
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(Alignment.CENTRE)
        cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE)
        WritableFont cellFont2 = new WritableFont(WritableFont.TIMES, 12);
        WritableCellFormat cellFormat2 = new WritableCellFormat(cellFont);
        cellFormat2.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN)
        cellFormat2.setAlignment(Alignment.CENTRE);
        sheet.addCell(new Label(0, 0, "Маршрут "+route_name,cellFormat));

        sheet.mergeCells(2, 0, 5, 0);
        sheet.mergeCells(7, 0, 10, 0);
        sheet.addCell(new Label(2, 0, "Прямий маршрут",cellFormat));
        sheet.addCell(new Label(7, 0, "Зворотній маршрут",cellFormat));

        sheet.addCell(new Label(2, 1, "№",cellFormat2));
        sheet.addCell(new Label(3, 1, "Зупинки",cellFormat2));
        sheet.addCell(new Label(4, 1, "Увійшло",cellFormat2));
        sheet.addCell(new Label(5, 1, "Вийшло",cellFormat2));

        sheet.addCell(new Label(7, 1, "№",cellFormat2));
        sheet.addCell(new Label(8, 1, "Зупинки",cellFormat2));
        sheet.addCell(new Label(9, 1, "Увійшло",cellFormat2));
        sheet.addCell(new Label(10, 1, "Вийшло",cellFormat2));

        sheet.setColumnView(0, 100);




        forward.eachWithIndex {
            elem,ind ->
                int i = 2+ind
                int numb = ind+1
                String st = elem
                sheet.addCell(new Number(2, i, numb,cellFormat2))
                Label label = new Label(3, i, st,cellFormat2);
                sheet.addCell(new Label(4, i, "",cellFormat2));
                sheet.addCell(new Label(5, i, "",cellFormat2));
                sheet.setRowView(ind+1, 21*20);
                sheet.setRowView(ind+2, 21*20);

                sheet.addCell(label);
                stops.add(st)
        }
        backward.eachWithIndex {
            elem,ind ->
                int i = 2+ind
                int numb = ind+1
                String st = elem
                sheet.addCell(new Number(7, i, numb,cellFormat2))
                Label label = new Label(8, i, st,cellFormat2);
                sheet.addCell(new Label(9, i, "",cellFormat2));
                sheet.addCell(new Label(10, i, "",cellFormat2));
                sheet.setRowView(ind+1, 21*20);
                sheet.setRowView(ind+2, 21*20);

                sheet.addCell(label);
                stops.add(st)
        }

        double CELL_DEFAULT_HEIGHT = 17;
        double CELL_DEFAULT_WIDTH = 80;

            String screen_name = "/home/reshet/transportua/"+route_name+"_"+route_link+"_screen.png";
            String command = "/home/reshet/phantomjs/bin/phantomjs " +
                    "/home/reshet/phantomjs/bin/rasterize.js " +
                    "http://www.eway.in.ua/ua/cities/kyiv/routes/"+route_link+
                    " "+screen_name
            if(!new File(screen_name).exists()){
                def proc = command.execute()
                proc.waitFor()
            }

        File imageFile = new File(screen_name);
        if(imageFile.exists()){
            BufferedImage input = ImageIO.read(imageFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(input, "PNG", baos);
            sheet.addImage(new WritableImage(0,1,input.getWidth()/800.0,
                    input.getHeight() / 32.0,baos.toByteArray()));

        }else{
            logfile.withWriterAppend {f -> f << new Date().getDateTimeString() +" __ " + screen_name+" not exist!!!"+"\n"}
        }
       // WritableImage.MOVE_WITH_CELLS;
       // WritableImage.NO_MOVE_OR_SIZE_WITH_CELLS;


        //def cell = sheet.getColumnView(0);
        //cell.setAutosize(true);
        sheet.setRowView(0, 32*20);
        for(int i = 3; i < 11 ;i++){
            if(i != 7 && i != 6){
                def cell = sheet.getColumnView(i);
                cell.setAutosize(true);
                sheet.setColumnView(i, cell);
            }
            //cell = sheet.getColumnView(3);
            //cell.setAutosize(true);
            //sheet.setColumnView(3, cell);
       }

    }
}

