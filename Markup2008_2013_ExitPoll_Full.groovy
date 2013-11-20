
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 8/7/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
  //File f = new File("/var/www/InteractiveMaps/js/addresses_merged.json")
  File f = new File("/home/reshet/cvkkiev/Mayor_Election_2008_streets.csv")
  File f2 = new File("/home/reshet/cvkkiev/Mayor_Election_2013_streets.csv")

  File f3 = new File("/var/www/InteractiveMaps/js/mer_2008_exitpoll_full.json")
  f3.createNewFile()
  File f4 = new File("/var/www/InteractiveMaps/js/mer_2013_exitpoll_full.json")
  f4.createNewFile()

@Grab('net.sf.json-lib:json-lib:2.3:jdk15')
import net.sf.json.groovy.JsonSlurper

def slurper = new JsonSlurper()

def candidates_2008 = [
        "-99":[name:"Нет ответа","votes":[]],
        "1":[name:"ЧЕРНОВЕЦКИЙ","votes":[]],
        "2":[name:"ТУРЧИНОВ","votes":[]],
        "3":[name:"КЛИЧКО","votes":[]],
        "4":[name:"ПИЛИПИШИН","votes":[]],
        "5":[name:"КАТЕРИНЧУК","votes":[]],
        "6":[name:"ГОРБАЛЬ","votes":[]],
        "7":[name:"ОМЕЛЬЧЕНКО","votes":[]],
        "8":[name:"ТЯГНИБОК","votes":[]],
        "9":[name:"За другого кандидата","votes":[]],
        "10":[name:"Против всех","votes":[]],
        "11":[name:"Не помню","votes":[]],
        "12":[name:"Трудно сказать / Отказ от ответа","votes":[]]
]
def candidates_2013 = [
        "-99":[name:"Нет ответа","votes":[]],
        "1":[name:"Катеринчук Николай","votes":[]],
        "2":[name: "Ляшко Олег","votes":[]],
        "3":[name: "Бондарчук Сергей","votes":[]],
        "4":[name:"Ильенко Андрей","votes":[]],
        "5":[name:"Попов Александр","votes":[]],
        "6":[name:"Порошенко Петр","votes":[]],
        "7":[name:"Томенко Николай","votes":[]],
        "8":[name:"Против всех кандидатов","votes":[]],
        "9":[name:"За другого кандидата","votes":[]],
        "10":[name:"Трудно сказать, еще не решил","votes":[]]
]
def builder = new groovy.json.JsonBuilder()

f.withReader("CP1251") {
    ff->
        ff.eachLine {
            ln->
                def lnarr = ln.split(";")
                def code = lnarr[0]
                def street = lnarr[1]
                street = "Киев, "+street;
                println street;
                def res = GoogleGeocoderCached.geocode(street,true);
                def coor = GoogleGeocoderCached.parseJSON(res)
                def vote = ["address":street]
                if(coor!= null && coor.lb!= null && coor.mb!= null){
                    vote["lat"]=coor.lb;
                    vote["lng"]=coor.mb;

                }
                if(candidates_2008.containsKey(code)) candidates_2008[code]["votes"].add(vote);
     }
}

f3.withWriter {
    ff3->
        builder(candidates_2008)
        ff3 << builder.toPrettyString()
}

f2.withReader("CP1251") {
    ff2->
        ff2.eachLine {
            ln->
                def lnarr = ln.split(";")
                def code = lnarr[0]
                def street = lnarr[1]
                street = "Киев, "+street;
                println street;
                def res = GoogleGeocoderCached.geocode(street,true);
                def coor = GoogleGeocoderCached.parseJSON(res)
                def vote = ["address":street]
                if(coor!= null && coor.lb!= null && coor.mb!= null){
                    vote["lat"]=coor.lb;
                    vote["lng"]=coor.mb;

                }
                if(candidates_2013.containsKey(code)) candidates_2013[code]["votes"].add(vote);
        }
}





f4.withWriter {
    ff4->
        builder(candidates_2013)
        ff4 << builder.toPrettyString()
}
