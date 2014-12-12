import groovy.io.FileType

    def uploadsPath = "/var/www/geocode/uploads"
    def codedPath = "/var/www/geocode/coded"

    def uploadDir = new File(uploadsPath)
    def codedDir = new File(codedPath)


    while(true) {
        def uploadFiles = []
        def codedFiles = []
        uploadDir.eachFileRecurse (FileType.FILES) { file ->
            uploadFiles << file
        }
        codedDir.eachFileRecurse (FileType.FILES) { file ->
            codedFiles << file
        }
        uploadFiles.each { file ->
            if (!codedFiles.contains("coded_"+ file.name)) {
               println "Start to geocode " + file.name
            }
        }
        sleep(10000)
    }

