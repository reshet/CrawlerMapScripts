
  def filename = args[0]//"25KMIS1-1"
  def addressVars = ["56", "57", "199","200","342","343","485"
          ,"486"
          ,"628"
          ,"629"
          ,"771"
          ,"772"
          ,"914"
          ,"915"
          ,"1057","1058","1200"
          ,"1201"
          ,"1342"
          ,"1376","1377","1519","1520","1662","1663","1805","1806","1948","1949"
          ,"2091"
          ,"2092"
          ,"2234"
          ,"2235"
          ,"2377"
          ,"2378"
          ,"2520"
          ,"2521"
          ,"2662"
  ]
  def Matrix = [:]

  def defaultPath = "/home/geocode/";
  File f = new File(defaultPath + filename)
  File f3 = new File(defaultPath + "rotated_" + filename + ".csv" )
  f3.createNewFile()

String osaEncoding = "Cp866"

    f.withReader(osaEncoding, {
        ff2->
            ff2.eachLine {
                ln->
                    def line = ln;
                    if (ln.contains(";") && !ln.startsWith(";") && ln.contains(" # ")) {
                        def lnarr = ln.split(";")
                        if (ln.length() >= 2 ) {
                            def items = lnarr[0].trim().split(" ");
                            def var = items[items.length - 1]
                            def Case = items[1]
                            if (var in addressVars) {
                                def address_coded = lnarr[lnarr.length-1]
                                def address_items = address_coded.split(" # ")
                                if (address_items.length == 4) {
                                    if (!Matrix.containsKey(Case)) {
                                        Matrix[Case] = [:]
                                    }
                                    caseMap = Matrix[Case]
                                    caseMap["case"] = Case
                                    caseMap[var] = address_items[0]
                                    caseMap[var + "_lat"] = address_items[1]
                                    caseMap[var + "_lng"] = address_items[2]
                                    caseMap[var + "_g_adr"] = address_items[3]
                                    Matrix[Case] = caseMap;
                                }
                            }
                        }
                    }
            }
    })

  f3.withWriter(osaEncoding, {
        ff3 ->
            def header = "Case;";
            addressVars.each { elem ->
                header += elem + ";" + elem + "_lat;" + elem + "_lng;" + elem + "_g_adr;"
            }
            ff3 << header +"\n";

            Matrix.each { Case, Vars ->
                def line = new StringBuilder(Case + ";")
                addressVars.each { elem ->
                    if (Vars.containsKey(elem)) {
                        line.append(Vars[elem])
                    }
                    line.append(";")
                    if (Vars.containsKey(elem + "_lat")) {
                        line.append(Vars[elem + "_lat"])
                    }
                    line.append(";")
                    if (Vars.containsKey(elem + "_lng")) {
                        line.append(Vars[elem + "_lng"])
                    }
                    line.append(";")
                    if (Vars.containsKey(elem + "_g_adr")) {
                        line.append(Vars[elem + "_g_adr"])
                    }
                    line.append(";")
                }
                ff3 << line.toString() + "\n";
            }
  })

  //println Matrix
