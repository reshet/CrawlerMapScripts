
  def filename = args[0]//"25KMIS1-1"
  def defaultPath = "/home/geocode/";
  File f = new File(defaultPath + filename)
  //File f3 = new File(defaultPath + "rotated_" + filename + ".csv" )
  //f3.createNewFile()

  String osaEncoding = "Cp866"

    f.withReader(osaEncoding, {
        ff->
            int i = 0;
            ff.eachLine {
                ln->
                    if (i > 0) {
                        def lnarr = ln.split(";")
                        lnarr.eachWithIndex {
                            column, index ->
                            if (index > 0) {
//                                if ((index - 1) % 4 == 0) {
//                                    println column
//                                }
                                if ((index - 1) % 4 == 1) {
                                    if (lnarr[index - 1] == "" && lnarr[index-2] != "") {
                                        println "Should geocode " + lnarr[index - 2]
                                    }
                                }
                            }

                        }
                    }
                    i++
            }
    })

//  f3.withWriter(osaEncoding, {
//        ff3 ->
//
//                ff3 << line.toString() + "\n";
//  })