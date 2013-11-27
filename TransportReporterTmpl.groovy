
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
import org.apache.commons.lang.StringUtils

//import org.apache.log4j.*;
//import org.docx4j.*;
//import jxl.write.*
import  org.apache.log4j.Logger
import org.docx4j.XmlUtils
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
import org.apache.xmlgraphics.image.loader.ImageException

import javax.imageio.ImageIO
import javax.xml.bind.JAXBElement
import javax.xml.bind.JAXBException
import java.awt.image.BufferedImage;


def readStopsSheet_2ptrn(def sheet){
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
    while(!curr_stop.equals("") && row != null && cell != null){
        row = sheet.getRow(curr_first_route_row);
        if(row != null){
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
                    //println curr_stop
                }

            }
        }
        curr_first_route_row++;
    }

    curr_first_route_row = first_route_start_row -1;
    row = sheet.getRow(first_route_start_row -1 );
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
                first_stops.add([n:stop_n,name:curr_stop,in:stop_in,out:stop_out])
                curr_first_route_row++;
                //println curr_stop
            }

        }

    }

    //here determine start row of reverse stops;
    def reverse_route_start_row = 0;
    def curr_reverse_route_row = reverse_route_start_row;
    cell =  row.getCell(first_route_numb_col);
    if(cell != null){
        switch(cell.getCellType()) {
            case XSSFCell.CELL_TYPE_NUMERIC:
                curr_stop = cell.getNumericCellValue()
                break;
            case XSSFCell.CELL_TYPE_STRING:
                curr_stop = cell.getStringCellValue()
                break;
        }


    }else{
        curr_stop = "";
    }

    while(!curr_stop.toString().contains("Зворотній маршрут") && curr_reverse_route_row < 120){
        //println curr_stop
        row = sheet.getRow(curr_reverse_route_row);
        if(row!=null){
            cell = row.getCell(first_route_numb_col);
            if(cell != null){
                switch(cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        curr_stop = cell.getNumericCellValue()
                        break;
                    case XSSFCell.CELL_TYPE_STRING:
                        curr_stop = cell.getStringCellValue()
                        break;
                }


            }else{
                curr_stop = "";
            }

        }else{
            curr_stop = "";
        }

        curr_reverse_route_row++;
    }
    reverse_route_start_row = curr_reverse_route_row + 1;
    //here reverse list start found

    curr_reverse_route_row =  reverse_route_start_row;

    def reverse_stops = []
    row = sheet.getRow(curr_reverse_route_row);
    cell =  row.getCell(first_route_start_col);
    curr_stop = cell.getStringCellValue()
    while(!curr_stop.equals("") && cell != null && row!=null){

        row = sheet.getRow(curr_reverse_route_row);
        if(row == null) break;
        cell = row.getCell(first_route_start_col);
        if(cell != null){
            curr_stop = cell.getStringCellValue()
            //println curr_stop;
            if(!curr_stop.equals("")){
                def stop_n = row.getCell(first_route_numb_col).getNumericCellValue()
                def in_cell =row.getCell(first_route_in_col);
                def stop_in = 0;
                if(in_cell!=null)stop_in = in_cell.getNumericCellValue()
                def out_cell = row.getCell(first_route_out_col)
                def stop_out = 0;
                if(out_cell!=null)stop_out = out_cell.getNumericCellValue()
                reverse_stops.add([n:stop_n,name:curr_stop,in:stop_in,out:stop_out])
                curr_reverse_route_row++;
                //println curr_stop
            }
        }
    }

    curr_reverse_route_row = reverse_route_start_row -1;
    row = sheet.getRow(reverse_route_start_row -1 );
    cell = row.getCell(reverse_route_start_col);
    curr_stop = cell.getStringCellValue()
    while(!curr_stop.equals("") && cell !=null){
        row = sheet.getRow(curr_reverse_route_row);
        if(row == null) break;
        cell = row.getCell(reverse_route_start_col);
        if(cell != null){
            curr_stop = cell.getStringCellValue()
            if(!curr_stop.equals("")){
                //println curr_stop;
                def stop_n = row.getCell(reverse_route_numb_col).getNumericCellValue()
                def in_cell =row.getCell(reverse_route_in_col);
                def stop_in = 0;
                if(in_cell!=null)stop_in = in_cell.getNumericCellValue()
                def out_cell = row.getCell(reverse_route_out_col)
                def stop_out = 0;
                if(out_cell!=null)stop_out = out_cell.getNumericCellValue()
                reverse_stops.add([n:stop_n,name:curr_stop,in:stop_in,out:stop_out])
                curr_reverse_route_row++;
                //println curr_stop
            }

        }

    }
    //println first_stops;
    //println reverse_stops;

    return [list:sheet.getSheetName(),first:first_stops,reverse:reverse_stops]

}
def readStopsSheet_1ptrn(def sheet){
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
    while(!curr_stop.equals("") && cell != null && row != null){
        row = sheet.getRow(curr_first_route_row);
        if(row == null)break;
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
        if(row == null) break;
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
def readStopsSheet(def sheet){
    //here determine stops list pattern (long in two columns or short)
    def row = sheet.getRow(0);
    cell = row.getCell(14);
    if(cell != null){
        def curr = cell.getStringCellValue()
        if(!curr.equals("") && curr.contains("Зворотній")){
            return readStopsSheet_1ptrn(sheet);
        }else{
            return readStopsSheet_2ptrn(sheet);
        }
    } else{
        return readStopsSheet_2ptrn(sheet);
    }
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

def readStopsInfo(def folder,def route_number){
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


def readAllRoutesInfo(def folder){
    Workbook w;
    def transp_measures = [:]

    try {
        File fold = new File(folder);
        File [] files = fold.listFiles();
        //println files;
        files.each{
            fl->
                //println fl.path

                def transp_measuring = [:]
                def route_number  = fl.name.substring(0,fl.name.length()-5);
                def route_number2 = route_number.split(" ");
                def route_number3 = route_number2[1];


                println "reading "+ fl.path
                println "route number is" + route_number3;
                FileInputStream filestr = new FileInputStream(fl);
                XSSFWorkbook workbook = new XSSFWorkbook(filestr);

                //byte [] img = readRouteMap(workbook);
                byte [] img = getFreshRouteImage("/home/reshet/transport/trams/fresh_maps/",route_number3);

                XSSFSheet sheet_pik = workbook.getSheetAt(0);
                //XSSFSheet sheet_mez = workbook.getSheetAt(1);
                //XSSFSheet sheet_vih = workbook.getSheetAt(2);
                def sh1 =  readStopsSheet(sheet_pik);
                //def sh2 = readStopsSheet(sheet_mez);
                //def sh3 = readStopsSheet(sheet_vih);
                transp_measuring["pik"] = sh1;
                //transp_measuring["mez"] = sh2;
                //transp_measuring["vih"] = sh3;
                transp_measuring["img"] = img;
                transp_measures[route_number3] = transp_measuring;

        }

    } catch (BiffException e) {
        e.printStackTrace();
    }
    return transp_measures;
}
def findTramInfo(def number, def trams){
    def tram_info
    trams.each{
        tr->
            if(tr.number == number){
                tram_info = tr;
                return ;
            }
    }
    return tram_info;
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
    for (int i = 24; i < 83; i++) {
        String test =  sheet.getCell(name_col, i).getContents();
        String depo =  sheet.getCell(agency_col, i).getContents();
        if(depo!="" && curr_depo == "")curr_depo = depo;
        if(depo!="" && depo != curr_depo)curr_depo = depo;
        def number =  sheet.getCell(number_col, i).getContents();
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
            //println "";        '
            for(int j = 0;j< 20;j++){
                print sheet.getCell(T_col+j, i).getContents()+"  "
            }
            println "";
            def tram = [agency:"КП «КИЇВПАСТРАНС», "+curr_depo+" ТРЕД",
                        number:number,
                        name:sheet.getCell(name_col, i).getContents(),
                        L:sheet.getCell(L_col, i).getContents(),
                        T:sheet.getCell(T_col, i).getContents(),
                        start:sheet.getCell(start_time_col, i).getContents(),
                        end:sheet.getCell(end_time_col, i).getContents()

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


private static void changeFontToTimesNewR(RPr runProperties) {
    RFonts runFont = new RFonts();
    runFont.setAscii("Times New Roman");
    runFont.setHAnsi("Times New Roman");
    runProperties.setRFonts(runFont);

}
private static void changeFontSize(RPr runProperties, int fontSize) {
    HpsMeasure size = new HpsMeasure();
    size.setVal(BigInteger.valueOf(fontSize));
    runProperties.setSz(size);
}
private static void addTableCell(Tr tableRow, String content,ObjectFactory factory, WordprocessingMLPackage  wordMLPackage,boolean bold,boolean center,int size,int merge) {
    Tc tableCell = factory.createTc();

    org.docx4j.wml.TcPr tcpr = factory.createTcPr();
    tableCell.setTcPr(tcpr);
    CTVerticalJc valign = factory.createCTVerticalJc();
    valign.setVal(STVerticalJc.TOP);
    tcpr.setVAlign(valign);
    org.docx4j.wml.TcPrInner.GridSpan gspan = factory.createTcPrInnerGridSpan();
    gspan.setVal(new BigInteger("" + merge));
    tcpr.setGridSpan(gspan);

    org.docx4j.wml.P  p = factory.createP();
    org.docx4j.wml.Text  t = factory.createText();
    t.setValue(content);
    org.docx4j.wml.R  run = factory.createR();
    run.getContent().add(t);

    org.docx4j.wml.RPr rpr = factory.createRPr();
    org.docx4j.wml.PPr ppr = factory.createPPr();

    org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
    b.setVal(true);

    if(bold){
        rpr.setB(b);
    }
    if(center){
        PPrBase.TextAlignment ta = new PPrBase.TextAlignment();
        ta.setVal("center");
        ppr.setTextAlignment(ta);
    }

    PPrBase.Spacing spacing = new PPrBase.Spacing();
    spacing.setAfter(BigInteger.ZERO);
    ppr.setSpacing(spacing)
    p.setPPr(ppr);

    changeFontToTimesNewR(rpr);
    changeFontSize(rpr,size);
    run.setRPr(rpr);
    /*   org.docx4j.wml.ParaRPr paraRpr = factory.createParaRPr();
       ppr.setRPr(paraRpr);*/

    p.getContent().add(run);


    tableCell.getContent().add(p);
    tableRow.getContent().add(tableCell);
}
private static void addTableCell(Tr tableRow, String content,ObjectFactory factory, WordprocessingMLPackage  wordMLPackage,boolean bold,boolean center,int size) {
    Tc tableCell = factory.createTc();

    org.docx4j.wml.P  p = factory.createP();
    org.docx4j.wml.Text  t = factory.createText();
    t.setValue(content);
    org.docx4j.wml.R  run = factory.createR();
    run.getContent().add(t);

        org.docx4j.wml.RPr rpr = factory.createRPr();
        org.docx4j.wml.PPr ppr = factory.createPPr();

    org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
        b.setVal(true);

        if(bold){
            rpr.setB(b);
        }
        if(center){
            PPrBase.TextAlignment ta = new PPrBase.TextAlignment();
            ta.setVal("center");
            ppr.setTextAlignment(ta);
        }

    PPrBase.Spacing spacing = new PPrBase.Spacing();
    spacing.setAfter(BigInteger.ZERO);
    ppr.setSpacing(spacing)
    p.setPPr(ppr);

    changeFontToTimesNewR(rpr);
    changeFontSize(rpr,size);
    run.setRPr(rpr);
     /*   org.docx4j.wml.ParaRPr paraRpr = factory.createParaRPr();
        ppr.setRPr(paraRpr);*/

    p.getContent().add(run);


    tableCell.getContent().add(p);
    tableRow.getContent().add(tableCell);
}
private static void addTableCell(Tr tableRow, String content,ObjectFactory factory, WordprocessingMLPackage  wordMLPackage) {
    Tc tableCell = factory.createTc();


    tableCell.getContent().add(
            wordMLPackage.getMainDocumentPart().createParagraphOfText(content));
    tableRow.getContent().add(tableCell);
}
public static void addBorders(Tbl table)
{
    table.setTblPr(new TblPr());
    CTBorder border = new CTBorder();
    border.setColor("auto");
    border.setSz(new BigInteger("4"));
    border.setSpace(BigInteger.ZERO);
    border.setVal(STBorder.SINGLE);

    TblBorders borders = new TblBorders();
    borders.setBottom(border);
    borders.setLeft(border);
    borders.setRight(border);
    borders.setTop(border);
    borders.setInsideH(border);
    borders.setInsideV(border);
    table.getTblPr().setTblBorders(borders);
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
public org.docx4j.wml.R newImageRun( WordprocessingMLPackage wordMLPackage,
                                  byte[] bytes,
                                  String filenameHint, String altText,
                                  int id1, int id2, long cx) throws Exception {

    BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

    Inline inline = imagePart.createImageInline( filenameHint, altText,
            id1, id2, cx);

    // Now add the inline in w:p/w:r/w:drawing
    org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    org.docx4j.wml.R  run = factory.createR();
    //p.getParagraphContent().add(run);
    org.docx4j.wml.Drawing drawing = factory.createDrawing();
    run.getRunContent().add(drawing);
    drawing.getAnchorOrInline().add(inline);

    return run;

}
def createTableOfStops(def stops){
    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
    ObjectFactory factory = Context.getWmlObjectFactory();

    Tbl table = factory.createTbl();
    Tr tableRow1 = factory.createTr();
    addTableCell(tableRow1, "№",factory,wordMLPackage);
    addTableCell(tableRow1, "Назва зупинки",factory,wordMLPackage);
    table.getContent().add(tableRow1);

    int i = 1;
    stops.each{
        stop->
            Tr tableRow = factory.createTr();
            addTableCell(tableRow, ""+i,factory,wordMLPackage);
            addTableCell(tableRow, stop.name,factory,wordMLPackage);
            table.getContent().add(tableRow);
            i++;
    }
    return table;

}
byte [] getFreshRouteImage(def folder,def route_number){
    File fold = new File(folder);
    byte [] ans = null;
    fold.listFiles().each{
      fl->
        def rname = fl.name.split("_")[0];
          if(rname == route_number){
              //File imageFile = fl;
              if(fl.exists()){
                  BufferedImage input = ImageIO.read(fl);
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  ImageIO.write(input, "PNG", baos);
                  ans = baos.toByteArray();
              }

             return;
          }
    }
    return ans;

}

private WordprocessingMLPackage getTemplate(String name) throws Docx4JException, FileNotFoundException {
    WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(name)));
    return template;
}

private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
    List<Object> result = new ArrayList<Object>();
    if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

    if (obj.getClass().equals(toSearch))
        result.add(obj);
    else if (obj instanceof ContentAccessor) {
        List<?> children = ((ContentAccessor) obj).getContent();
        for (Object child : children) {
            result.addAll(getAllElementFromObject(child, toSearch));
        }

    }
    return result;
}


/*private static void replaceMapImage(def newimgp, Tr workingRow) {
    List<Object> paragraphs = getAllElementFromObject(workingRow, P.class);

    for (Object p : paragraphs) {
        List<Object> draws = getAllElementFromObject(p, Drawing.class);
        if(draws.size()>0){
            p = newimgp;
            break;
            //toReplace = (P) p;
        }
    }
}*/
private static void replaceMapImage(ObjectFactory factory,def newimgr, Tr workingRow) {
    //Tc tblCell = factory.createTc();
    //tblCell.getContent().add(newimgp);
    //workingRow.getContent().remove(0);
    //workingRow.getContent().get(0).s;
    List<Object> paragraphs = getAllElementFromObject(workingRow, P.class);

    for (Object p : paragraphs) {
        List<Object> draws = getAllElementFromObject(p, Drawing.class);
        if(draws.size()>0){
            ((P)p).getParagraphContent().remove(0);
            ((P)p).getParagraphContent().add(newimgr);
            break;
            //toReplace = (P) p;
        }
    }
}
private void replacePlaceholder(WordprocessingMLPackage template, String name, String placeholder ) {
    List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

    for (Object text : texts) {
        Text textElement = (Text) text;
        if (textElement.getValue().equals(placeholder)) {
            textElement.setValue(name);
        }
    }
}
private void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template, ContentAccessor addTo) {
    // 1. get the paragraph
    List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);

    P toReplace = null;
    for (Object p : paragraphs) {
        List<Object> texts = getAllElementFromObject(p, Text.class);
        for (Object t : texts) {
            Text content = (Text) t;
            if (content.getValue().equals(placeholder)) {
                toReplace = (P) p;
                break;
            }
        }
    }

    // we now have the paragraph that contains our placeholder: toReplace
    // 2. split into seperate lines
    String [] ass = StringUtils.splitPreserveAllTokens(textToAdd, '\n');

    for (int i = 0; i < ass.length; i++) {
        String ptext = ass[i];

        // 3. copy the found paragraph to keep styling correct
        P copy = (P) XmlUtils.deepCopy(toReplace);

        // replace the text elements from the copy
        List<?> texts = getAllElementFromObject(copy, Text.class);
        if (texts.size() > 0) {
            Text textToReplace = (Text) texts.get(0);
            textToReplace.setValue(ptext);
        }

        // add the paragraph to the document
        addTo.getContent().add(copy);
    }

    // 4. remove the original one
    ((ContentAccessor)toReplace.getParent()).getContent().remove(toReplace);

}
private Tbl getTemplateTable(List<Object> tables, String templateKey) throws Docx4JException, JAXBException {
    for (Iterator<Object> iterator = tables.iterator(); iterator.hasNext();) {
        Object tbl = iterator.next();
        List<?> textElements = getAllElementFromObject(tbl, Text.class);
        for (Object text : textElements) {
            Text textElement = (Text) text;
            if (textElement.getValue() != null && textElement.getValue().equals(templateKey))
                return (Tbl) tbl;
        }
    }
    return null;
}
private int getInsetPositionAfter(String tag,WordprocessingMLPackage template){
    Body b = template.getMainDocumentPart().getJaxbElement().getBody();
    int addPoint = -1, count = 0;
    def lst =  b.getEGBlockLevelElts();
    for (Object o : lst) {
        if (o instanceof JAXBElement){
            o = ((JAXBElement<?>) o).getValue();
            if(o.getClass().equals(Tbl.class)){
                List<?> textElements = getAllElementFromObject(o, Text.class);
                for (Object text : textElements) {
                    Text textElement = (Text) text;
                    def val = textElement.getValue();
                    if (textElement.getValue() != null && textElement.getValue().equals(tag)){
                        addPoint = count + 1;
                        break;
                    }

                }
            }

           if(addPoint!=-1) break;
        }
        count++;
    }
   return addPoint;
}
private static void addRowToTable(Tbl reviewtable, Tr templateRow, Map<String, String> replacements) {
    Tr workingRow = (Tr) XmlUtils.deepCopy(templateRow);
    List<?> textElements = getAllElementFromObject(workingRow, Text.class);
    for (Object object : textElements) {
        Text text = (Text) object;
        String replacementValue = (String) replacements.get(text.getValue());
        if (replacementValue != null)
            text.setValue(replacementValue);
    }

    reviewtable.getContent().add(workingRow);
}
private static void editRowInTable(Tbl reviewtable, Tr templateRow, Map<String, String> replacements) {
    Tr workingRow = templateRow;
    List<?> textElements = getAllElementFromObject(workingRow, Text.class);
    for (Object object : textElements) {
        Text text = (Text) object;
        String replacementValue = (String) replacements.get(text.getValue());
        if (replacementValue != null)
            text.setValue(replacementValue);
    }

    //reviewtable.getContent().add(workingRow);
}
private void replaceTable1(String[] placeholders, List<Map<String, String>> textToAdd,
                          WordprocessingMLPackage template) throws Docx4JException, JAXBException {
    List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

    // 1. find the table
    Tbl tempTable = getTemplateTable(tables, placeholders[0]);
    List<Object> rows = getAllElementFromObject(tempTable, Tr.class);

    // first row is header, second row is content
    if (rows.size() == 4) {
        // this is our template row
        Tr templateRow = (Tr) rows.get(3);

        for (Map<String, String> replacements : textToAdd) {
            // 2 and 3 are done in this method
            addRowToTable(tempTable, templateRow, replacements);
        }

        // 4. remove the template row
        tempTable.getContent().remove(templateRow);
    }
}
private void replaceTable2(String[] placeholders, List<Map<String, String>> textToAdd,
                           WordprocessingMLPackage template,int base_row) throws Docx4JException, JAXBException {
    List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

    // 1. find the table
    Tbl tempTable = getTemplateTable(tables, placeholders[0]);
    List<Object> rows = getAllElementFromObject(tempTable, Tr.class);

    // first row is header, second row is content
    if (rows.size() > base_row) {
        // this is our template row
        Tr templateRow = (Tr) rows.get(base_row);

        for (Map<String, String> replacements : textToAdd) {
            // 2 and 3 are done in this method
            addRowToTable(tempTable, templateRow, replacements);
        }

        // 4. remove the template row
        tempTable.getContent().remove(templateRow);
    }
}
private void replaceTable3Row(String[] placeholders, Map<String, String> textToAdd,
                           WordprocessingMLPackage template,int base_row) throws Docx4JException, JAXBException {
    List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

    // 1. find the table
    Tbl tempTable = getTemplateTable(tables, placeholders[0]);
    List<Object> rows = getAllElementFromObject(tempTable, Tr.class);

    // first row is header, second row is content
    if (rows.size() > base_row) {
        // this is our template row
        Tr templateRow = (Tr) rows.get(base_row);
        editRowInTable(tempTable, templateRow, textToAdd)
    }
}

private void copyTablesFromTmpl(String placeholder,String replacement,WordprocessingMLPackage template, int times,int tmpl_row_number) throws Docx4JException, JAXBException {
    List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
   // 1. find the table
    Tbl tempTable = getTemplateTable(tables, placeholder);
    Body b = template.getMainDocumentPart().getJaxbElement().getBody();
    int afterPos = getInsetPositionAfter(placeholder,template);
    int templatePos = afterPos - 1;
    org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    for(int i = 0; i < times;i++){
        Tbl copiedTable = (Tbl) XmlUtils.deepCopy(tempTable);

        List<Object> rows = getAllElementFromObject(copiedTable, Tr.class);
        Tr templateRow = (Tr) rows.get(tmpl_row_number);
        def replacer = [:]
        replacer.put(placeholder,replacement+"_"+i);
        //  ["MY_N":"MY_N_"+i]
        editRowInTable(copiedTable, templateRow,replacer);
        //copiedTable.getContent().remove(templateRow);

        org.docx4j.wml.P  p = factory.createP();
        b.getEGBlockLevelElts().add(afterPos, p);
        afterPos++;
        b.getEGBlockLevelElts().add(afterPos, copiedTable);
        afterPos++;
        //template.getMainDocumentPart().addParagraphOfText("");
        //template.getMainDocumentPart().addObject(copiedTable);
    }
    b.getEGBlockLevelElts().remove(templatePos);

}
def writeTramsToDoc2(def trams){
    WordprocessingMLPackage template = getTemplate("/home/reshet/transport/trams/report_example_true5.docx");

    ObjectFactory factory = Context.getWmlObjectFactory();

    //Tbl table = factory.createTbl();
    //addBorders(table);
    //Tr tableRow = factory.createTr();


   /* addTableCell(tableRow, "Номер маршруту",factory,wordMLPackage,true,false,20);
    addTableCell(tableRow, "Напрямок",factory,wordMLPackage,true,false,20);
    addTableCell(tableRow, "Довжина маршруту",factory,wordMLPackage,true,false,20);
    addTableCell(tableRow, "Тривалість оборотного",factory,wordMLPackage,true,false,20);*/
    //addTableCell(tableRow, "Початок роботи",factory,wordMLPackage,true,false,20);
    //addTableCell(tableRow, "Останнє відправлення",factory,wordMLPackage,true,false,20);

    //String placeholder = "SJ_EX1";
    //String toAdd = "jos\ndirksen";

    //replaceParagraph(placeholder, toAdd, template, template.getMainDocumentPart());
    //replacePlaceholder(template,"ssssss",placeholder);
    //table.getContent().add(tableRow);
    int i = 1;

   /* Map<String,String> repl1 = new HashMap<String, String>();
    repl1.put("MY_N", "function1");*/
    //repl1.put("SJ_DESC", "desc1");
    //repl1.put("SJ_PERIOD", "period1");

   /* Map<String,String> repl2 = new HashMap<String, String>();
    repl2.put("SJ_FUNCTION", "function2");
    repl2.put("SJ_DESC", "desc2");
    repl2.put("SJ_PERIOD", "period2");

    Map<String,String> repl3 = new HashMap<String, String>();
    repl3.put("SJ_FUNCTION", "function3");
    repl3.put("SJ_DESC", "desc3");
    repl3.put("SJ_PERIOD", "period3");*/


    String [] repl = ["MYNTRAM","name_route","len_r","try_r","t_start1","t_start2","t_end1","t_end2"];

    def replaces = [];
    trams.each{  tram->
       /* Tr tableRow1 = factory.createTr();
        addTableCell(tableRow1, tram.number,factory,wordMLPackage,false,false,16);
        addTableCell(tableRow1, tram.name,factory,wordMLPackage,false,false,16);
        addTableCell(tableRow1, tram.L,factory,wordMLPackage,false,false,16);
        addTableCell(tableRow1, tram.T,factory,wordMLPackage,false,false,16);
        //addTableCell(tableRow1, tram.start,factory,wordMLPackage,false,false,16);
        //addTableCell(tableRow1, tram.end,factory,wordMLPackage,false,false,16);

        table.getContent().add(tableRow1);*/
        def replace_row = [MYNTRAM: tram.number,name_route:tram.name,len_r: tram.L,
                           try_r:tram.T,
                           T_start1:tram.start,T_start2:tram.start,
                           T_end1: tram.end,T_end2: tram.end]
        replaces.add(replace_row);
        i++;
    }
    replaceTable1(repl, replaces, template);

    //copyTablesFromTmpl(repl[0],"MY_N_TRAM",template,3);

    String filenameHint = null;
    String altText = null;
    int id1 = 0;
    int id2 = 1;

    //def route8 = readStopsInfo("/home/reshet/transport/trams/input/",12);
    //println route8["pik"];
    //byte [] img = route8["img"];
    /*org.docx4j.wml.P p = newImage( wordMLPackage, img,filenameHint, altText,id1, id2,6000);
    Tr tableRowImg = factory.createTr();
    Tc tableCell = factory.createTc();
    tableCell.getContent().add(p);
    tableRowImg.getContent().add(tableCell);
*/
    //table.getContent().add(tableRowImg);

    /*wordMLPackage.getMainDocumentPart().addParagraphOfText("");
    wordMLPackage.getMainDocumentPart().addObject(table);

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle","Додаток 5");
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle","Схеми та характеристики трамвайних маршрутів");
*/

    def transp_measures = readAllRoutesInfo("/home/reshet/transport/trams/input/");
    def transp_set = transp_measures.keySet();
    copyTablesFromTmpl("MY_NUMB_TRAM_TMPL","MY_NUMB_TRAM",template,transp_set.size(),1);
    i = 0;
    transp_set.each{  tram_n->

        def tram_info = findTramInfo(tram_n,trams)
        def replaces2 = [];
        def name = "";
        def agency = "";
        if(tram_info != null){
            name = tram_info.name;
            agency = tram_info.agency;
        }
        def tram_n_holder = "MY_NUMB_TRAM_"+i;
        String [] repl2 = [tram_n_holder];
        def replace_row = [name_route:name,Name_agency:agency];
        replace_row.put(tram_n_holder,tram_n);
        replaces2.add(replace_row);


        byte [] img_r = transp_measures[tram_n]["img"];
        if(img_r != null){
            //org.docx4j.wml.P pp = newImage(template, img_r,filenameHint, altText,id1, id2,9300);
            org.docx4j.wml.R r = newImageRun(template, img_r,filenameHint, altText,id1, id2,9300);
            List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
            Tbl tTable = getTemplateTable(tables,tram_n_holder);
            List<Object> rows = getAllElementFromObject(tTable, Tr.class);
            replaceMapImage(factory,r,rows.get(3))

        }

        //replaceTable1(repl2, replaces2, template);



        def reps = []
        String [] repl3 = [tram_n_holder];


        def first = transp_measures[tram_n]["pik"]["first"];
        def reverse = transp_measures[tram_n]["pik"]["reverse"];
        int k_rows = Math.max(first.size(),reverse.size());
        for(int j = 0; j < k_rows;j++){
            def replrow = [:];
            //Tr tableR = factory.createTr();
            if(j<first.size()){
                replrow.put("stop_n_start",(j+1)+"");
                replrow.put("stop_name_start", first[j].name);
                //addTableCell(tableR, (j+1)+"",factory,wordMLPackage,false,false,16)
                //addTableCell(tableR, first[j].name,factory,wordMLPackage,false,false,16)
            }else{
                replrow.put("stop_n_start","");
                replrow.put("stop_name_start","");

                //addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
                //addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
            }
            if(j<reverse.size()){
                replrow.put("Stop_n_end",(j+1)+"");
                replrow.put("stop_name_end", reverse[j].name);
                /*addTableCell(tableR, ""+(j+1),factory,wordMLPackage,false,false,16)
                addTableCell(tableR, reverse[j].name,factory,wordMLPackage,false,false,16)*/
            }else{
                replrow.put("Stop_n_end","");
                replrow.put("stop_name_end","");
           /*     addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
                addTableCell(tableR, "",factory,wordMLPackage,false,false,16);*/
            };
            reps.add(replrow);
            //tbl.getContent().add(tableR);
        }
        replaceTable2(repl3, reps, template,6);

        replaceTable3Row(repl2, replace_row, template,1);


        /*
        Tr tableRow4 = factory.createTr();
        addTableCell(tableRow4, "Перелік зупинок на маршруті",factory,wordMLPackage,true,true,24,4);
        tbl.getContent().add(tableRow4);

        Tr tableRow5 = factory.createTr();
        addTableCell(tableRow5, "Прямий напрямок",factory,wordMLPackage,true,true,24,2);
        addTableCell(tableRow5, "",factory,wordMLPackage);
        addTableCell(tableRow5, "",factory,wordMLPackage);
        addTableCell(tableRow5, "Зворотній напрямок",factory,wordMLPackage,true,true,24,2);

        tbl.getContent().add(tableRow5);

        *//*Tr tableRow6 = factory.createTr();
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(createTableOfStops(transp_measures[tram_n]["pik"]["first"]));
        tableRow6.getContent().add(tableCell);
        tableCell = factory.createTc();
        tableCell.getContent().add(createTableOfStops(transp_measures[tram_n]["pik"]["reverse"]));
        tableRow6.getContent().add(tableCell);
        tbl.getContent().add(tableRow6);
*//*

        Tr tableRow6 = factory.createTr();
        addTableCell(tableRow6, "№",factory,wordMLPackage,true,false,20);
        addTableCell(tableRow6, "Назва зупинки",factory,wordMLPackage,true,false,20);
        addTableCell(tableRow6, "",factory,wordMLPackage);
        addTableCell(tableRow6, "№",factory,wordMLPackage,true,false,20);
        addTableCell(tableRow6, "Назва зупинки",factory,wordMLPackage,true,false,20);
        tbl.getContent().add(tableRow6);

        def first = transp_measures[tram_n]["pik"]["first"];
        def reverse = transp_measures[tram_n]["pik"]["reverse"];
        int k_rows = Math.max(first.size(),reverse.size());
        for(int j = 0; j < k_rows;j++){
            Tr tableR = factory.createTr();
            if(j<first.size()){
                addTableCell(tableR, (j+1)+"",factory,wordMLPackage,false,false,16)
                addTableCell(tableR, first[j].name,factory,wordMLPackage,false,false,16)
            }else{
                addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
                addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
            };
            addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
            if(j<reverse.size()){
                addTableCell(tableR, ""+(j+1),factory,wordMLPackage,false,false,16)
                addTableCell(tableR, reverse[j].name,factory,wordMLPackage,false,false,16)
            }else{
                addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
                addTableCell(tableR, "",factory,wordMLPackage,false,false,16);
            };
            tbl.getContent().add(tableR);
        }


        wordMLPackage.getMainDocumentPart().addParagraphOfText("");
        //addBorders(tbl);
        wordMLPackage.getMainDocumentPart().addObject(tbl);
        i++;
    }*/
        i++;
    }


    template.save(new File("/home/reshet/transport/trams/tmpl20.docx"));


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


