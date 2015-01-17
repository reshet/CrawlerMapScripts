
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 12/16/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */

//File log1 = new File("/home/reshet/transportlogs/mss.log");
String fname = "/home/reshet/transportlogs/foundtotal.log";
File log1 = new File(fname);
File log_out = new File(fname+".good2");

int c = 0;
int seq = 0;

private boolean withinAZ(char ch){
    return (((byte)ch) >= ((byte)'A') && ((byte)ch) <= ((byte)'Z'));
}
private int indexOfNextLetter(String str){
   char [] chars = str.getChars();
    boolean hasCloseTag = false;
    boolean hasIndent = false;

    int index = 0;
    chars.eachWithIndex { char entry, int i ->
        if(index != 0) return;
        if(entry == '"') {
            hasCloseTag = true
        }else
        if(hasCloseTag && entry.isWhitespace()){
            hasIndent = true
        }else
        if(withinAZ(entry) && hasCloseTag && hasIndent) {
            index = i-2;
            hasCloseTag = false;
            hasIndent = false;
            return;
        }else{
            hasCloseTag = false;
            hasIndent = false;
        }
    }
    return index;
}

//here tests
//println indexOfNextLetter("somer");
//println indexOfNextLetter("somer \"");
//println indexOfNextLetter("somer \" ");
//println indexOfNextLetter("somer \" s");
//println indexOfNextLetter("somer \" клм");
//println indexOfNextLetter("somer \"GHJR\" клм");








private String replaceLineParamQuotes(String ln,String symb){
    if(ln.indexOf(symb+"=\"") > 0){
        //c++;
        def st1 = ln.substring(ln.indexOf(symb+"=\"")+2+symb.length(),ln.size());
        def st2 = st1.substring(0,indexOfNextLetter(st1));
        def st3 = st2.replaceAll("\\\\"," ");
        st3 = st3.replaceAll("\""," ");
        st3 = st3.replaceAll("\'"," ");
        //println c+" "+st2;
        //println c+" "+st3;
        println st3;
        if(!st3.contains('+')){
            ln = ln.replaceAll(st2,st3);
        }

    }
    return ln;
}

log_out.withWriter {
    out->
    log1.eachLine{
        ln->
            ln = replaceLineParamQuotes(ln,"Q10_1");
            ln = replaceLineParamQuotes(ln,"Q11_1");
            ln = replaceLineParamQuotes(ln,"Q10");
            ln = replaceLineParamQuotes(ln,"Q11");
            ln = replaceLineParamQuotes(ln,"A2");
            ln = replaceLineParamQuotes(ln,"B2");
            ln = replaceLineParamQuotes(ln,"C2");
            ln = replaceLineParamQuotes(ln,"D2");
            ln = replaceLineParamQuotes(ln,"К2");
            ln = replaceLineParamQuotes(ln,"L2");
            ln = replaceLineParamQuotes(ln,"M2");
            ln = replaceLineParamQuotes(ln,"N2");




            /* if(ln.indexOf("Q10_1=\"") > 0){
                 c++;
                 def st1 = ln.substring(ln.indexOf("Q10_1=\"")+7,ln.size());
                 def st2 = st1.substring(0,st1.indexOf("\" Q"));
                 def st3 = st2.replaceAll("\""," ");
                 st3 = st3.replaceAll("\'"," ");
                 println c+" "+st2;
                 println c+" "+st3;
                 if(st3!="+"){
                     ln = ln.replaceAll(st2,st3);
                 }

             }
             if(ln.indexOf("Q11_1=\"") > 0){
                 c++;
                 def st1 = ln.substring(ln.indexOf("Q11_1=\"")+7,ln.size());
                 def st2 = st1.substring(0,st1.indexOf("\" Q"));
                 def st3 = st2.replaceAll("\""," ");
                 st3 = st3.replaceAll("\'"," ");
                 println c+" "+st2;
                 println c+" "+st3;
                 if(st3!="+"){
                     ln = ln.replaceAll(st2,st3);
                 }
             }*/
            out << ln << "\n";

    }
}
