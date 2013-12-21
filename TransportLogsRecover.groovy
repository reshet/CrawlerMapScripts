
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 12/16/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */

//File log1 = new File("/home/reshet/transportlogs/mss.log");
String fname = "/home/reshet/transportlogs/mss_backup12112013.log";
File log1 = new File(fname);
File log_out = new File(fname+".found");

int c = 0;
int seq = 0;
/*log1.eachLine{
    ln->
        if(seq == 11){
            c++;
            println c+" "+ln
            seq = 0;
        }else
        if(seq >= 3){
            //c++;
            //println c+" "+ln
            seq++;
        }else{
            if(ln.contains("Packet 1 has 0 ints"))
            {
                seq=1;
                //c++;
                //println c+" "+ln
            };
            if(ln.contains("Packet 2 has 0 ints"))
            {
                seq=2;
                //c++;
                //println c+" "+ln
            };
            if(ln.contains("Packet 3 has 0 ints"))
            {
                seq=3;
            };
        }

}*/

class Prev4{
    private String[] prev = new String[4];
    public void push(String str){
        prev[0] = prev[1];
        prev[1] = prev[2];
        prev[2] = prev[3];
        prev[3] = str;
    }
    public String getFirst(){
        return prev[0];
    }
}
Prev4 prevs = new Prev4();
log_out.withWriter {
    out->
    log1.eachLine{
        ln->
        prevs.push(ln);
        if(seq == 3){
            c++;
            def st = prevs.getFirst();
            st = st.substring(0,st.indexOf("</RQ>")+5)
            println c+" "+st;
            out << st << "\n";
            seq = 0;
        }else{
            if(ln.contains("Packet 1 has 0 ints"))
            {
                seq=1;
                //c++;
                //println c+" "+ln
            };
            if(ln.contains("Packet 2 has 0 ints"))
            {
                seq=2;
                //c++;
                //println c+" "+ln
            };
            if(ln.contains("Packet 3 has 0 ints"))
            {
                seq=3;
            };
        }

    }
}
