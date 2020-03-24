package io.emeraldpay.dshackle.config

import io.emeraldpay.dshackle.FileResolver
import io.emeraldpay.dshackle.test.TestingCommons
import io.emeraldpay.grpc.Chain
import spock.lang.Specification

class MainConfigReaderSpec extends Specification {

    MainConfigReader reader = new MainConfigReader(TestingCommons.fileResolver())

    def "Read full config"() {
        setup:
        def config = this.class.getClassLoader().getResourceAsStream("dshackle-full.yaml")
        when:
        def act = reader.read(config)

        then:
        act != null
        act.host == "192.168.1.101"
        act.port == 2448
        act.tls != null
        with(act.tls) {
            certificate == "/path/127.0.0.1.crt"
            key == "/path/127.0.0.1.p8.key"
            !clientRequire
            clientCa == "/path/ca.dshackle.test.crt"
        }
        act.cache != null
        with(act.cache) {
            redis != null
            redis.host == "redis-master"
        }
        act.proxy != null
        with(act.proxy) {
            port == 8082
            tls != null
            routes != null
            routes.size() == 3
            with(routes[0]) {
                id == "eth"
                blockchain == Chain.ETHEREUM
            }
            with(routes[1]) {
                id == "etc"
                blockchain == Chain.ETHEREUM_CLASSIC
            }
            with(routes[2]) {
                id == "kovan"
                blockchain == Chain.TESTNET_KOVAN
            }
        }
        act.upstreams != null
        with(act.upstreams) {
            defaultOptions != null
            defaultOptions.size() == 1
            with(defaultOptions[0]) {
                chains == ["ethereum"]
                options.minPeers == 3
            }
            upstreams.size() == 3
            with(upstreams[0]) {
                id == "remote"
            }
            with(upstreams[1]) {
                id == "local"
            }
            with(upstreams[2]) {
                id == "infura"
            }
        }
    }
}
