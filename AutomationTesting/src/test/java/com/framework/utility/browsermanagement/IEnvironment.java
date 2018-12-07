package com.framework.utility.browsermanagement;

import org.aeonbits.owner.Config;

@Config.Sources({"file:src/test/resources/testenvironments/${environment}.properties"})

public interface IEnvironment extends Config {

    @Key("HomeUrl")
    String getHomepageUrl();

    @Key("DigitalIdUrl")
    String getDigitalIdUrl();

    @Key("OurPostUrl")
    String getOurPostUrl();

    @Key("IpgUrl")
    String getIpgUrl();
}
