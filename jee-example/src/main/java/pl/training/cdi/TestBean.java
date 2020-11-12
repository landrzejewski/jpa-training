package pl.training.cdi;

import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class TestBean {

    @Setter
    @Inject
    private AccountsService accountsService;

    @PostConstruct
    public void setup() {
        accountsService.createAccount();
    }

}
