
/**
 * Created with IntelliJ IDEA.
 * User: reshet
 * Date: 11/3/13
 * Time: 10:00 AM
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
) @Grab(group = 'org.codehaus.gpars', module = 'gpars', version = '1.1.0') @Grab(
        group = 'net.sourceforge.jexcelapi',
        module = 'jxl',
        version = '2.6.12'
) @Grab(group = 'org.codehaus.gpars', module = 'gpars', version = '1.1.0')
import java.lang.Object



@Grab(group='org.ccil.cowan.tagsoup',
        module='tagsoup', version='1.2' )
def tagsoupParser = new org.ccil.cowan.tagsoup.Parser() as Object
def slurper = new XmlSlurper(tagsoupParser)

def text = new File("/var/www/DEBUG/mss.log").getText("UTF-8");
def xml = slurper.parseText(text);

xml."**".findAll{it.name().toString().startsWith("interview")}.each{
    elem ->
        println elem.toString();


}
