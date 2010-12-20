



def main() {
	basedir = '/home/jsmith/jsi/fce-release-2/fcw/WebRoot/WEB-INF'
	APPCTX_PATTERN = ~/applicationContext.*xml/
	new File(basedir).eachFileMatch(APPCTX_PATTERN) { file ->
		println "Found file $file"
	}
}

main()


