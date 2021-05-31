package com.sope.sync.service.fi;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTimeZone;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TvShowConverterUtil {

    public final static Map<String,Integer> tvShowOrdinal = new ImmutableMap.Builder<String,Integer>()
            .put("yle1", 1)
            .put("yle2", 2)
            .put("mtv3", 3)
            .put("nelonen", 4)
            .put("yle fem", 5)
            .put("sub", 6)
            .put("yle teema", 7)
            .put("liv", 8)
            .put("jim", 9)
            .put("tv5", 10)
            .put("kutonen", 11)
            .put("fox", 12)
            .put("ava", 13)
            .put("hero", 14)
            .put("frii", 15)
            .put("tlc finland", 16)
            
            .build();
    // FIXME parempi tapa muuttaa ikonit 
    public final static Map<String,String> tvShowIcons = new ImmutableMap.Builder<String,String>()
            .put("yle1", "show_yletv1")
            .put("yle2", "show_yletv2")
            .put("mtv3", "show_mtv3")
            .put("nelonen", "show_tv4")
            .put("sub", "show_sub")
            .put("jim", "show_jim")
            .put("yle teema", "show_yleteema")
            .put("yle fem", "show_ylefem")
            .put("liv", "show_liv")
            .put("tv5", "show_tv5")
            .put("kutonen", "show_tv6")
            .put("fox", "show_fox")
            .put("ava", "show_ava")
            .put("hero", "show_hero")
            .put("frii", "show_frii")
            .put("tlc finland", "show_tlcfinland")
            .build();

    // Old exclude-list that should have been include. Fixed
    private static List<String> excludeChannels = new ImmutableList.Builder<String>()
            .add("mtv max")
            .add("mtv sport 1")
            .add("mtv sport 2")
            .add("cmore first")
            .add("cmore hits")
            .add("mtv juniori")
            .add("cmore tennis")
            .add("cmore golf")
            .add("cmore series")
            .add("cmore stars")
            .add("mtv fakta")
            .add("sf-kanalen")
            .add("mtv max")
            .add("mtv fakta")
            .add("mtv juniori")
            .add("nelonen prime")
            .add("eurosport")
            .add("mtv sport 1")
            .add("mtv sport 2")
            .add("viasat sport")
            .add("viasat hockey")
            .add("viasat golf")
            .add("eurosport hd")
            .add("nelonen pro 1")
            .add("nelonen pro 2")
            .add("nelonen maailma")
            .add("nelonen nappula")
            .add("mtv3 hd")
            .add("nelonen pro 3")
            .add("nelonen pro 4")
            .add("nelonen pro 5")
            .add("nelonen pro 6")
            .add("nelonen pro 7")
            .add("nelonen pro 8")
            .add("viasat urheilu")
            .add("yle tv1 hd")
            .add("yle tv2 hd")
            .add("yle fem hd")
            .add("yle teema hd")
            .add("nhl xtra 7")
            .add("nhl xtra 1")
            .add("nhl xtra 2")
            .add("nhl xtra 3")
            .add("nhl xtra 4")
            .add("nhl xtra 5")
            .add("nhl xtra 6")
            .add("nhl xtra 8")
            .add("alfa tv")
            .add("mtv sport f1 on board")
            .add("mtv sport f1 varikko")
            .add("mtv sport f1 sijainti")
            .add("mtv sport f1 data")
            .add("mtv sport f1 kohokohdat")
            .add("viasat jääkiekko hd")
            .add("viasat jalkapallo hd")
            .add("viasat sport premium hd")
            .add("viasat fotboll hd")
            .build();

    
    public static final DateTimeZone TIMEZONE_HELSINKI = DateTimeZone.forID("Europe/Helsinki");

    // hardcoded values in phone
    public static String getShowIconForPhone(String channelName) {
        String lowerCaseIcon = channelName.toLowerCase();
        if (tvShowIcons.containsKey(lowerCaseIcon.toLowerCase())) {
            return tvShowIcons.get(lowerCaseIcon);
        }
        if (channelName != null && channelName != "") {
            return channelName.replaceAll(" ", "");
        }
        return "MISSING";
    }

    public static int getShowOrdinal(String channel) {
        String lowerChaseChannel = channel.toLowerCase();
        try {
            if (tvShowOrdinal.containsKey(lowerChaseChannel)) {
                return tvShowOrdinal.get(lowerChaseChannel);
            }
        } catch (Exception e) {
               e.printStackTrace();
        }
        return 100;
    }

    public static boolean includeChannel(String channel) {
        return tvShowIcons.containsKey(channel.toLowerCase());
    }
}
