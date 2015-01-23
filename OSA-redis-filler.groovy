
@Grab('redis.clients:jedis:2.6.2')

def redis = new redis.clients.jedis.Jedis("localhost")

assert "PONG" == redis.ping()

  def filename = args[0]//geocache3.csv


  File f = new File(filename)

    f.withReader {
        ff2->
            def i = 0;
            ff2.eachLine {
                ln->
                    i++;
                    def line = ln;
                    if (ln.contains(";")) {
                        def lnarr = ln.split(";")
                        def addressKey = lnarr[0];
                        def addressLng = lnarr[1];
                        def addressLat = lnarr[2];
                        def addressFull = lnarr[3];
                        def addressPrecision = lnarr[4];
                        if (!redis.exists(addressKey)) {
                            println "PUT"
                            redis.hset(addressKey, "lng", addressLng)
                            redis.hset(addressKey, "lat", addressLat)
                            redis.hset(addressKey, "full", addressFull)
                            redis.hset(addressKey, "precision", addressPrecision)
                        }
                        def fromRedis = redis.hgetAll(addressKey)
                        println fromRedis.lng + " : " + fromRedis.lat
                    }
            }
            println "Total records: " + i;
            //redis.hgetAll("key");

    }

