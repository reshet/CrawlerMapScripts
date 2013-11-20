
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  File f = new File("/home/reshet/cvkkiev/addresses.csv")
  File f2 = new File("/var/www/InteractiveMaps/php/geocache.csv")
  File f3 = new File("/var/www/InteractiveMaps/js/addresses_merged_polygons.json")
  f3.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper


def polygons = [:]

public boolean hasDigit(String st){
    return st.contains("1")||st.contains("2")||st.contains("3")||st.contains("4") ||st.contains("5")||st.contains("6")||st.contains("7")||st.contains("8")||st.contains("9")||st.contains("0")
}
public String [] toAddresses(def arr){
    //println ""
    //println arr
    def final_arr = []
    boolean street_found = false
    String curr_street = ""
    for(int i = 0; i < arr.length;i++){
        String elem = arr[i]
            elem = elem.replaceAll(" ","")
            elem = elem.replaceAll(",","")
            elem = elem.replaceAll("/.","")
            elem = elem.replaceAll(":","")
    if(elem.length()>0 && elem!=" "){
            //println elem
            if(!street_found){
                if(elem.length()>3)curr_street = curr_street+elem+" "
                else{
                    if(elem.length()<=4 && hasDigit(elem)){
                        final_arr.add(curr_street+" "+elem)
                    }
                    if(curr_street.length()>3) street_found = true;
                }
            }else{
                if(elem.length()<=4 && hasDigit(elem)){
                    final_arr.add(curr_street+" "+elem)
                }
                else if(elem.length()>3){
                    curr_street = elem+" "
                    street_found = false
                }
            }
        }

     }
    if(curr_street.length()>3 && !street_found) final_arr.add(curr_street);

    return final_arr
}

f.withReader {
    ff->
        ff.eachLine {
            ln->
                def lnarr = ln.split(";")
                if(lnarr.length > 2){
                    def add_center = lnarr[2]
                    def title = lnarr[0]+lnarr[1]
                    add_center = add_center.replaceAll("\"","'");
                    add_center = add_center.substring(add_center.lastIndexOf("Київ"),add_center.length());
                    //println add_center
                    def add_bounds = lnarr[3]
                    def comp = add_bounds.toLowerCase();
                    comp = comp.replaceAll("будинки №№"," ")
                    comp = comp.replaceAll("будинок №"," ")
                    comp = comp.replaceAll("будинки, №№"," ")
                    comp = comp.replaceAll("- всі будинки"," ")
                    comp = comp.replaceAll("всі будинки"," ")
                    comp = comp.replaceAll("(один будинок)"," ")
                    comp = comp.replaceAll("один будинок"," ")
                    comp = comp.replaceAll("буд. №№"," ")
                    comp = comp.replaceAll("буд. №"," ")
                    comp = comp.replaceAll("буд."," ")
                    comp = comp.replaceAll("№№"," ")
                    comp = comp.replaceAll("№"," ")
                    comp = comp.replaceAll("приватні будинки"," ")



                   // , сектору, непарної, сторони
                    comp = comp.replaceAll("вулиця"," ")
                    comp = comp.replaceAll("приватного"," ")
                    comp = comp.replaceAll("сектору"," ")
                    comp = comp.replaceAll("непарної"," ")
                    comp = comp.replaceAll("парної"," ")
                    comp = comp.replaceAll("сторони"," ")
                    comp = comp.replaceAll("сторони"," ")


                    comp = comp.replaceAll("ці:"," ")
                    comp = comp.replaceAll("лки:"," ")



                    comp = comp.replaceAll(" ця "," ")
                    comp = comp.replaceAll("ця "," ")
                    comp = comp.replaceAll(" з "," ")
                    comp = comp.replaceAll(" до "," ")
                    comp = comp.replaceAll(",з "," ")
                    comp = comp.replaceAll(",до "," ")

                    comp = comp.replaceAll("//.//."," ")
                    comp = comp.replaceAll("//."," ")
                    //comp = comp.replaceAll("//|"," ")
                    comp = comp.replaceAll("-"," ")
                    comp = comp.replaceAll("  "," ")
                    comp = comp.replaceAll("всі приватного сектора у межаж"," ")




                    def arr_final = []
                    if(comp.contains("вул.")||comp.contains("вулиця")
                     ||comp.contains("проспект")||comp.contains("просп.")
                     ||comp.contains("бульвар")||comp.contains("бульв.") ||comp.contains("бул.")
                     ||comp.contains("провулок")||comp.contains("пров.")){

                        def arr1 = comp.split(/вул.|вулиця|проспект|просп.|пр.|бульвар|бульв.|бул.|провулок|пров./)
                        arr1.eachWithIndex{
                            el,ind ->
                                def arr2 = el.split(/ |,/);
                                if(arr2.length>0)
                                    toAddresses(arr2).each{
                                        arr_final.add(it)
                                    }
                                else
                                    if(el!=" ")arr_final.add(el)
                        }

                        //println arr1
                    }else{
                        def arr2 = comp.split(/ |,/);
                        if(arr2.length>0)
                            toAddresses(arr2).each{
                                arr_final.add(it)
                            }

                        else
                        if(comp!=" ")arr_final.add(comp)
                    }

                   // println arr_final
                    polygons[title]=["address":add_center,"bounds":arr_final]
                }


        }
}

f3.withWriter {
    ff3->
        ff3 << "[\n"
            int p_count = polygons.size()
            polygons.eachWithIndex{
                elem,index->
                   ff3 << "{\"address\":\""+elem.value.address+"\", \"title\":\""+elem.key+"\",\"bounds\":\n["
                    int b_count = elem.value.bounds.size()
                    elem.value.bounds.eachWithIndex{
                        el,ind->
                            ff3 << "{\"address\":\"Київ "+el+"\"}"
                            if(ind < b_count -1)ff3 << ","
                            ff3 << "\n"
                    }
                   ff3 <<  "]}"
                   if(index < p_count -1)ff3 << ","
                   ff3 << "\n"
            }
        ff3 << "]\n"

}
