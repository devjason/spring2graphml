// http://groovy.codehaus.org/groovy-jdk/

def parseAppCtx(file) {
	def slurper = new XmlSlurper()
	def beans = slurper.parse(file)
	beans.bean.each { bean ->
		println bean.@id.text()
		bean.attributes().keySet().findAll { it =~ /.*ref/ }
	}
}


@Grab(group='org.codehaus.gpars', module='gpars', version='0.11')
def main() {
	basedir = '/home/jsmith/jsi/fce-release-2/fcw/WebRoot/WEB-INF'
	APPCTX_PATTERN = ~/applicationContext.*xml/
	APPCTX_PATTERN = ~/applicationContext-database.xml/
	parsers = []
	new File(basedir).eachFileMatch(APPCTX_PATTERN) { file ->
		parseAppCtx(file)
	}
}

main()


