// http://groovy.codehaus.org/groovy-jdk/

class Node {
    String name
    String clazz
    String parent
    String file
    Set<String> links
    Node(String name, String clazz, String parent, String file, Collection<String> links) {
        this.name = name
        this.clazz = clazz
        this.parent = parent
        this.file = file
        this.links = new HashSet<String>(links)
    }
    public String toString() {
    	return "Node(${name}[${clazz}] < ${parent} : ${links})"
    }
}

def parseAppCtx(file) {
	def nodes = []
    def slurper = new XmlSlurper()
    def beans = slurper.parse(file)
    beans.bean.each { bean ->
   	    def links = []
        name = bean.@id.text()
        clazz = bean.@class.text()
        parent = bean.@parent.text()

        // find *-ref injections
        bean.attributes().keySet().each { 
        	if (it =~ /.*-ref/) {
        		links << bean.attributes().get(it)
        	}
        }
        
        // find <property ref="*"> injections
        bean.'**'.grep { it.@ref != '' }.'@ref'.each { links << it }
        
        // find <property><ref bean="*"> injections
        bean.'**'.grep { it.@bean != '' }.'@bean'.each {links << it }
        
        def node = new Node(name, clazz, parent, file.name, links)
        nodes << node
    }
    nodes
}

def generateXML(Collection<Node> nodes) {
	StringBuilder xml = new StringBuilder()
	
	// Header
	xml.append(
"""<?xml version="1.0" encoding="UTF-8"?>
<graphml xmlns="http://graphml.graphdrawing.org/xmlns" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">""")
	
	nodes.eachWithIndex { node, idx ->
		id = node.name != null & node.name.length() > 0 ? node.name : node.clazz
		if (id == null || id.length() == 0) return
		xml.append("""<node id="${id}"/>\n""")
		node.links.each { link -> 
			xml.append("""<edge source="${id}" target="${link}" />\n""")
		}
	}
	xml.append("""</graphml>""")
	xml.toString()
}


@Grab(group='org.codehaus.gpars', module='gpars', version='0.11')
def main() {
    basedir = '/home/jsmith/jsi/fce-release-2/fcw/WebRoot/WEB-INF'
    APPCTX_PATTERN = ~/applicationContext.*xml/
//    APPCTX_PATTERN = ~/applicationContext-validators.xml/
    APPCTX_PATTERN = ~/a.*.*xml/
    def nodes = []
    new File(basedir).eachFileMatch(APPCTX_PATTERN) { file ->
        nodes.addAll( parseAppCtx(file) )
    }
    println generateXML(nodes)
}

main()
