package com.saucedemo.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources("classpath:application.properties")
public interface SauceDemoTestConfig extends Config {

    @Key("saucedemo.standard-username")
    String standardUsername();

    @Key("saucedemo.locked-out-user")
    String lockedOutUser();

    @Key("saucedemo.problem-user")
    String problemUser();

    @Key("saucedemo.performance-glitch-user")
    String performanceGlitchUser();

    @Key("saucedemo.error-user")
    String errorUser();

    @Key("saucedemo.visual-user")
    String visualUser();

    @Key("saucedemo.standard-password")
    String standardPassword();

    static SauceDemoTestConfig get() {
        return ConfigFactory.create(SauceDemoTestConfig.class);
    }
}
