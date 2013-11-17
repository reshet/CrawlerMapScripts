
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 11/15/13
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */

@Grab(
        group='net.sourceforge.jexcelapi',
        module='jxl',
        version='2.6.12'
)
@Grab('log4j:log4j:1.2.16')
@Grab(group='org.apache.poi', module='poi-ooxml', version='3.9')
//@Grab(group='org.apache.xmlgraphics', module='xmlgraphics-commons', version='1.4')
/*@Grab(group='fr.opensagres.xdocreport', module='fr.opensagres.xdocreport.converter.docx.docx4j', version='1.0.2')*/
@Grab(group='org.docx4j', module='docx4j', version='2.8.0')
import jxl.*
import jxl.read.biff.BiffException
//import org.apache.log4j.*;
//import org.docx4j.*;
//import jxl.write.*
import  org.apache.log4j.Logger
import org.docx4j.dml.wordprocessingDrawing.Inline
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage

//import org.docx4j.vml.ObjectFactory
import org.docx4j.wml.Tbl
import org.docx4j.wml.Tc
import org.docx4j.wml.Tr;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.*;
/*import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableRow;*/
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageManager;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import org.apache.xmlgraphics.image.loader.ImageException;


def readStopsSheet(def sheet){
    //println sheet.getSheetName();
    int first_route_numb_col = 9;
    int first_route_start_col = 10;
    int first_route_in_col = 11;
    int first_route_out_col = 12;


    int reverse_route_numb_col = 14;
    int reverse_route_start_col = 15;
    int reverse_route_in_col = 16;
    int reverse_route_out_col = 17;


    int first_route_start_row = 2;
    def row = sheet.getRow(first_route_start_row);

    //read first route stops and stats
    def first_stops = []
    int curr_first_route_row = first_route_start_row;
    def cell =  row.getCell(first_route_start_col);
    def curr_stop = cell.getStringCellValue()
    while(!curr_stop.equals("") && cell != null){
        row = sheet.getRow(curr_first_route_row);
        cell = row.getCell(first_route_start_col);
        if(cell != null){
            curr_stop = cell.getStringCellValue()
            if(!curr_stop.equals("")){
                def stop_n = row.getCell(first_route_numb_col).getNumericCellValue()
                def in_cell =row.getCell(first_route_in_col);
                def stop_in = 0;
                if(in_cell!=null)stop_in = in_cell.getNumericCellValue()
                def out_cell = row.getCell(first_route_out_col)
                def stop_out = 0;
                if(out_cell!=null)stop_out = out_cell.getNumericCellValue()
                first_stops.add([n:stop_n,name:curr_stop,in:stop_in,out:stop_out])
                curr_first_route_row++;
                //println curr_stop
            }
        }


    }

    def reverse_stops = []
    curr_first_route_row = first_route_start_row;
    row = sheet.getRow(first_route_start_row);
    cell = row.getCell(reverse_route_start_col);
    curr_stop = cell.getStringCellValue()
    while(!curr_stop.equals("") && cell !=null){
        row = sheet.getRow(curr_first_route_row);
        cell = row.getCell(reverse_route_start_col);
        if(cell != null){
            curr_stop = cell.getStringCellValue()
            if(!curr_stop.equals("")){
                def stop_n = row.getCell(reverse_route_numb_col).getNumericCellValue()
                def in_cell =row.getCell(reverse_route_in_col);
                def stop_in = 0;
                if(in_cell!=null)stop_in = in_cell.getNumericCellValue()
                def out_cell = row.getCell(reverse_route_out_col)
                def stop_out = 0;
                if(out_cell!=null)stop_out = out_cell.getNumericCellValue()
                reverse_stops.add([n:stop_n,name:curr_stop,in:stop_in,out:stop_out])
                curr_first_route_row++;
                //println curr_stop
            }

        }

    }

    //println first_stops;
    //println reverse_stops;

    return [list:sheet.getSheetName(),first:first_stops,reverse:reverse_stops]

}


def readRouteMap(def workbook){
    List lst = workbook.getAllPictures();
    def pict = lst.get(0);
    String ext = pict.suggestFileExtension();
    byte[] data = pict.getData();
   /* if (ext.equals("png")){
        FileOutputStream out = new FileOutputStream("/home/reshet/transport/trams/pict.png");
        out.write(data);
        out.close();
    }*/
   /* for (Iterator it = lst.iterator(); it.hasNext(); ) {

    }*/
    return data;
}
def readStopsInfo(def folder,int route_number){
    def transp_measuring = [:]
    Workbook w;
    try {
        File fold = new File(folder);
        File route_fl = null;
        File [] files = fold.listFiles();
        //println files;
        files.each{
            fl->
                //println fl.path
                if(fl.path.contains(" "+route_number+".xlsx")){
                    route_fl = fl;
                    return;
                }
        }

        if(route_fl!=null){
            println "reading "+route_fl.path
            FileInputStream filestr = new FileInputStream(route_fl);
            XSSFWorkbook workbook = new XSSFWorkbook(filestr);
            byte [] img = readRouteMap(workbook);
            XSSFSheet sheet_pik = workbook.getSheetAt(0);
            XSSFSheet sheet_mez = workbook.getSheetAt(1);
            XSSFSheet sheet_vih = workbook.getSheetAt(2);
            def sh1 =  readStopsSheet(sheet_pik);
            def sh2 = readStopsSheet(sheet_mez);
            def sh3 = readStopsSheet(sheet_vih);
            transp_measuring["pik"] = sh1;
            transp_measuring["mez"] = sh2;
            transp_measuring["vih"] = sh3;
            transp_measuring["img"] = img;




            //sheet.getCell(first_route_start_row, first_route_start_col).getContents()
        }

    } catch (BiffException e) {
        e.printStackTrace();
    }
    transp_measuring["route"] = route_number;
    return transp_measuring;
}

def readTramsInfo(Sheet sheet){
    def trams = []
    int agency_col = 0;
    int number_col = 1;
    int name_col = 2;
    int L_col = 17;
    int T_col = 18;
    int start_time_col = 33;
    int end_time_col = 34;

    String curr_depo = ""
    for (int i = 1; i < 83; i++) {
        String test =  sheet.getCell(name_col, i).getContents();
        String depo =  sheet.getCell(agency_col, i).getContents();
        if(depo!="" && curr_depo == "")curr_depo = depo;
        if(depo!="" && depo != curr_depo)curr_depo = depo;

        if(test !="" && !test.contains("Всього")){
            //print curr_depo+ " "
            //print sheet.getCell(agency_col, i).getContents()+" "
            /*print sheet.getCell(number_col, i).getContents()+" "
            print sheet.getCell(name_col, i).getContents()+" "
            print sheet.getCell(L_col, i).getContents()+" "
            print sheet.getCell(T_col, i).getContents()+" "
            */
            //print sheet.getCell(start_time_col, i).getContents()+" "
            //print sheet.getCell(end_time_col, i).getContents()+" "
            println "";
            def tram = [agency:curr_depo,
                        number:sheet.getCell(number_col, i).getContents(),
                        name:sheet.getCell(name_col, i).getContents(),
                        L:sheet.getCell(L_col, i).getContents(),
                        T:sheet.getCell(T_col, i).getContents()
            ]
            println tram
            trams.add(tram)
        }
        //println "";


        //CellType type = cell.getType();
        /*if (type == CellType.LABEL) {
            System.out.println("I got a label "
                    + cell.getContents());
        }

        if (type == CellType.NUMBER) {
            System.out.println("I got a number "
                    + cell.getContents());
        }*/

    }
    return trams;
}
/*
def writeTramsToDoc(def trams){
    XWPFDocument document = new XWPFDocument();
    XWPFParagraph tmpParagraph = document.createParagraph();
    XWPFRun tmpRun = tmpParagraph.createRun();
    tmpRun.setText("Загальна інформація про трамвайні маршрути");
    tmpRun.setFontSize(18);
    XWPFTable table = document.createTable(trams.size()+1, 6);
    //table.setRowBandSize(1);
    //table.setWidth(1);
    //table.setColBandSize(1);
    //table.setCellMargins(1, 1, 100, 30);

    //table.setStyleID("finest");
    XWPFTableRow firstrow = table.getRow(0);


    firstrow.getCell(0).setText("Номер маршруту");

    //firstrow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(100));
    firstrow.getCell(1).setText("Напрямок");
    //firstrow.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(200));
    firstrow.getCell(2).setText("Довжина маршруту");
    //firstrow.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(100));
    firstrow.getCell(3).setText("Тривалість оборотного");
   // firstrow.getCell(3).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(100));
    //firstrow.getCell(4).setText("Початок роботи");
    //firstrow.getCell(5).setText("Останнє відправлення");

    int i = 1;
    trams.each{  tram->
        XWPFTableRow row = table.getRow(i);
        row.getCell(0).setText(tram.number);
        row.getCell(1).setText(tram.name);
        row.getCell(2).setText(tram.L);
        row.getCell(3).setText(tram.T);
        //row.getCell(5).setText("Початок роботи");
        //row.getCell(6).setText("Останнє відправлення");
        i++;
   }
    table.setWidth(650);
    File f = new File("/home/reshet/transport/trams/temp.docx");
    f.createNewFile();
    FileOutputStream fos = new FileOutputStream(f);
    document.write(fos);
    fos.close();
    *//*f.withWriter{
        document.write(it);
    }*//*

}*/


private static void addTableCell(Tr tableRow, String content,ObjectFactory factory, WordprocessingMLPackage  wordMLPackage) {
    Tc tableCell = factory.createTc();
    tableCell.getContent().add(
            wordMLPackage.getMainDocumentPart().createParagraphOfText(content));
    tableRow.getContent().add(tableCell);
}

public org.docx4j.wml.P newImage( WordprocessingMLPackage wordMLPackage,
                                         byte[] bytes,
                                         String filenameHint, String altText,
                                         int id1, int id2){

    BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

    Inline inline = imagePart.createImageInline( filenameHint, altText,
            id1, id2);

    // Now add the inline in w:p/w:r/w:drawing
    org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    org.docx4j.wml.P  p = factory.createP();
    org.docx4j.wml.R  run = factory.createR();
    p.getParagraphContent().add(run);
    org.docx4j.wml.Drawing drawing = factory.createDrawing();
    run.getRunContent().add(drawing);
    drawing.getAnchorOrInline().add(inline);

    return p;

}

public org.docx4j.wml.P newImage( WordprocessingMLPackage wordMLPackage,
                                         byte[] bytes,
                                         String filenameHint, String altText,
                                         int id1, int id2, long cx) throws Exception {

    BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

    Inline inline = imagePart.createImageInline( filenameHint, altText,
            id1, id2, cx);

    // Now add the inline in w:p/w:r/w:drawing
    org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    org.docx4j.wml.P  p = factory.createP();
    org.docx4j.wml.R  run = factory.createR();
    p.getParagraphContent().add(run);
    org.docx4j.wml.Drawing drawing = factory.createDrawing();
    run.getRunContent().add(drawing);
    drawing.getAnchorOrInline().add(inline);

    return p;

}

def writeTramsToDoc2(def trams){
    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "Hello Word!");
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle",
            "This is a subtitle!");

    ObjectFactory factory = Context.getWmlObjectFactory();

    Tbl table = factory.createTbl();
    Tr tableRow = factory.createTr();


    addTableCell(tableRow, "Номер маршруту",factory,wordMLPackage);
    addTableCell(tableRow, "Напрямок",factory,wordMLPackage);
    addTableCell(tableRow, "Довжина маршруту",factory,wordMLPackage);
    addTableCell(tableRow, "Тривалість оборотного",factory,wordMLPackage);

    table.getContent().add(tableRow);
    int i = 1;
    trams.each{  tram->
        Tr tableRow1 = factory.createTr();
        addTableCell(tableRow1, tram.number,factory,wordMLPackage);
        addTableCell(tableRow1, tram.name,factory,wordMLPackage);
        addTableCell(tableRow1, tram.L,factory,wordMLPackage);
        addTableCell(tableRow1, tram.T,factory,wordMLPackage);
        table.getContent().add(tableRow1);
        i++;
    }


    String filenameHint = null;
    String altText = null;
    int id1 = 0;
    int id2 = 1;

    def route8 = readStopsInfo("/home/reshet/transport/trams/input/",8);
    byte [] img = route8["img"];
    org.docx4j.wml.P p = newImage( wordMLPackage, img,filenameHint, altText,id1, id2,6000);
    // Now add our p to the document
    wordMLPackage.getMainDocumentPart().addObject(p);
    wordMLPackage.getMainDocumentPart().addParagraphOfText("");
    wordMLPackage.getMainDocumentPart().addObject(table);
    //wordMLPackage.getMainDocumentPart().addParagraphOfText("Hello Word!");
    wordMLPackage.save(new File("/home/reshet/transport/trams/temp2.docx"));


  /*  tmpRun.setText("Загальна інформація про трамвайні маршрути");
    XWPFTable table = document.createTable(trams.size()+1, 6);
    XWPFTableRow firstrow = table.getRow(0);
    firstrow.getCell(0).setText("Номер маршруту");
    firstrow.getCell(1).setText("Напрямок");
    firstrow.getCell(2).setText("Довжина маршруту");
    firstrow.getCell(3).setText("Тривалість оборотного");
    int i = 1;
    trams.each{  tram->
        XWPFTableRow row = table.getRow(i);
        row.getCell(0).setText(tram.number);
        row.getCell(1).setText(tram.name);
        row.getCell(2).setText(tram.L);
        row.getCell(3).setText(tram.T);
        //row.getCell(5).setText("Початок роботи");
        //row.getCell(6).setText("Останнє відправлення");
        i++;
    }
    File f = new File("/home/reshet/transport/trams/temp.docx");
    f.createNewFile();
  */

}
def timetableReader(File inputWorkbook){
    Workbook w;
    try {
        w = Workbook.getWorkbook(inputWorkbook);
        // Get the first sheet
        Sheet sheet = w.getSheet(0);
        def trams = readTramsInfo(sheet);
        writeTramsToDoc2(trams);

    } catch (BiffException e) {
        e.printStackTrace();
    }

}

File trams_timetable = new File("/home/reshet/transport/trams/trams_timetable1.xls");
timetableReader(trams_timetable);
//readStopsInfo("/home/reshet/transport/trams/input/",2);
//readStopsInfo("/home/reshet/transport/trams/input/",4);
//readStopsInfo("/home/reshet/transport/trams/input/",5);
//def res = readStopsInfo("/home/reshet/transport/trams/input/",8);
//println res;
//println readStopsInfo("/home/reshet/transport/trams/input/",11);

//println readStopsInfo("/home/reshet/transport/trams/input/",12);
//println readStopsInfo("/home/reshet/transport/trams/input/",14);
//println readStopsInfo("/home/reshet/transport/trams/input/",16);
//println readStopsInfo("/home/reshet/transport/trams/input/",18);


