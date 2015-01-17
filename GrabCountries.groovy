
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 11/19/13
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */





def text = ("http://www.geonames.org/countries/").toURL().getText();
//println text
@Grab(group='org.ccil.cowan.tagsoup',
        module='tagsoup', version='1.2' )
def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
def slurper = new XmlSlurper(tagsoupParser)

def map = [:]
def xml = slurper.parseText(text)
    xml."**".findAll { it.td.a.@name.toString().size()==2}.eachWithIndex {
        elem, index ->
                def name = elem.td[4].text().toString();
                def code = elem.td[0].a.@name.toString();
            map.put(name,code);

            def str = index+ " "+code+" "+name;
                println str;
                //String route_name = elem.text().toString()
                //String route_link = elem.@id.toString()

}
map.sort();
println map;

def builder = new groovy.json.JsonBuilder()
File f3 = new File("/home/reshet/cities/countries.json");
f3.createNewFile();
f3.withWriter {
    ff3->
        builder(map)
        ff3 << builder.toPrettyString()
}


//http://www.overpass-api.de/api/xapi?node[bbox=68.11,20.12,74.48,24.71][place=city]
//area[name="België - Belgique - Belgien"];(node[place="city"](area););out;