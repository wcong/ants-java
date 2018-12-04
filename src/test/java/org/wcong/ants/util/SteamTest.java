package org.wcong.ants.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SteamTest {

    @Test
    public void testSteam(){
        Map<String,String> cookie = new HashMap<>();
        cookie.put("_ga","GA1.2.1154624872.1540874344");
        cookie.put("device_id","2dcfeb0e672b5029ca8978a1555ad1d9");
        cookie.put("s","ei11ydqbk2");
        cookie.put("__utmz","1.1540874348.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        cookie.put("bid","b254469f257554b0cf380cbb415dddcf_jnv8vymk");
        cookie.put("aliyungf_tc","AQAAAOZqKWoVDgIAm6A6e5NdkXBndU27");
        cookie.put("xq_a_token","6125633fe86dec75d9edcd37ac089d8aed148b9e");
        cookie.put("xq_a_token.sig","CKaeIxP0OqcHQf2b4XOfUg-gXv0");
        cookie.put("xq_r_token","335505f8d6608a9d9fa932c981d547ad9336e2b5");
        cookie.put("xq_r_token.sig","i9gZwKtoEEpsL9Ck0G7yUGU42LY");
        cookie.put("u","381543740767299");
        cookie.put("Hm_lvt_1db88642e346389874251b5a1eded6e3","1542257082,1543740767");
        cookie.put("__utma","1.1154624872.1540874344.1540950060.1543740777.3");
        cookie.put("__utmc","1");
        cookie.put("Hm_lpvt_1db88642e346389874251b5a1eded6e3","1543741257");
        System.out.println(cookie.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue())
                .reduce((v1, v2) -> v1 + ";" + v2).get());
    }

}
