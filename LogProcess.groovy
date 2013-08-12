
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/1/13
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
 File file = new File("/home/reshet/workspace-idea/ParallelPhoneGrabber/src/crawling.log")
 file.withReader {
     f->
     f.eachLine { line ->
         //println line
         def arr = line.split("__")
         def st = arr[1]
         if(st.contains(".csv") && !st.contains("NUMBERS"))   {
             //println st
             def ar = st.split(".csv")
             if(ar[0].contains("_")){
                 def ar2 = ar[0].split("_")
                 def ar1 = ar[1].split(" ")
                 println ar2[0]+" "+ar1[1]
             }

         }

     }
 }